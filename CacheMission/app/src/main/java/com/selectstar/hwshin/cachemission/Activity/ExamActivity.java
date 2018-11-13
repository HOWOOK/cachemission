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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
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
            if(taskType.equals("BOXCROPEXAM") || taskType.equals("TWOPOINTEXAM")){//BOXCROP에서는 파트를 넣어서 요청해야함
                partNum = partType();
                param.put("option",partNum);
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
                            Date after28time = addMinutesToDate(28,new Date());
                            ((JSONObject)waitingTasks.get(0)).put("time",DateToString(after28time));
                            currentTask = waitingTasks.get(waitingTasks.size()-1);
                            if(taskType.equals("BOXCROPEXAM") || taskType.equals("TWOPOINTEXAM"))
                                mTaskView.setContent(currentTask.get("content")+"*<"+currentTask.get("answer"));
                            else if (taskType.equals("SUGGESTEXAM"))
                                mTaskView.setContent(currentTask.get("content")+"("+currentTask.get("answer"));
                            else
                                mTaskView.setContent((String) currentTask.get("content"));
                            answerID = ((Integer)currentTask.get("id")).toString();
                            mExamView.setContent((String) currentTask.get("answer"),taskID);

                        } else if (resultTemp.get("success").toString().equals("false")) {
                                if (resultTemp.get("message").toString().equals("login")) {
                                    Intent in = new Intent(mContext, LoginActivity.class);
                                    startActivity(in);
                                    Toast.makeText(mContext, "로그인이 만료되었습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if (resultTemp.get("message").toString().equals("task")) {
                                    Toast.makeText(mContext, "테스크가 만료되었습니다. 다른 테스크를 선택해주세요", Toast.LENGTH_SHORT).show();
                                    deleteWaitingTasks();
                                    finish();
                                } else {
                                    Toast.makeText(mContext, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                return;

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
            String key = taskID;
            if(taskType.contains("BOXCROP"))
                key = key +"/"+ String.valueOf(partType());
            else
                key = key + "/-1";
            if(taskType.contains("EXAM"))
                key = key + "/" + String.valueOf(examType);
            else
                key = key + "/-1";
            waitingTasks = JSONtoArray(new JSONArray(getPreference("waitingTasks",key)));
            System.out.println("flag1");
            System.out.println(waitingTasks);
            if(waitingTasks.size()>0) {
                if (timeCheck(((JSONObject) waitingTasks.get(0)).get("time").toString())) {
                    if(mTaskView.isEmpty())
                        mTaskView.setPreviewContents(waitingTasks);
                    currentTask = (JSONObject)waitingTasks.get(waitingTasks.size()-1);
                    answerID = currentTask.get("id").toString();
                    if(taskType.equals("BOXCROPEXAM")||taskType.equals("TWOPOINTEXAM"))
                        mTaskView.setContent(currentTask.get("content")+"*<"+currentTask.get("answer"));
                    else
                        mTaskView.setContent((String) currentTask.get("content"));

                    mExamView.setContent((String) currentTask.get("answer"),taskID);
                }
                else
                {
                    getNewTask();
                }
            }
            else
            {
                getNewTask();
            }
        }catch(JSONException e)
        {
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        intent = getIntent();

        final TextView optionText = findViewById(R.id.optionText);

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
                boolean answer = false;
                if(examFlag.equals("confirm"))
                    answer=true;
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
                    Log.d("옵션", ((Integer)partType()).toString());
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
                                        Log.d("examActivity리무브", "불림");
                                    }
                                    updateWaitingTasks();
                                    startTask();
                                    goldSetting(String.valueOf(resultTemp.get("gold")));
                                    maybeSetting(String.valueOf(resultTemp.get("maybe")));

                                } else {
                                    Toast.makeText(getApplicationContext(),"남은 검수작업이 없습니다. 테스크리스트로 돌아갑니다.",Toast.LENGTH_SHORT).show();
                                    deleteWaitingTasks();
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