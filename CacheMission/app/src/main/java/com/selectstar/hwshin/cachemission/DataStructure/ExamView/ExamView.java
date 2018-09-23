package com.selectstar.hwshin.cachemission.DataStructure.ExamView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;

public abstract class ExamView {
    public int ExamViewID;
    protected AppCompatActivity parentActivity;
    protected boolean isCheck = true;
    public boolean isChecked()
    {
        return isCheck;
    }
    public abstract void setContent(String content,String taskID);


    public void setParentActivity(AppCompatActivity parentActivity) {
        this.parentActivity = parentActivity;
    }
    public abstract void setLayout(String id, View view, Context c, Intent in, String buttons, String Answer);
}
