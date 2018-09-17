package com.selectstar.hwshin.cachemission.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Photo;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class TaskActivity extends AppCompatActivity {

    Intent intent;
    int controllerID, taskViewID;
    String mId;
    View controllerView;
    TaskView mTaskView;
    Controller mController;
    int[][] mParameter;
    String tempsrcURI = "foobar";
    String tasktitle;
    String buttons;
    Uri photoUri;
    private UIHashMap uiHashMap;
    private JSONObject currentTask;
    String answerID;
    String taskType;
    Dialog explainDialog;
    ImageView backButton;
    TextView nowGold;
    TextView pendingGold;
    TextView taskCount;
    String taskID;
    String loginToken;
    private int upGold;
    private String gold;
    private String maybe;
    private ArrayList<JSONObject> waitingTasks;
    public int getUpGold()
    {
        return upGold;
    }
    public void setGold(String value)
    {
        if(value=="-1")
        {
            value = String.valueOf(Integer.parseInt(gold) + upGold);
        }
        gold = value;
        nowGold.setText("현재 : \uFFE6 " + gold);
    }
    public void setMaybe(String value)
    {
        maybe = value;
        pendingGold.setText("예정 : \uFFE6 " + maybe);
    }
    public void deleteWaitingTasks()
    {
        savePreference("waitingTasks",taskType,new JSONArray().toString());
    }
    public void updateWaitingTasks()
    {
        waitingTasks.remove(waitingTasks.size()-1);
        savePreference("waitingTasks",taskType,ARRAYtoJSON(waitingTasks).toString());
    }
    //사투리특별전용옵션
    static String region_dialect;
    public String getPreference(String title,String key)
    {
        SharedPreferences SPF = getSharedPreferences(title, MODE_PRIVATE);
        return SPF.getString(key, (new ArrayList<JSONObject>()).toString());
    }
    private static Date addMinutesToDate(int minutes, Date beforeTime){
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }
    public void savePreference(String title, String key, String value)
    {

        SharedPreferences SPF = getSharedPreferences(title, MODE_PRIVATE);
        SharedPreferences.Editor editor = SPF.edit();
        editor.putString(key, value);
        editor.commit();

    }
    public String getAnswerID() {
        try {
            return currentTask.get("id").toString();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            return "-1";
        }
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

    public TaskView getmTaskView() {
        return mTaskView;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //사투리선택해야하는 테스크면 선택하게 만들어야함
        String region;
        SharedPreferences tasktoken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
        region = explain.getString("region", null);
        if ((taskType.equals("DIALECT") || taskType.equals("RECORD")) && tasktoken.getInt(taskType + "tasktoken", 0) == 100 && region == null) {
            Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
            intent_region.putExtra("wanttochange", "false");
            startActivity(intent_region);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //지역 재 선택을 위한 인터페이스
        if (taskType.equals("DIALECT") || taskType.equals("RECORD")) {
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
                    intent_region.putExtra("wanttochange", "true");
                    startActivity(intent_region);
                }
            });
        }


    }

    public void getNewTask()
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
            new HurryHttpRequest(this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    JSONObject resultTemp = null;
                    try {
                        resultTemp = new JSONObject(result);
                        System.out.println(result);
                        if ((boolean) resultTemp.get("success")) {
                            waitingTasks = new ArrayList<>();
                            JSONArray tempTasks = (JSONArray)resultTemp.get("answers");
                            for(int i=0;i<tempTasks.length();i++)
                                waitingTasks.add((JSONObject)tempTasks.get(i));
                            mTaskView.setPreviewContents(waitingTasks);
                            Date after28time = addMinutesToDate(28,new Date());
                            ((JSONObject)waitingTasks.get(0)).put("time",DateToString(after28time));
                            currentTask = waitingTasks.get(waitingTasks.size()-1);
                            System.out.println((String)currentTask.get("content"));
                            System.out.println("------------");
                            mTaskView.setContent((String) currentTask.get("content"));
                            answerID = ((Integer)currentTask.get("id")).toString();
                            mController.resetContent(controllerView,taskID);

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
            }.execute(getString(R.string.mainurl) + "/newTaskGet", param, loginToken);
        } catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    private String DateToString(Date from) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String to = transFormat.format(from);
        return to;
    }
    private Date StringToDate(String from)
    {
        try {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date to = transFormat.parse(from);
            return to;
        }
        catch(Exception e)
        {
        }
        return null;
    }
    private boolean timeCheck(String x)
    {
        System.out.println(StringToDate(x));
        System.out.println(new Date());
        if(StringToDate(x).after(new Date()))
            return true;
        System.out.println("??");
        return false;
    }
    // 새로운 테스크(베이스)를 받을 때 호출하는 함수
    private JSONArray ARRAYtoJSON(ArrayList<JSONObject> list)
    {
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<list.size();i++)
            jsonArray.put(list.get(i));
        return jsonArray;
    }
    private ArrayList<JSONObject> JSONtoArray(JSONArray jsonArray)
    {
        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++)
                list.add((JSONObject) jsonArray.get(i));
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public void startTask()
    {
        try {
            waitingTasks = JSONtoArray(new JSONArray(getPreference("waitingTasks",taskType)));
            if(waitingTasks.size()>0) {
                if (timeCheck(((JSONObject) waitingTasks.get(0)).get("time").toString())) {
                    if(mTaskView.isEmpty())
                        mTaskView.setPreviewContents(waitingTasks);
                    currentTask = (JSONObject)waitingTasks.get(waitingTasks.size()-1);
                    answerID = currentTask.get("id").toString();
                    mTaskView.setContent((String) currentTask.get("content"));
                    mController.resetContent(controllerView,taskID);
                }
                else
                {
                    getNewTask();
                }
            }
            else
            {
                getNewTask();
            }
        }catch(JSONException e)
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
    public void setDailyQuest(String rawText)
    {
        if(rawText=="-1") {
            int startPoint = -1, endPoint = -1, midPoint = -1;
            for (int i = 0; i < rawText.length(); i++) {
                if (rawText.charAt(i) == '[')
                    startPoint = i;
                if (rawText.charAt(i) == ']')
                    endPoint = i;
                if (rawText.charAt(i) == '/')
                    midPoint = i;
            }
            if (startPoint == -1 || endPoint == -1 || midPoint == -1)
                return;
            if (startPoint >= endPoint)
                return;
            int count = Integer.parseInt(rawText.substring(startPoint+1,midPoint));
            int allCount = Integer.parseInt(rawText.substring(midPoint+1,endPoint));
            if(count + 1 == allCount)
                return;
            else
                rawText.replace(String.valueOf(count),String.valueOf(count+1));

        }
        taskCount.setText(parseDailyQuest(rawText));
    }
    private String parseDailyQuest(String rawText) {
            if(rawText.contains("\n"))
        {
            rawText = rawText.replace("\n"," (");
            rawText = rawText + ")";
        }
        return rawText;
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
        backButton = findViewById(R.id.back);
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
        upGold = Integer.parseInt(intent.getStringExtra("upgold").substring(2));
        gold =intent.getStringExtra("goldnow");
        maybe = intent.getStringExtra("goldpre");
        nowGold.setText("현재 : \uFFE6 " + gold);
        pendingGold.setText("예정 : \uFFE6 " + maybe);
        if(intent.hasExtra("daily"))
            taskCount.setText(parseDailyQuest(intent.getStringExtra("daily")));
        uiHashMap = new UIHashMap();
        mId=(String)intent.getStringExtra("taskid");
        taskID = (String)intent.getStringExtra("taskid");
        mTaskView =  uiHashMap.taskViewHashMap.get(intent.getStringExtra("taskview"));
        mTaskView.setParentActivity(this);
        mController =  uiHashMap.controllerHashMap.get(intent.getStringExtra("controller"));
        mParameter =  (int[][]) uiHashMap.taskHashMap.get(intent.getStringExtra("tasktype"));
        tasktitle = intent.getStringExtra("tasktitle");
        buttons= intent.getStringExtra("buttons");
        TextView mtasktitle = findViewById(R.id.tasktitletext);
        mtasktitle.setText(tasktitle);
        taskViewID = mTaskView.taskViewID;
        controllerID = mController.controllerID;
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
        constraintSet.connect(R.id.textAnimation, ConstraintSet.BOTTOM, R.id.controller, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.textAnimation, ConstraintSet.LEFT, R.id.sendbtn, ConstraintSet.RIGHT);
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
        mController.setParentActivity(this);
        mController.setLayout(controllerView,  taskID);
        startTask();

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
    //        Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            System.out.println(photoUri);
            ((Controller_Photo) mController).addPhoto(photoUri);
        }

    }
}
