package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anthony-Parkour on 11/23/16.
 */
public class ForecastListener {
    public OnForecastChangeListener listener;

    public JSONObject currently;

    public void setOnForecastChangeListener(OnForecastChangeListener listener){
        this.listener = listener;
    }

    public JSONObject get(){
        return currently;
    }

    public void set(JSONObject aCurrent) throws JSONException {

        currently = aCurrent;

        if(listener != null){

            listener.onForecastChanged(aCurrent);
        }else{
        }
    }
}
