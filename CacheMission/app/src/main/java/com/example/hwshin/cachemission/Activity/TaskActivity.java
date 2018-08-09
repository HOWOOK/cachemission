package com.example.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hwshin.cachemission.DataStructure.Controller;
import com.example.hwshin.cachemission.DataStructure.TaskView;
import com.example.hwshin.cachemission.R;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {

    Intent intent;
    int controllerID, taskViewID;
    TaskView mTaskView;
    Controller mController;
    int[][] mParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //intent로 부터 받아와야할 것 :
        // 1. 어떤 controller를 사용하는지
        // 2. 어떤 taskview를 사용하는지
        // 3. 두개의 constraint 관계는 어떤지
        intent = getIntent();
        mTaskView = (TaskView) intent.getSerializableExtra("taskview");
        mController = (Controller) intent.getSerializableExtra("controller");
        mParameter =  (int[][]) intent.getSerializableExtra("parameters");

        taskViewID = mTaskView.taskViewID;
        controllerID = mController.controllerID;

        // TaskView Inflating
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent1 = (ViewGroup) findViewById(R.id.taskview);
        inflater.inflate(taskViewID, parent1);

        ViewGroup parent2 = (ViewGroup) findViewById(R.id.controller);
        inflater.inflate(controllerID, parent2);

        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout =  (ConstraintLayout) findViewById(R.id.taskConstLayout);
        constraintSet.clone(constraintLayout);
        //ConstraintSet.TOP -> 3 // ConstraintSet.BOTTOM -> 4
        constraintSet.connect(R.id.taskview, ConstraintSet.TOP, mParameter[1][0], mParameter[1][1]);
        constraintSet.connect(R.id.taskview, ConstraintSet.BOTTOM, mParameter[2][0], mParameter[2][1] );
        constraintSet.connect(R.id.controller, ConstraintSet.TOP, mParameter[3][0], mParameter[3][1] );
        constraintSet.connect(R.id.controller, ConstraintSet.BOTTOM, mParameter[4][0], mParameter[4][1]);
        constraintSet.applyTo(constraintLayout);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();

        params.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params);
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) parent2.getLayoutParams();






    }

}
