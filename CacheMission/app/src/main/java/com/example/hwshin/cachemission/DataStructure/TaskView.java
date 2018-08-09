package com.example.hwshin.cachemission.DataStructure;

import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.Serializable;

public abstract class TaskView implements Serializable{
    //why Serializable??? 나중에 공부해봅시다.
    public int taskViewID;
    protected abstract void setContent(String ContentURI,View parentView, View view);
}
