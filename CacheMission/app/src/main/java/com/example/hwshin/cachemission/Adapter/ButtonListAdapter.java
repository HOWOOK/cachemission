package com.example.hwshin.cachemission.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hwshin.cachemission.DataStructure.TaskListItem;
import com.example.hwshin.cachemission.R;

import java.util.ArrayList;

public class ButtonListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> mTaskList;
    private int layout;

    public ButtonListAdapter(Context context, int layout, ArrayList<String> tasklist){
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

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(layout, parent, false);
        String temp = mTaskList.get(position);
        TextView tv=(TextView) convertView.findViewById(R.id.buttontext);
        tv.setText(temp);
        return convertView;
    }
}
