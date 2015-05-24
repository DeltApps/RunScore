package com.deltapps.runscore;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class GPSTracker extends Service implements SharedPreferences.OnSharedPreferenceChangeListener,
        InitialConditionsResponse {

    /* Estados de una carrera */
    public static final int START_RACE_NOT_AVAILABLE = 0;
    public static final int START_RACE_AVAILABLE = 1;
    public static final int START_REQUESTED = 2;
    public static final int COUNTING_DOWN = 3;
    public static final int RACE_ON = 4;
    public static final int RACE_ABORTED = 5;
    public static final int RACE_FINISHED = 6;

    /* Estados del servicio */
    public static final int GPS_TRACKER_OFF = 0;
    public static final int GPS_TRACKER_ON = 1;

    /* Estados del gps */
    public static final int GPS_OFF = 0;
    public static final int GPS_ACTIVATED = 1;
    public static final int GPS_FIXED = 2;

    /* Umbrales de actualización de la localizacion por gps */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 5000;

    private PowerManager.WakeLock cpuWakeLock;

    private Calendar cal;
    private long raceBeganAt = 0;

    private Score score;

    private GpsStatus.Listener mGPSstatusListener;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private long lastLocationMillis;
    private double latitude;
    private double longitude;
    private float speed;
    private float raceDistance;
    private float distance=0;
    private float distanceInt;
    private long pace;
    private long avgPace;
    private double altitudeInt;
    private double altitude;
    private long totalScore = 0;
    private long intervalScore;
    private int raceStatus;

    private int temp;
    private int humidity;

    private SharedPreferences InitialCondsPrefs;
    private SharedPreferences gpsPrefs;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        // No deja al dispositivo entrar en suspension
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "gps_service");
        cpuWakeLock.acquire();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        raceDistance = intent.getIntExtra("raceDistance", 5)*1000;

        // Inicializa los objetos para leer y escribir datos en GPSPrefs
        gpsPrefs = getSharedPreferences("GPSPrefs", MODE_MULTI_PROCESS);
        editor = gpsPrefs.edit();

        // Objeto para calcular las puntuaciones
        score = new Score();

        // Indica que el servicio GPSTracker esta activo
        setGPSTrackerStatus(GPS_TRACKER_ON);
        Toast.makeText(getApplicationContext(), "GPSTracker ON", Toast.LENGTH_SHORT).show();

        // Indica que la carrera no puede ser iniciada aun por el usuario
        setRaceStatus(START_RACE_NOT_AVAILABLE);

        // Pide las condiciones meteo actuales (sii lastUpdate > 30min)


        locationManager= (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        // Listener de los estados en los que esta el GPS
        mGPSstatusListener= new GpsStatus.Listener() {
            public void onGpsStatusChanged(int event) {
                boolean isGPSFix = false;
                switch (event) {
                    // Llamado periodicamente para comprobar el estado del gps (fijado o perdido)
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        if (location != null)
                            if ((SystemClock.elapsedRealtime() - lastLocationMillis) < MIN_TIME_BW_UPDATES*3)
                                isGPSFix = true;

                        if (isGPSFix) { // A fix has been acquired.
                                /* GPS FIJADO */
                            if(getGPSstatus()!=GPS_FIXED) {
/*                                setGPSstatus(GPS_FIXED);
                                if(raceStatus==START_RACE_NOT_AVAILABLE)
                                    setRaceStatus(START_RACE_AVAILABLE);
*/                            }

                        } else { // The fix has been lost.
                                /* GPS PERDIDO */
                            if(getGPSstatus()!=GPS_ACTIVATED) {
 /*                               setGPSstatus(GPS_ACTIVATED);
                                if(raceStatus==START_RACE_AVAILABLE)
                                    setRaceStatus(START_RACE_NOT_AVAILABLE);
*/                            }
                        }

                        break;

                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        /* GPS FIJADO POR PRIMERA VEZ */
                        Toast.makeText(getApplicationContext(), "POSICIÓN GPS FIJADA",
                                Toast.LENGTH_SHORT).show();

                        // Indica que el gps se ha fijado
                        setGPSstatus(GPS_FIXED);

                        // Pide la localizacion (ultima conocida)
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        /************
                         * Bloque para la obtencion de la altitud del terreno por GoogleMaps y las
                         * condiciones meteorologicas iniciales.
                         * Debe ser obtenida desde un hilo para no bloquear el princiapal  del servicio
                         ************/
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        altitude = location.getAltitude();

                        GetInitialConditions gic =
                                new GetInitialConditions(getApplicationContext(), latitude, longitude);
                        gic.delegate = GPSTracker.this;
                        gic.execute();

                        break;
                    case GpsStatus.GPS_EVENT_STARTED:
                            /* GPS INICIADO: BUSCANDO SATÉLITES... */
                        Toast.makeText(getApplicationContext(), "BUSCANDO SATÉLITES...",
                                Toast.LENGTH_SHORT).show();

                        setGPSstatus(GPS_ACTIVATED);

                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                            /* GPS INHABILITADO */
                        break;
                }
            }
        };

        // Listener para obtener los cambios en la localizacion del dispositivo
        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                if(location!=null){
                    if(raceStatus==COUNTING_DOWN){  // Activity pone cuenta atrás
                        distanceInt = 0;
                        altitudeInt = 0;
                    }else if(raceStatus==RACE_ON){ // Carrera en marcha
                        distanceInt = location.distanceTo(GPSTracker.this.location);
                        altitudeInt = location.getAltitude() - GPSTracker.this.location.getAltitude();
                    }

                    // Recoge la localizacion  y guarda el momento en que se actualizo
                    GPSTracker.this.location = location;
                    lastLocationMillis = SystemClock.elapsedRealtime();

                    // Calcula los valores necesarios para informar al usuario
                    speed = location.getSpeed()*3600/1000; // m/s a km/h
                    pace = (long) (1000/location.getSpeed()*1000); // m/s a millis/km
                    distance = distance + distanceInt;
                    cal = Calendar.getInstance();
                    if(raceBeganAt!=0 && distance!=0)
                        avgPace = (long) ((cal.getTimeInMillis()-raceBeganAt)/(distance/1000));
                    altitude = altitude + altitudeInt;
                    score.setIntervalValues(altitudeInt, speed);
                    intervalScore = score.getIntervalScore();
                    totalScore = totalScore + intervalScore;

                    if(distance < raceDistance) { // Si no se ha alcanzado la distancia objetivo...
                        // Guarda nuevos valores
                        editor.putFloat("speed", speed);
                        editor.putLong("pace", pace);
                        editor.putFloat("distance", distance / 1000); // m a km
                        editor.putLong("avgPace", avgPace);
                        editor.putString("altitude", Double.toString(altitude));
                        editor.putString("altitudeInt", Double.toString(altitudeInt));
                        editor.putInt("score", (int) totalScore);
                        editor.commit();
                    }else if(raceStatus==RACE_ON){
                        // Si se cumple la distancia objetivo y la carrera está en marcha
                        // guarda los ultimos datos y la finaliza (carrera y servicio)
                        editor.putFloat("speed", speed);
                        editor.putFloat("avgSpeed", raceDistance/(cal.getTimeInMillis()-raceBeganAt));
                        editor.putLong("pace", pace);
                        editor.putFloat("distance", raceDistance/1000);
                        editor.putLong("avgPace", avgPace);
                        editor.putString("altitude", Double.toString(altitude));
                        editor.putString("altitudeInt", Double.toString(altitudeInt));
                        totalScore = Math.round(totalScore*score.getCorrectFactor());
                        editor.putInt("totalScore", (int)totalScore);
                        cal = Calendar.getInstance();
                        editor.putLong("duration", cal.getTimeInMillis()-raceBeganAt);
                        editor.putLong("raceBeganAt", raceBeganAt);
                        editor.putString("weather", InitialCondsPrefs.getString("icon", "0"));
                        editor.putFloat("temperature", temp);
                        editor.putInt("humidity", humidity);
                        setRaceStatus(RACE_FINISHED);
                        Toast.makeText(getApplicationContext(), "Carrera finalizada!",
                                Toast.LENGTH_SHORT).show();
                        stopSelf();
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS HABILITADO", Toast.LENGTH_SHORT).show();

            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS DESHABILITADO", Toast.LENGTH_SHORT).show();
                setGPSstatus(GPS_OFF);
            }
        };

        // Registra el listener de cambios de estado del gps
        locationManager.addGpsStatusListener(mGPSstatusListener);

        // Pide actualizaciones de localizacion
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener);

        // Registra el listener para captar los cambios en GPSPrefs
        gpsPrefs.registerOnSharedPreferenceChangeListener(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Elimina todos los listener antes de la destruccion del servicio
        gpsPrefs.registerOnSharedPreferenceChangeListener(this);
        locationManager.removeUpdates(locationListener);
        locationManager.removeGpsStatusListener(mGPSstatusListener);
        setRaceStatus(START_RACE_NOT_AVAILABLE);
        setGPSTrackerStatus(GPS_TRACKER_OFF);

        // Indica que el servicio se cierra
        Toast.makeText(getApplicationContext(), "GPSTracker OFF", Toast.LENGTH_SHORT).show();

        // Vuelve a permitir al dispositivo entrar en suspension
        if (cpuWakeLock.isHeld())
            cpuWakeLock.release();
    }

    /* Metodo para la obtencion de la elevacion del terreno con la API de Google Maps */
    private double getElevationFromGoogleMaps(double longitude, double latitude) {
        double result = Double.NaN;

        // Prepara la peticion HTTP GET
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "http://maps.googleapis.com/maps/api/elevation/"
                + "xml?sensor=true" + "&locations=" + Double.toString(latitude)
                + "," + Double.toString(longitude);

        HttpGet httpGet = new HttpGet(url);
        try {
            // Ejecuta la peticion y espera la respuesta
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = Double.parseDouble(value);
                }
                instream.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /* Establece el estado de la carrera en GPSPrefs */
    private void setRaceStatus(int status){
        editor.putInt("raceStatus", status);
        editor.commit();
    }

    /* Establece el estado del GPS en GPSPrefs */
    private void setGPSstatus(int status){
        editor.putInt("GPSstatus", status);
        editor.commit();
    }

    /* Consigue el estado guardado del GPS en GPSPrefs */
    private int getGPSstatus(){
        int status;

        gpsPrefs = getApplicationContext().getSharedPreferences("GPSPrefs",
                MODE_MULTI_PROCESS);
        status = gpsPrefs.getInt("GPSstatus", GPS_ACTIVATED);

        return status;
    }

    /* Establece el estado del servicio GPSTracker en GPSPrefs */
    private void setGPSTrackerStatus(int status){
        editor = gpsPrefs.edit();
        editor.putInt("GPSTrackerStatus", status);
        editor.commit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* Llamado cuando se producen cambios en un SharedPreferences */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if(key.equals("raceStatus")){
            raceStatus = sp.getInt(key, RACE_FINISHED);

            if(raceStatus==START_REQUESTED){ // Activity manda que se inicie la carrera
                // Inicia la carrera y registra el momento del inicio
                setRaceStatus(RACE_ON);
                cal = Calendar.getInstance();
                raceBeganAt = cal.getTimeInMillis();
            }else if(raceStatus==RACE_ABORTED) { // Activity aborta la carrera
                // Finaliza el servicio
                stopSelf();
            }
        }
    }

    @Override
    public void asyncTaskCompleted() {
        InitialCondsPrefs = getSharedPreferences("InitialConditions", MODE_MULTI_PROCESS);

        temp = InitialCondsPrefs.getInt("temperature", 0);
        humidity = InitialCondsPrefs.getInt("humidity", 0);
        String auxAlt = InitialCondsPrefs.getString("GoogleMapsAltitude", "Error");
        if(auxAlt != null){
            if(!auxAlt.equals("Error")){
                altitude = Double.parseDouble(auxAlt);
            }
        }

        // Establece los valores iniciales de carrera para la puntuacion final
        score.setInitialConditions(altitude, temp, humidity);

        editor.putString("altitude", Double.toString(altitude));
        editor.putInt("temperature", temp);
        editor.putInt("humidity", humidity);
        setRaceStatus(START_RACE_AVAILABLE);
    }
}
