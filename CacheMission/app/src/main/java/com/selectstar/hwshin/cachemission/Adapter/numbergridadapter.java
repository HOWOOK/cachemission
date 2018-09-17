package com.selectstar.hwshin.cachemission.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;

public class numbergridadapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ImageView> mTaskList;
    private int layout;

    public numbergridadapter(Context context, int layout, ArrayList<ImageView> tasklist){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTaskList=tasklist;
        this.layout=layout;
    }


    @Override
    public int getCount() {
        return mTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(layout, parent, false);

        TextView taskIv = (TextView) convertView.findViewById(R.id.numnum);

        taskIv.setText(String.valueOf(position+1));
       //convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorwhite));

        return convertView;
    }
}
