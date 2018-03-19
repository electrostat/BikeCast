package agbytech.com.bikecast.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

public class EveningListener {
    public OnEveningChangeListener listener;

    public JSONObject currently;

    public void setOnEveningChangeListener(OnEveningChangeListener listener){
        this.listener = listener;
    }

    public JSONObject get(){
        return currently;
    }

    public void set(JSONObject aCurrent) throws JSONException {

        currently = aCurrent;

        if(listener != null){

            listener.onEveningChanged(aCurrent);
        }else{
        }
    }
}
