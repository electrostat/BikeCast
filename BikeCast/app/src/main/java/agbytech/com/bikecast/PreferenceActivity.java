package agbytech.com.bikecast;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PreferenceActivity extends AppCompatActivity {

    private final String LOG_TAG="BikeCast";
    public int boolNum;
    private static final String PREFERENCE_NAME = "boolPreferences";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        final View createView = (View) findViewById(R.id.create_panel);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addBool);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) createView.getLayoutParams();
//                params.height = 650;
//                createView.setLayoutParams(params);
//
//                createView.setVisibility(View.VISIBLE);
//                createView.animate().translationY(-1300);
            }
        });

//        final Button saveButt = (Button) findViewById(R.id.saveBool);
//        saveButt.animate().translationX(saveButt.getWidth());
//        saveButt.animate().translationY(-saveButt.getHeight());
//
//        saveButt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e(LOG_TAG, "save");
//
//                //get text values
//                EditText param = (EditText) findViewById(R.id.parameter);
//                EditText operand = (EditText) findViewById(R.id.operand);
//                EditText value = (EditText) findViewById(R.id.value);
//
//                Log.e(LOG_TAG, "Test values: param -" + param.getText() + "\noperand - " + operand.getText() + "\nvalue - " + value.getText());
//
////                saveBool("Hot and Humid", "apparentTemperature", ">", 90, "humidity", ">", 0.9);
//            }
//        });

//        final Button nextButt = (Button) findViewById(R.id.next);
//        nextButt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e(LOG_TAG, "next");
//
//                //get text values
//                EditText param = (EditText) findViewById(R.id.parameter);
//                EditText operand = (EditText) findViewById(R.id.operand);
//                EditText value = (EditText) findViewById(R.id.value);
//
//                Log.e(LOG_TAG, "Test values: param -" + param.getText() + "\noperand - " + operand.getText() + "\nvalue - " + value.getText());
//
//                nextButt.animate().translationX(-nextButt.getWidth());
//                saveButt.animate().translationX(-saveButt.getWidth());
//            }
//        });

        updateTable();
    }

    public void updateTable(){
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);

//        TableLayout table = (TableLayout) findViewById(R.id.pref_table);

//        table.removeAllViews();

        boolNum = prefs.getInt("arraySize", 0);

        Log.e(LOG_TAG, "Array Size: " + boolNum);

        for(int i = 1; i < boolNum + 1; i++){

            //reverse number
            int revNum = boolNum + 1 - i;

            String nameString = prefs.getString("name"+revNum, null);
            String param1String = prefs.getString("1param"+revNum, null);
            String operator1String = prefs.getString("1operator"+revNum, null);
            String value1String = prefs.getString("1value"+revNum, null);
            String param2String = prefs.getString("2param"+revNum, null);
            String operator2String = prefs.getString("2operator"+revNum, null);
            String value2String = prefs.getString("2value"+revNum, null);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT);

            LinearLayout LL = new LinearLayout(this);
            LL.setOrientation(LinearLayout.VERTICAL);

            LL.setWeightSum(6f);
            LL.setLayoutParams(layoutParams);

            TextView name = new TextView(this);
            name.setText(nameString);
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setPadding(5, 5, 0, 0);
            name.setLayoutParams(layoutParams);

//            TextView param1 = new TextView(this);
//            param1.setText(param1String);
//            param1.setPadding(5, 5, 0, 0);
//            param1.setLayoutParams(layoutParams);
//
//            TextView operator1 = new TextView(this);
//            operator1.setText(operator1String);
//            operator1.setPadding(5, 5, 0, 0);
//            operator1.setLayoutParams(layoutParams);

            TextView line1 = new TextView(this);
            line1.setText("If " + param1String + " " + operator1String + " " + value1String);
            line1.setPadding(5, 5, 0, 0);
            line1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            line1.setLayoutParams(layoutParams);

            TextView and = new TextView(this);
            and.setText("AND");
            and.setPadding(5, 5, 0, 0);
            and.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            and.setLayoutParams(layoutParams);

            TextView line2 = new TextView(this);
            line2.setText("If " + param2String + " " + operator2String + " " + value2String);
            line2.setPadding(5, 5, 0, 0);
            line2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            line2.setLayoutParams(layoutParams);

//            TextView param2 = new TextView(this);
//            param2.setText(param2String);
//            param2.setPadding(5, 5, 0, 0);
//            param2.setLayoutParams(layoutParams);
//
//            TextView operator2 = new TextView(this);
//            operator2.setText(operator2String);
//            operator2.setPadding(5, 5, 0, 0);
//            operator2.setLayoutParams(layoutParams);
//
//            TextView value2 = new TextView(this);
//            value2.setText(value2String);
//            value2.setPadding(5, 5, 0, 0);
//            value2.setLayoutParams(layoutParams);

            LL.addView(name);
//            LL.addView(param1);
//            LL.addView(operator1);
            LL.addView(line1);

            if(!param2String.equals("")){
                LL.addView(and);
                LL.addView(line2);
            }

//            LL.addView(value2);

            row.addView(LL);

            //add divider
            View v = new View(this);
            v.setPadding(0, 5, 0, 0);
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            v.setBackgroundColor(Color.rgb(51, 51, 51));

//            table.addView(row);
//            table.addView(v);
        }
    }

    public void saveBool(String name, String param1, String operator1, double val1, String param2, String operator2, double val2){
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = prefs.edit();

        int boolNumber = prefs.getInt("arraySize", 0);
        boolNumber++;

        editor.putInt("arraySize", boolNumber);

        editor.putString("name" + boolNumber, name);
        editor.putString("1param" + boolNumber, param1);
        editor.putString("1operator" + boolNumber, operator1);
        editor.putString("1value" + boolNumber, String.valueOf(val1));
        editor.putString("2param" + boolNumber, param2);
        editor.putString("2operator" + boolNumber, operator2);
        editor.putString("2value" + boolNumber, String.valueOf(val2));

        editor.commit();

        updateTable();
        hideAddScreen();
    }

    public void hideAddScreen(){
//        final View addView = (View) findViewById(R.id.create_panel);
//
//        addView.animate().translationY(350);
//        addView.setVisibility(View.INVISIBLE);
//
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) addView.getLayoutParams();
//        params.height = 1;
//        addView.setLayoutParams(params);
    }
}
