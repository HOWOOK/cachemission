package com.selectstar.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ExamActivity extends AppCompatActivity {

    Intent intent;
    Context context=this;
    AppCompatActivity activity=this;
    int controllerID, taskViewID;
    String mId;
    TaskView mTaskView;
    ExamView mExamView;
    int[][] mParameter;
    String tempsrcURI="foobar";
    String tasktitle;
    String buttons;
    private UIHashMap uiHashMap;
    String baseID;
    String tasktype;
    int examtype;
    String examFlag="";
    ImageView backButton;
    String loginToken;

    public void startTask()
    {

    }

    private void showDescription()
    {

        Intent intent_taskExplain = new Intent(ExamActivity.this, TaskExplainActivity.class);
        intent_taskExplain.putExtra("tasktype", tasktype);
        intent_taskExplain.putExtra("examType", examtype);
        startActivity(intent_taskExplain);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        //캡쳐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        loginToken = token.getString("logintoken",null);
        if(loginToken==null){
            loginToken="";
        }

        intent = getIntent();
        uiHashMap = new UIHashMap();
        mId=(String)intent.getStringExtra("taskid");
        mTaskView = uiHashMap.taskViewHashMap.get(intent.getStringExtra("taskview"));
        mExamView = (ExamView) uiHashMap.examViewHashMap.get(intent.getStringExtra("examview"));
        mParameter =  (int[][]) uiHashMap.taskHashMap.get(intent.getStringExtra("tasktype"));
        tasktitle = intent.getStringExtra("tasktitle");
        buttons= intent.getStringExtra("buttons");

        TextView mtasktitle = findViewById(R.id.tasktitletextexam);
        mtasktitle.setText(tasktitle);
        taskViewID = mTaskView.taskViewID;
        controllerID = mExamView.ExamViewID;

        tasktype = intent.getStringExtra("tasktype");
        examtype = intent.getIntExtra("examtype",0);

        // TaskView Inflating
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent1 = (ViewGroup) findViewById(R.id.taskviewexam);
        inflater.inflate(taskViewID, parent1);
        ViewGroup parent2 = (ViewGroup) findViewById(R.id.examview);
        inflater.inflate(controllerID, parent2);

        //TaskView와 Controller의 constraint 설정
        //ConstraintSet.TOP -> 3 // ConstraintSet.BOTTOM -> 4
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout =  (ConstraintLayout) findViewById(R.id.taskConstLayoutexam);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.taskviewexam, ConstraintSet.TOP, mParameter[1][0], mParameter[1][1]);
        constraintSet.connect(R.id.taskviewexam, ConstraintSet.BOTTOM, mParameter[2][0], mParameter[2][1] );
        constraintSet.connect(R.id.examview, ConstraintSet.TOP, mParameter[3][0], mParameter[3][1] );
        constraintSet.connect(R.id.examview, ConstraintSet.BOTTOM, mParameter[4][0], mParameter[4][1]);
        constraintSet.applyTo(constraintLayout);

        //TaskView의 weight설정 (default == 10)
        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();
        params1.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params1);
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) parent2.getLayoutParams();
        params2.verticalWeight = mParameter[5][0];
        parent2.setLayoutParams(params2);


        final Button confirm=findViewById(R.id.confirmbutton);
        final Button reject=findViewById(R.id.rejectbutton);
        Button sendExam=findViewById(R.id.examsend);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(examFlag.equals("confirm")){


                }
                else {
                    examFlag="confirm";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        confirm.setBackground(getDrawable(R.drawable.examining_truepush));
                        reject.setBackground(getDrawable(R.drawable.examining_false));
                    }
                }

            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(examFlag.equals("reject")){


                }
                else {
                    examFlag="reject";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        reject.setBackground(getDrawable(R.drawable.examining_falsepush));
                        confirm.setBackground(getDrawable(R.drawable.examining_true));
                    }
                }

            }
        });
        sendExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(examFlag.equals("")) {
                    Toast.makeText(getApplicationContext(),"테스크가 잘 되었는지 여부를 먼저 선택해 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean answer = false;
                if(examFlag.equals("true"))
                    answer=true;
                examFlag = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    reject.setBackground(getDrawable(R.drawable.examining_false));
                    confirm.setBackground(getDrawable(R.drawable.examining_true));
                }
                JSONObject param = new JSONObject();
                try {
                    param.put("taskID", Integer.parseInt(mId));
                    param.put("answerID", baseID);
                    param.put("examType", intent.getIntExtra("examtype",0));

                    param.put("submit", answer);
                    new WaitHttpRequest(context) {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            JSONObject resultTemp = null;
                            try {
                                resultTemp = new JSONObject(result);

                                if ((boolean) resultTemp.get("success")) {
                                    startTask();

                                } else {
                                    Toast.makeText(getApplicationContext(),"남은 검수작업이 없습니다. 테스크리스트로 돌아갑니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }.execute(getString(R.string.mainurl)+"/examSubmit", param, loginToken);



                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //해당 task가 처음이라면 설명서 띄워주는 것

        SharedPreferences taskToken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        if(!(taskToken.getInt(tasktype+examtype+"tasktoken",0) == 100)){
            showDescription();
        }

        //물음표버튼누르면 설명서 띄워주는것
        ImageView howBtn = findViewById(R.id.howbtn);
        howBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescription();
            }
        });



    }
}