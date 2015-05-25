package com.deltapps.runscore;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class HistorialBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Race> list;

    HistorialBaseAdapter(Context context, ArrayList<Race> list){
        this.context = context;
        this.list = list;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    static class ViewHolder{
        LinearLayout rowRace;
        TextView distance;
        TextView score;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Race race = list.get(position);
        ViewHolder holder;

        if(convertView == null){

            convertView= inflater.inflate(R.layout.row_race, parent, false);
            holder = new ViewHolder();

            holder.rowRace = (LinearLayout) convertView.findViewById(R.id.rowRace);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.score = (TextView) convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();

        holder.distance.setText(Integer.toString(race.getDistance()));
        holder.score.setText(Long.toString(race.getScore(true)));

        if(race.getUsername(false)!=null && !race.getUsername(false).isEmpty()) {
            if (race.getScore(true) > race.getScore(false))
                holder.rowRace.setBackgroundColor(Color.parseColor("#32AE27"));
            else if (race.getScore(true) < race.getScore(false))
                holder.rowRace.setBackgroundColor(Color.parseColor("#AE0000"));
            else
                holder.rowRace.setBackgroundColor(Color.parseColor("#D08A00"));

        }else
            holder.rowRace.setBackgroundColor(Color.parseColor("#888888"));

        return convertView;
    }
}
