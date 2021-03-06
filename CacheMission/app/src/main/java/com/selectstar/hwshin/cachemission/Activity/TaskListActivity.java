package com.selectstar.hwshin.cachemission.Activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;
import com.selectstar.hwshin.cachemission.Adapter.ListviewAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.AsyncTaskCancelTimerTask;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private TextView myPage;
    private Button refreshButton;
    private  TextView refreshText;
    private TextView nowGold;


    public int runningHTTPRequest = 0;
    NotificationCompat.Builder mBuilder;

    NotificationManager notifManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initiateListInfo();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr")) {
            Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
            t.setScreenName("TaskListActivity");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
        setContentView(R.layout.activity_tasklist);

        ConstraintLayout drawerCL = findViewById(R.id.drawerCL);
        drawerCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        refreshText=findViewById(R.id.refreshText);
        refreshButton=findViewById(R.id.refreshbutton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshButton.setVisibility(View.GONE);
                refreshText.setVisibility(View.GONE);
                SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
                final String loginToken = token.getString("loginToken","");
                if(checkIfTimePassed())
                    getTaskList(loginToken);
                else {
                    getPreviousList(loginToken);
                }
            }
        });
        uiHashMap = new UIHashMap();
        subscribePush();
        setVersion();
        setDrawer();

        //검수자들에게 푸쉬를 보낼겁니다!! 지금은 일단 더럽게 해놓는다.
        FirebaseMessaging.getInstance().unsubscribeFromTopic("ValidationUserPush");

        myPage=findViewById(R.id.mypage);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myPageIntent=new Intent(TaskListActivity.this,MyPageActivity.class);
                startActivity(myPageIntent);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        final String loginToken = token.getString("loginToken","");

        //수정 요망 checkIfTimePassed 함수가 전혀 작동의미가 없음->현재는 10분이 지나지 않을 경우 재로딩을 하지 않는 메커니즘을 사용하지 않기 때문임. 나중에 getPreviousList를 받아오게 바뀌면 자연스럽게 해결될것임
        if(checkIfTimePassed())
            getTaskList(loginToken);
        else{
            getTaskList(loginToken);
        }
        SharedPreferences noticeFlag=getSharedPreferences("noticeFlag",MODE_PRIVATE);


        if(noticeFlag.getBoolean("noticeActivation",true)&&!(loginToken.equals(""))){
            Intent noticeIntent=new Intent(TaskListActivity.this,TaskExplainActivity.class);
            noticeIntent.putExtra("notice","notice");
            startActivity(noticeIntent);
        }

    }
    public void forcedUpdate(){

        Uri uri = Uri.parse("market://details?id="+this.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    //네트워크에 연결되어 있는지(모바일 데이터, 와이파이) 확인한다.
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

    //4G에서 와이파이로 넘어갈 때 토스트
    public class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Toast.makeText(context, "네트워크변화가 감지되었습니다. 4G사용시 데이터요금이 부과될 수 있습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 문제가 생겨 taskList를 불러올수 없을때, 새로고침 버튼 띄우기
    public void clearListForAccident(){
        if(runningHTTPRequest==0)
        mTaskList.clear();
        refreshButton.setVisibility(View.VISIBLE);
        refreshText.setVisibility(View.VISIBLE);
    }

// 쉐프퍼에 불러온 테스크리스트 아이템 한개 저장
    public void insertItem(JSONObject item){

        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        String previousList="";

        JSONArray previousListJSON=null;
        JSONObject content=null;

        try {

            previousList=listInfo.getString("listInfoData","[]");
            previousListJSON=new JSONArray(previousList);
            previousListJSON.put(item);

            editor.putString("listInfoData",previousListJSON.toString());
            editor.apply();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
//쉐프퍼에 불러온 유저정보 저장
    public void insertUserInfo(JSONObject userInfo){
        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        String previousList="";

        JSONObject previousListJSON=null;
        JSONObject content=null;
        Log.d("userinfo",userInfo.toString());

        try {

            previousList=listInfo.getString("listInfoData","{}");
            previousListJSON=new JSONObject(previousList);
            content=(JSONObject) previousListJSON.get("content");

            content.put("user",userInfo);
            previousListJSON.put("content",content);
            Log.d("previous",previousListJSON.toString());
            editor.putString("listInfoData",previousListJSON.toString());
            editor.apply();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
//리스트인포 쉐프퍼를 초기화
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
    //리스트인포 쉐프퍼 내용 전부 비우기
    public void clearItem(){
        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        editor.putString("listInfoData","[]");
        editor.apply();
    }
// 현재날짜와 저장된 날짜를 비교하여 날짜가 변했다면 오늘 번 돈 0으로 초기화. 저장된 날짜와 현재날짜가 같다면 오늘 번 돈 계산하여 리턴
    public String updateTodayEarnedMoney(){

        int moneyDifference=0;

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String currentDate = dateFormat.format(date);
        String currentHour = hourFormat.format(date);

        SharedPreferences currentGold=getSharedPreferences("currentGold",MODE_PRIVATE);

        String nowGoldString=currentGold.getString("currentGold","0");


        SharedPreferences todayMoney=getSharedPreferences("todayMoney",MODE_PRIVATE);
        String todayDate=todayMoney.getString("todayDate","");
        String todayStartMoney=todayMoney.getString("todayStartMoney","");
        SharedPreferences.Editor editor=todayMoney.edit();

        if(todayDate.equals("")){
            editor.putString("todayDate",currentDate);
            editor.putString("todayStartMoney",nowGoldString);
            editor.commit();
            return "0";
        }
        else if(!todayDate.equals(currentDate)){
            editor.putString("todayDate",currentDate);
            editor.putString("todayStartMoney",nowGoldString);
            editor.commit();
            return "0";
        }
        Log.d("todayStart",todayStartMoney);

        Log.d("nowgold",nowGoldString);
        moneyDifference=Integer.parseInt(nowGoldString)-Integer.parseInt(todayStartMoney);
        return String.valueOf(moneyDifference);


    }
    //현재시간이 저장된 시간으로부터 10분 이상이 지났는지 계산하여 불린값 리턴
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

            previousList = listInfo.getString("listInfoTime", "{}");
            if(previousList.equals("{}"))
                return true;
            previousListJSON = new JSONObject(previousList);
            preTime = (Integer) previousListJSON.get("time");
            preDate = (String) previousListJSON.get("date");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("timeee",String.valueOf(nowTime));
        Log.d("pretimeee",String.valueOf(preTime));

        if (nowDate.equals(preDate)) {
            if (nowTime - preTime < 1) {
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
//현재시간정보 리스트인포 쉐프퍼에 저장
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

            previousList = listInfo.getString("listInfoTime", "{}");
            previousListJSON = new JSONObject(previousList);

            previousListJSON.put("time",nowTime);
            previousListJSON.put("date",nowDate);

            editor.putString("listInfoTime",previousListJSON.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
//이전에 저장해놓은 리스트인포 정보로 테스크리스트 인플레이트
    public void getPreviousList(String loginToken){
        SharedPreferences listInfo=getSharedPreferences("listInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=listInfo.edit();

        String previousList="";

        JSONObject previousListJSON=null;

        JSONObject content=null;
        JSONObject user = null;

        try {
            previousList = listInfo.getString("listInfoData", "{}");

            getJustUserInfo(loginToken);
            System.out.println(previousList);
            System.out.println("----");
            JSONArray items = new JSONArray(previousList);

            mTaskList.clear();
            for(int i=0; i<items.length(); i++){
                mTaskList.add((JSONObject)items.get(i));
            }
            lv_main = findViewById(R.id.taskList);
            adapter =new ListviewAdapter(getApplicationContext(),R.layout.task_lv,mTaskList,TaskListActivity.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(TaskListActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            lv_main.setLayoutManager(layoutManager);
            lv_main.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            for (int i =0;i < adapter.getItemCount(); i++)
            {
                getOneTask(i,loginToken,false);
            }

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

    }
//유저정보만 서버에게 요청하기
    private void getJustUserInfo(final String loginToken)
    {
        if(!isNetworkConnected(this)){
            Toast.makeText(this,"네트워크연결이 끊어졌습니다. 연결상태를 확인하고 앱을 다시 실행해 주세요.",Toast.LENGTH_SHORT).show();
            clearListForAccident();
            return;
        }
runningHTTPRequest++;
        JSONObject param = new JSONObject();
        WaitHttpRequest asyncTask=new WaitHttpRequest(this) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                try {
                    if (result == "")
                        return;
                    JSONObject resultTemp = new JSONObject(result);
                    JSONObject user = (JSONObject)resultTemp.get("user");
                    ImageView userRank = findViewById(R.id.userrank);
                    TextView userLevel = findViewById(R.id.userLevel);
                    adapter.setUserInfo(user);
                    TextView userGold = findViewById(R.id.mygold);
                    int allGold = (int) user.get("gold") + (int) user.get("maybe");
                    userGold.setText(String.valueOf(allGold));

                    SharedPreferences currentGold=getSharedPreferences("currentGold",MODE_PRIVATE);
                    SharedPreferences.Editor editor=currentGold.edit();
                    editor.putString("currentGold",String.valueOf(allGold));
                    editor.commit();
                    updateTodayEarnedMoney();


                    notifManager = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);
                    notifManager.cancelAll();
                    SharedPreferences notificationOnOffBoolean=getSharedPreferences("notificationOnOffBoolean",MODE_PRIVATE);
                    topBarSetting(notificationOnOffBoolean.getBoolean("OnOff",true));

                    TextView userNameDrawer = findViewById(R.id.usernamedrawer);
                    userNameDrawer.setText(String.valueOf(user.get("name")));
                    setUserRankImage(userRank, userLevel, (int)user.get("rank"));
                    ProgressBar progress = findViewById(R.id.mainProgressBar);
                    setUserProgressBar(progress,(int)user.get("rank"), (int)user.get("success_count"));
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                runningHTTPRequest--;

            }
        };
        //CountDownTimer adf= new AsyncTaskCancelTimerTask(asyncTask,Integer.parseInt(getString(R.string.hTTPTimeOut)),1000,true,this).start();
        asyncTask.execute(getString(R.string.mainurl) + "/testing/getTaskList", param, loginToken);
    }

    private void subscribePush()
    {
        final SharedPreferences push = getSharedPreferences("push", MODE_PRIVATE);
        String OnOff = push.getString("push", "true"); //default가 true라서 앱처음켰을때는 자동 true;
        if(OnOff.equals("true"))
            FirebaseMessaging.getInstance().subscribeToTopic("RetentionPush");


        // 테스트할때만 쓰이는 Topic임으로 unsubscribe가 default
        //FirebaseMessaging.getInstance().subscribeToTopic("testPush");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("testPush");
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

    //드로어 뷰 셋팅
    private void setDrawer()
    {
        final DrawerLayout drawer = findViewById(R.id.drawer) ;

        //메뉴버튼
        ConstraintLayout menuBtn = findViewById(R.id.drawviewbtn);
        Button menuBtn2 = findViewById(R.id.drawviewbtnsrc);
        View.OnClickListener menuClickListener =  new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT) ;
                }
            }
        };
        menuBtn.setOnClickListener(menuClickListener);
        menuBtn2.setOnClickListener(menuClickListener);

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
        TextView notificationOnOffBtn=findViewById(R.id.notificationOnOff);
        notificationOnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oDialog = new AlertDialog.Builder(mContext,
                        android.R.style.Theme_DeviceDefault_Light_Dialog);

                oDialog.setMessage("상태바 알림을 사용하시겠습니까?")
                        .setTitle("상태바 알림 사용 선택")
                        .setPositiveButton("상태바 알림 해제", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SharedPreferences notificationOnOffBoolean=getSharedPreferences("notificationOnOffBoolean",MODE_PRIVATE);
                                SharedPreferences.Editor editor=notificationOnOffBoolean.edit();
                                editor.putBoolean("OnOff",false);
                                editor.commit();
                                notifManager.cancelAll();
                                Toast.makeText(getApplicationContext(), "상태바 알림이 해제되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNeutralButton("상태바 알림 적용", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SharedPreferences notificationOnOffBoolean=getSharedPreferences("notificationOnOffBoolean",MODE_PRIVATE);
                                SharedPreferences.Editor editor=notificationOnOffBoolean.edit();
                                editor.putBoolean("OnOff",true);
                                editor.commit();
                                //topBarSetting(true);
                                Toast.makeText(getApplicationContext(), "상태바 알림이 적용되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.


                        .show();

            }
        });
    }

    private void getOneTask(final int i,final String loginToken,final boolean isNew)
    {
        if(!isNetworkConnected(this)){
            Toast.makeText(this,"네트워크연결이 끊어졌습니다. 연결상태를 확인하고 앱을 다시 실행해 주세요.",Toast.LENGTH_SHORT).show();
            clearListForAccident();
            return;
        }
        String version = null;
        try {
            PackageInfo in = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = in.versionName;
        } catch(PackageManager.NameNotFoundException e) {

        }
        runningHTTPRequest++;
        try {
            JSONObject param = new JSONObject();
            param.put("taskID", mTaskList.get(i).get("id"));
            param.put("version",version);
            Log.d("버전",version);
            final String taskType = mTaskList.get(i).get("taskType").toString();
            int examType = (int) mTaskList.get(i).get("examType");
            if(taskType.contains("EXAM")){
                param.put("examType", examType);
            };
            if(taskType.contains("TWOPOINT"))
                param.put("option", 111);
            if(taskType.equals("CLASSIFICATION") && mTaskList.get(i).get("difficulty").toString().equals("1"))
                param.put("option", 1001);
            if(taskType.equals("CLASSIFICATION") && mTaskList.get(i).get("difficulty").toString().equals("2"))
                param.put("option", 1002);
            if(taskType.equals("CLASSIFICATION") && mTaskList.get(i).get("difficulty").toString().equals("3"))
                param.put("option", 1003);

            final JSONObject pp = param;
            System.out.println("안녕 난 "+taskType);
            System.out.println("안녕 변수야 : "+param);
            HurryHttpRequest asyncTask = new HurryHttpRequest(TaskListActivity.this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {
                        JSONObject resultTemp = new JSONObject(result);
                        System.out.println("안녕 결과야 : "+result);
                        if(resultTemp.get("success").toString().equals("true")) {
                            if (isNew) {
                                if(taskType.contains("EXAM"))
                                    FirebaseMessaging.getInstance().subscribeToTopic("ValidationUserPush");
                                mTaskList.get(i).put("questList",resultTemp.get("questList"));
                                adapter.addItem(mTaskList.get(i));
                                clearItem();
                                for(int k=0;k<adapter.getmTaskList().size();k++){

                                    insertItem(adapter.getmTaskList().get(k));
                                }

                            }
                            else
                            {
                                adapter.updateQuest(i,(JSONArray)resultTemp.get("questList"));
                                adapter.notifyItemChanged(i);
                                //adapter.updateItem(holder,(JSONArray)resultTemp.get("questList"));

                            }
                        }
                    }catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                    runningHTTPRequest--;
                }
            };
           // CountDownTimer adf= new AsyncTaskCancelTimerTask(asyncTask,Integer.parseInt(getString(R.string.hTTPTimeOut)),1000,true,this).start();
            asyncTask.execute(getString(R.string.mainurl) + "/testing/taskValid", pp, loginToken);


        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void getTaskList(final String loginToken)
    {
        if(!isNetworkConnected(this)){
            Toast.makeText(this,"네트워크연결이 끊어졌습니다. 연결상태를 확인하고 앱을 다시 실행해 주세요.",Toast.LENGTH_SHORT).show();
            clearListForAccident();
            return;
        }
        runningHTTPRequest++;
        String version = null;
        try {
            PackageInfo i = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = i.versionName;
        } catch(PackageManager.NameNotFoundException e) {

        }
        JSONObject param = new JSONObject();
        try {
            param.put("version", version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WaitHttpRequest asyncTask= new WaitHttpRequest(this) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                try {
                    if (result == "")
                        return;
                    JSONObject resultTemp = new JSONObject(result);
                    Log.d("getTaskList",resultTemp.toString());

                    try {
                        if ((resultTemp.getString("forced")).equals("True")) {
                            getForcedUpdateDialog("필수적인 업데이트사항이 있습니다.","먼저 버전 업데이트를 진행해 주세요.");
                            //forcedUpdate();
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    //getForcedUpdateDialog("필수적인 업데이트사항이 있습니다.","먼저 버전 업데이트를 진행해 주세요.");

                    //특정개인에게 푸시알람보내기
                    JSONObject user = (JSONObject)resultTemp.get("user");
                    String userID = user.get("id").toString();
                    SharedPreferences userPush = getSharedPreferences("userPush",MODE_PRIVATE);
                    String saveUserID = userPush.getString("userID", null);
                    //기존에 저장되어있던 아이디가 있다면 해당 아이디 구독 취소 (로그아웃시 발생가능한 문제)
                    if(saveUserID != null)
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(saveUserID + "userPush");
                    //새로운 아이디로 구독 및 쉐프퍼에 저장
                    SharedPreferences.Editor editor2=userPush.edit();
                    editor2.putString("userID", userID);
                    editor2.apply();
                    FirebaseMessaging.getInstance().subscribeToTopic(userID + "userPush");

                    SharedPreferences userInfo=getSharedPreferences("userInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor=userInfo.edit();
                    editor.putString("userInfo",user.toString());
                    editor.apply();

                    ImageView userRank = findViewById(R.id.userrank);
                    TextView userLevel = findViewById(R.id.userLevel);
                    TextView userGold = findViewById(R.id.mygold);
                    int allGold = (int) user.get("gold") + (int) user.get("maybe");
                    userGold.setText(String.valueOf(allGold));

                    //오늘번돈 상단고정바 셋팅
                    SharedPreferences currentGold=getSharedPreferences("currentGold",MODE_PRIVATE);
                    SharedPreferences.Editor currentGoldEditor=currentGold.edit();
                    currentGoldEditor.putString("currentGold",String.valueOf(allGold));
                    Log.d("todayStartfirst",String.valueOf(allGold));
                    currentGoldEditor.commit();
                    updateTodayEarnedMoney();
                    notifManager = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);
                    notifManager.cancelAll();
                    SharedPreferences notificationOnOffBoolean=getSharedPreferences("notificationOnOffBoolean",MODE_PRIVATE);
                    topBarSetting(notificationOnOffBoolean.getBoolean("OnOff",true));

                    // 이거의 역할은 뭐지...?
                    mTaskList.clear();
                    clearItem();
                    insertTime();

                    //drawView에 userNameSetting
                    TextView userNameDrawer = findViewById(R.id.usernamedrawer);
                    userNameDrawer.setText(String.valueOf(user.get("name")));
                    setUserRankImage(userRank, userLevel, (int)user.get("rank"));
                    ProgressBar progress = findViewById(R.id.mainProgressBar);
                    setUserProgressBar(progress,(int)user.get("rank"), (int)user.get("success_count"));
//                    if(!resultTemp.get("emergency").toString().equals(""))
//                        Toast.makeText(mContext,resultTemp.get("emergency").toString(),Toast.LENGTH_SHORT);

                    //tasklist, examlist 인플레이팅
                    JSONArray examList = (JSONArray) resultTemp.get("exam_data");
                    for (int i = 0; i < examList.length(); i++)
                        mTaskList.add((JSONObject) examList.get(i));
                    JSONArray taskList = (JSONArray) resultTemp.get("task_data");
                    for (int i = 0; i < taskList.length(); i++)
                        mTaskList.add((JSONObject) taskList.get(i));
                    RecyclerView lv_main = findViewById(R.id.taskList);
                    adapter = new ListviewAdapter(getApplicationContext(), R.layout.task_lv, new ArrayList<JSONObject>(),TaskListActivity.this,user);
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
                runningHTTPRequest--;

            }
        };
        CountDownTimer adf= new AsyncTaskCancelTimerTask(asyncTask,Integer.parseInt(getString(R.string.hTTPTimeOut)),100,true,this).start();
        asyncTask.execute(getString(R.string.mainurl) + "/testing/getTaskList", param, loginToken);




    }

    public synchronized void defaultTopBarSetting(String todayMoney){

        Bitmap mLargeIcon= BitmapFactory.decodeResource(getResources(),R.drawable.cashmissioniconround);
        PendingIntent mPendingIntent=PendingIntent.getActivity(
                TaskListActivity.this,
                0,
                new Intent(getApplicationContext(),TaskListActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT

        );


        String channelId = "channel";
        String channelName = "Channel Name";




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.enableVibration(false);

            notifManager.createNotificationChannel(mChannel);

        }

        mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);

        Intent notificationIntent = new Intent(getApplicationContext()

                , TaskListActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent
                = PendingIntent.getActivity(getApplicationContext()

                , requestID

                , notificationIntent

                , PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder
                .setSmallIcon(R.drawable.cashmissioniconround)
                .setContentTitle("오늘 번 돈")
                .setContentText("\uFFE6"+todayMoney)
                .setLargeIcon(mLargeIcon)
                .setOngoing(true)
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setNumber(0)
                .setContentIntent(pendingIntent);

    }
    public synchronized void topBarSetting( boolean notificationFlag){
        if(!notificationFlag)
            return;

        defaultTopBarSetting(updateTodayEarnedMoney());

mBuilder.setNumber(0);
        notifManager.notify(0, mBuilder.build());

    }

    private void setUserRankImage(ImageView userRank, TextView userLevel, int rank) {

        if(rank==1) {
            Glide.with(this).load(R.drawable.main_flag_1).into(userRank);
            userLevel.setText("Lv.01");
        }
        else if(rank==2) {
            Glide.with(this).load(R.drawable.main_flag_2).into(userRank);
            userLevel.setText("Lv.02");
        }
        else if(rank==3) {
            Glide.with(this).load(R.drawable.main_flag_3).into(userRank);
            userLevel.setText("Lv.03");
        }
        else if(rank==151){
            Glide.with(this).load(R.drawable.main_flag151).into(userRank);
            userLevel.setText("Lv.99");
        }
        else {
            Glide.with(this).load(R.drawable.main_flag_1).into(userRank);
            userLevel.setText("Lv.00");
        }
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

    @Override
    protected void onStop(){
        super.onStop();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void getForcedUpdateDialog(String title, String value)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskListActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                forcedUpdate();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }
}