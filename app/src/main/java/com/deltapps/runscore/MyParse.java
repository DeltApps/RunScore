package com.deltapps.runscore;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/*****************************************************
 * Clase para gestionar toda la comunicación con Parse
 *****************************************************/
public class MyParse {

    private boolean result;
    private ArrayList<Race> racesList;

    public boolean signUp (String username, String email, String password, String repeatedPassword){

        result = false;
        if(!email.isEmpty() && !username.isEmpty() && !password.isEmpty()
                && !repeatedPassword.isEmpty()){
            if(password.equals(repeatedPassword)) {

                // Cuenta atrás para esperar a una tarea en background
                final CountDownLatch mCountDownLatch = new CountDownLatch(1);

                // Crea un usuario parse
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);

                // Guarda el usuario parse
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            // Registro y login completado!
                            result = true;
                        } else {
                            // No se pudo hacer el registro
                            result = false;
                        }
                        mCountDownLatch.countDown();
                    }
                });

                try{
                    mCountDownLatch.await();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public boolean logIn(String user, String password){

        result = false;
        if(!user.isEmpty() && !password.isEmpty()){

            // Cuenta atrás para esperar a una tarea en background
            final CountDownLatch mCountDownLatch = new CountDownLatch(1);

            // Inicia sesion de user, password
            ParseUser.logInInBackground(user, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, com.parse.ParseException e) {
                    if (user != null) {
                        // Login completado!
                        result = true;
                    } else {
                        // No se pudo iniciar sesion
                        result = false;
                    }
                    mCountDownLatch.countDown();
                }
            });
            try{
                mCountDownLatch.await();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public String getCurrentUser(){

        return ParseUser.getCurrentUser().getUsername();
    }

    public boolean isLogged(){
        if(ParseUser.getCurrentUser()!=null)
            return true;
        else
            return false;
    }

    public void saveRace(final Race race){

        // Cuenta atrás para esperar a una tarea en background
        final CountDownLatch mCountDownLatch = new CountDownLatch(1);
        // Peticion a parse de un objeto de clave race con una distancia determinada
        // y sin adversario
        ParseQuery<ParseObject> query = ParseQuery.getQuery("race");
        query.whereEqualTo("distance", race.getDistance());
        query.whereNotEqualTo("username1", getCurrentUser());
        query.whereEqualTo("username2", "");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {

                    if(objects.size()>0) {
                        // adversario encontrado

                        // Establece acceso R/W restringido
                        final ParseACL acl = new ParseACL();
                        acl.setPublicWriteAccess(false);
                        acl.setPublicReadAccess(true);

                        // Añade los datos de la carrera al objeto parse
                        objects.get(0).setACL(acl);
                        objects.get(0).put("username2", race.getUsername(true));
                        objects.get(0).put("duration2", race.getDuration(true));
                        objects.get(0).put("avgPace2", race.getAvgPace(true));
                        objects.get(0).put("initialTime2", race.getInitialTime(true));
                        objects.get(0).put("initialWeather2", race.getInitialWeather(true));
                        objects.get(0).put("initialTemp2", race.getInitialTemp(true));
                        objects.get(0).put("initialHumidity2", race.getInitialHumidity(true));
                        objects.get(0).put("score2", race.getScore(true));

                        // Actualiza el objeto parse
                        objects.get(0).saveEventually();
                    }else{
                        // adversario no encontrado

                        // Crea un objeto parse y añade los datos de la carrera
                        ParseObject raceParseObject = new ParseObject("race");
                        raceParseObject.put("distance", race.getDistance());
                        raceParseObject.put("username1", race.getUsername(true));
                        raceParseObject.put("duration1", race.getDuration(true));
                        raceParseObject.put("avgPace1", race.getAvgPace(true));
                        raceParseObject.put("initialTime1", race.getInitialTime(true));
                        raceParseObject.put("initialWeather1", race.getInitialWeather(true));
                        raceParseObject.put("initialTemp1", race.getInitialTemp(true));
                        raceParseObject.put("initialHumidity1", race.getInitialHumidity(true));
                        raceParseObject.put("score1", race.getScore(true));
                        raceParseObject.put("username2", "");
                        raceParseObject.put("duration2", 0);
                        raceParseObject.put("avgPace2", 0);
                        raceParseObject.put("initialTime2", 0);
                        raceParseObject.put("initialWeather2", "0");
                        raceParseObject.put("initialTemp2", 999);
                        raceParseObject.put("initialHumidity2", 999);
                        raceParseObject.put("score2", 0);

                        // Guarda el objeto parse
                        raceParseObject.saveEventually();
                    }

                }
                mCountDownLatch.countDown();
            }
        });
        try {
            mCountDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public ArrayList<Race> loadRaces(){

        racesList = new ArrayList<>();

        // queries combinados para formar query1 OR query2
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("race");
        query1.whereEqualTo("username1", getCurrentUser());
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("race");
        query2.whereEqualTo("username2", getCurrentUser());
        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        // Cuenta atrás para esperar a una tarea en background
        final CountDownLatch mCountDownLatch = new CountDownLatch(1);
        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> races, ParseException e) {
                if(e == null){
                    // Ordena carreras cronológicamente (F->f) si hay mas de una
                    if (races.size() > 1) {
                        Collections.sort(races, new Comparator<ParseObject>() {
                            @Override
                            public int compare(ParseObject o1, ParseObject o2) {

                                String user = getCurrentUser();

                                long initialTime1;
                                if(o1.get("username1").equals(user))
                                    initialTime1 = Long.parseLong(o1.get("initialTime1").toString());
                                else
                                    initialTime1 = Long.parseLong(o1.get("initialTime2").toString());

                                long initialTime2;
                                if(o1.get("username1").equals(user))
                                    initialTime2 = Long.parseLong(o2.get("initialTime1").toString());
                                else
                                    initialTime2 = Long.parseLong(o2.get("initialTime2").toString());


                                if (initialTime1 < initialTime2) // si -> o2, o1
                                    return 1;
                                else if(initialTime1 > initialTime2) // si -> o1, o2
                                    return -1;
                                else                            // default ->o2, o1
                                    return 1;
                            }
                        });
                    }

                    // Establece los valores de las carreras cargadas en la lista
                    for(int i=0; i<races.size(); i++){
                        Race race = new Race();
                        race.setRaceValues((int)races.get(i).get("distance"),
                                races.get(i).get("username1").toString(),
                                Long.parseLong(races.get(i).get("duration1").toString()),
                                Long.parseLong(races.get(i).get("avgPace1").toString()),
                                Long.parseLong(races.get(i).get("initialTime1").toString()),
                                races.get(i).get("initialWeather1").toString(),
                                (int)races.get(i).get("initialTemp1"),
                                (int)races.get(i).get("initialHumidity1"),
                                (int)races.get(i).get("score1"),
                                (String)races.get(i).get("username2"),
                                Long.parseLong(races.get(i).get("duration2").toString()),
                                Long.parseLong(races.get(i).get("avgPace2").toString()),
                                Long.parseLong(races.get(i).get("initialTime2").toString()),
                                races.get(i).get("initialWeather2").toString(),
                                (int)races.get(i).get("initialTemp2"),
                                (int)races.get(i).get("initialHumidity2"),
                                (int)races.get(i).get("score2"));
                        racesList.add(race);
                    }
                    mCountDownLatch.countDown();
                }
            }
        });

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return racesList;
    }

}
