package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_PhotoView extends TaskView {

    public TaskView_PhotoView(){
        taskViewID = R.layout.taskview_photoview;
    }

    @Override
    public void setContent(String content) {

    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
