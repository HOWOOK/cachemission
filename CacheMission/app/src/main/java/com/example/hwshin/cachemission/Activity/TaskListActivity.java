package com.example.hwshin.cachemission.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

                        JSONArray res = (JSONArray) resulttemp.get("data");
                        mTaskList.clear();
                        for (int i = 0; i < res.length(); i++) {

                            JSONObject temp = (JSONObject) res.get(i);
                            Log.d("dataget", temp.toString());


                            TextView tv = findViewById(R.id.possibleTask);
                            tv.setText("진행 가능한 작업");


                            mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"), (String) temp.get("taskView"), (String) temp.get("controller"), String.valueOf(temp.get("gold")),(JSONArray)temp.get("buttons")));

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