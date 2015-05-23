package com.deltapps.runscore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Race race = list.get(position);
        ViewHolder holder;

        if(convertView == null){

            convertView= inflater.inflate(R.layout.row_race, parent, false);
            holder = new ViewHolder();



            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();

        return convertView;
    }
}
