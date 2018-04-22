package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

public interface OnEveningChangeListener {
    public void onEveningChanged(JSONObject current) throws JSONException;
}
