package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.Serializable;

public abstract class TaskView implements Serializable{
    //why Serializable??? 나중에 공부해봅시다.
    public int taskViewID;
    private   int taskID;
    public void setID(int id){



    }
    public abstract void setContent(String id, String contentURI, Context context, View view);
}
