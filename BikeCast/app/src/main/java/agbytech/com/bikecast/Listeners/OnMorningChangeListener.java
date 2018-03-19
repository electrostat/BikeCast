package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

public interface OnMorningChangeListener {
    public void onMorningChanged(JSONObject current) throws JSONException;
}
