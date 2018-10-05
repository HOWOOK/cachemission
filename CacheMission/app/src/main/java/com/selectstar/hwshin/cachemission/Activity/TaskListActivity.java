package com.selectstar.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.selectstar.hwshin.cachemission.Adapter.ListviewAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskListActivity extends AppCompatActivity {

    final static ArrayList<JSONObject> mTaskList = new ArrayList<>();
    static ListviewAdapter adapter;
    private UIHashMap uiHashMap;
    private String versionName;
    private String versionCode;
    private Context mContext = this;
    private RecyclerView lv_main;
    public void insertItem(JSONObject item){

        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        String previousList="";

        JSONObject previousListJSON=null;
        JSONObject content=null;
        JSONArray items=null;

        try {

            previousList=listInfo.getString("listInfoData","{}");
            previousListJSON=new JSONObject(previousList);
            content=(JSONObject) previousListJSON.get("content");
            items=(JSONArray) content.get("items");
            items.put(item);
            content.put("items",items);
            previousListJSON.put("content",content);

            editor.putString("listInfoData",previousListJSON.toString());
            editor.apply();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void insertUserInfo(JSONObject userInfo){
        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        String previousList="";

        JSONObject previousListJSON=null;
        JSONObject content=null;


        try {

            previousList=listInfo.getString("listInfoData","{}");
            previousListJSON=new JSONObject(previousList);
            content=(JSONObject) previousListJSON.get("content");

            content.put("user",userInfo);
            previousListJSON.put("content",content);

            editor.putString("listInfoData",previousListJSON.toString());
            editor.apply();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void initiateListInfo(){
        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        JSONObject previousListJSON = new JSONObject();
        int time=0;
        String date="";
        JSONObject content = new JSONObject();
        JSONObject user = new JSONObject();
        JSONArray items = new JSONArray();

        try {

            content.put("user",user);
            content.put("items",items);
            previousListJSON.put("content",content);
            previousListJSON.put("time",time);
            previousListJSON.put("date",date);
            editor.putString("listInfoData",previousListJSON.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void clearItem(){
        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        String previousList="";

        JSONObject previousListJSON=null;
        JSONObject content=null;
        JSONArray items=null;

        try {


            previousList=listInfo.getString("listInfoData","{}");
            previousListJSON=new JSONObject(previousList);
            content=(JSONObject) previousListJSON.get("content");
            ;
            content.put("items",new JSONArray());
            previousListJSON.put("content",content);

            editor.putString("listInfoData",previousListJSON.toString());
            editor.apply();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean checkIfTimePassed() {

        SharedPreferences listInfo = getSharedPreferences("listInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = listInfo.edit();
        String preDate = "";
        String nowDate = "";
        int preTime = 0;
        int nowTime = 0;
        String previousList = "";
        JSONObject previousListJSON = null;

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String getDate = dateFormat.format(date);
        String getHour = hourFormat.format(date);
        String getMinute = minuteFormat.format(date);

        nowDate = getDate;
        nowTime = Integer.parseInt(getHour) * 60 + Integer.parseInt(getMinute);

        try {

            previousList = listInfo.getString("listInfoData", "{}");
            previousListJSON = new JSONObject(previousList);
            preTime = (Integer) previousListJSON.get("time");
            preDate = (String) previousListJSON.get("date");


        } catch (JSONException e) {
            e.printStackTrace();
        }



        if (nowDate.equals(preDate)) {
            if (nowTime - preTime < 10) {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }
    }
    public void insertTime(){
        SharedPreferences listInfo = getSharedPreferences("listInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = listInfo.edit();
        String nowDate = "";
        int nowTime = 0;
        String previousList = "";
        JSONObject previousListJSON = null;

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String getDate = dateFormat.format(date);
        String getHour = hourFormat.format(date);
        String getMinute = minuteFormat.format(date);

        nowDate = getDate;
        nowTime = Integer.parseInt(getHour) * 60 + Integer.parseInt(getMinute);


        try {

            previousList = listInfo.getString("listInfoData", "{}");
            previousListJSON = new JSONObject(previousList);

            previousListJSON.put("time",nowTime);
            previousListJSON.put("date",nowDate);

            editor.putString("listInfoData",previousListJSON.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void getPreviousList(String loginToken){
        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        String previousList="";

        JSONObject previousListJSON=null;

        JSONObject content=null;
        JSONObject user = null;
        JSONArray items=null;

        try {
            previousList = listInfo.getString("listInfoData", "{}");
            previousListJSON = new JSONObject(previousList);

            content=(JSONObject)previousListJSON.get("content");
            items=(JSONArray)content.get("items");
            user=(JSONObject)content.get("user");
            mTaskList.clear();
            for(int i=0; i<items.length(); i++){
                mTaskList.add((JSONObject)items.get(i));
            }
            lv_main = findViewById(R.id.taskList);
            adapter =new ListviewAdapter(getApplicationContext(),R.layout.task_lv,mTaskList,TaskListActivity.this,user);
            LinearLayoutManager layoutManager = new LinearLayoutManager(TaskListActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            lv_main.setLayoutManager(layoutManager);
            lv_main.setAdapter(adapter);
            for (int i =0;i < mTaskList.size(); i++)
            {
                getOneTask(i,loginToken,false);
            }
            getJustUserInfo(loginToken);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

    }
    private void getJustUserInfo(final String loginToken)
    {

        JSONObject param = new JSONObject();
        new WaitHttpRequest(this) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(responseCode == 401)
                {
                    Intent loginIntent = new Intent(TaskListActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
                mTaskList.clear();
                try {
                    if (result == "")
                        return;
                    JSONObject resultTemp = new JSONObject(result);
                    JSONObject user = (JSONObject)resultTemp.get("user");
                    ImageView userRank = findViewById(R.id.userrank);
                    TextView userGold = findViewById(R.id.mygold);
                    int allGold = (int) user.get("gold") + (int) user.get("maybe");
                    userGold.setText(String.valueOf(allGold));
                    TextView userNameDrawer = findViewById(R.id.usernamedrawer);
                    userNameDrawer.setText(String.valueOf(user.get("name")));
                    setUserRankImage(userRank, (int)user.get("rank"));
                    ProgressBar progress = findViewById(R.id.mainProgressBar);
                    setUserProgressBar(progress,(int)user.get("rank"), (int)user.get("success_count"));
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }.execute(getString(R.string.mainurl) + "/testing/getTaskList", param, loginToken);
    }
    private void subscribePush()
    {
        final SharedPreferences push = getSharedPreferences("push", MODE_PRIVATE);
        String OnOff = push.getString("push", "true");
        if(OnOff.equals("true"))
            FirebaseMessaging.getInstance().subscribeToTopic("RetentionPush");
        FirebaseMessaging.getInstance().subscribeToTopic("testPush");
    }
    private void setVersion()
    {
        try{
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            TextView tv_versionName = findViewById(R.id.version_text);
            tv_versionName.setText("현재 버전 v"+versionName);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

    }
    private void setDrawer()
    {
        final DrawerLayout drawer = findViewById(R.id.drawer) ;

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
        TextView noticeBtn = findViewById(R.id.noticeb);
        noticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_notice= new Intent(TaskListActivity.this, NoticeActivity.class);
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT) ;
                }
                startActivity(intent_notice);

            }
        });
        TextView exchangeBtn = findViewById(R.id.exchange);
        exchangeBtn.setOnClickListener(new View.OnClickListener() {
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
    private void getOneTask(final int i,final String loginToken,final boolean isNew)
    {
        try {
            JSONObject param = new JSONObject();
            param.put("id", mTaskList.get(i).get("id"));
            String taskType = mTaskList.get(i).get("taskType").toString();
            int examType = (int) mTaskList.get(i).get("examType");
            if(taskType.contains("EXAM")){
                param.put("examType", examType);
            };
            final JSONObject pp = param;
            new HurryHttpRequest(TaskListActivity.this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {
                        JSONObject resultTemp = new JSONObject(result);
                        if(resultTemp.get("success").toString().equals("true")) {
                            if (isNew) {
                                adapter.addItem(mTaskList.get(i));
                            }
                            else
                            {
                                ListviewAdapter.ItemViewHolder holder = (ListviewAdapter.ItemViewHolder)lv_main.findViewHolderForAdapterPosition(i);
                                adapter.updateItem(holder,(JSONArray)resultTemp.get("questList"));
                            }
                        }
                    }catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }.execute(getString(R.string.mainurl) + "/testing/taskValid", pp, loginToken);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    private void getTaskList(final String loginToken)
    {
        clearItem();
        insertTime();
        JSONObject param = new JSONObject();
        new WaitHttpRequest(this) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(responseCode == 401)
                {
                    Intent loginIntent = new Intent(TaskListActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
                mTaskList.clear();
                try {
                    if (result == "")
                        return;
                    JSONObject resultTemp = new JSONObject(result);
                    JSONObject user = (JSONObject)resultTemp.get("user");
                    ImageView userRank = findViewById(R.id.userrank);
                    TextView userGold = findViewById(R.id.mygold);
                    int allGold = (int) user.get("gold") + (int) user.get("maybe");
                    userGold.setText(String.valueOf(allGold));
                    TextView userNameDrawer = findViewById(R.id.usernamedrawer);
                    userNameDrawer.setText(String.valueOf(user.get("name")));
                    setUserRankImage(userRank, (int)user.get("rank"));
                    ProgressBar progress = findViewById(R.id.mainProgressBar);
                    setUserProgressBar(progress,(int)user.get("rank"), (int)user.get("success_count"));

                    JSONArray examList = (JSONArray) resultTemp.get("exam_data");
                    for (int i = 0; i < examList.length(); i++)
                        mTaskList.add((JSONObject) examList.get(i));
                    JSONArray taskList = (JSONArray) resultTemp.get("task_data");
                    for (int i = 0; i < taskList.length(); i++)
                        mTaskList.add((JSONObject) taskList.get(i));
                    RecyclerView lv_main = findViewById(R.id.taskList);
                    adapter =new ListviewAdapter(getApplicationContext(),R.layout.task_lv,new ArrayList<JSONObject>(),TaskListActivity.this,user);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(TaskListActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    lv_main.setLayoutManager(layoutManager);
                    lv_main.setAdapter(adapter);
                    for (int i =0;i < mTaskList.size(); i++)
                    {
                        getOneTask(i,loginToken,true);
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }.execute(getString(R.string.mainurl) + "/testing/getTaskList", param, loginToken);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        uiHashMap = new UIHashMap();
        subscribePush();
        setVersion();
        setDrawer();
        TextView myPage = findViewById(R.id.mypage);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskListActivity.this, "구현 중이에용~~",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        final String loginToken = token.getString("loginToken","");
        if(checkIfTimePassed())
            getTaskList(loginToken);
        else
            getPreviousList(loginToken);

    }
    private void setUserRankImage(ImageView userRank, int rank) {

        if(rank==1)
            Glide.with(this).load(R.drawable.rank_begginner).into(userRank);
        else if(rank==2)
            Glide.with(this).load(R.drawable.rank_intermediate).into(userRank);
        else if(rank==3)
            Glide.with(this).load(R.drawable.rank_master).into(userRank);
        else if(rank==151)
            Glide.with(this).load(R.drawable.rank_admin).into(userRank);
        else
            Glide.with(this).load(R.drawable.rank_begginner).into(userRank);
    }

    private void setUserProgressBar(ProgressBar progressBar, int rank, int count) {
        float percent;
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