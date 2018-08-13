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

public class ListviewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<TaskListItem> mTaskList;
    private int layout;

    public ListviewAdapter(Context context, int layout, ArrayList<TaskListItem> tasklist){
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
        String type;
        type =  mTaskList.get(position).getTaskType();

//        if (type.equals("labeling"))
//            return 1000;
//        else if (type.equals("examining"))
//            return 1001;
//        else if (type.equals("recording"))
//            return 1002;
//        else
//            return 0;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(layout, parent, false);

        TaskListItem taskItem = mTaskList.get(position);
//
//
//        ImageView taskIv = (ImageView) convertView.findViewById(R.id.taskType);
//
//        if(taskItem.getTaskType().equals("labeling"))
//            Glide.with(convertView).load(R.drawable.picture_95b3d7).into(taskIv);
//        else if (taskItem.getTaskType().equals("recording"))
//            Glide.with(convertView).load(R.drawable.microphone_d99694).into(taskIv);
//        else if (taskItem.getTaskType().equals("examining"))
//            Glide.with(convertView).load(R.drawable.picture_b3a2c7).into(taskIv);
//        else
//            Glide.with(convertView).load(R.drawable.userc_3d69b).into(taskIv);
//
        TextView taskTv = (TextView) convertView.findViewById(R.id.taskTitle);
        taskTv.setText(taskItem.getTaskName());
        TextView gold=(TextView) convertView.findViewById(R.id.gold);
        gold.setText(taskItem.getGold());

        return convertView;
    }
}
