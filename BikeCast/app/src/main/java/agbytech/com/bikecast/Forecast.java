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

import javax.net.ssl.HttpsURLConnection;

import agbytech.com.bikecast.Listeners.EveningListener;
import agbytech.com.bikecast.Listeners.ForecastListener;
import agbytech.com.bikecast.Listeners.MorningListener;

public class Forecast {
    private final String LOG_TAG="BikeCast";

    static Config config = new Config();

    private static String API_KEY = config.darksky;

    public JSONArray current;
    public JSONArray hourly;

    public static ForecastListener forecastListener = new ForecastListener();
    public static MorningListener morningListener = new MorningListener();
    public static EveningListener eveningListener = new EveningListener();


    public void getCurrent(final Location location){
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

                    JSONObject currentlyObject = jsonObject.getJSONObject("currently");

                    forecastListener.set(currentlyObject);
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

    public void getMorning(final Location location, final long time){
        //track most recent outdoor location point for reporting
        new Thread(){
            public void run(){
                HttpURLConnection connection = null;

                try{
                    URL myURL = new URL("https://api.forecast.io/forecast/" + API_KEY + "/" + location.getLatitude() + "," + location.getLongitude() + "," + time);
                    connection = (HttpsURLConnection)myURL.openConnection();
                    connection.setRequestMethod("GET");

                    connection.connect();

                    InputStream iStream = connection.getInputStream();

                    String response = readStream(iStream);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject currentlyObject = jsonObject.getJSONObject("currently");

                    morningListener.set(currentlyObject);
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

    public void getEvening(final Location location, final long time){
        //track most recent outdoor location point for reporting
        new Thread(){
            public void run(){
                HttpURLConnection connection = null;

                try{
                    URL myURL = new URL("https://api.forecast.io/forecast/" + API_KEY + "/" + location.getLatitude() + "," + location.getLongitude() + "," + time);
                    connection = (HttpsURLConnection)myURL.openConnection();
                    connection.setRequestMethod("GET");

                    connection.connect();

                    InputStream iStream = connection.getInputStream();

                    String response = readStream(iStream);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject currentlyObject = jsonObject.getJSONObject("currently");

                    eveningListener.set(currentlyObject);
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
