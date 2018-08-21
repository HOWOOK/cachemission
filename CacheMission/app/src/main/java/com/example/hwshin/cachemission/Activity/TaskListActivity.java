package com.example.hwshin.cachemission.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.support.v4.widget.DrawerLayout;

import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hwshin.cachemission.Adapter.ListviewAdapter;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.DataStructure.TaskListItem;
import com.example.hwshin.cachemission.DataStructure.UIHashmap;
import com.example.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TaskListActivity extends AppCompatActivity {
    final int P_RECORD_AUDIO=77;
    private ListView lv_main;
    final static ArrayList<TaskListItem> mTaskList = new ArrayList<TaskListItem>();
    static ListviewAdapter adapter;
    private UIHashmap uiHashmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        uiHashmap = new UIHashmap();
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)  ){

            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO,WRITE_EXTERNAL_STORAGE},
                    P_RECORD_AUDIO);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //애니매이션 시작
        final ImageView hexagon1 = findViewById(R.id.hexagon1);
        final ImageView hexagon2 = findViewById(R.id.hexagon2);
        Animation anim_cw = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cwrotate);
        Animation anim_ccw = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ccwrotate);
        hexagon1.startAnimation(anim_cw);
        hexagon2.startAnimation(anim_ccw);


        //시작하면 일단 토큰을 받아옴
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        String stringtoken="";
        stringtoken = token.getString("logintoken",null);
        if(stringtoken==null){
            stringtoken="";
        }
        JSONObject param = new JSONObject();
        try {
            param.put("requestlist", "tasklist");


            new HttpRequest() {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {
                        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer) ;

                        //메뉴버튼
                        Button menubtn = findViewById(R.id.drawviewbtn);
                        menubtn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {

                                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                                    drawer.openDrawer(Gravity.LEFT) ;
                                }
                            }
                        });

                        ImageView settingbtn = findViewById(R.id.settingbtn);
                        settingbtn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                Intent intent_setting = new Intent(TaskListActivity.this, SettingActivity.class);
                                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                                    drawer.closeDrawer(Gravity.LEFT) ;
                                }
                                TaskListActivity.this.startActivity(intent_setting);
                            }
                        });

                        TextView exchangebtn = findViewById(R.id.exchange);
                        exchangebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_exchange= new Intent(TaskListActivity.this, ExchangeActivity.class);
                                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                                    drawer.closeDrawer(Gravity.LEFT) ;
                                }
                                startActivity(intent_exchange);
                            }
                        });

                        TextView suggestionbtn = findViewById(R.id.suggestion);
                        suggestionbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_suggestion = new Intent(TaskListActivity.this, SuggestionActivity.class);
                                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                                    drawer.closeDrawer(Gravity.LEFT) ;
                                }
                                startActivity(intent_suggestion);
                            }
                        });

                        Log.d("good",result.toString());
/*
                        if(result!=){
                            Intent gotologin = new Intent(TaskListActivity.this,LoginActivity.class);
                            startActivity(gotologin);
                            finish();

                        }*/
                        JSONObject resulttemp = new JSONObject(result);

                        if (resulttemp.get("success").toString().equals("login")) {
                            Intent gotologin = new Intent(TaskListActivity.this,LoginActivity.class);
                            startActivity(gotologin);
                            finish();
                        }
                        else{

                            //유저정보세팅
                            JSONObject user = (JSONObject) resulttemp.get("user");
                            TextView username = findViewById(R.id.username);
                            TextView usernamedrawer = findViewById(R.id.usernamedrawer);
                            TextView money = findViewById(R.id.money);
                            TextView maybemoney = findViewById(R.id.maybemoney);
                            money.setText("\uFFE6 "+String.valueOf(user.get("gold")));
                            maybemoney.setText("\uFFE6 "+String.valueOf(user.get("maybe")));
                            username.setText(String.valueOf(user.get("name")));
                            usernamedrawer.setText(String.valueOf(user.get("name")));
                            mTaskList.clear();
                            JSONArray exam_res = (JSONArray) resulttemp.get("exam_data");
                            JSONArray task_res = (JSONArray) resulttemp.get("task_data");
                            for (int i = 0; i < exam_res.length(); i++) {

                                JSONObject temp = (JSONObject) exam_res.get(i);
                                Log.d("dataget", temp.toString());


                                mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                                        (String) temp.get("taskView"), (String) temp.get("controller"), String.valueOf(temp.get("gold")) + " p", (JSONArray) temp.get("buttons"), 1));
                            }




                            for (int i = 0; i < task_res.length(); i++) {

                                JSONObject temp = (JSONObject) task_res.get(i);
                                Log.d("dataget", temp.toString());


                                mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                                        (String) temp.get("taskView"), (String) temp.get("controller"), String.valueOf(temp.get("gold"))+" p",(JSONArray)temp.get("buttons"), 0));


                            }

                        }
                        //Log.d("mtask", mTaskList.get(0).getController());
                        //Log.d("mtask", mTaskList.get(1).getController());


                        lv_main = (ListView) findViewById(R.id.taskList);
                        adapter = new ListviewAdapter(getApplicationContext(), R.layout.task_lv, TaskListActivity.mTaskList);
                        lv_main.setAdapter(adapter);
                        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                                //각 리스트를 클릭하여 TaskActivity로 넘어가게 되는데 거기에 intent될 데이터들을 구분해서 넘겨줘야합니다.
                                //지금은 모든 리스트들이 OCR task를 할수있도록 taskview는 image를 controller는 edittext

                                Intent intent_task = new Intent(TaskListActivity.this, TaskActivity.class);
                                Intent intent_exam = new Intent(TaskListActivity.this, ExamActivity.class);

                                int flag=((TaskListItem)adapterView.getItemAtPosition(position)).getTaskFlag();

                                String tasktitle = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskName();
                                String tasktype = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskType();
                                String taskview = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskView();
                                String controller = ((TaskListItem) adapterView.getItemAtPosition(position)).getController();
                                String buttons=((TaskListItem) adapterView.getItemAtPosition(position)).getButtons().toString();

                                String taskid = ((TaskListItem) adapterView.getItemAtPosition(position)).getId();

                                if(flag==0) {
                                    intent_task.putExtra("tasktitle", tasktitle);
                                    intent_task.putExtra("tasktype", tasktype);
                                    intent_task.putExtra("taskview", taskview);
                                    intent_task.putExtra("controller", controller);
                                    intent_task.putExtra("buttons", buttons);
                                    intent_task.putExtra("taskid", taskid);

                                    TaskListActivity.this.startActivity(intent_task);
                                }
                                else if(flag==1) {
                                    intent_exam.putExtra("tasktitle", tasktitle);
                                    intent_exam.putExtra("tasktype", tasktype);
                                    intent_exam.putExtra("taskview", taskview);
                                    intent_exam.putExtra("examview", controller);
                                    intent_exam.putExtra("buttons", buttons);
                                    intent_exam.putExtra("taskid", taskid);

                                    TaskListActivity.this.startActivity(intent_exam);
                                }

                            }
                        });




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute("http://18.222.204.84/main", param, stringtoken);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
