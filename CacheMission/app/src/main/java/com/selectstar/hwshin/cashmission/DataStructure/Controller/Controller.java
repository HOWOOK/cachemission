package com.selectstar.hwshin.cashmission.DataStructure.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.selectstar.hwshin.cashmission.DataStructure.TaskView.TaskView;

public abstract class Controller  {
    public int controllerID;
    private int taskID;
    public TaskView mtaskview;

    protected AppCompatActivity parentActivity;
    protected Intent parentIntent;
    public Activity usingactivity;

    public abstract void setLayout(String id, View view, Context c, String tasktype, Intent in, String buttons);
    public void setTaskID(int taskID){
        this.taskID=taskID;

    };
    public void setParent(AppCompatActivity context, Intent intent)
    {
        this.parentActivity = context;
        this.parentIntent = intent;
    }

}
