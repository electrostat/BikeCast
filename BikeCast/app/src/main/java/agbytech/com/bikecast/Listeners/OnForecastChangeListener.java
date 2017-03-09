package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anthony-Parkour on 11/23/16.
 */
public interface OnForecastChangeListener {
    public void onForecastChanged(JSONObject current) throws JSONException;
}
