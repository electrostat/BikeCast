package agbytech.com.bikecast;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Anthony-Parkour on 11/23/16.
 */
public class ForecastListener {
    public OnForecastChangeListener listener;

    public JSONArray hourly;

    public void setOnForecastChangeListener(OnForecastChangeListener listener){
        this.listener = listener;
    }

    public JSONArray get(){
        return hourly;
    }

    public void set(JSONArray aHourly) throws JSONException {

        hourly = aHourly;

        if(listener != null){

            listener.onForecastChanged(aHourly);
        }else{
        }
    }
}
