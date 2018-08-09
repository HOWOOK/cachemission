package com.example.hwshin.cachemission.DataStructure;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hwshin.cachemission.R;

public class TaskView_Image extends TaskView {

    public TaskView_Image() {
        taskViewID = R.layout.taskview_image;

    }

    //protected int taskViewID = R.layout.taskview_image;
    @Override
    protected void setContent(String ContentURI, View parentView, View view) {
       Glide.with(parentView).load(ContentURI).into((ImageView) view);
    }
}
