package agbytech.com.bikecast;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import agbytech.com.bikecast.Listeners.OnForecastChangeListener;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG="BikeCast";

    //initial location zoom
    private LocationManager gpsLocManager;
    private LocationManager netLocManager;

    private static final int REQUEST_CODE_PERMISSION = 2;

    public Forecast forecast = new Forecast();
    private TextView bikeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        bikeResult = (TextView) findViewById(R.id.bikeResponse);
    }

    @Override
    protected void onStart(){
        super.onStart();

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_CODE_PERMISSION);
//        }

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

                if(okToBike(hour1)){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            bikeResult.setText("Yep");
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bikeResult.setText("Nope");
                        }
                    });
                }

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

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_CODE_PERMISSION);
//        }
        gpsLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, burstListener);
        netLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, burstListener);
    }

    private LocationListener burstListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            gpsLocManager.removeUpdates(burstListener);
            netLocManager.removeUpdates(burstListener);

            forecast.getCurrent(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };


    private boolean okToBike(JSONObject anHour) throws JSONException {

        //parameters I care about
        double precipIntensity = anHour.getDouble("precipIntensity");
        double precipProbability = anHour.getDouble("precipProbability");
        double windSpd = anHour.getDouble("windSpeed");
        double feelTemp = anHour.getDouble("apparentTemperature");
        double humidity = anHour.getDouble("humidity");

        //conditions I care about
        boolean humidHot = false;
        boolean coldWindy = false;
        boolean rainy = false;

        //standalone limits
        if(feelTemp < 5 || feelTemp > 100){
            return false;
        }

        if(windSpd > 20){
            return  false;
        }

        if(feelTemp > 90 && humidity > 0.9){
            humidHot = true;
        }

        if(feelTemp < 10 && windSpd > 15){
            coldWindy = true;
        }

        if(precipIntensity > 0.3 && precipProbability > 0.5){
            rainy = true;
        }

        if(humidHot || coldWindy || rainy){
            return false;
        }else{
            return true;
        }
    }

}
