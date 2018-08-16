package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.Serializable;

public abstract class TaskView {
    //why Serializable??? 나중에 공부해봅시다.
    public int taskViewID;
    private   int taskID;
    protected AppCompatActivity parentActivity;
    protected Intent parentIntent;
    public void setParent(AppCompatActivity context, Intent intent)
    {
        this.parentActivity = context;
        this.parentIntent = intent;
    }
    public void settaskID(int id){
        this.taskID=id;
    }
    public int gettaskID(){
        return this.taskID;
    }
    public abstract void setContent(String id, String contentURI, Context context, View view);
}