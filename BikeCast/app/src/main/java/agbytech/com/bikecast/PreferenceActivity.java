package agbytech.com.bikecast;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;

public class PreferenceActivity extends AppCompatActivity {

    private final String LOG_TAG="BikeCast";
    private static final String PREFERENCE_NAME = "boolPreferences";
    private int minTemp;
    private int maxTemp;
    private int windSpd;
    private int rainChance;
    private int humidity;

    //ui
    private SeekBar minTempBar;
    private SeekBar maxTempBar;
    private SeekBar windSpdBar;
    private SeekBar rainChanceBar;
    private SeekBar humidityBar;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;
    private TextView minTempText;
    private TextView maxTempText;
    private TextView windSpdText;
    private TextView rainChanceText;
    private TextView humidityText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //generate UI
        seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar == minTempBar){
                    minTempText.setText(Integer.toString(i));
                    saveStat("minTemp", i);
                }else if(seekBar == maxTempBar){
                    maxTempText.setText(Integer.toString(i));
                    saveStat("maxTemp", i);
                }else if(seekBar == windSpdBar){
                    windSpdText.setText(Integer.toString(i));
                    saveStat("windSpd", i);
                }else if(seekBar == rainChanceBar){
                    rainChanceText.setText(Integer.toString(i));
                    saveStat("rainChance", i);
                }else if(seekBar == humidityBar){
                    humidityText.setText(Integer.toString(i));
                    saveStat("humidity", i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        minTempBar = (SeekBar) findViewById(R.id.minTempSeekBar);
        minTempBar.setOnSeekBarChangeListener(seekBarChangeListener);
        maxTempBar = (SeekBar) findViewById(R.id.maxTempSeekBar);
        maxTempBar.setOnSeekBarChangeListener(seekBarChangeListener);
        windSpdBar = (SeekBar) findViewById(R.id.maxWindSeekBar);
        windSpdBar.setOnSeekBarChangeListener(seekBarChangeListener);
        rainChanceBar = (SeekBar) findViewById(R.id.maxRainSeekBar);
        rainChanceBar.setOnSeekBarChangeListener(seekBarChangeListener);
        humidityBar = (SeekBar) findViewById(R.id.maxHumiditySeekBar);
        humidityBar.setOnSeekBarChangeListener(seekBarChangeListener);

        minTempText = (TextView) findViewById(R.id.minTempAmt);
        maxTempText = (TextView) findViewById(R.id.maxTempAmt);
        windSpdText = (TextView) findViewById(R.id.maxWindAmt);
        rainChanceText = (TextView) findViewById(R.id.maxRainAmt);
        humidityText = (TextView) findViewById(R.id.maxHumidityAmt);

        grabCurrentStats();
    }

    private void grabCurrentStats() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        minTemp = prefs.getInt("minTemp", 30);
        maxTemp = prefs.getInt("maxTemp", 100);
        windSpd = prefs.getInt("windSpd", 10);
        rainChance = prefs.getInt("rainChance", 50);
        humidity = prefs.getInt("humidity", 80);

        updateVisuals();
    }

    private void updateVisuals() {
        //update bar
        minTempBar.setProgress(minTemp);
        maxTempBar.setProgress(maxTemp);
        windSpdBar.setProgress(windSpd);
        rainChanceBar.setProgress(rainChance);
        humidityBar.setProgress(humidity);

        //update numbers
        minTempText.setText(Integer.toString(minTemp));
        maxTempText.setText(Integer.toString(maxTemp));
        windSpdText.setText(Integer.toString(windSpd));
        rainChanceText.setText(Integer.toString(rainChance));
        humidityText.setText(Integer.toString(humidity));
    }

    public void saveStat(String stateName, int value){
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(stateName, value);

        editor.commit();
    }
}
