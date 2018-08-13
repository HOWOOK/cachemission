package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
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
    public void setContent(String id, String contentURI, Context context, View view) {
        Glide.with(context).load(contentURI).into((ImageView) view);

    }
}
