package com.example.hwshin.cachemission.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
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

import com.example.hwshin.cachemission.Adapter.ButtonListAdapter;
import com.example.hwshin.cachemission.Adapter.ListviewAdapter;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.DataStructure.TaskListItem;
import com.example.hwshin.cachemission.DataStructure.UIHashmap;
import com.example.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskListActivity extends AppCompatActivity {

    private ListView lv_main;
    final static ArrayList<TaskListItem> mTaskList = new ArrayList<TaskListItem>();
    static ListviewAdapter adapter;
    private UIHashmap uiHashmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        uiHashmap = new UIHashmap();
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
        stringtoken=token.getString("logintoken",null);
        if(stringtoken==null){
            stringtoken="";
        Log.d("tokkk",stringtoken);}
        JSONObject param = new JSONObject();
        try {
            param.put("requestlist", "tasklist");


            new HttpRequest() {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {


                        JSONObject resulttemp = new JSONObject(result);

                        if (resulttemp.get("success").toString().equals("login")) {
                            Intent gotologin = new Intent(TaskListActivity.this,LoginActivity.class);
                            startActivity(gotologin);
                        }
                        else{

                            //유저정보세팅
                            JSONObject user = (JSONObject) resulttemp.get("user");
                            TextView username = findViewById(R.id.username);
                            TextView usernamedrawer = findViewById(R.id.usernamedrawer);
                            TextView money = findViewById(R.id.money);
                            TextView maybemoney = findViewById(R.id.maybemoney);
                            money.setText("\\ "+String.valueOf(user.get("gold")));
                            maybemoney.setText("\\ "+String.valueOf(user.get("maybe")));
                            username.setText(String.valueOf(user.get("name")));
                            usernamedrawer.setText(String.valueOf(user.get("name")));


                        JSONArray res = (JSONArray) resulttemp.get("data");
                        mTaskList.clear();
                        for (int i = 0; i < res.length(); i++) {

                            JSONObject temp = (JSONObject) res.get(i);
                            Log.d("dataget", temp.toString());

<<<<<<< HEAD

                            TextView tv = findViewById(R.id.possibleTask);
                            tv.setText("진행 가능한 작업");


                            mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"), (String) temp.get("taskView"), (String) temp.get("controller"), String.valueOf(temp.get("gold")),(JSONArray)temp.get("buttons")));
=======
                            mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                                    (String) temp.get("taskView"), (String) temp.get("controller"), String.valueOf(temp.get("gold"))+" p"));
>>>>>>> design

                        }
                        Log.d("mtask", mTaskList.get(0).getController());
                        Log.d("mtask", mTaskList.get(1).getController());

                        lv_main = (ListView) findViewById(R.id.taskList);
                        adapter = new ListviewAdapter(getApplicationContext(), R.layout.task_lv, TaskListActivity.mTaskList);
                        lv_main.setAdapter(adapter);
                        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                                //각 리스트를 클릭하여 TaskActivity로 넘어가게 되는데 거기에 intent될 데이터들을 구분해서 넘겨줘야합니다.
                                //지금은 모든 리스트들이 OCR task를 할수있도록 taskview는 image를 controller는 edittext

                                Intent intent_lv = new Intent(TaskListActivity.this, TaskActivity.class);

                                String tasktitle = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskName();
                                String tasktype = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskType();
                                String taskview = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskView();
                                String controller = ((TaskListItem) adapterView.getItemAtPosition(position)).getController();
                                String buttons=((TaskListItem) adapterView.getItemAtPosition(position)).getButtons().toString();


                                String taskid = ((TaskListItem) adapterView.getItemAtPosition(position)).getId();

                                intent_lv.putExtra("tasktitle", tasktitle);
                                intent_lv.putExtra("tasktype", tasktype);
                                intent_lv.putExtra("taskview", taskview);
                                intent_lv.putExtra("controller", controller);
                                intent_lv.putExtra("buttons", buttons);

                                intent_lv.putExtra("taskid", taskid);

                                TaskListActivity.this.startActivity(intent_lv);

                            }
                        });


                        //메뉴버튼
                            Button menubtn = findViewById(R.id.drawviewbtn);
                            menubtn.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer) ;
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
                                    TaskListActivity.this.startActivity(intent_setting);
                                }
                            });

                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute("http://18.222.204.84/main", param,stringtoken);
            //new HttpRequest().execute("http://18.222.204.84/ocr",param);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        //mTaskList에 현재 진행가능한 테스트들을 넣어줘야합니다. 현재는 테스트로 넣어봤습니다.
        //mTaskList.add(new TaskListItem("labeling", "테스트입니다."));


    }
}
