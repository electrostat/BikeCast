package agbytech.com.bikecast;

import android.location.Location;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Anthony-Parkour on 5/29/16.
 */
public class Forecast {
    private final String LOG_TAG="BikeCast";

    static Config config = new Config();

    private static String API_KEY = config.darksky;

    public void getForecast(final Location location){
        //track most recent outdoor location point for reporting
        new Thread(){
            public void run(){
                HttpURLConnection connection = null;

                try{
                    URL myURL = new URL("https://api.forecast.io/forecast/" + API_KEY + "/" + location.getLatitude() + "," + location.getLongitude());
                    connection = (HttpsURLConnection)myURL.openConnection();
                    connection.setRequestMethod("GET");

                    connection.connect();

                    InputStream iStream = connection.getInputStream();

                    String response = readStream(iStream);

                    Log.e(LOG_TAG, "Forecast Response: " + response);

                }
                catch (MalformedURLException ex){
                    Log.e(LOG_TAG, "Invalid URL", ex);
                }
                catch (IOException ex){
                    Log.e(LOG_TAG, "IO/Connection Error", ex);
                }finally {
                    if(connection == null){
                        connection.disconnect();
                    }

                    currentThread().interrupt();
                }
//
            }
        }.start();
    }


    private String readStream(InputStream is) {

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
