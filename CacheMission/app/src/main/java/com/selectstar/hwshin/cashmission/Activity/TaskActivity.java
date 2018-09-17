package com.selectstar.hwshin.cashmission.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cashmission.DataStructure.NewController.NewController;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.DataStructure.NewTaskView.NewTaskView;
import com.selectstar.hwshin.cashmission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cashmission.DataStructure.UIHashmap;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskActivity extends AppCompatActivity {

    Intent intent;
    int controllerID, taskViewID;
    String mId;
    View controllerView;
    TaskView mTaskView;
    NewTaskView mNewTaskView;
    NewController mNewController;
    Controller mController;
    int[][] mParameter;
    String tempsrcURI="foobar";
    String tasktitle;
    String buttons;
    private UIHashmap uiHashmap;
    String taskType;
    Dialog explainDialog;
    ImageView backButton;
    TextView nowGold;
    TextView pendingGold;
    TextView taskCount;
    private String answerID;
    String taskID;
    String loginToken;
    //사투리특별전용옵션
    static String region_dialect;

    public String getAnswerID() {
        return answerID;
    }
    public String getTaskID() {
        return taskID;
    }
    public String getLoginToken() {
        return loginToken;
    }
    public String getTaskType() {
        return taskType;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //사투리선택해야하는 테스크면 선택하게 만들어야함
        String region;
        SharedPreferences tasktoken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
        region = explain.getString("region",null);
        if((taskType.equals("DIALECT") || taskType.equals("RECORD"))&& tasktoken.getInt(taskType + "tasktoken",0) == 100 && region==null){
            Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
            intent_region.putExtra("wanttochange","false");
            startActivity(intent_region);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //지역 재 선택을 위한 인터페이스
        if(taskType.equals("DIALECT") || taskType.equals("RECORD")) {
            String region;
            TextView regionText = findViewById(R.id.regionText);
            SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
            region = explain.getString("region", null);

            if (region != null)
                regionText.setText("[선택지역] " + region);
            regionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
                    intent_region.putExtra("wanttochange","true");
                    startActivity(intent_region);
                }
            });
        }

    }

    // 새로운 테스크(베이스)를 받을 때 호출하는 함수
    public void startTask()
    {
        JSONObject param = new JSONObject();
        try {
            param.put("id", taskID);
            if(taskType.equals("RECORD")){//RECORD일때는 지역을 같이 넣어서 요청해야함
                String region;
                SharedPreferences explain = getSharedPreferences("region", Context.MODE_PRIVATE);
                region = explain.getString("region",null);
                param.put("region", region);
            }
            new HttpRequest(this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    JSONObject resultTemp = null;
                    try {
                        resultTemp = new JSONObject(result);
                        if ((boolean) resultTemp.get("success")) {
                            mNewTaskView.setContent((String) resultTemp.get("content"));
                            answerID = (String)resultTemp.get("baseID");
                            mNewController.resetContent(controllerView,taskID);
                            nowGold.setText("현재 : \uFFE6 " + String.valueOf(resultTemp.get("gold")));
                            pendingGold.setText("예정 : \uFFE6 " + String.valueOf(resultTemp.get("pending_gold")));
                            String textCount = resultTemp.get("task_count").toString();
                            if(!textCount.equals("")) {
                                textCount = textCount.replace("\n", " ");
                                textCount = textCount.replace("추가 보상", "(추가 보상");
                                if (!textCount.contains("!"))
                                    textCount = textCount + ")";
                                taskCount.setText(textCount);
                            }
                        } else {
                            if (getIntent().getIntExtra("from", 0) == 0) {
                                Toast.makeText(TaskActivity.this, "회원님이 선택하신 지역에 해당하는 과제가 더이상 없습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(TaskActivity.this, "테스크를 모두 완료했습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }.execute(getString(R.string.mainurl) + "/taskGet", param, loginToken);
        } catch(JSONException e)
        {
            e.printStackTrace();
        }

    }


    //해당 task가 처음이라면 설명서 띄워주는 것
    public void showDescription()
    {
        SharedPreferences taskToken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        if(taskToken.getInt(taskType + "tasktoken",0) == 100)
            return;
        Intent intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
        intent_taskExplain.putExtra("tasktype", taskType);
        startActivity(intent_taskExplain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        //캡쳐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        nowGold = findViewById(R.id.goldnow);
        pendingGold = findViewById(R.id.goldpre);
        taskCount = findViewById(R.id.regionText);
        backButton = findViewById(R.id.taskback);
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

        /*
         *intent로 부터 받아와야할 것 :   1. 어떤 controller를 사용하는지
         * 2. 어떤 taskview를 사용하는지  3. 두개의 constraint 관계는 어떤지
         */
        explainDialog = new Dialog(this);
        intent = getIntent();
        uiHashmap = new UIHashmap();
        mId=(String)intent.getStringExtra("taskid");
        taskID = (String)intent.getStringExtra("taskid");
        mNewTaskView =  uiHashmap.taskViewHashMap.get(intent.getStringExtra("taskview"));
        mNewTaskView.setParentActivity(this);
        mNewController =  uiHashmap.controllerHashMap.get(intent.getStringExtra("controller"));
        mParameter =  (int[][]) uiHashmap.taskHashMap.get(intent.getStringExtra("tasktype"));
        tasktitle = intent.getStringExtra("tasktitle");
        buttons= intent.getStringExtra("buttons");

        TextView mtasktitle = findViewById(R.id.tasktitletext);
        mtasktitle.setText(tasktitle);
        taskViewID = mNewTaskView.taskViewID;
        controllerID = mNewController.controllerID;
//        taskViewID = mTaskView.taskViewID;
//        controllerID = mController.controllerID;

        taskType = intent.getStringExtra("tasktype");

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


        //해당 task가 처음이라면 설명서 띄워주는 것
        showDescription();

        //물음표버튼누르면 설명서 띄워주는것
        ImageView howbtn = findViewById(R.id.howbtn);
        howbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
                intent_taskExplain.putExtra("tasktype", taskType);
                startActivity(intent_taskExplain);
            }
        });

        controllerView = findViewById(R.id.controller);
        mNewController.setParentActivity(this);
        mNewController.setLayout(controllerView, this, taskID);
        startTask();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        View srcTaskView1 = (View) findViewById(R.id.srcview);
        View srcTaskView2 = null;
        mTaskView.setContent(mId, tempsrcURI, this, taskType, 0, srcTaskView1);
    }
}
