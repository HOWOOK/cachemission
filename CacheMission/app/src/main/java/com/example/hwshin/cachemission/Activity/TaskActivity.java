package com.example.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hwshin.cachemission.DataStructure.Controller.Controller;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.example.hwshin.cachemission.DataStructure.UIHashmap;
import com.example.hwshin.cachemission.R;

public class TaskActivity extends AppCompatActivity {

    Intent intent;
    int controllerID, taskViewID;
    String mId;
    TaskView mTaskView;
    Controller mController;
    int[][] mParameter;
    String tempsrcURI;
    String tasktitle;
    String buttons;
    private UIHashmap uiHashmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        /*
        *intent로 부터 받아와야할 것 :   1. 어떤 controller를 사용하는지
        * 2. 어떤 taskview를 사용하는지  3. 두개의 constraint 관계는 어떤지
        */
        intent = getIntent();
        uiHashmap = new UIHashmap();
        mId=(String)intent.getStringExtra("taskid");
        mTaskView = (TaskView) uiHashmap.taskViewHashMap.get(intent.getStringExtra("taskview"));
        mController = (Controller) uiHashmap.controllerHashMap.get(intent.getStringExtra("controller"));
        mParameter =  (int[][]) uiHashmap.taskHashMap.get(intent.getStringExtra("tasktype"));
        tasktitle = intent.getStringExtra("tasktitle");
        buttons= intent.getStringExtra("buttons");

        TextView mtasktitle = findViewById(R.id.tasktitletext);
        mtasktitle.setText(tasktitle);
        taskViewID = mTaskView.taskViewID;
        controllerID = mController.controllerID;

        // TaskView Inflating
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent1 = (ViewGroup) findViewById(R.id.taskview);
        inflater.inflate(taskViewID, parent1);
        ViewGroup parent2 = (ViewGroup) findViewById(R.id.controller);
        inflater.inflate(controllerID, parent2);

        //TaskView와 Controller의 constraint 설정
        //ConstraintSet.TOP -> 3 // ConstraintSet.BOTTOM -> 4
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout =  (ConstraintLayout) findViewById(R.id.taskConstLayout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.taskview, ConstraintSet.TOP, mParameter[1][0], mParameter[1][1]);
        constraintSet.connect(R.id.taskview, ConstraintSet.BOTTOM, mParameter[2][0], mParameter[2][1] );
        constraintSet.connect(R.id.controller, ConstraintSet.TOP, mParameter[3][0], mParameter[3][1] );
        constraintSet.connect(R.id.controller, ConstraintSet.BOTTOM, mParameter[4][0], mParameter[4][1]);
        constraintSet.applyTo(constraintLayout);

        //TaskView의 weight설정 (default == 10)
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();
        params.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params);

        //TaskView에 source설정
        View srcTaskView = (View) findViewById(R.id.srcview);
        mTaskView.setContent(mId, tempsrcURI, this, srcTaskView);

        //TODO: Controller에 입력된 데이터를 받아오는거 일명<getanswer>
        View view = findViewById(R.id.controller);
        mTaskView.setParent(this,intent);
        mController.setParent(this,intent);
        mController.usingactivity=this;
        mController.mtaskview=mTaskView;
        Log.d("finalval",String.valueOf(mTaskView.gettaskID()));
        mController.setLayout(mId,view,getApplicationContext(),intent,buttons);



        //TODO: 서버로 값을 보내는거, 일단 getanswer() 구현


    }

}
