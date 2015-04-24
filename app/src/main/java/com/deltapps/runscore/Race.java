package com.deltapps.runscore;

/**
 * Created by Javier on 24/04/2015.
 */
public class Race {
    float distance = 0;
    String username = null;
    long duration = 0;
    long avgPace = 0;
    float avgSpeed = 0;
    float maxSpeed = 0;
    long initialTime = 0;
    String initialWeather = null;
    float initialTemp = 0;
    float initialWind = 0;
    int initialHumidity = 0;
    String username1 = null;
    long duration1 = 0;
    long avgPace1 = 0;
    float avgSpeed1 = 0;
    float maxSpeed1 = 0;
    long initialTime1 = 0;
    String initialWeather1 = null;
    float initialTemp1 = 0;
    float initialWind1 = 0;
    int initialHumidity1 = 0;
    String username2 = null;
    long duration2 = 0;
    long avgPace2 = 0;
    float avgSpeed2 = 0;
    float maxSpeed2 = 0;
    long initialTime2 = 0;
    String initialWeather2 = null;
    float initialTemp2 = 0;
    float initialWind2 = 0;
    int initialHumidity2 = 0;

    public void setNewRaceValues(float distance, String username){
        this.distance = distance;
        this.username = username;
    }
    public void setMyRaceValues(float distance, String username, long duration, long avgPace,
                                float avgSpeed, float maxSpeed, long initialTime,
                                String initialWeather, float initialTemp, float initialWind,
                                int initialHumidity){
        this.distance = distance;
        this.username = username;
        this.duration = duration;
        this.avgPace = avgPace;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.initialTime = initialTime;
        this.initialWeather = initialWeather;
        this.initialTemp = initialTemp;
        this.initialWind = initialWind;
        this.initialHumidity = initialHumidity;

    }
    public void setRaceValues(float distance, String username1, long duration1, long avgPace1,
                              float avgSpeed1, float maxSpeed1, long initialTime1, String initialWeather1,
                              float initialTemp1, float initialWind1, int initialHumidity1,
                              String username2, long duration2, long avgPace2, float avgSpeed2,
                              float maxSpeed2, long initialTime2, String initialWeather2,
                              float initialTemp2, float initialWind2, int initialHumidity2){
        this.distance = distance;
        this.username1 = username1;
        this.duration1 = duration1;
        this.avgPace1 = avgPace1;
        this.avgSpeed1 = avgSpeed1;
        this.maxSpeed1 = maxSpeed1;
        this.initialTime1 = initialTime1;
        this.initialWeather1 = initialWeather1;
        this.initialTemp1 = initialTemp1;
        this.initialWind1 = initialWind1;
        this.initialHumidity1 = initialHumidity1;

        this.username2 = username2;
        this.duration2 = duration2;
        this.avgPace2 = avgPace2;
        this.avgSpeed2 = avgSpeed2;
        this.maxSpeed2 = maxSpeed2;
        this.initialTime2 = initialTime2;
        this.initialWeather2 = initialWeather2;
        this.initialTemp2 = initialTemp2;
        this.initialWind2 = initialWind2;
        this.initialHumidity2 = initialHumidity2;

    }
    public float getDistance(){
        return distance;
    }
    public String getUsername(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return username1;
            else
                return username2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return username1;
            else
                return username2;
        }
    }
    public long getDuration(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return duration1;
            else
                return duration2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return duration1;
            else
                return duration2;
        }
    }
    public long getAvgPace(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return avgPace1;
            else
                return avgPace2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return avgPace1;
            else
                return avgPace2;
        }
    }
    public float getAvgSpeed(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return avgSpeed1;
            else
                return avgSpeed2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return avgSpeed1;
            else
                return avgSpeed2;
        }
    }
    public float getMaxSpeed(){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return maxSpeed1;
            else
                return maxSpeed2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return maxSpeed1;
            else
                return maxSpeed2;
        }
    }
    public long getInitialTime(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return initialTime1;
            else
                return initialTime2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return initialTime1;
            else
                return initialTime2;
        }
    }
    public String getInitialWeather(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return initialWeather1;
            else
                return initialWeather2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return initialWeather1;
            else
                return initialWeather2;
        }
    }
    public int getInitialTemp(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return initialTemp1;
            else
                return initialTemp2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return initialTemp1;
            else
                return initialTemp2;
        }
    }
    public float getInitialWind(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return initialWind1;
            else
                return initialWind2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return initialWind1;
            else
                return initialWind2;
        }
    }
    public int getInitialHumidity(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser() == username1)
                return initialHumidity1;
            else
                return initialHumidity2;
        }
        else {
            if (myparse.getCurrentUser() != username1)
                return initialHumidity1;
            else
                return initialHumidity2;
        }
    }
}
