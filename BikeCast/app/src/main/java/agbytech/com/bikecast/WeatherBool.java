package agbytech.com.bikecast;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherBool {
    private final String LOG_TAG="weatherbool";

    //initialize bool doubles
    String param1;
    String operator1;
    double value1;
    String combiner;
    String param2;
    String operator2;
    double value2;

    public boolean isTrue(JSONObject thisForecast) throws JSONException {

        boolean param1OK = convertOperands(param1, operator1, value1, thisForecast);

        if(param2 != null){
            boolean param2OK = convertOperands(param2, operator2, value2, thisForecast);

                return param1OK && param2OK;
        }else{

            return param1OK;
        }
    }

    private boolean convertOperands(String param, String operator, double value, JSONObject thisForecast) throws JSONException {

        double thisValue = thisForecast.getDouble(param);

        if(operator.equals('<')){
            if(thisValue < value){
                return true;
            }else{
                return false;
            }
        }else if(operator.equals('>')){
            if(thisValue > value){
                return true;
            }else{
                return false;
            }
        }else{
            if(thisValue == value){
                return true;
            }else{
                return false;
            }
        }
    }
}
