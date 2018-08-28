package com.example.hwshin.cachemission.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
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
    String tempsrcURI="foobar";
    String tasktitle;
    String buttons;
    private UIHashmap uiHashmap;
    String tasktype;
    Dialog explainDialog;

    //사투리특별전용옵션
    static String region_dialect;

    @Override
    protected void onStart() {
        super.onStart();

        //지역 재 선택을 위한 인터페이스
        if(tasktype.equals("DIALECT") || tasktype.equals("RECORD")) {
            String region;
            TextView regionText = findViewById(R.id.regionText);
            SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
            region = explain.getString("region", null);

            if (region != null)
                regionText.setText("선택지역 : " + region);

            regionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
                    startActivity(intent_region);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        //캡쳐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        /*
        *intent로 부터 받아와야할 것 :   1. 어떤 controller를 사용하는지
        * 2. 어떤 taskview를 사용하는지  3. 두개의 constraint 관계는 어떤지
        */
        explainDialog = new Dialog(this);
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

        tasktype = intent.getStringExtra("tasktype");

        //사투리선택해야하는 테스크면 선택하게 만들어야함
        if(tasktype.equals("DIALECT") || tasktype.equals("RECORD")){
            String region;
            SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
            region = explain.getString("region",null);

            //지역 값이 선택 되어있지 않으면 선택하도록 함
            if(region == null){
                Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
                startActivity(intent_region);
            }
        }

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
        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();
        params1.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params1);
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) parent2.getLayoutParams();
        params2.verticalWeight = mParameter[5][0];
        parent2.setLayoutParams(params2);
        mTaskView.setParent(this,intent);
        mController.setParent(this,intent);
        mController.usingactivity=this;
        mController.mtaskview=mTaskView;

        //TaskView에 source설정
        View srcTaskView = (View) findViewById(R.id.srcview);
        mTaskView.setContent(mId, tempsrcURI, this, srcTaskView);

        //Controller에 source설정
        View view = findViewById(R.id.controller);
        mController.setLayout(mId,view,getApplicationContext(),intent,buttons);

        //해당 task가 처음이라면 설명서 띄워주는 것
        SharedPreferences tasktoken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        if(tasktoken.getInt(tasktype+"tasktoken",0) == 1){
            //do nothing
        }else{
            Intent intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
            SharedPreferences.Editor editor = tasktoken.edit();
            editor.putInt(tasktype+"tasktoken", 1);
            editor.commit();
            intent_taskExplain.putExtra("tasktype", tasktype);
            startActivity(intent_taskExplain);
        }

        //물음표버튼누르면 설명서 띄워주는것
        ImageView howbtn = findViewById(R.id.howbtn);
        howbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
                intent_taskExplain.putExtra("tasktype", tasktype);
                startActivity(intent_taskExplain);
            }
        });




    }

}
