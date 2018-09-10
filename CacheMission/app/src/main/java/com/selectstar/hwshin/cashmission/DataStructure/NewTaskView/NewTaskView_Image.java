package com.selectstar.hwshin.cashmission.DataStructure.NewTaskView;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cashmission.DataStructure.NewTaskView.NewTaskView;
import com.selectstar.hwshin.cashmission.R;

public class NewTaskView_Image extends NewTaskView {
    private ImageView myView;
    public NewTaskView_Image()
    {
        taskViewID = R.layout.taskview_image;
    }
    public void setContent(String content)
    {
        myView = parentActivity.findViewById(R.id.srcview);
        myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(parentActivity)
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ content))
                .thumbnail(0.1f)
                .into(myView);
    }
}
