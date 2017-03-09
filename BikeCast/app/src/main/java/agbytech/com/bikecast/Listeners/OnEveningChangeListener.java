package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anthony-Parkour on 2/28/17.
 */
public interface OnEveningChangeListener {
    public void onEveningChanged(JSONObject current) throws JSONException;
}
