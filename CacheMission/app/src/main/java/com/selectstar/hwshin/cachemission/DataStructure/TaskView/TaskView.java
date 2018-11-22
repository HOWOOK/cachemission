package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.support.v7.app.AppCompatActivity;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class TaskView {
    protected PatherActivity parentActivity;
    public int taskViewID;
    public JSONArray BoxCropTestAnswer;
    public boolean testFlag=false;
    public abstract void setContent(String content);
    public abstract void setPreviewContents(ArrayList<JSONObject> list);
    public abstract boolean isEmpty();
    public void setParentActivity(PatherActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

}
