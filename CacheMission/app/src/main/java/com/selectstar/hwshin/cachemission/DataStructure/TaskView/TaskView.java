package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class TaskView {
    protected AppCompatActivity parentActivity;
    public int taskViewID;
    public abstract void setContent(String content);
    public abstract void setPreviewContents(ArrayList<JSONObject> list);
    public abstract boolean isEmpty();
    public void setParentActivity(AppCompatActivity parentActivity) {
        this.parentActivity = parentActivity;
    }
}
