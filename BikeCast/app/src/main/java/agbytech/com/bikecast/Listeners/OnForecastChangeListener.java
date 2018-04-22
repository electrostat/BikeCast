package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

public interface OnForecastChangeListener {
    public void onForecastChanged(JSONObject current) throws JSONException;
}
