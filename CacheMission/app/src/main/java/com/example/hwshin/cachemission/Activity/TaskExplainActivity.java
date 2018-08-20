package com.example.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hwshin.cachemission.R;

public class TaskExplainActivity extends AppCompatActivity {

    Intent intent;
    String tasktype="";
    int taskExplainViewID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskexplain);

        intent = getIntent();
        tasktype = intent.getStringExtra("tasktype");

        taskExplainViewID = findtaskExplainViewID(tasktype);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) findViewById(R.id.taskexplainview);
        inflater.inflate(taskExplainViewID, parent);

        //확인하면 튜토리얼 종료
        Button checkbtn = findViewById(R.id.checkbtn);
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private int findtaskExplainViewID(String tasktype) {

        if(tasktype.equals("OCR"))
            return R.layout.taskexplain_ocr;
        else if (tasktype.equals("OCREXAM"))
            return R.layout.taskexplain_ocrexam;
        else if (tasktype.equals("VIDEO"))
            return R.layout.taskexplain_video;
        else if (tasktype.equals("VIDEOEXAM"))
            return R.layout.taskexplain_videoexam;
        else if (tasktype.equals("RECORD"))
            return R.layout.taskexplain_record;
        else if (tasktype.equals("RECORDEXAM"))
            return R.layout.taskexplain_recordexam;
        else if (tasktype.equals("DICTATION"))
            return R.layout.taskexplain_dictation;
        else if (tasktype.equals("DICTATIONEXAM"))
            return R.layout.taskexplain_dictationexam;
        else
            return R.layout.taskexplain_ocr;

    }
}
