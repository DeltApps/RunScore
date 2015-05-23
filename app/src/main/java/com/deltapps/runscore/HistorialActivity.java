package com.deltapps.runscore;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.util.ArrayList;


public class HistorialActivity extends ActionBarActivity {

    private ArrayList<Race> racesList;
    RaceHolder raceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
    }

    @Override
    protected void onResume() {
        super.onResume();

        racesList = new ArrayList<>();

        // Hilo para cargar las carreras
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Carga las carreras realizadas por el usuario
                MyParse myparse = new MyParse();
                racesList = myparse.loadRaces();

                if(racesList.size() > 0){
                    // Hilo principal para inflar el listview
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listview = (ListView) findViewById(R.id.racesListView);
                            BaseAdapter adapter = new HistorialBaseAdapter(HistorialActivity.this,
                                    racesList);
                            adapter.notifyDataSetChanged();
                            listview.setAdapter(adapter);
                            listview.setVisibility(View.VISIBLE);
                            listview.setOnItemClickListener(onItemClickList);
                        }
                    });
                }
            }
        }).start();
    }

    /* Accion tras pulsar un item del ListView de carreras */
    private AdapterView.OnItemClickListener onItemClickList = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            raceHolder = RaceHolder.getInstance();
            raceHolder.setRace(racesList.get(position));
            startActivity(new Intent(HistorialActivity.this, DetailActivity.class));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_historial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.newRace) {
            onCreateDistanceDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Cuadro de dialogo para elegir la distancia de una nueva carrera */
    private void onCreateDistanceDialog() {

        final Dialog d = new Dialog(this);

        d.setTitle(getString(R.string.distance));
        d.setContentView(R.layout.dialog_distance);

        final NumberPicker distancePicker = (NumberPicker) d.findViewById(R.id.distancePicker);
        distancePicker.setMaxValue(100);
        distancePicker.setMinValue(1);
        distancePicker.setValue(5);
        distancePicker.setWrapSelectorWheel(false);

        Button ok = (Button) d.findViewById(R.id.okDistance);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HistorialActivity.this, RaceActivity.class);
                i.putExtra("distance", distancePicker.getValue());
                startActivity(i);
                d.dismiss();
            }
        });

        d.show();
    }
}
