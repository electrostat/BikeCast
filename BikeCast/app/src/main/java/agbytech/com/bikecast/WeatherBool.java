package agbytech.com.bikecast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anthony-Parkour on 3/29/17.
 */

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
//            if(combiner.equals("&&")){
//                if(param1OK && param2OK){
//                    Log.e(LOG_TAG, "both param1 and param2 called");
//                    return false;
//                }else{
//                    return true;
//                }
//            }else{
//                if(param1OK || param2OK){
//                    return true;
//                }else{
//                    return false;
//                }
//            }
        }else{

            return param1OK;
//            if(param1OK){
//
//                return false;
//            }else{
//                return true;
//            }
        }

//        return true;
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
