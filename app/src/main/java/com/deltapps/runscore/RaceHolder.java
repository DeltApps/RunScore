package com.deltapps.runscore;


public class RaceHolder {
    private Race race;
    public Race getRace() { return race; }
    public void setRace(Race race) { this.race = race; }

    private static RaceHolder holder;
    public static RaceHolder getInstance() {
        if(holder == null)
            holder = new RaceHolder();

        return holder; }
}