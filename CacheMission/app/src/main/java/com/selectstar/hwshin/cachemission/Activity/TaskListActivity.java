package com.selectstar.hwshin.cachemission.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
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
    public int runningHTTPRequest=0;
    NotificationCompat.Builder mBuilder;
    NotificationManager notifManager = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);


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
    public void clearListForAccident(){
        if(runningHTTPRequest==0)
        mTaskList.clear();
        refreshButton.setVisibility(View.VISIBLE);
        refreshText.setVisibility(View.VISIBLE);


    }
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

        editor.putString("listInfoData","[]");
        editor.apply();


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
        TextView exchangeBtn = findViewById(R.id.exchange);
        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runningHTTPRequest++;
                SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
                final String loginToken = token.getString("loginToken","");
                JSONObject param = new JSONObject();
                WaitHttpRequest asyncTask=new WaitHttpRequest(mContext) {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        try {
                            if (result == "")
                                return;
                            JSONObject resultTemp = new JSONObject(result);
                            String url=resultTemp.get("url").toString();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);



                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                        runningHTTPRequest--;

                    }
                };
                //CountDownTimer adf= new AsyncTaskCancelTimerTask(asyncTask,Integer.parseInt(getString(R.string.hTTPTimeOut)),1000,true,this).start();
                asyncTask.execute(getString(R.string.mainurl) + "/testing/exchange", param, loginToken);



                Intent intent_exchange= new Intent(TaskListActivity.this, ExchangeActivity.class);
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT) ;
                }
               // startActivity(intent_exchange);
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
        if(!isNetworkConnected(this)){
            Toast.makeText(this,"네트워크연결이 끊어졌습니다. 연결상태를 확인하고 앱을 다시 실행해 주세요.",Toast.LENGTH_SHORT).show();
            clearListForAccident();
            return;
        }
        runningHTTPRequest++;
        try {
            JSONObject param = new JSONObject();
            param.put("taskID", mTaskList.get(i).get("id"));
            String taskType = mTaskList.get(i).get("taskType").toString();
            int examType = (int) mTaskList.get(i).get("examType");
            if(taskType.contains("EXAM")){
                param.put("examType", examType);
            };
            final JSONObject pp = param;
            HurryHttpRequest asyncTask = new HurryHttpRequest(TaskListActivity.this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {
                        JSONObject resultTemp = new JSONObject(result);
                        if(resultTemp.get("success").toString().equals("true")) {
                            if (isNew) {
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
                    JSONObject user = (JSONObject)resultTemp.get("user");

                    SharedPreferences userInfo=getSharedPreferences("userInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor=userInfo.edit();
                    editor.putString("userInfo",user.toString());
                    editor.apply();

                    ImageView userRank = findViewById(R.id.userrank);
                    TextView userLevel = findViewById(R.id.userLevel);
                    TextView userGold = findViewById(R.id.mygold);
                    int allGold = (int) user.get("gold") + (int) user.get("maybe");
                    userGold.setText(String.valueOf(allGold));
                    TextView userNameDrawer = findViewById(R.id.usernamedrawer);
                    mTaskList.clear();
                    clearItem();
                    insertTime();
                    userNameDrawer.setText(String.valueOf(user.get("name")));
                    setUserRankImage(userRank, userLevel, (int)user.get("rank"));
                    ProgressBar progress = findViewById(R.id.mainProgressBar);
                    setUserProgressBar(progress,(int)user.get("rank"), (int)user.get("success_count"));
//                    if(!resultTemp.get("emergency").toString().equals(""))
//                        Toast.makeText(mContext,resultTemp.get("emergency").toString(),Toast.LENGTH_SHORT);
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

                    System.out.println("붸뷉"+mTaskList.size());
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
    public synchronized void topBarSetting(String todayMoney,boolean notificationFlag){

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

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

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
                .setContentTitle("캐시미션")
                .setContentText("오늘번돈 : "+R.string.wonunicode+todayMoney)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setLargeIcon(mLargeIcon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);

        notifManager.notify(0, mBuilder.build());

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // initiateListInfo();
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("TaskListActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        setContentView(R.layout.activity_tasklist);
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
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        final String loginToken = token.getString("loginToken","");
        if(checkIfTimePassed())
            getTaskList(loginToken);
        else{
            getTaskList(loginToken);
        }

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
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}