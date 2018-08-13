package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.Serializable;

public abstract class Controller implements Serializable {
    public int controllerID;
    private int taskID;
    protected AppCompatActivity parentActivity;
    protected Intent parentIntent;
    //public abstract String getAnswer();
    public abstract void setLayout(String id, View view, Context c, Intent in);
    public void setTaskID(int taskID){
        this.taskID=taskID;

    };
    public void setParent(AppCompatActivity context, Intent intent)
    {
        this.parentActivity = context;
        this.parentIntent = intent;
    }

}
