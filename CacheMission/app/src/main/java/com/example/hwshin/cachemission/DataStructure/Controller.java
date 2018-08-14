package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONArray;

import java.io.Serializable;

public abstract class Controller  {
    public int controllerID;
    private int taskID;
    public TaskView mtaskview;
    protected AppCompatActivity parentActivity;
    protected Intent parentIntent;
    //public abstract String getAnswer();
    public abstract void setLayout(String id, View view, Context c, Intent in, String buttons);
    public void setTaskID(int taskID){
        this.taskID=taskID;

    };
    public void setParent(AppCompatActivity context, Intent intent)
    {
        this.parentActivity = context;
        this.parentIntent = intent;
    }

}
