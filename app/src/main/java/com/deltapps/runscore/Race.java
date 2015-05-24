package com.deltapps.runscore;

public class Race {

    MyParse myparse = new MyParse();

    int distance = 0;
    String username1 = null;
    long duration1 = 0;
    long avgPace1 = 0;
    long initialTime1 = 0;
    String initialWeather1 = null;
    float initialTemp1 = 0;
    int initialHumidity1 = 0;
    long score1 = 0;
    String username2 = null;
    long duration2 = 0;
    long avgPace2 = 0;
    long initialTime2 = 0;
    String initialWeather2 = null;
    float initialTemp2 = 0;
    int initialHumidity2 = 0;
    long score2 = 0;

    public void setMyRaceValues(int distance, String username, long duration, long avgPace,
                                long initialTime, String initialWeather, float initialTemp,
                                int initialHumidity, long score){
        this.distance = distance;
        this.username1 = username;
        this.duration1 = duration;
        this.avgPace1 = avgPace;
        this.initialTime1 = initialTime;
        this.initialWeather1 = initialWeather;
        this.initialTemp1 = initialTemp;
        this.initialHumidity1 = initialHumidity;
        this.score1 = score;

    }
    public void setRaceValues(int distance, String username1, long duration1, long avgPace1,
                              long initialTime1, String initialWeather1, float initialTemp1,
                              int initialHumidity1, long score1, String username2, long duration2,
                              long avgPace2, long initialTime2, String initialWeather2,
                              float initialTemp2, int initialHumidity2, long score2){
        this.distance = distance;
        this.username1 = username1;
        this.duration1 = duration1;
        this.avgPace1 = avgPace1;
        this.initialTime1 = initialTime1;
        this.initialWeather1 = initialWeather1;
        this.initialTemp1 = initialTemp1;
        this.initialHumidity1 = initialHumidity1;
        this.score1 = score1;

        this.username2 = username2;
        this.duration2 = duration2;
        this.avgPace2 = avgPace2;
        this.initialTime2 = initialTime2;
        this.initialWeather2 = initialWeather2;
        this.initialTemp2 = initialTemp2;
        this.initialHumidity2 = initialHumidity2;
        this.score2 = score2;
    }
    public int getDistance(){

        return distance;
    }
    public String getUsername(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser().equals(username1))
                return username1;
            else
                return username2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return username1;
            else
                return username2;
        }
    }
    public long getDuration(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser().equals(username1))
                return duration1;
            else
                return duration2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return duration1;
            else
                return duration2;
        }
    }
    public long getAvgPace(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser().equals(username1))
                return avgPace1;
            else
                return avgPace2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return avgPace1;
            else
                return avgPace2;
        }
    }
    public long getInitialTime(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser().equals(username1))
                return initialTime1;
            else
                return initialTime2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return initialTime1;
            else
                return initialTime2;
        }
    }
    public String getInitialWeather(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser().equals(username1))
                return initialWeather1;
            else
                return initialWeather2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return initialWeather1;
            else
                return initialWeather2;
        }
    }
    public float getInitialTemp(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser().equals(username1))
                return initialTemp1;
            else
                return initialTemp2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return initialTemp1;
            else
                return initialTemp2;
        }
    }
    public int getInitialHumidity(boolean myValue){
        if (myValue) {
            if (myparse.getCurrentUser().equals(username1))
                return initialHumidity1;
            else
                return initialHumidity2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return initialHumidity1;
            else
                return initialHumidity2;
        }
    }

    public long getScore(boolean myValue){
        if (myValue){
            if (myparse.getCurrentUser().equals(username1))
                return score1;
            else
                return score2;
        }
        else {
            if (myparse.getCurrentUser().equals(username1))
                return score1;
            else
                return score2;
        }
    }
}
