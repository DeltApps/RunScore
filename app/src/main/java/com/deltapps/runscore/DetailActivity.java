package com.deltapps.runscore;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class DetailActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView distance = (TextView)findViewById(R.id.distancia_total);
        TextView user1 = (TextView)findViewById(R.id.user1);
        TextView user2 = (TextView)findViewById(R.id.user2);
        TextView time1 = (TextView)findViewById(R.id.time_user1);
        TextView time2 = (TextView)findViewById(R.id.time_user2);
        TextView average_rate_user1 = (TextView)findViewById(R.id.average_rate_user1);
        TextView average_rate_user2 = (TextView)findViewById(R.id.average_rate_user2);
        TextView score_user1 = (TextView)findViewById(R.id.score_user1);
        TextView score_user2 = (TextView)findViewById(R.id.score_user2);

        Race race = RaceHolder.getInstance().getRace();

        distance.setText(race.getDistance()+" km");

        user1.setText(race.getUsername(true));
        time1.setText(millisToChrono(race.getDuration(true)));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(race.getAvgPace(true));
        average_rate_user1.setText(lessThanTen(cal.get(Calendar.MINUTE))
                + ":" + lessThanTen(cal.get(Calendar.SECOND))+" min/km");
        score_user1.setText(String.valueOf(race.getScore(true))+" puntos");

        if(!race.getUsername(false).isEmpty()) {
            user2.setText(race.getUsername(false));
            time2.setText(millisToChrono(race.getDuration(false)));
            cal = Calendar.getInstance();
            cal.setTimeInMillis(race.getAvgPace(false));
            average_rate_user2.setText(lessThanTen(cal.get(Calendar.MINUTE))
                    + ":" + lessThanTen(cal.get(Calendar.SECOND))+" min/km");
            score_user2.setText(String.valueOf(race.getScore(false))+" puntos");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

    private String millisToChrono(long duration){
        long seconds  = (duration/ 1000) % 60 ;
        long minutes  = ((duration/ (1000*60)) % 60);
        long hours   = ((duration/ (1000*60*60)) % 24);

        return lessThanTen(hours)+":"+lessThanTen(minutes)+":"+lessThanTen(seconds);
    }

    private String lessThanTen(long num){
        if(num<10)
            return "0"+Long.toString(num);
        else
            return Long.toString(num);
    }
}
