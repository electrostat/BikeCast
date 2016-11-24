package agbytech.com.bikecast;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Anthony-Parkour on 11/23/16.
 */
public interface OnForecastChangeListener {
    public void onForecastChanged(JSONArray hourly) throws JSONException;
}
