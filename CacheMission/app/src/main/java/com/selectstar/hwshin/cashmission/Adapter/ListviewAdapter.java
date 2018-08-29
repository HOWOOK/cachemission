package com.selectstar.hwshin.cashmission.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cashmission.DataStructure.TaskListItem;
import com.selectstar.hwshin.cashmission.R;

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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(layout, parent, false);
        TaskListItem taskItem = mTaskList.get(position);
        ImageView taskIv = (ImageView) convertView.findViewById(R.id.taskType);
        ImageView tasklv_check = (ImageView) convertView.findViewById(R.id.taskTypeCheck);

        String taskType = taskItem.getTaskType().toString();


        if(taskType.equals("OCR"))
            Glide.with(convertView).load(R.drawable.tasktype_ocr).into(taskIv);
        else if(taskType.equals("OCREXAM")) {
            Glide.with(convertView).load(R.drawable.tasktype_ocr).into(taskIv);
            Glide.with(convertView).load(R.drawable.tasktypeadd).into(tasklv_check);
        }
        else if(taskType.equals("VIDEO"))
            Glide.with(convertView).load(R.drawable.tasktype_video).into(taskIv);
        else if(taskType.equals("VIDEOEXAM")) {
            Glide.with(convertView).load(R.drawable.tasktype_video).into(taskIv);
            Glide.with(convertView).load(R.drawable.tasktypeadd).into(tasklv_check);
        }
        else if(taskType.equals("DICTATION"))
            Glide.with(convertView).load(R.drawable.tasktype_dictation).into(taskIv);
        else if(taskType.equals("DICTATIONEXAM")) {
            Glide.with(convertView).load(R.drawable.tasktype_dictation).into(taskIv);
            Glide.with(convertView).load(R.drawable.tasktypeadd).into(tasklv_check);
        }
        else if(taskType.equals("RECORD"))
            Glide.with(convertView).load(R.drawable.tasktype_voice).into(taskIv);
        else if(taskType.equals("RECORDEXAM")) {
            Glide.with(convertView).load(R.drawable.tasktype_voice).into(taskIv);
            Glide.with(convertView).load(R.drawable.tasktypeadd).into(tasklv_check);
        }
        else if(taskType.equals("NUMBERING"))
            Glide.with(convertView).load(R.drawable.tasktype_numbering).into(taskIv);
        else if(taskType.equals("NUMBERINGEXAM")) {
            Glide.with(convertView).load(R.drawable.tasktype_numbering).into(taskIv);
            Glide.with(convertView).load(R.drawable.tasktypeadd).into(tasklv_check);
        }
        else if(taskType.equals("DIALECT"))
            Glide.with(convertView).load(R.drawable.tasktype_dialect).into(taskIv);
        else if(taskType.equals("DIALECTEXAM")) {
            Glide.with(convertView).load(R.drawable.tasktype_dialect).into(taskIv);
            Glide.with(convertView).load(R.drawable.tasktypeadd).into(tasklv_check);
        }
        else
            Glide.with(convertView).load(R.drawable.imagenotload).into(taskIv);

        TextView taskTv = (TextView) convertView.findViewById(R.id.taskTitle);
        taskTv.setText(taskItem.getTaskName());
        TextView gold=(TextView) convertView.findViewById(R.id.gold);
        gold.setText(taskItem.getGold());

        return convertView;
    }
}
