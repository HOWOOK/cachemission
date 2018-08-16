package com.example.hwshin.cachemission.Activity;

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
import android.widget.Button;
import android.widget.TextView;

import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.example.hwshin.cachemission.DataStructure.UIHashmap;
import com.example.hwshin.cachemission.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
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
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();
        params.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params);

        //TaskView에 source설정
        final View srcTaskView = (View) findViewById(R.id.srcview);



        JSONObject param = new JSONObject();
        try {
            param.put("taskID", Integer.parseInt(mId));

            new HttpRequest() {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    JSONObject resulttemp = null;
                    try {
                        resulttemp = new JSONObject(result);
                        Log.d("hey2",resulttemp.toString());
                        if((boolean)resulttemp.get("success")){
                            tempsrcURI=resulttemp.get("url").toString();
                            baseID = resulttemp.get("baseID").toString();
                            Log.d("baseid",baseID);
                            mTaskView.setContent(mId, tempsrcURI, context, srcTaskView);
                            View view = findViewById(R.id.examview);
                            mTaskView.setParent(activity,intent);
                            mExamView.setParent(activity,intent);
                            mExamView.usingactivity=activity;
                            mExamView.mtaskview=mTaskView;
                            Log.d("finalval",String.valueOf(mTaskView.gettaskID()));
                            mExamView.setLayout(mId,view,getApplicationContext(),intent,buttons,resulttemp.get("answer").toString());


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute("http://18.222.204.84/taskURI", param,logintoken);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        //TODO: Controller에 입력된 데이터를 받아오는거 일명<getanswer>
        Button confirm=findViewById(R.id.confirmbutton);
        Button reject=findViewById(R.id.rejectbutton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject param = new JSONObject();
                try {
                    param.put("taskID", Integer.parseInt(mId));
                    param.put("baseID", baseID);
                    param.put("submit","confirm");
                    new HttpRequest() {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            startActivity(intent);
                            finish();

                        }
                    }.execute("http://18.222.204.84/taskURI", param,logintoken);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject param = new JSONObject();
                try {
                    param.put("taskID", Integer.parseInt(mId));
                    param.put("baseID", baseID);
                    param.put("submit","confirm");
                    new HttpRequest() {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            startActivity(intent);
                            finish();

                        }
                    }.execute("http://18.222.204.84/taskURI", param,logintoken);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        //TODO: 서버로 값을 보내는거, 일단 getanswer() 구현


    }

}
