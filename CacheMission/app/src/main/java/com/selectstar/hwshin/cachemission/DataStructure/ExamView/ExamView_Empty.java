package com.selectstar.hwshin.cachemission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.selectstar.hwshin.cachemission.R;

public class ExamView_Empty extends ExamView {
    public ExamView_Empty() {
        ExamViewID = R.layout.examview_empty;
    }
    @Override
    public void setContent(String content, String taskID) {

    }

    @Override
    public void setLayout(String id, View view, Context c, Intent in, String buttons, String Answer) {

    }

    @Override
    public void setParentActivity(AppCompatActivity parentActivity) {
        super.setParentActivity(parentActivity);
    }
}

