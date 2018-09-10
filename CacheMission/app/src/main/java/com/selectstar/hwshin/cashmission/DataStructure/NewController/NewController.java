package com.selectstar.hwshin.cashmission.DataStructure.NewController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.selectstar.hwshin.cashmission.Activity.TaskActivity;
import com.selectstar.hwshin.cashmission.DataStructure.TaskView.TaskView;

public abstract class NewController {
    public int controllerID;

    protected TaskActivity parentActivity;
    public void setParentActivity(TaskActivity parentActivity) {
        this.parentActivity = parentActivity;
    }
    public abstract void resetContent(View view, final String taskID);
    public abstract void setLayout(View view, Context context, String taskID);

}
