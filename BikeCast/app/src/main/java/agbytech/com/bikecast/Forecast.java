package agbytech.com.bikecast;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Anthony-Parkour on 5/29/16.
 */
public class Forecast {
    private final String LOG_TAG="BikeCast";

    static Config config = new Config();

    private static String API_KEY = config.darksky;

    public JSONArray hourly;

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

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject hourlyObject = jsonObject.getJSONObject("hourly");
                    hourly = hourlyObject.getJSONArray("data");

                    JSONObject hour1 = hourly.getJSONObject(0);
                    long time1 = hour1.getLong("time");

                    Date date = new Date(time1);
                    DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                    String dateFormatted = formatter.format(date);

//                    Log.e(LOG_TAG, "first hour: " + dateFormatted);
                }
                catch (MalformedURLException ex){
                    Log.e(LOG_TAG, "Invalid URL", ex);
                }
                catch (IOException ex){
                    Log.e(LOG_TAG, "IO/Connection Error", ex);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if(connection == null){
                        connection.disconnect();
                    }

                    currentThread().interrupt();
                }

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
