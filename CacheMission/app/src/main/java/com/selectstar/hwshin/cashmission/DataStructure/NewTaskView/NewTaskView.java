package com.selectstar.hwshin.cashmission.DataStructure.NewTaskView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class NewTaskView {
    protected AppCompatActivity parentActivity;
    public int taskViewID;
    public abstract void setContent(String content);

    public void setParentActivity(AppCompatActivity parentActivity) {
        this.parentActivity = parentActivity;
    }
}
