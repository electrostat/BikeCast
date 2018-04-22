package agbytech.com.bikecast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import agbytech.com.bikecast.Listeners.OnEveningChangeListener;
import agbytech.com.bikecast.Listeners.OnForecastChangeListener;
import agbytech.com.bikecast.Listeners.OnMorningChangeListener;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG="BikeCast";

    //initial location zoom
    private LocationManager gpsLocManager;
    private LocationManager netLocManager;

    private static final int REQUEST_CODE_PERMISSION = 2;
    public Forecast forecast = new Forecast();
    private TextView bikeResult;

    //map
    static Config config = new Config();
    private static String MAPBOX_TOKEN = config.mapbox_token;
    private MapView mapView;
    private MapboxMap map;

    //preferences
    private static final String PREFERENCE_NAME = "boolPreferences";
    private int minTemp;
    private int maxTemp;
    private int windSpd;
    private int rainChance;
    private int humidity;

    //stoplights
    private FloatingActionButton nowLight;
    private FloatingActionButton amLight;
    private FloatingActionButton pmLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupLights();

        bikeResult = (TextView) findViewById(R.id.bikeResponse);

        Mapbox.getInstance(this, MAPBOX_TOKEN);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                Location lastLocation = retrieveLastLocation();
                updateMapCenter(lastLocation, 13);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mapView.onStart();
        grabCurrentStats();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }else {
            getCurrentLocation();
        }

        forecast.forecastListener.setOnForecastChangeListener(new OnForecastChangeListener() {

            @Override
            public void onForecastChanged(JSONObject hourly) throws JSONException {
                JSONObject hour1 = hourly;
                long time1 = hour1.getLong("time");

                Date date = new Date(time1);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                String dateFormatted = formatter.format(date);

                okToBike(hour1, "current");

                bikeResult = ((TextView) findViewById(R.id.bikeResponse));

            }
        });

        forecast.morningListener.setOnMorningChangeListener(new OnMorningChangeListener() {

            @Override
            public void onMorningChanged(JSONObject hourly) throws JSONException {

                JSONObject hour1 = hourly;
                long time1 = hour1.getLong("time");

                Date date = new Date(time1);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                String dateFormatted = formatter.format(date);

                okToBike(hour1, "morning");

                bikeResult = ((TextView) findViewById(R.id.bikeResponse));

            }
        });

        forecast.eveningListener.setOnEveningChangeListener(new OnEveningChangeListener() {

            @Override
            public void onEveningChanged(JSONObject hourly) throws JSONException {

                JSONObject hour1 = hourly;
                long time1 = hour1.getLong("time");

                Date date = new Date(time1);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                String dateFormatted = formatter.format(date);

                okToBike(hour1, "evening");

                bikeResult = ((TextView) findViewById(R.id.bikeResponse));
            }
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_PERMISSION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                    getCurrentLocation();
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_preference) {
            Intent i = new Intent(getApplicationContext(), PreferenceActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        //set criteria
        Criteria criteria = new Criteria();
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        gpsLocManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        gpsLocManager.getBestProvider(criteria, true);
        netLocManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        netLocManager.getBestProvider(criteria, true);

        gpsLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, burstListener);
        netLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, burstListener);
    }

    private LocationListener burstListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            gpsLocManager.removeUpdates(burstListener);
            netLocManager.removeUpdates(burstListener);

            forecast.getCurrent(location);
            callCommute(location);

            updateMapCenter(location, 13);

            retainLastLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };


    private void okToBike(JSONObject anHour, String time) throws JSONException {
        double temp = anHour.getDouble("temperature");
        double wind = anHour.getDouble("windSpeed");
        double rain = anHour.getDouble("precipProbability");
        double humid = anHour.getDouble("humidity");

        if(!temperatureOk(temp)){
            updateUI(time, false, "temp");
        }

        if(!windOk(wind)) {
            updateUI(time, false, "wind");
        }

        if(!rainOk(rain)) {
            updateUI(time, false, "rain");
        }

        if(!humidityOk(humid)){
            updateUI(time, false, "humidity");
        }

        updateUI(time, true, "all");
    }

    private void callCommute(Location location){
        forecast.getMorning(location, determineMorningTime());
        forecast.getEvening(location, determineEveningTime());
    }

    private long determineMorningTime(){
        int morn = 10;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, morn);
        Date date = cal.getTime();

        long mornTime = date.getTime();
        long currentTime= System.currentTimeMillis();

        if(currentTime > mornTime){
            return (mornTime + 86400000)/1000;
        }else{
            return mornTime/1000;
        }
    }

    private long determineEveningTime(){
        int evening = 18;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, evening);
        Date date = cal.getTime();

        long evenTime = date.getTime();
        long currentTime= System.currentTimeMillis();

        if(currentTime > evenTime){
            return (evenTime + 86400000)/1000;
        }else{
            return evenTime/1000;
        }
    }
  
  private void updateMapCenter(Location location, int zoomLevel){

        if(location != null) {
            CameraPosition position = new CameraPosition.Builder()
              .target(new LatLng(location.getLatitude(), location.getLongitude()))
              .zoom(zoomLevel)
              .build();

            if(map != null) {
                map.animateCamera(CameraUpdateFactory
                  .newCameraPosition(position), 5000);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void retainLastLocation(Location location){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BikeCast", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong("lastLat", Double.doubleToRawLongBits(location.getLatitude()));
        editor.putLong("lastLon", Double.doubleToRawLongBits(location.getLongitude()));
        editor.putString("lastProvider", location.getProvider());
        editor.commit();
    }

    private Location retrieveLastLocation(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BikeCast", 0);

        String lastProvider = pref.getString("lastProvider", null);
        long lastLat = pref.getLong("lastLat", 0);
        long lastLon = pref.getLong("lastLon", 0);

        if(lastProvider == null || lastLat == 0 || lastLon == 0){
            return null;
        }

        Location lastLocation = new Location(lastProvider);
        lastLocation.setLatitude(Double.longBitsToDouble(lastLat));
        lastLocation.setLongitude(Double.longBitsToDouble(lastLon));

        return lastLocation;
    }

    private void grabCurrentStats() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        minTemp = prefs.getInt("minTemp", 30);
        maxTemp = prefs.getInt("maxTemp", 100);
        windSpd = prefs.getInt("windSpd", 10);
        rainChance = prefs.getInt("rainChance", 50);
        humidity = prefs.getInt("humidity", 80);

        Log.v(LOG_TAG, "currentStats -- mintemp:" + minTemp + ", maxtemp:" + maxTemp + ", windSpd: " + windSpd + ", rainChance: " + rainChance + ", humidity: " + humidity);
    }

    private boolean temperatureOk(double temperature) {
        return temperature < maxTemp && temperature > minTemp;
    }

    private boolean windOk(double wind) {
        return wind < windSpd;
    }

    private boolean rainOk(double rain) {
        return rain < rainChance;
    }

    private boolean humidityOk(double humidityPercent) {
        return humidityPercent < humidity;
    }

    private void updateUI(String time, boolean status, String reason) {

        if (time.equalsIgnoreCase("current")) {
            updateNowLight(status);
        } else if (time.equalsIgnoreCase("morning")) {
            updateAmLight(status);
        } else {
            updatePmLight(status);
        }

        if (status) {
            Log.e(LOG_TAG, "Bike " + time +  " all good");
        } else {
            Log.e(LOG_TAG, "Do not bike " + time +  " due to " + reason);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void updateNowLight(boolean status) {
        if (status) {
            nowLight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGo)));
        } else {
            nowLight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorStop)));
        }
    }

    @SuppressLint("ResourceAsColor")
    private void updateAmLight(boolean status) {
        if (status) {
            amLight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGo)));
        } else {
            amLight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorStop)));
        }
    }

    @SuppressLint("ResourceAsColor")
    private void updatePmLight(boolean status) {
        if (status) {
            pmLight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGo)));
        } else {
            pmLight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorStop)));
        }
    }

    private void setupLights() {
        nowLight = findViewById(R.id.nowLight);
        nowLight.setImageBitmap(textAsBitmap("NOW", 40F, Color.WHITE));

        amLight = findViewById(R.id.amLight);
        amLight.setImageBitmap(textAsBitmap("AM", 40F, Color.WHITE));

        pmLight = findViewById(R.id.pmLight);
        pmLight.setImageBitmap(textAsBitmap("PM", 40F, Color.WHITE));
    }

    private Bitmap textAsBitmap(String text, Float textSize, int textColor) {
        Paint paint  = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        Float baseline  = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.0f);
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas  = new Canvas(image);
        canvas.drawText(text, 0F, baseline, paint);
        return image;
    }
}
