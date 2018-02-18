package agbytech.com.bikecast;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import agbytech.com.bikecast.Listeners.OnEveningChangeListener;
import agbytech.com.bikecast.Listeners.OnForecastChangeListener;
import agbytech.com.bikecast.Listeners.OnMorningChangeListener;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG="BikeCast";

    //initial location zoom
    private LocationManager gpsLocManager;
    private LocationManager netLocManager;

    private static final int REQUEST_CODE_PERMISSION = 2;
    public Forecast forecast = new Forecast();
    private TextView bikeResult;
    private ArrayList <WeatherBool> weatherBools = new ArrayList();

    //map
    static Config config = new Config();
    private static String MAPBOX_TOKEN = config.mapbox_token;
    private MapView mapView;
    private MapboxMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
//                getCurrentLocation();

                Intent i = new Intent(getApplicationContext(), PreferenceActivity.class);
                startActivity(i);

//                String[] invitees = new String[] {"Beth,Adam,4,2", "Cass,Adam,3,4", "Dole,Adam,2,3", "Evan,Beth,3,1", "Fury,Evan,2,2", "Greg,Dole,6,2", "Hugh,Cass,4,4", "Ivan,Cass,6,4", "Juan,Cass,3,1", "Kale,Ivan,1,6", "Leon,Ivan,2,5", "Mark,Ivan,1,6"};
//
//                ArrayList<String> doNotInviteList = doNotInvite(invitees);
//
//                Log.e(LOG_TAG, "Do not invite: " + String.valueOf(doNotInviteList));
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mapView.onStart();
        setBools();

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
                Log.e(LOG_TAG, "Acquired");

                JSONObject hour1 = hourly;
                long time1 = hour1.getLong("time");

                Date date = new Date(time1);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                String dateFormatted = formatter.format(date);

                Log.e(LOG_TAG, "first hour: " + hour1);
                Log.e(LOG_TAG, "should I bike? " + okToBike(hour1));

                bikeResult = ((TextView) findViewById(R.id.bikeResponse));

//                if(okToBike(hour1)){
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            bikeResult.setText("Yep");
//                        }
//                    });
//                }else{
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            bikeResult.setText("Nope");
//                        }
//                    });
//                }

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

                Log.e(LOG_TAG, "should I bike in the morning? " + okToBike(hour1));

                bikeResult = ((TextView) findViewById(R.id.bikeResponse));

//                if(okToBike(hour1)){
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            bikeResult.setText("Yep");
//                        }
//                    });
//                }else{
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            bikeResult.setText("Nope");
//                        }
//                    });
//                }

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

                Log.e(LOG_TAG, "should I bike in the evening? " + okToBike(hour1));

                bikeResult = ((TextView) findViewById(R.id.bikeResponse));

//                if(okToBike(hour1)){
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            bikeResult.setText("Yep");
//                        }
//                    });
//                }else{
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            bikeResult.setText("Nope");
//                        }
//                    });
//                }

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


    private boolean okToBike(JSONObject anHour) throws JSONException {
        //test weatherbool system
        for(int i = 0; i < weatherBools.size(); i++) {
            if (weatherBools.get(i).isTrue(anHour)) {
                return false;
            }
        }

        return true;
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

    private void setBools(){
        WeatherBool weatherBool1 = new WeatherBool();

        weatherBool1.param1 = "apparentTemperature";
        weatherBool1.operator1 = ">";
        weatherBool1.value1 = 90;
        weatherBool1.param2 = "humidity";
        weatherBool1.operator2 = ">";
        weatherBool1.value2 = 0.9;

        weatherBools.add(weatherBool1);

        //2
        WeatherBool weatherBool2 = new WeatherBool();

        weatherBool2.param1 = "apparentTemperature";
        weatherBool2.operator1 = "<";
        weatherBool2.value1 = 10;
        weatherBool2.param2 = "windSpeed";
        weatherBool2.operator2 = ">";
        weatherBool2.value2 = 15;

        weatherBools.add(weatherBool2);

        //3
        WeatherBool weatherBool3 = new WeatherBool();

        weatherBool3.param1 = "precipIntensity";
        weatherBool3.operator1 = ">";
        weatherBool3.value1 = 0.3;
        weatherBool3.param2 = "precipProbability";
        weatherBool3.operator2 = ">";
        weatherBool3.value2 = 0.5;

        weatherBools.add(weatherBool3);

        //4
        WeatherBool weatherBool4 = new WeatherBool();

        weatherBool4.param1 = "apparentTemperature";
        weatherBool4.operator1 = "<";
        weatherBool4.value1 = 5;

        weatherBools.add(weatherBool4);

        //5
        WeatherBool weatherBool5 = new WeatherBool();

        weatherBool5.param1 = "apparentTemperature";
        weatherBool5.operator1 = ">";
        weatherBool5.value1 = 100;

        weatherBools.add(weatherBool5);

        //6
        WeatherBool weatherBool6 = new WeatherBool();

        weatherBool6.param1 = "windSpeed";
        weatherBool6.operator1 = ">";
        weatherBool6.value1 = 20;

        weatherBools.add(weatherBool6);
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

    private ArrayList<String> doNotInvite(String[] inviteees){
        ArrayList<String> noInviteList = new ArrayList<String>();
        Invitee[] reviewedInvitees = new Invitee[inviteees.length];

        for(int i = inviteees.length - 1; i >= 0; i--){
            String[] thisInviteeString = inviteees[i].split(",");

            Invitee thisInvitee = new Invitee(thisInviteeString);

            int netCandy = thisInvitee.candyBrought - thisInvitee.candyCosumed;

            for(int a = 0; a < inviteees.length - 1 - i; a++){
                Invitee reviewInvitee = reviewedInvitees[a];

                if(reviewInvitee.invitor.equals(thisInvitee.guest)){
                    netCandy = netCandy + reviewInvitee.netCandy;
                }
            }

            thisInvitee.netCandy = netCandy;
            reviewedInvitees[inviteees.length - 1 - i] = thisInvitee;

            if(netCandy < 0){
                noInviteList.add(thisInvitee.guest);
            }
        }

        return noInviteList;
    }
}

class Invitee {
    //initialize bool doubles
    String guest;
    String invitor;
    int candyBrought;
    int candyCosumed;
    int netCandy;

    public Invitee(String[] input) {
        guest = input[0];
        invitor = input[1];
        candyBrought = Integer.parseInt(input[2]);
        candyCosumed = Integer.parseInt(input[3]);
    }
}
