package com.selectstar.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithBox;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithLine;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ExamActivity extends PatherActivity {

    ExamView mExamView;
    String buttons;
    String examFlag="";
    ImageView backButton;
    TextView answerIDtv, taskUserIDtv;
    Context mContext=this;

    protected void showDescription(Context context)
    {
        Intent intent_taskExplain = new Intent(context, TaskExplainActivity.class);
        intent_taskExplain.putExtra("taskType", taskType);
        intent_taskExplain.putExtra("examType", examType);
        startActivity(intent_taskExplain);
    }

    @Override
    public void getNewTask() {
        JSONObject param = new JSONObject();
        try {
            param.put("taskID", taskID);
            param.put("examType",examType);
            if(taskType.equals("BOXCROPEXAM")){//BOXCROP에서는 파트를 넣어서 요청해야함
                partNum = partType();
                param.put("option",partNum);
            }
            if(taskType.equals("TWOPOINTEXAM")){// TWOPOINT(전선)은 옵션에 111을 넣어서주어야한다.
                param.put("option",111);
            }
            if((taskType.equals("RECORDEXAM") && examType == 2)||taskType.equals("DIRECTRECORDEXAM")){//RECORDEXAM 지역을 넣어서 요청해야한다.
                String region;
                region = ((TextView)findViewById(R.id.optionText)).getText().toString();
                param.put("option", region);
            }
            new HurryHttpRequest(this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    JSONObject resultTemp;
                    try {
                        resultTemp = new JSONObject(result);

                        if ((boolean) resultTemp.get("success")) {
                            waitingTasks = new ArrayList<>();
                            JSONArray tempTasks = (JSONArray)resultTemp.get("answers");

                            for(int i=0;i<tempTasks.length();i++)
                                waitingTasks.add((JSONObject)tempTasks.get(i));

                            mTaskView.setPreviewContents(waitingTasks);
                            currentTask = waitingTasks.get(waitingTasks.size()-1);

                            //answer ID랑 작업자 화면에 띄우는거 세팅
                            String taskUserID = currentTask.getString("user");
                            answerID = ((Integer)currentTask.get("id")).toString();
                            if(taskUserID != null)
                                taskUserIDtv.setText("작업자 ID : " + taskUserID);
                            if(answerID != null)
                                answerIDtv.setText("Answer ID : " + answerID);

                            //examType 2에서는 이렇게 안보내주던가?? (확인요망)
                            if(taskType.equals("BOXCROPEXAM") || taskType.equals("TWOPOINTEXAM"))
                                mTaskView.setContent(currentTask.get("content")+"*<"+currentTask.get("answer"));
                            else if (taskType.equals("SUGGESTEXAM"))
                                mTaskView.setContent(currentTask.get("content")+"("+currentTask.get("answer"));
                            else
                                mTaskView.setContent((String) currentTask.get("content"));

                            mExamView.setContent((String) currentTask.get("answer"),taskID);

                        } else if (resultTemp.get("success").toString().equals("false")) {
                                new ServerMessageParser().examSubmitFailParse(mContext, resultTemp);
                                finish();
                            }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }.execute(getString(R.string.mainurl) + "/testing/examGet", param, getLoginToken());
        } catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void startTask()
    {
        try{
            System.out.println("Exam Activity 웨이팅 테스크 : " + waitingTasks);
            if(waitingTasks != null && waitingTasks.size() > 0 && waitingTasks.get(0).get("taskOption").toString().equals(taskOption)) {
                currentTask = (JSONObject)waitingTasks.get(waitingTasks.size()-1);
                String taskUserID = currentTask.getString("user");
                answerID = currentTask.get("id").toString();

                if(taskUserID != null)
                    taskUserIDtv.setText("작업자 ID : " + taskUserID);
                if(answerID != null)
                    answerIDtv.setText("Answer ID : " + answerID);

                if(taskType.equals("BOXCROPEXAM")||taskType.equals("TWOPOINTEXAM"))
                    mTaskView.setContent(currentTask.get("content")+"*<"+currentTask.get("answer"));
                else
                    mTaskView.setContent((String) currentTask.get("content"));

                mExamView.setContent((String) currentTask.get("answer"),taskID);

                }else{
                    getNewTask();
                }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("ExamActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        setContentView(R.layout.activity_exam);
        //캡쳐방지
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        intent = getIntent();

        final TextView optionText = findViewById(R.id.optionText);
        answerIDtv = findViewById(R.id.answerID);
        taskUserIDtv = findViewById(R.id.taskUserID);

        nowGold = findViewById(R.id.goldnow);
        pendingGold = findViewById(R.id.goldpre);

        gold =intent.getStringExtra("goldNow");
        maybe = intent.getStringExtra("goldPre");
        nowGold.setText("현재 : \uFFE6 " + gold);
        pendingGold.setText("예정 : \uFFE6 " + maybe);

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        setLoginToken(token.getString("loginToken",null));

        uiHashMap = new UIHashMap();
        taskID=intent.getStringExtra("taskId");
        taskDifficulty = (String)intent.getStringExtra("taskDifficulty");
        mTaskView = uiHashMap.taskViewHashMap.get(intent.getStringExtra("taskView"));
        mTaskView.setParentActivity(this);
        mExamView = uiHashMap.examViewHashMap.get(intent.getStringExtra("examView"));
        mExamView.setParentActivity(this);
        mParameter = uiHashMap.taskHashMap.get(intent.getStringExtra("taskType"));
        taskTitle = intent.getStringExtra("taskTitle");
        buttons= intent.getStringExtra("buttons");

        TextView mTaskTitle = findViewById(R.id.tasktitletextexam);
        mTaskTitle.setText(taskTitle);
        taskViewID = mTaskView.taskViewID;
        controllerID = mExamView.ExamViewID;

        taskType = intent.getStringExtra("taskType");
        examType = intent.getIntExtra("examType",0);

        // TaskView Inflating
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent1 = (ViewGroup) findViewById(R.id.taskViewExam);
        inflater.inflate(taskViewID, parent1);
        ViewGroup parent2 = (ViewGroup) findViewById(R.id.controllerExam);
        inflater.inflate(controllerID, parent2);

        //TaskView와 Controller의 constraint 설정
        //ConstraintSet.TOP -> 3 // ConstraintSet.BOTTOM -> 4
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout =  (ConstraintLayout) findViewById(R.id.taskConstLayoutExam);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.taskViewExam, ConstraintSet.TOP, mParameter[1][0], mParameter[1][1]);
        constraintSet.connect(R.id.taskViewExam, ConstraintSet.BOTTOM, mParameter[2][0], mParameter[2][1] );
        constraintSet.connect(R.id.controllerExam, ConstraintSet.TOP, mParameter[3][0], mParameter[3][1] );
        constraintSet.connect(R.id.controllerExam, ConstraintSet.BOTTOM, mParameter[4][0], mParameter[4][1]);
        constraintSet.applyTo(constraintLayout);

        //TaskView의 weight설정 (default == 10)
        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();
        params1.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params1);
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) parent2.getLayoutParams();
        params2.verticalWeight = mParameter[5][0];
        parent2.setLayoutParams(params2);

        //boxcrop이면 파트 선택되고나서 로딩해야함
        //record면 지역 선택되고나서 로딩해야함
        if(!(taskType.equals("BOXCROPEXAM")||(taskType.equals("RECORDEXAM") && examType == 2)||taskType.equals("DIRECTRECORDEXAM"))) {

            startTask();
        }
        //boxcropEXAM이면 partSelectDialog를 띄워줘야한다.
        final TextView partText = findViewById(R.id.optionText);
        if((taskType.equals("BOXCROPEXAM"))){
            findViewById(R.id.option).setBackgroundColor(this.getResources().getColor(R.color.colorDark2));
            ((TextView) findViewById(R.id.optionText)).setTextColor(this.getResources().getColor(R.color.colorPrimary));
            partDialogShow(partText);
        }

        //RECORDEXAM regionSelectDialog를 띄워줘야한다.
        String regionText;
        SharedPreferences tasktoken = getSharedPreferences("taskToken", MODE_PRIVATE);
        SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
        regionText = explain.getString("region", null);
        if(((taskType.equals("RECORDEXAM") && examType == 2)||taskType.equals("DIRECTRECORDEXAM"))
                && tasktoken.getInt(taskType + "taskToken", 0) == 100){
            if(regionText != null) {
                optionText.setText(regionText);

                startTask();
            }
            else
                regionDialogShow(optionText);
        }

        //사투리 테스크는 옵션 눌러서 변경가능함
        optionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((taskType.equals("RECORDEXAM") && examType == 2)||taskType.equals("DIRECTRECORDEXAM"))
                    regionDialogShow(optionText);
            }
        });

        final Button confirm=findViewById(R.id.confirmbutton);
        final Button reject=findViewById(R.id.rejectbutton);
        Button sendExam=findViewById(R.id.examsend);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!examFlag.equals("confirm")){
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
                if(!examFlag.equals("reject")){
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
                if(!mExamView.isChecked())
                {
                    Toast.makeText(ExamActivity.this,"컨텐츠를 확인하고 판단해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(examFlag.equals("")) {
                    Toast.makeText(getApplicationContext(),"테스크가 잘 되었는지 여부를 먼저 선택해 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                String answer = "0";
                if(examFlag.equals("confirm"))
                    answer = "1";
                if(examFlag.equals("reject"))
                    answer = "3";
                examFlag = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    reject.setBackground(getDrawable(R.drawable.examining_false));
                    confirm.setBackground(getDrawable(R.drawable.examining_true));
                }
                JSONObject param = new JSONObject();
                try {
                    param.put("taskID", Integer.parseInt(taskID));
                    param.put("answerID", answerID);
                    param.put("examType", intent.getIntExtra("examType",0));
                    param.put("submit", answer);
                    param.put("option",partType());
                    Log.d("보내는파라미터", param.toString());
                    new WaitHttpRequest(ExamActivity.this) {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            JSONObject resultTemp = null;
                            try {
                                resultTemp = new JSONObject(result);

                                if ((boolean) resultTemp.get("success")) {
                                    if(taskType.equals("BOXCROPEXAM")){
                                        ((TaskView_PhotoWithBox)mTaskView).removeAnswer();
                                    }
                                    if(taskType.equals("TWOPOINTEXAM")){
                                        ((TaskView_PhotoWithLine)mTaskView).removeAnswer();
                                    }
                                    updateWaitingTasks();
                                    startTask();
                                    goldSetting(String.valueOf(resultTemp.get("gold")));
                                    maybeSetting(String.valueOf(resultTemp.get("maybe")));

                                } else {
                                    Toast.makeText(getApplicationContext(),"false : "+resultTemp.get("message"),Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }.execute(getString(R.string.mainurl)+"/testing/examSubmit", param, getLoginToken());

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //해당 task가 처음이라면 설명서 띄워주는 것

        SharedPreferences taskToken = getSharedPreferences("taskToken", MODE_PRIVATE);
        if(!(taskToken.getInt(taskType+examType+"taskToken",0) == 100)){
            showDescription(this);
        }

        //물음표버튼누르면 설명서 띄워주는것
        ImageView howBtn = findViewById(R.id.howbtn);
        howBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescription(ExamActivity.this);
            }
        });



    }

    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    public void onBackPressed() {
        //박스 검수 테스크는 뒤로가기 누르면 파트 선택가능해야함
        if(taskType.equals("BOXCROPEXAM")){
            TaskView_PhotoWithBox taskView = (TaskView_PhotoWithBox) mTaskView;
            TextView partText = findViewById(R.id.optionText);
            if(taskView.isExamFlag) {
                partText.setText("");
                partDialogShow(partText);
                taskView.removeAnswer();
            }else{
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }

}