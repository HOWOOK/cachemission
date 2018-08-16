package com.example.hwshin.cachemission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView;

public class ExamView_EditText extends ExamView {
    @Override
    public void setLayout(String id, View view, Context c, Intent in, String buttons, String answer) {
        TextView tv=(TextView) view;
        ((TextView) view).setText(answer);
    }
}
