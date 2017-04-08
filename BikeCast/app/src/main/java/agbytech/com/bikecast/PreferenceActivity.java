package agbytech.com.bikecast;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Anthony-Parkour on 6/16/16.
 */
public class PreferenceActivity extends AppCompatActivity {

    private final String LOG_TAG="BikeCast";
    public int boolNum;
    private static final String PREFERENCE_NAME = "boolPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addBool);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(LOG_TAG, "add clicked" +
                        "");
            }
        });
    }

    public void updateTable(){
//        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);

        TableLayout table = (TableLayout) findViewById(R.id.pref_table);

        table.removeAllViews();

        boolNum = prefs.getInt("arraySize", 0);

        Log.e(LOG_TAG, "Array Size: " + boolNum);

        for(int i = 1; i < boolNum + 1; i++){

            //reverse number
            int revNum = boolNum + 1 - i;

            String nameString = prefs.getString("name"+revNum, null);
            String addString = prefs.getString("add"+revNum, null);
            String catString = prefs.getString("cat"+revNum, null);
            String cat2String = prefs.getString("cat2"+revNum, null);
            String distString = prefs.getString("dist"+revNum, null);
            String locString = prefs.getString("loc"+revNum, null);
            String timeString = prefs.getString("time"+revNum, null);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

            LinearLayout LL = new LinearLayout(this);
            LL.setOrientation(LinearLayout.VERTICAL);

            LL.setWeightSum(6f);
            LL.setLayoutParams(layoutParams);

            TextView name = new TextView(this);
            name.setText(nameString);
            name.setPadding(5, 5, 0, 0);
            name.setLayoutParams(layoutParams);

            TextView address = new TextView(this);
            address.setText(addString);
            address.setPadding(5, 5, 0, 0);
            address.setLayoutParams(layoutParams);

            TextView categories = new TextView(this);
            categories.setText("Categories: " + catString + ", " + cat2String);
            categories.setPadding(5, 5, 0, 0);
            categories.setLayoutParams(layoutParams);

            TextView userLocation = new TextView(this);
            userLocation.setText("User Location: " + locString);
            userLocation.setPadding(5, 5, 0, 0);
            userLocation.setLayoutParams(layoutParams);

            TextView distance = new TextView(this);
            distance.setText("Distance from Venue: " + distString);
            distance.setPadding(5, 5, 0, 0);
            distance.setLayoutParams(layoutParams);

            TextView time = new TextView(this);
            time.setText("Time: " + timeString);
            time.setPadding(5, 5, 0, 0);
            time.setLayoutParams(layoutParams);

            LL.addView(name);
            LL.addView(address);
            LL.addView(categories);
            LL.addView(userLocation);
            LL.addView(distance);
            LL.addView(time);

            row.addView(LL);

            //add divider
            View v = new View(this);
            v.setPadding(0, 5, 0, 0);
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            v.setBackgroundColor(Color.rgb(51, 51, 51));

            table.addView(row);
            table.addView(v);
        }
    }

    public void saveVenues(String name, String add, String cat1, String cat2, double dist, Location userloc){
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);

        SharedPreferences.Editor editor = prefs.edit();

        int venueNumber = prefs.getInt("arraySize", 0);
        venueNumber++;

        editor.putInt("arraySize", venueNumber);

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        editor.putString("name" + venueNumber, name);
        editor.putString("add" + venueNumber, add);
        editor.putString("cat" + venueNumber, cat1);
        editor.putString("cat2" + venueNumber, cat2);
        editor.putString("dist" + venueNumber, String.valueOf(dist));
        editor.putString("loc" + venueNumber, "" + userloc.getLatitude() + ", " + userloc.getLongitude());
        editor.putString("time" + venueNumber, currentDateTimeString);

        editor.commit();
    }
}
