package com.deltapps.runscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;


public class RaceActivity extends ActionBarActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final long HOUR = 3600000;

    private Chronometer chrono;
    private TextView speedTexvView;
    private TextView paceTextView;
    private TextView distanceTextView;
    private TextView avgPaceTextView;
    private TextView altitudeTextView;
    private TextView scoreTextView;
    private Button startButton;
    private Button abortButton;
    private Button saveButton;
    private Button discardButton;

    private SharedPreferences GPSPrefs;
    private SharedPreferences.Editor editor;

    private Calendar cal;

    private float speed;
    private long pace;
    private float distance;
    private long avgPace;
    private double altitude;
    private long duration;
    private int GPSstatus;
    private int score;

    private boolean savingRace = false;

    private int raceStatus;
    private int countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        // Instancia los objetos para manejar la informacion en pantalla
        chrono = (Chronometer) findViewById(R.id.chronometer);
        speedTexvView = (TextView) findViewById(R.id.speed);
        paceTextView = (TextView) findViewById(R.id.pace);
        distanceTextView = (TextView) findViewById(R.id.distance);
        avgPaceTextView = (TextView) findViewById(R.id.avgPace);
        altitudeTextView = (TextView) findViewById(R.id.altitude);
        scoreTextView = (TextView) findViewById(R.id.score);
        startButton = (Button) findViewById(R.id.start);
        abortButton = (Button) findViewById(R.id.abort);
        saveButton = (Button) findViewById(R.id.save);
        discardButton = (Button) findViewById(R.id.discard);

        GPSPrefs = getSharedPreferences("GPSPrefs", MODE_MULTI_PROCESS);
        editor = GPSPrefs.edit();

        // Comprueba si hay una carrera en marcha o no
        if(GPSPrefs.getInt("raceStatus", GPSTracker.START_RACE_NOT_AVAILABLE)!=GPSTracker.RACE_ON){
            // Si no la hay, resetea valores e inicia el servicio si no esta activo
            GPSstatus = GPSPrefs.getInt("GPSstatus", GPSTracker.GPS_OFF);
            editor.putFloat("speed", 0);
            editor.putLong("pace", 0);
            editor.putFloat("distance", 0);
            editor.putFloat("distanceInt", 0);
            editor.putLong("avgPace", 0);
            editor.putString("altitude", "0");
            editor.putString("altitudeInt", "0");
            editor.putInt("score", 0);
            editor.commit();
            if(GPSPrefs.getInt("GPSTrackerStatus", GPSTracker.GPS_TRACKER_OFF)==GPSTracker.GPS_TRACKER_OFF)
                startService(new Intent(this, GPSTracker.class));
        }else{
            // Si la hay, recoge los ultimos valores de la carrera y los muestra por pantalla
            GPSstatus = GPSPrefs.getInt("GPSstatus", GPSTracker.GPS_OFF);
            speed = GPSPrefs.getFloat("speed", 0);
            pace = GPSPrefs.getLong("pace", 0);
            distance = GPSPrefs.getFloat("distance", 0);
            avgPace = GPSPrefs.getLong("avgPace", 0);
            altitude = Double.parseDouble(GPSPrefs.getString("altitude", "0"));
            score = GPSPrefs.getInt("score", 0);
            printRaceValues();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Registra el listener para captar los cambios en GPSPrefs
        GPSPrefs.registerOnSharedPreferenceChangeListener(this);

        if(raceStatus == GPSTracker.RACE_ON) {
            // Recoge los ultimos valores de la carrera y los muestra por pantalla
            GPSstatus = GPSPrefs.getInt("GPSstatus", GPSTracker.GPS_OFF);
            speed = GPSPrefs.getFloat("speed", 0);
            pace = GPSPrefs.getLong("pace", 0);
            distance = GPSPrefs.getFloat("distance", 0);
            avgPace = GPSPrefs.getLong("avgPace", 0);
            score = GPSPrefs.getInt("score", 0);
            altitude = Double.parseDouble(GPSPrefs.getString("altitude", "0"));
            printRaceValues();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Deja de captar los cambios por el listener
        GPSPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Si se cierra la actividad y no hay carrera en marcha, se finaliza el servicio
        if(raceStatus!=GPSTracker.RACE_ON)
            stopService(new Intent(this, GPSTracker.class));

    }

    public void click_Start (View view){
        if(raceStatus==GPSTracker.START_RACE_AVAILABLE){
            setRaceStatus(GPSTracker.COUNTING_DOWN);

            // Hilo para crear una cuenta atras
            new Thread(new Runnable() {
                public void run() {
                    for(countDown=2; countDown>0; countDown--){
                        runOnUiThread(new Runnable(){
                            public void run(){
                                if(countDown==2)
                                    Toast.makeText(RaceActivity.this, "READY?", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(RaceActivity.this, "SET", Toast.LENGTH_SHORT).show();
                            }
                        });
                        try{
                            Thread.sleep(2500);
                        }catch(Exception ignored){
                        }
                    }
                    runOnUiThread(new Runnable(){
                        public void run(){
                            setRaceStatus(GPSTracker.START_REQUESTED);
                            Toast.makeText(RaceActivity.this, "GO!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }else{
            Toast.makeText(RaceActivity.this, "raceStatus: "+raceStatus, Toast.LENGTH_SHORT).show();
        }
    }

    public void click_Abort(View view){
        setRaceStatus(GPSTracker.RACE_ABORTED);
        finish();
    }

    public void click_Save(View view){
        if(!savingRace) {
            savingRace = true;
            saveButton.setBackgroundColor(Color.parseColor("#888888"));
            discardButton.setBackgroundColor(Color.parseColor("#888888"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyParse myparse = new MyParse();
                    Race race = new Race();
                    race.setMyRaceValues((int) distance, myparse.getCurrentUser(), duration, avgPace,
                            Long.parseLong(GPSPrefs.getString("raceBeganAt", "0")),
                            GPSPrefs.getString("weather", "0"), GPSPrefs.getFloat("temperature", 999f),
                            GPSPrefs.getInt("humidity", 999), score);

                    myparse.saveRace(race);
                    finish();
                }
            }).start();
        }
    }

    public void click_Discard(View view){
        if(!savingRace)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_race, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Establece el estado de carrera en GPSPrefs indicado por el usuario */
    private void setRaceStatus(int status){
        editor.putInt("raceStatus", status);
        editor.commit();
    }

    /* Imprime los valores obtenidos del GPS por pantalla */
    private void printRaceValues() {
        speedTexvView.setText("Vel: "+round(speed,1)+" km/h");
        if (pace > HOUR){
            paceTextView.setText("Ritmo: - min/km·int");
        }else {
            cal = Calendar.getInstance();
            cal.setTimeInMillis(pace);
            paceTextView.setText(cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + " min/km");
        }
        distanceTextView.setText("Dist: "+round(distance,1)+" km");
        if (avgPace > HOUR){
            avgPaceTextView.setText("Ritmo med: - min/km");
        }else{
            cal = Calendar.getInstance();
            cal.setTimeInMillis(avgPace);
            avgPaceTextView.setText("Ritmo med: "+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+" min/km");
        }
        altitudeTextView.setText("Alt: "+round((float)altitude,1) + " m");
        scoreTextView.setText(score + " puntos");

    }

    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if(key.equals("raceStatus")){
            raceStatus = sp.getInt(key, GPSTracker.START_RACE_NOT_AVAILABLE);
            if (raceStatus==GPSTracker.RACE_ON) {
                // Si la carrera se pone en marcha, inicia crono e imprime valores iniciales
                startButton.setVisibility(View.GONE);
                abortButton.setVisibility(View.VISIBLE);
                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.start();
                speed = sp.getFloat("speed", 0);
                pace = sp.getLong("pace", 0);
                distance = sp.getFloat("distance", 0);
                avgPace = sp.getLong("avgPace", 0);
                altitude = Double.parseDouble(sp.getString("altitude", "0"));
                score = GPSPrefs.getInt("score", 0);
                printRaceValues();
            }else if(raceStatus==GPSTracker.RACE_FINISHED){
                // Si la carrera finaliza, para el crono y corrige la duracion total
                abortButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
                discardButton.setVisibility(View.VISIBLE);
                chrono.stop();
                Calendar cal;
                cal = Calendar.getInstance();
                duration = sp.getLong("duration", 0);
                cal.setTimeInMillis(duration);
                chrono.setText(cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)
                        +":"+cal.get(Calendar.SECOND));
            }else if(raceStatus==GPSTracker.START_RACE_AVAILABLE){
                // Si la carrera puede iniciarse...
                startButton.setBackgroundColor(Color.parseColor("#003366"));

            }
        }else if(key.equals("speed") || key.equals("pace") || key.equals("distance") ||
                key.equals("avgPace") || key.equals("altitude") || key.equals("altitudeInt")
                || key.equals("latitude") || key.equals("longitude")){
            // Si cambia algun valor de la carrera, se actualizan
            speed = sp.getFloat("speed", 0);
            pace = sp.getLong("pace", 0);
            distance = sp.getFloat("distance", 0);
            avgPace = sp.getLong("avgPace", 0);
            altitude = Double.parseDouble(sp.getString("altitude", "0"));
            printRaceValues();
        }
    }
}
