package com.selectstar.hwshin.cachemission.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.selectstar.hwshin.cachemission.Adapter.ListviewAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.AsyncTaskCancelTimerTask;
import com.selectstar.hwshin.cachemission.DataStructure.TaskListItem;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;
//import com.selectstar.hwshin.cachemission.DataStructure.

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskListActivity extends AppCompatActivity {

    private ListView lv_main;
    final static ArrayList<TaskListItem> mTaskList = new ArrayList<TaskListItem>();
    static ListviewAdapter adapter;
    private UIHashMap uiHashMap;
    private String versionName;
    private String versionCode;
    private Context mContext = this;
    private Activity mActivity=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        uiHashMap = new UIHashMap();
        TextView tv_versionName = findViewById(R.id.version_text);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("파이어베이스토큰", "Refreshed token: " + refreshedToken);

        //FirebaseMessaging.getInstance().subscribeToTopic("RetentionPush");
        //테스트를 위해 사용하는 푸시 Topic 'unsubscribeFromTopic 지우지 말것 (호욱)
        //FirebaseMessaging.getInstance().subscribeToTopic("TestPush");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("TestPush");

        final SharedPreferences push = getSharedPreferences("push", MODE_PRIVATE);
        String OnOff = push.getString("push", "true");
        if(OnOff.equals("true"))
            FirebaseMessaging.getInstance().subscribeToTopic("RetentionPush");


        //버전가져오기
        try{
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            tv_versionName.setText("현재 버전 v"+versionName);
            //tv_versionCode.setText(String.valueOf(pi.versionCode));
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);

        //메뉴버튼
        Button menuBtn = findViewById(R.id.drawviewbtn);
        menuBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT) ;
                }
            }
        });
        TextView myPage = findViewById(R.id.mypage);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskListActivity.this, "구현 중이에용~~",Toast.LENGTH_SHORT).show();
            }
        });
        ImageView settingBtn = findViewById(R.id.settingbtn);
        settingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent_setting = new Intent(TaskListActivity.this, SettingActivity.class);
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT) ;
                }
                TaskListActivity.this.startActivity(intent_setting);
            }
        });
        TextView noticebtn = findViewById(R.id.noticeb);
        noticebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_notice= new Intent(TaskListActivity.this, NoticeActivity.class);
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT) ;
                }
                startActivity(intent_notice);

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

        TextView suggestionBtn = findViewById(R.id.suggestion);
        suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_suggestion = new Intent(TaskListActivity.this, SuggestionActivity.class);
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT) ;
                }
                startActivity(intent_suggestion);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences lazyLoding = getSharedPreferences("lazyLoding", MODE_PRIVATE);
        final SharedPreferences.Editor editor = lazyLoding.edit();


        final Intent intent_task = new Intent(TaskListActivity.this, TaskActivity.class);
        final Intent intent_exam = new Intent(TaskListActivity.this, ExamActivity.class);
        //시작하면 일단 토큰을 받아옴
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        String stringtoken;
        stringtoken = token.getString("loginToken",null);
        if(stringtoken == null){
            stringtoken="";
        }
        final Button refresh=findViewById(R.id.refreshbutton);
        if(isNetworkConnected(this)){

        }else{
            editor.putString("preresult","");
            editor.apply();
            mTaskList.clear();

            refresh.setVisibility(View.VISIBLE);
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
                    String stringtoken;
                    stringtoken = token.getString("loginToken",null);
                    if(stringtoken==null){
                        stringtoken="";
                    }
                    JSONObject param = new JSONObject();
                    try {
                        param.put("requestlist", "tasklist");
                        param.put("version", versionName);
                        if(isNetworkConnected(mContext)){
                            AsyncTask as=new WaitHttpRequest(mContext) {
                                @Override
                                protected void onPostExecute(Object o) {
                                    super.onPostExecute(o);
                                    try {
                                        JSONObject resulttemp = new JSONObject(result);
                                        editor.putString("preresult",resulttemp.toString());
                                        editor.apply();
                                        System.out.println("통신실패하면 " + resulttemp);
                                        if (resulttemp.get("success").toString().equals("login")) {


                                            Intent gotologin = new Intent(TaskListActivity.this, LoginActivity.class);
                                            startActivity(gotologin);
                                            finish();
                                        } else {

                                            refresh.setVisibility(View.GONE);
                                            editor.putString("isfirst","no");
                                            editor.apply();
                                            Log.d("hogu","hogu");
                                            //유저정보세팅
                                            JSONObject user = (JSONObject) resulttemp.get("user");
                                            TextView usernamedrawer = findViewById(R.id.usernamedrawer);
                                            ImageView userrank = findViewById(R.id.userrank);
                                            TextView money = findViewById(R.id.mygold);
                                            ProgressBar progress = (ProgressBar) findViewById(R.id.mainProgressBar);
                                            int allGold = (int) user.get("gold") + (int) user.get("maybe");
                                            money.setText(String.valueOf(allGold));
                                            usernamedrawer.setText(String.valueOf(user.get("name")));
                                            setuserrankImage(userrank, Integer.parseInt(user.get("rank").toString()));
                                            //progress bar setting
                                            setuserprogressbar(progress, Integer.parseInt(user.get("rank").toString()), Integer.parseInt(user.get("success_count").toString()));
                                            intent_task.putExtra("goldNow", String.valueOf(user.get("gold")));
                                            intent_task.putExtra("goldPre", String.valueOf(user.get("maybe")));
                                            intent_exam.putExtra("goldNow", String.valueOf(user.get("gold")));
                                            intent_exam.putExtra("goldPre", String.valueOf(user.get("maybe")));

                                            //긴급공지사항있을시 토스트 띄우기
                                            try {
                                                if (resulttemp.getString("emergency") != null)
                                                    Toast.makeText(getApplicationContext(), resulttemp.getString("emergency"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                            }

                                            //Task 리스트 띄우기
                                            mTaskList.clear();
                                            JSONArray exam_res = (JSONArray) resulttemp.get("exam_data");
                                            JSONArray task_res = (JSONArray) resulttemp.get("task_data");
                                            for (int i = 0; i < exam_res.length(); i++) {

                                                JSONObject temp = (JSONObject) exam_res.get(i);
                                                mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                                                        (String) temp.get("taskView"), (String) temp.get("controller"), "\uFFE6" + String.valueOf(temp.get("gold")), (String) temp.get("dailyMission"), (JSONArray) temp.get("buttons"), 1, Integer.parseInt(String.valueOf(temp.get("examType")))));
                                            }


                                            for (int i = 0; i < task_res.length(); i++) {

                                                JSONObject temp = (JSONObject) task_res.get(i);
                                                mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                                                        (String) temp.get("taskView"), (String) temp.get("controller"), "\uFFE6" + String.valueOf(temp.get("gold")), (String) temp.get("dailyMission"), (JSONArray) temp.get("buttons"), 0));


                                            }

                                        }

                                        lv_main = (ListView) findViewById(R.id.taskList);
                                        adapter = new ListviewAdapter(getApplicationContext(), R.layout.task_lv, TaskListActivity.mTaskList);
                                        lv_main.setAdapter(adapter);
                                        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                                                int flag = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskFlag();

                                                String tasktitle = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskName();
                                                String tasktype = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskType();
                                                String taskview = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskView();
                                                String controller = ((TaskListItem) adapterView.getItemAtPosition(position)).getController();
                                                String buttons = ((TaskListItem) adapterView.getItemAtPosition(position)).getButtons().toString();
                                                String taskid = ((TaskListItem) adapterView.getItemAtPosition(position)).getId();
                                                String upgold = ((TaskListItem) adapterView.getItemAtPosition(position)).getGold();
                                                String dailyQuest = ((TaskListItem) adapterView.getItemAtPosition(position)).getDailyMission();


                                                if (flag == 0) {
                                                    intent_task.putExtra("taskTitle", tasktitle);
                                                    intent_task.putExtra("taskType", tasktype);
                                                    intent_task.putExtra("taskView", taskview);
                                                    intent_task.putExtra("upGold", upgold);
                                                    intent_task.putExtra("controller", controller);
                                                    intent_task.putExtra("buttons", buttons);
                                                    intent_task.putExtra("taskId", taskid);
                                                    intent_task.putExtra("daily", dailyQuest);
                                                    intent_task.putExtra("from", 0);

                                                    TaskListActivity.this.startActivity(intent_task);
                                                } else if (flag == 1) {
                                                    intent_exam.putExtra("taskTitle", tasktitle);
                                                    intent_exam.putExtra("taskType", tasktype);
                                                    intent_exam.putExtra("taskView", taskview);
                                                    intent_exam.putExtra("examView", controller);
                                                    intent_exam.putExtra("upGold", upgold);
                                                    intent_exam.putExtra("buttons", buttons);
                                                    intent_exam.putExtra("taskId", taskid);
                                                    intent_exam.putExtra("examType", ((TaskListItem) adapterView.getItemAtPosition(position)).getExamType());
                                                    intent_exam.putExtra("from", 0);
                                                    TaskListActivity.this.startActivity(intent_exam);
                                                }

                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            CountDownTimer adf= new AsyncTaskCancelTimerTask(as,7000,700,true,mActivity).start();
                            as.execute(getString(R.string.mainurl) + "/main", param, stringtoken);}
                        else{
                            Toast.makeText(mContext,"네트워크가 연결되어있지 않습니다. 연결상태를 확인하고 앱을 재실행해 주세요",Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }
            });

            Toast.makeText(this,"네트워크가 연결되어있지 않습니다. 연결상태를 확인하고 앱을 재실행해 주세요",Toast.LENGTH_LONG).show();

        }
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String getDate = dateFormat.format(date);
        String getHour = hourFormat.format(date);
        String getMinute = minuteFormat.format(date);
        Log.d("date",getHour);

        Log.d("seddddd",lazyLoding.getString("isfirst",""));
        if(lazyLoding.getString("isfirst","").equals("")){
        Log.d("secccc",lazyLoding.getString("isfirst",""));
            editor.putString("isfirst","yes");
        }
        String ymd="";
        ymd=lazyLoding.getString("ymd","");
        int hms=lazyLoding.getInt("hms",0);
        int nowTime=Integer.parseInt(getHour)*60+Integer.parseInt(getMinute);
        Log.d("timeclose1",String.valueOf(hms));
        Log.d("timeclose2",String.valueOf(nowTime));
        Log.d("timeclose3",ymd);
        Log.d("firrrr",lazyLoding.getString("isfirst",""));
        SharedPreferences token2 = getSharedPreferences("token",MODE_PRIVATE);

        Log.d("logintokenishere",token.getString("loginToken","def"));

        if(((!lazyLoding.getString("isfirst","").equals("yes"))&&(ymd.equals(getDate)&&nowTime-hms<10)&&!token.getString("loginToken","").equals(""))||!isNetworkConnected(this)) {
Log.d("timeclose",String.valueOf(nowTime-hms));
            try {
                JSONObject resTemp=new JSONObject(lazyLoding.getString("preresult",""));
                JSONObject user = (JSONObject) resTemp.get("user");
                TextView usernamedrawer = findViewById(R.id.usernamedrawer);
                ImageView userrank = findViewById(R.id.userrank);
                TextView money = findViewById(R.id.mygold);
                ProgressBar progress = (ProgressBar) findViewById(R.id.mainProgressBar);
                int allGold = (int) user.get("gold") + (int) user.get("maybe");
                money.setText(String.valueOf(allGold));
                usernamedrawer.setText(String.valueOf(user.get("name")));
                setuserrankImage(userrank, Integer.parseInt(user.get("rank").toString()));
                //progress bar setting
                setuserprogressbar(progress, Integer.parseInt(user.get("rank").toString()), Integer.parseInt(user.get("success_count").toString()));
                intent_task.putExtra("goldNow", String.valueOf(user.get("gold")));
                intent_task.putExtra("goldPre", String.valueOf(user.get("maybe")));
                intent_exam.putExtra("goldNow", String.valueOf(user.get("gold")));
                intent_exam.putExtra("goldPre", String.valueOf(user.get("maybe")));

                //긴급공지사항있을시 토스트 띄우기
                try {
                    if (resTemp.getString("emergency") != null)
                        Toast.makeText(getApplicationContext(), resTemp.getString("emergency"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                }


                //Task 리스트 띄우기
                mTaskList.clear();
                JSONArray exam_res = (JSONArray) resTemp.get("exam_data");
                JSONArray task_res = (JSONArray) resTemp.get("task_data");
                for (int i = 0; i < exam_res.length(); i++) {

                    JSONObject temp = (JSONObject) exam_res.get(i);
                    mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                            (String) temp.get("taskView"), (String) temp.get("controller"), "\uFFE6" + String.valueOf(temp.get("gold")), (String) temp.get("dailyMission"), (JSONArray) temp.get("buttons"), 1, Integer.parseInt(String.valueOf(temp.get("examType")))));
                }


                for (int i = 0; i < task_res.length(); i++) {

                    JSONObject temp = (JSONObject) task_res.get(i);
                    mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                            (String) temp.get("taskView"), (String) temp.get("controller"), "\uFFE6" + String.valueOf(temp.get("gold")), (String) temp.get("dailyMission"), (JSONArray) temp.get("buttons"), 0));


                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            lv_main = (ListView) findViewById(R.id.taskList);
            adapter = new ListviewAdapter(getApplicationContext(), R.layout.task_lv, TaskListActivity.mTaskList);
            lv_main.setAdapter(adapter);
            lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    int flag = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskFlag();

                    String tasktitle = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskName();
                    String tasktype = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskType();
                    String taskview = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskView();
                    String controller = ((TaskListItem) adapterView.getItemAtPosition(position)).getController();
                    String buttons = ((TaskListItem) adapterView.getItemAtPosition(position)).getButtons().toString();
                    String taskid = ((TaskListItem) adapterView.getItemAtPosition(position)).getId();
                    String upgold = ((TaskListItem) adapterView.getItemAtPosition(position)).getGold();
                    String dailyQuest = ((TaskListItem) adapterView.getItemAtPosition(position)).getDailyMission();


                    if (flag == 0) {
                        intent_task.putExtra("taskTitle", tasktitle);
                        intent_task.putExtra("taskType", tasktype);
                        intent_task.putExtra("taskView", taskview);
                        intent_task.putExtra("upGold", upgold);
                        intent_task.putExtra("controller", controller);
                        intent_task.putExtra("buttons", buttons);
                        intent_task.putExtra("taskId", taskid);
                        intent_task.putExtra("daily", dailyQuest);
                        intent_task.putExtra("from", 0);

                        TaskListActivity.this.startActivity(intent_task);
                    } else if (flag == 1) {
                        intent_exam.putExtra("taskTitle", tasktitle);
                        intent_exam.putExtra("taskType", tasktype);
                        intent_exam.putExtra("taskView", taskview);
                        intent_exam.putExtra("examView", controller);
                        intent_exam.putExtra("upGold", upgold);
                        intent_exam.putExtra("buttons", buttons);
                        intent_exam.putExtra("taskId", taskid);
                        intent_exam.putExtra("examType", ((TaskListItem) adapterView.getItemAtPosition(position)).getExamType());
                        intent_exam.putExtra("from", 0);
                        TaskListActivity.this.startActivity(intent_exam);
                    }

                }
            });

        }

        else {
            Log.d("notreach","notnot");

            editor.putString("ymd", getDate);
            editor.putInt("hms",nowTime);
            editor.apply();

            JSONObject param = new JSONObject();
            try {
                param.put("requestlist", "tasklist");
                param.put("version", versionName);
if(isNetworkConnected(this)){
                AsyncTask as=new WaitHttpRequest(this) {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        try {
                            JSONObject resulttemp = new JSONObject(result);
                            editor.putString("preresult",resulttemp.toString());
                            editor.apply();
                            System.out.println("통신실패하면 " + resulttemp);
                            if (resulttemp.get("success").toString().equals("login")) {
                                Intent gotologin = new Intent(TaskListActivity.this, LoginActivity.class);
                                startActivity(gotologin);
                                finish();
                            } else {

                                editor.putString("isfirst","no");
                                editor.apply();
                                Log.d("hogu","hogu");
                                //유저정보세팅
                                JSONObject user = (JSONObject) resulttemp.get("user");
                                TextView usernamedrawer = findViewById(R.id.usernamedrawer);
                                ImageView userrank = findViewById(R.id.userrank);
                                TextView money = findViewById(R.id.mygold);
                                ProgressBar progress = (ProgressBar) findViewById(R.id.mainProgressBar);
                                int allGold = (int) user.get("gold") + (int) user.get("maybe");
                                money.setText(String.valueOf(allGold));
                                usernamedrawer.setText(String.valueOf(user.get("name")));
                                setuserrankImage(userrank, Integer.parseInt(user.get("rank").toString()));
                                //progress bar setting
                                setuserprogressbar(progress, Integer.parseInt(user.get("rank").toString()), Integer.parseInt(user.get("success_count").toString()));
                                intent_task.putExtra("goldNow", String.valueOf(user.get("gold")));
                                intent_task.putExtra("goldPre", String.valueOf(user.get("maybe")));
                                intent_exam.putExtra("goldNow", String.valueOf(user.get("gold")));
                                intent_exam.putExtra("goldPre", String.valueOf(user.get("maybe")));

                                //긴급공지사항있을시 토스트 띄우기
                                try {
                                    if (resulttemp.getString("emergency") != null)
                                        Toast.makeText(getApplicationContext(), resulttemp.getString("emergency"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                }

                                //Task 리스트 띄우기
                                mTaskList.clear();
                                JSONArray exam_res = (JSONArray) resulttemp.get("exam_data");
                                JSONArray task_res = (JSONArray) resulttemp.get("task_data");
                                for (int i = 0; i < exam_res.length(); i++) {

                                    JSONObject temp = (JSONObject) exam_res.get(i);
                                    mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                                            (String) temp.get("taskView"), (String) temp.get("controller"), "\uFFE6" + String.valueOf(temp.get("gold")), (String) temp.get("dailyMission"), (JSONArray) temp.get("buttons"), 1, Integer.parseInt(String.valueOf(temp.get("examType")))));
                                }


                                for (int i = 0; i < task_res.length(); i++) {

                                    JSONObject temp = (JSONObject) task_res.get(i);
                                    mTaskList.add(new TaskListItem(String.valueOf(temp.get("id")), (String) temp.get("taskName"), (String) temp.get("taskType"),
                                            (String) temp.get("taskView"), (String) temp.get("controller"), "\uFFE6" + String.valueOf(temp.get("gold")), (String) temp.get("dailyMission"), (JSONArray) temp.get("buttons"), 0));


                                }

                            }

                            lv_main = (ListView) findViewById(R.id.taskList);
                            adapter = new ListviewAdapter(getApplicationContext(), R.layout.task_lv, TaskListActivity.mTaskList);
                            lv_main.setAdapter(adapter);
                            lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                                    int flag = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskFlag();

                                    String tasktitle = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskName();
                                    String tasktype = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskType();
                                    String taskview = ((TaskListItem) adapterView.getItemAtPosition(position)).getTaskView();
                                    String controller = ((TaskListItem) adapterView.getItemAtPosition(position)).getController();
                                    String buttons = ((TaskListItem) adapterView.getItemAtPosition(position)).getButtons().toString();
                                    String taskid = ((TaskListItem) adapterView.getItemAtPosition(position)).getId();
                                    String upgold = ((TaskListItem) adapterView.getItemAtPosition(position)).getGold();
                                    String dailyQuest = ((TaskListItem) adapterView.getItemAtPosition(position)).getDailyMission();


                                    if (flag == 0) {
                                        intent_task.putExtra("taskTitle", tasktitle);
                                        intent_task.putExtra("taskType", tasktype);
                                        intent_task.putExtra("taskView", taskview);
                                        intent_task.putExtra("upGold", upgold);
                                        intent_task.putExtra("controller", controller);
                                        intent_task.putExtra("buttons", buttons);
                                        intent_task.putExtra("taskId", taskid);
                                        intent_task.putExtra("daily", dailyQuest);
                                        intent_task.putExtra("from", 0);

                                        TaskListActivity.this.startActivity(intent_task);
                                    } else if (flag == 1) {
                                        intent_exam.putExtra("taskTitle", tasktitle);
                                        intent_exam.putExtra("taskType", tasktype);
                                        intent_exam.putExtra("taskView", taskview);
                                        intent_exam.putExtra("examView", controller);
                                        intent_exam.putExtra("upGold", upgold);
                                        intent_exam.putExtra("buttons", buttons);
                                        intent_exam.putExtra("taskId", taskid);
                                        intent_exam.putExtra("examType", ((TaskListItem) adapterView.getItemAtPosition(position)).getExamType());
                                        intent_exam.putExtra("from", 0);
                                        TaskListActivity.this.startActivity(intent_exam);
                                    }

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
               CountDownTimer adf= new AsyncTaskCancelTimerTask(as,7000,700,true,this).start();
               as.execute(getString(R.string.mainurl) + "/main", param, stringtoken);}
               else{
    Toast.makeText(this,"네트워크가 연결되어있지 않습니다. 연결상태를 확인하고 앱을 재실행해 주세요",Toast.LENGTH_LONG).show();
}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
public static boolean isNetworkConnected(Context context){
    ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo=manager.getActiveNetworkInfo();
    if(activeNetworkInfo!=null){

        if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && activeNetworkInfo.isConnectedOrConnecting()&&activeNetworkInfo.isAvailable()) {
        return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE && activeNetworkInfo.isConnectedOrConnecting()&&activeNetworkInfo.isAvailable()) {
                          // 모바일 네트워크 연결중
            return true;
            }
                       else{
            return false;
        }


    }else{
        return false;
    }

}
    public class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Toast.makeText(context, "네트워크변화가 감지되었습니다. 4G사용시 데이터요금이 부과될 수 있습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void setuserrankImage(ImageView userrank, int rank) {

        if(rank==1)
            Glide.with(this).load(R.drawable.rank_begginner).into(userrank);
        else if(rank==2)
            Glide.with(this).load(R.drawable.rank_intermediate).into(userrank);
        else if(rank==3)
            Glide.with(this).load(R.drawable.rank_master).into(userrank);
        else if(rank==151)
            Glide.with(this).load(R.drawable.rank_admin).into(userrank);
        else
            Glide.with(this).load(R.drawable.rank_begginner).into(userrank);
    }

    private void setuserprogressbar(ProgressBar progressBar, int rank, int count) {
        float percent = 0;
        float count_f = (float)count;

        if(rank == 1)
            //50회 이상 진행시 레벨업
            percent = (count_f/50)*100;
        else if(rank == 2)
            //300회 이상 진행시 레벨업 (50+250)
            percent = ((count_f-50)/250)*100;
        else if(rank == 3)
            //현재 최고레벨
            percent = 0;
        else if(rank == 151)
            //관리자
            percent = 100;
        else//not reachable
            percent = 100;

        progressBar.setProgress((int)percent);

    }
}
