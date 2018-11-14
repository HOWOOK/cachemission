package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.support.v7.widget.RecyclerView;

import com.selectstar.hwshin.cachemission.Photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_PhotoView extends TaskView {

    private PhotoView photoView;

    public TaskView_PhotoView(){
        taskViewID = R.layout.taskview_photoview;
    }

    @Override
    public void setContent(String content) {
        photoView = parentActivity.findViewById(R.id.srcview);


    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
