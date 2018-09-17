package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_PhotoView extends TaskView {
    private PhotoView myView;
    public TaskView_PhotoView()
    {
        taskViewID = R.layout.taskview_photoview;
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public boolean isEmpty() {
        return false;
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