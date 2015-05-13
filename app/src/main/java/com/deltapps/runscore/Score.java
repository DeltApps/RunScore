package com.deltapps.runscore;

public class Score {
    static final float factorAltitud = 0.001f;
    static final float factorHumidity = 0.01f;
    static final float factorPendiente = 0.1f;

    float factorGeneral = 0;

    double altitude = 0;
    String weather;
    int temp = 0;
    float wind = 0;
    int humidity = 0;

    float distanceInt = 0;
    float altitudeInt = 0;
    float speedInt = 0;

    long score = 0;

    public void setInitialConditions(double altitude, String weather, int temp,
                                     float wind, int humidity){
        this.altitude = altitude;
        this.weather = weather;
        this.temp = temp;
        this.wind = wind;
        this.humidity = humidity;
    }

    public float getCorrectFactor(){
        if(altitude < 0)
            altitude = 0;
        factorGeneral = (float)altitude*factorAltitud +
                (float)humidity*factorHumidity;
        return factorGeneral;
    }

    public void setIntervalValues(float altitudeInt, float speedInt){
        this.altitudeInt = altitudeInt;
        this.speedInt = speedInt;
    }

    public long getIntervalScore(){
        score = Math.round(10*(speedInt + altitudeInt*factorPendiente));

        return score;
    }
}
