package com.selectstar.hwshin.cashmission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.R;

public class ExamView_EditText extends ExamView {
    public ExamView_EditText() {
        ExamViewID = R.layout.examview_edittext;
    }
    @Override
    public void setLayout(String id, View view, Context c, Intent in, String buttons, String answer) {
        ConstraintLayout textlayout=(ConstraintLayout) view;
        TextView tv=textlayout.findViewById(R.id.examviewedittext) ;
        tv.setText(answer);
    }
}
