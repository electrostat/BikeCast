package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

public class MorningListener {
    public OnMorningChangeListener listener;

    public JSONObject currently;

    public void setOnMorningChangeListener(OnMorningChangeListener listener){
        this.listener = listener;
    }

    public JSONObject get(){
        return currently;
    }

    public void set(JSONObject aCurrent) throws JSONException {

        currently = aCurrent;

        if(listener != null){

            listener.onMorningChanged(aCurrent);
        }else{
        }
    }
}
