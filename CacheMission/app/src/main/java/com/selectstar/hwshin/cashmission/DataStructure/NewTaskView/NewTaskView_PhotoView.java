package com.selectstar.hwshin.cashmission.DataStructure.NewTaskView;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cashmission.R;

public class NewTaskView_PhotoView extends NewTaskView {
    private PhotoView myView;
    public NewTaskView_PhotoView()
    {
        taskViewID = R.layout.taskview_photoview;
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
