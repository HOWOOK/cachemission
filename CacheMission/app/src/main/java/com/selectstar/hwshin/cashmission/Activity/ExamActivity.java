package com.selectstar.hwshin.cashmission.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.DataStructure.ExamView.ExamView;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cashmission.DataStructure.UIHashmap;
import com.selectstar.hwshin.cashmission.R;

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
    private UIHashmap uiHashmap;
    String baseID;
    String tasktype;
    int examtype;
    String colorflag="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        //캡쳐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        SharedPreferences token = getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        /*
         *intent로 부터 받아와야할 것 :   1. 어떤 controller를 사용하는지
         * 2. 어떤 taskview를 사용하는지  3. 두개의 constraint 관계는 어떤지
         */
        intent = getIntent();
        uiHashmap = new UIHashmap();
        mId=(String)intent.getStringExtra("taskid");
        mTaskView = (TaskView) uiHashmap.taskViewHashMap.get(intent.getStringExtra("taskview"));
        mExamView = (ExamView) uiHashmap.examviewHashMap.get(intent.getStringExtra("examview"));
        mParameter =  (int[][]) uiHashmap.taskHashMap.get(intent.getStringExtra("tasktype"));
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
        Log.d("examview",String.valueOf(controllerID));
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

        JSONObject param = new JSONObject();
        try {
            param.put("taskID", Integer.parseInt(mId));
            param.put("examType", intent.getIntExtra("examtype",0));

            new HttpRequest(this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    JSONObject resulttemp = null;
                    try {
                        resulttemp = new JSONObject(result);

                        if((boolean)resulttemp.get("success")){

                            if(mTaskView.taskViewID == R.layout.taskview_text){
                                tempsrcURI = resulttemp.get("content_text").toString();

                            }else {
                                tempsrcURI = resulttemp.get("content_url").toString();
                            }
                            baseID = resulttemp.get("answerID").toString();

                            mTaskView.setParent(activity,intent);
                            mExamView.setParent(activity,intent);
                            mExamView.usingactivity=activity;
                            mExamView.mtaskview=mTaskView;
                            //TaskView에 source설정 (보통은 srcTaskView1에만 들어가며 Text의 경우 2개를 받는다)
                            View srcTaskView1 = (View) findViewById(R.id.srcview);
                            View srcTaskView2 = null;
                            if(intent.getStringExtra("taskview").equals("text")) {
                                srcTaskView2 = (View) findViewById(R.id.srcview2);
                            }
                            mTaskView.setContent(mId, tempsrcURI, context, tasktype, examtype, srcTaskView1, srcTaskView2);
                            View view = findViewById(R.id.examview);


                            String answer="";
                            if(mExamView.ExamViewID == R.layout.examview_voice){
                                answer = resulttemp.get("answer_url").toString();

                            }
                            else{
                                answer = resulttemp.get("answer_text").toString();
                            }
Log.d("answernow",answer);
                            mExamView.setLayout(mId,view,getApplicationContext(),intent,buttons,answer);

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"남은 테스크가 없습니다. 테스크 리스트로 돌아갑니다.",Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(getString(R.string.mainurl)+"/examGet", param,logintoken);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        final Button confirm=findViewById(R.id.confirmbutton);
        final Button reject=findViewById(R.id.rejectbutton);
        Button sendexam=findViewById(R.id.examsend);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorflag.equals("confirm")){


                }
                else {
                    colorflag="confirm";
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
                if(colorflag.equals("reject")){


                }
                else {
                    colorflag="reject";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        reject.setBackground(getDrawable(R.drawable.examining_falsepush));
                        confirm.setBackground(getDrawable(R.drawable.examining_true));
                    }
                }

            }
        });
        sendexam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject param = new JSONObject();
                try {
                    param.put("taskID", Integer.parseInt(mId));
                    param.put("answerID", baseID);
                    Log.d("examshibal",String.valueOf(intent.getIntExtra("examtype",0)));
                    param.put("examType", intent.getIntExtra("examtype",0));

                    boolean exam=false;
                    if(colorflag.equals("confirm")){
                        exam=true;
                    }
                    if(colorflag.equals("")){
                        Toast.makeText(getApplicationContext(),"테스크가 잘 되었는지 여부를 먼저 선택해 주세요",Toast.LENGTH_SHORT).show();


                    }else {
                        Log.d("examwhat",String.valueOf(exam));
                        param.put("submit", exam);
                        new HttpRequest(context) {
                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                JSONObject resulttemp = null;
                                try {
                                    resulttemp = new JSONObject(result);
                                    Log.d("hey2bad", resulttemp.toString());
                                    if ((boolean) resulttemp.get("success")) {

                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"남은 검수작업이 없습니다. 테스크리스트로 돌아갑니다.",Toast.LENGTH_SHORT).show();
                                        Intent notaskanymore = new Intent(getApplicationContext(), TaskListActivity.class);
                                        startActivity(notaskanymore);
                                        finish();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }.execute(getString(R.string.mainurl)+"/examSubmit", param, logintoken);

                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //해당 task가 처음이라면 설명서 띄워주는 것
        SharedPreferences tasktoken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        if(tasktoken.getInt(tasktype+examtype+"tasktoken",0) == 100){
            //do nothing
        }else{
            Intent intent_taskExplain = new Intent(ExamActivity.this, TaskExplainActivity.class);
            intent_taskExplain.putExtra("tasktype", tasktype);
            intent_taskExplain.putExtra("examType", examtype);
            startActivity(intent_taskExplain);
        }

        //물음표버튼누르면 설명서 띄워주는것
        ImageView howbtn = findViewById(R.id.howbtn);
        howbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_taskExplain = new Intent(ExamActivity.this, TaskExplainActivity.class);
                intent_taskExplain.putExtra("tasktype", tasktype);
                intent_taskExplain.putExtra("examType", examtype);
                startActivity(intent_taskExplain);
            }
        });

    }

}