package com.deltapps.runscore;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class GetInitialConditions extends AsyncTask<Void, Void, Boolean> {

    public InitialConditionsResponse delegate = null;

    /* Letra que corresponde a cada simbolo */
    private final String SUNNY= "A";
    private final String LUNA= "I";
    private final String PARCIAL_NUBLADO_DIA= "C";
    private final String PARCIAL_NUBALDO_NOCHE= "J";
    private final String NUBLADO= "P";
    private final String LLUVIA= "R";
    private final String NIEVE= "W";
    private final String AGUANIEVE= "X";
    private final String VIENTO= "a";
    private final String NIEBLA= "N";
    private final String TORMENTA= "U";
    private final String TORNADO= "d";

    /* Objetos para leer/guardar los datos del archivo XML */
    private SharedPreferences initialConditions;
    private SharedPreferences.Editor editor;

    /* Objeto para guardar el contexto actual de la aplicacion */
    private Context context;

    /* Variables necesarias para la descarga del tiempo */
    private double latitude;
    private double longitude;
    private String query;
    private String key;
    private String urlWeatherRequest;
    private String urlAltitudeRequest;
    private HttpClient httpClient;

    /* Objetos para leer el archivo descargado (JSON) */
    private JSONObject jObj;
    private JSONObject currently;

    GetInitialConditions(Context context, double latitude, double longitude){
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /* Metodo llamado antes de comenzar el hilo */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try{

            // Forma la URL desde donde descargara el tiempo
            query= URLEncoder.encode(Double.toString(latitude) + "," + Double.toString(longitude), "UTF-8");
            key= URLEncoder.encode("35f3e924a72b4fd0a7ae16764451aebe", "UTF-8");
            urlWeatherRequest= "https://api.forecast.io/forecast/" + key + "/" + query + "?units=ca";

            // Forma la URL desde donde descargara la altitud
            urlAltitudeRequest = "http://maps.googleapis.com/maps/api/elevation/"
                    + "xml?sensor=true" + "&locations=" + query;

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /* hilo */
    @Override
    protected Boolean doInBackground(Void... params) {

        boolean result = false;
//			android.os.Debug.waitForDebugger();

        /* Crea/abre el archivo para los datos e inicializa el editor para escribir en el */
        initialConditions = context.getSharedPreferences("InitialConditions", Context.MODE_MULTI_PROCESS);
        editor = initialConditions.edit();
        editor.putFloat("temperature", 999f);
        editor.putInt("humidity", 999);
        editor.putString("GoogleMapsAltitude", "Error");
        editor.commit();


        try {
            /* Descarga la elevacion del terreno */
            httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(urlAltitudeRequest);
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
                    editor.putString("GoogleMapsAltitude", value);
                    editor.commit();
                }
                instream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            /* Descarga el tiempo */
            // defaultHttpClient
            httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(urlWeatherRequest);

            HttpResponse resp = httpClient.execute(get);

            StatusLine status = resp.getStatusLine();
            if (status.getStatusCode() != 200)
                Log.d("HTTP-GET", "HTTP error, invalid server status code: " + resp.getStatusLine());
            else{

                HttpEntity httpEntity = resp.getEntity();
                InputStream is= httpEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                try {

                    /* Lee y guarda el tiempo en un archivo XML */
                    jObj = new JSONObject(sb.toString());
                    currently= jObj.getJSONObject("currently");
                    result = true;
                    // condiciones actuales //
                    try{
                        editor.putLong("time", Long.parseLong(currently.getString("time")));
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    try{
                        editor.putString("icon", currently.getString("icon"));
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    try{
                        editor.putInt("temperature",
                                Math.round(Float.parseFloat(currently.getString("temperature"))));
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    try{
                        editor.putInt("humidity", 100 * Integer.parseInt(currently.getString("humidity")));
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    editor.commit();

                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }

    /* Metodo llamado tras la finalizacion del hilo */
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        delegate.asyncTaskCompleted();

    }
}
