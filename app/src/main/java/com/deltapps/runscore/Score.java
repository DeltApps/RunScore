package com.deltapps.runscore;

public class Score {
    static final float factorAltitud     = 0.001f;
    static final float factorHumidity    = 0.01f;
    static final float factorPendiente   = 0.1f;
    static final float factorTempExtremo = 0.2f;
    static final float factorTempMedio   = 0.1f;
    static final float factorTempBajo    = 0.05f;

    float factorGeneral = 0;

    double altitude = 0;
    String weather;
    int temp = 0;
    int humidity = 0;

    float altitudeInt = 0;
    float speedInt = 0;

    long score = 0;

    public void setInitialConditions(double altitude, String weather, int temp, int humidity){
        this.altitude = altitude;
        this.weather = weather;
        this.temp = temp;
        this.humidity = humidity;
    }

    public float getCorrectFactor(){
        float factorTempAUX;
        if(altitude < 0)
            altitude = 0;
        if((temp < 0) || (temp > 40))
            factorTempAUX = factorTempExtremo;
        else {
            if((temp < 15) || (temp > 30))
                factorTempAUX = factorTempMedio;
            else
                factorTempAUX = factorTempBajo;
        }

        factorGeneral = (float)altitude*factorAltitud +
                (float)humidity*factorHumidity +
                factorTempAUX;
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
