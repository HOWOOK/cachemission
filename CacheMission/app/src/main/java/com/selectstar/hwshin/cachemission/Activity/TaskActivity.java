package com.selectstar.hwshin.cachemission.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_2DBox;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_TwoPoint;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Photo;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithBox;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithLine;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.sum;


public class TaskActivity extends PatherActivity {

    View controllerView;
    Controller mController;
    String buttons;
    Uri photoUri;
    Dialog explainDialog;
    ImageView backButton;
    ArrayList<String> pic=new ArrayList<>();
    //사투리특별전용옵션
    static String region_dialect;
    String questString="";
    int currentIndex=0;
    private TextView answerIDtv;


    public TaskView getmTaskView() {
        return this.mTaskView;
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        setQuestList(intent.getStringExtra("questList"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName(taskType+"TaskActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        setContentView(R.layout.activity_task);

        //캡쳐방지
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        final TextView optionText = findViewById(R.id.optionText);
        answerIDtv = findViewById(R.id.answerID);
        nowGold = findViewById(R.id.goldnow);
        pendingGold = findViewById(R.id.goldpre);
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        setLoginToken(token.getString("loginToken",null));

        /*
         *intent로 부터 받아와야할 것 :   1. 어떤 controller를 사용하는지
         * 2. 어떤 taskview를 사용하는지  3. 두개의 constraint 관계는 어떤지
         */
        explainDialog = new Dialog(this);
        intent = getIntent();
        upGold = Integer.parseInt(intent.getStringExtra("upGold").substring(1)); //string(0)은 \표시
        gold =intent.getStringExtra("goldNow");
        maybe = intent.getStringExtra("goldPre");
        nowGold.setText("현재 : \uFFE6 " + gold);
        pendingGold.setText("예정 : \uFFE6 " + maybe);
        uiHashMap = new UIHashMap();
        taskID = (String)intent.getStringExtra("taskId");
        mTaskView =  uiHashMap.taskViewHashMap.get(intent.getStringExtra("taskView"));
        mTaskView.setParentActivity(this);
        mController =  uiHashMap.controllerHashMap.get(intent.getStringExtra("controller"));
        findViewById(R.id.howbtn).bringToFront();
        mParameter =  (int[][]) uiHashMap.taskHashMap.get(intent.getStringExtra("taskType"));
        taskTitle = intent.getStringExtra("taskTitle");
        if(intent.hasExtra("buttons"))
            buttons= intent.getStringExtra("buttons");
        TextView mTaskTitle = findViewById(R.id.tasktitletext);
        mTaskTitle.setText(taskTitle);
        taskViewID = mTaskView.taskViewID;
        controllerID = mController.controllerID;

        taskType = intent.getStringExtra("taskType");
        questString=intent.getStringExtra("questList");

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
//        constraintSet.connect(R.id.textAnimation, ConstraintSet.BOTTOM, R.id.controller, ConstraintSet.BOTTOM);
//        constraintSet.connect(R.id.textAnimation, ConstraintSet.LEFT, R.id.sendbtn, ConstraintSet.RIGHT);
        constraintSet.applyTo(constraintLayout);

        //TaskView의 weight설정 (default == 10)
        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();
        params1.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params1);
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) parent2.getLayoutParams();
        params2.verticalWeight = mParameter[5][0];
        parent2.setLayoutParams(params2);


        //해당 task가 처음이라면 설명서 띄워주는 것
        //지금은 임시방편으로 new description activity인 것들은///이거 안하게함 모르겠다


        // 현재는 showDescription 에서 기존의 설명서를 쓰는 테스크들의 경우만 띄워주고 새로 바뀐 형태의 설명서를 사용하는 테스크들의 경우 아무것도 하지 않게 조정함
        //if(!(taskType.equals("BOXCROP")||taskType.equals("PHOTO")))
        showDescription();

        controllerView = findViewById(R.id.controller);
        mController.setParentActivity(this);
        mController.setLayout(controllerView,  taskID);

        // boxcrop이면 파트 선택되고나서 로딩해야함
        // reccord, dialect, directrecord면 지역 선택되고나서 로딩해야함, 물론 지역선택 예전에 해놨으면 바로 테스크 시작될 거임
        if(!(taskType.equals("BOXCROP")||taskType.equals("RECORD")||taskType.equals("DIALECT")||taskType.equals("DIRECTRECORD")))
            startTask();

        //boxcrop이면 partSelectDialog를 띄워줘야한다.
        if((taskType.equals("BOXCROP"))){
            findViewById(R.id.option).setBackgroundColor(this.getResources().getColor(R.color.colorDark2));
            ((TextView) findViewById(R.id.optionText)).setTextColor(this.getResources().getColor(R.color.colorPrimary));
            partDialogShow(optionText);
        }

        //DIALECT, RECOR, DIRECTRECORD이면 regionSelectDialog를 띄워줘야한다.
        String regionText;
        SharedPreferences tasktoken = getSharedPreferences("taskToken", MODE_PRIVATE);
        SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
        regionText = explain.getString("region", null);
        if((taskType.equals("DIALECT") || taskType.equals("RECORD") || taskType.equals("DIRECTRECORD"))){
            if(regionText != null) {
                optionText.setText(regionText);
                startTask();
            }else
                regionDialogShow(optionText);
        }

        //사투리 테스크는 옵션 눌러서 변경가능함
        optionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((taskType.equals("DIALECT") || taskType.equals("RECORD") || taskType.equals("DIRECTRECORD")))
                    regionDialogShow(optionText);
            }
        });


        //물음표버튼누르면 설명서 띄워주는것
        ImageView howbtn = findViewById(R.id.howbtn);
        howbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_taskExplain;
                Log.d("boxbox",taskType);

                if(taskType.equals("BOXCROP")||taskType.equals("PHOTO")){
                    intent_taskExplain = new Intent(TaskActivity.this, NewExplainActivity.class);
                    intent_taskExplain.putExtra("part", optionText.getText());
                    intent_taskExplain.putExtra("partNum", partType());
                    intent_taskExplain.putExtra("taskID", taskID);
                    System.out.println("shibal"+taskID);
                    intent_taskExplain.putExtra("loginToken", getLoginToken());

                    System.out.println("가져온 텍스트 : "+optionText.getText());

                }else{
                    intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
                }

                intent_taskExplain.putExtra("taskType", taskType);
                startActivity(intent_taskExplain);

            }
        });
    }

    public int getAvailableCount(){
        JSONArray questList= null;
        int count=0;
        try {
            questList = new JSONArray(questString);
            JSONObject quest=(JSONObject) questList.get(0);
            count=(int)quest.get("questTotal")-(int)quest.get("questDone");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //        Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoUri = mController.getPhotoUri();
            ((Controller_Photo) mController).addPhoto(photoUri);
        }
        if(requestCode==999&& resultCode == RESULT_OK){
            pic= data.getStringArrayListExtra("result");
            for(int i=0;i<pic.size();i++){
                Uri uri=Uri.parse(pic.get(i));
                ((Controller_Photo)mController).addPhoto(uri);
            }

        }


    }

    @Override
    public void onBackPressed() {
        //박스 테스크의 경우 확대 잘못하면 취소 눌렀을 때 다시 확대 할수있도록 해야함
        if(taskType.equals("BOXCROP")){
            TaskView_PhotoWithBox taskView = (TaskView_PhotoWithBox) mTaskView;
            Controller_2DBox controller = (Controller_2DBox) mController;
            TextView partText = findViewById(R.id.optionText);
            if(!taskView.expandFlag && taskView.getPhotoView()!=null){
                taskView.expandFlag = true;
                controller.getPinButton().setBackgroundResource(R.drawable.twodbox_icon_pin_on);
                controller.pinFlag = true;
                taskView.getPhotoView().setScale(1);
                findViewById(R.id.boxCL).setVisibility(View.INVISIBLE);
                findViewById(R.id.textDragCL).setVisibility(View.VISIBLE);
                findViewById(R.id.textDragCL).bringToFront();
            } else if(taskView.expandFlag){
                partText.setText("");
                partDialogShow(partText);
                taskView.removeAnswer();
            }else{
                super.onBackPressed();
            }
        }else if(taskType.equals("TWOPOINT")){
            TaskView_PhotoWithLine taskView = (TaskView_PhotoWithLine) mTaskView;
            Controller_TwoPoint controller = (Controller_TwoPoint) mController;
            if(!controller.firstPointFlag && controller.secondPointFlag){//한점만 쳐진경우
                controller.firstPointFlag = true;
                controller.secondPointFlag = false;
                ((TextView) findViewById(R.id.textDrag)).setText("라인의 시작점을 지정해 주세요.");
                if(controller.firstPoint.getVisibility() == View.VISIBLE)
                    controller.firstPoint.setVisibility(View.INVISIBLE);
                if(controller.firstPointTouch.getVisibility() == View.VISIBLE)
                    controller.firstPointTouch.setVisibility(View.INVISIBLE);
                ((Controller_TwoPoint.LineView) controller.pointLine).pointReset();

            }else if(!controller.firstPointFlag && !controller.secondPointFlag){//두점이 다 쳐진경우
                controller.firstPointFlag = false;
                controller.secondPointFlag = true;
                if(controller.secondPoint.getVisibility() == View.VISIBLE)
                    controller.secondPoint.setVisibility(View.INVISIBLE);
                if(controller.secondPointTouch.getVisibility() == View.VISIBLE)
                    controller.secondPointTouch.setVisibility(View.INVISIBLE);
                if(((ConstraintLayout) findViewById(R.id.textDragCL)).getVisibility() == View.INVISIBLE)
                    ((ConstraintLayout) findViewById(R.id.textDragCL)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textDrag)).setText("라인의 끝점을 지정해 주세요.");
                ((Controller_TwoPoint.LineView) controller.pointLine).samePointSetting(
                        ((Controller_TwoPoint.LineView) controller.pointLine).getLines()[0],
                        ((Controller_TwoPoint.LineView) controller.pointLine).getLines()[1]);

            }else{
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void getNewTask(){
        JSONObject param = new JSONObject();
        try {
            param.put("taskID", taskID);
            if(taskType.equals("BOXCROP")){//BOXCROP에서는 파트를 넣어서 요청해야함
                partNum = partType();
                param.put("option",partNum);
            }
            if(taskType.equals("TWOPOINT")){//TWOPOINT에서는 파트(11=전선)를 넣어서 요청해야함
                param.put("option",11);
            }
            if(taskType.equals("RECORD")){//RECORD일때는 지역을 같이 넣어서 요청해야함
                String region;
                region = ((TextView)findViewById(R.id.optionText)).getText().toString();
                param.put("option", region);
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
                            answerID = currentTask.getString("id");
                            if(answerID != null)
                                answerIDtv.setText("Answer ID : " + answerID);
                            System.out.println("컨텐츠 : "+ currentTask.get("content"));
                            System.out.println("------------");
                            mTaskView.setContent((String) currentTask.get("content"));
                            answerID = ((Integer)currentTask.get("id")).toString();
                            mController.resetContent(controllerView, taskID);

                        } else {
                            new ServerMessageParser().taskGetFailParse(TaskActivity.this, resultTemp);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }.execute(getString(R.string.mainurl) + "/taskGet", param, getLoginToken());
        } catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void startTask(){
        try {
            String key = taskID;
            if(taskType.contains("BOXCROP"))
                key = key +"/"+ String.valueOf(partType());
            else
                key = key + "/-1";
            if(taskType.contains("EXAM"))
                key = key + "/" + String.valueOf(examType);
            else
                key = key + "/-1";
            waitingTasks = JSONtoArray(new JSONArray(getPreference("waitingTasks",key)));
            if(waitingTasks.size()>0) {
                if (timeCheck(((JSONObject) waitingTasks.get(0)).get("time").toString())) {
                    if(mTaskView.isEmpty())
                        mTaskView.setPreviewContents(waitingTasks);
                    currentTask = (JSONObject)waitingTasks.get(waitingTasks.size()-1);
                    String answerID = currentTask.getString("id");
                    if(answerID != null)
                        answerIDtv.setText("Answer ID : " + answerID);
                    answerID = currentTask.get("id").toString();
                    mTaskView.setContent((String) currentTask.get("content"));
                    mController.resetContent(controllerView,taskID);
                }else{
                    getNewTask();
                }
            }else{
                getNewTask();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    //해당 task가 처음이라면 설명서 띄워주는 것
    public void showDescription(){
        SharedPreferences taskToken = getSharedPreferences("taskToken", MODE_PRIVATE);
        if(taskToken.getInt(taskType + "taskToken",0) == 100)
            return;
        Intent intent_taskExplain;
        TextView partText = findViewById(R.id.optionText);
        Log.d("boxbox",taskType);
        if(taskType.equals("PHOTO")){
            intent_taskExplain = new Intent(TaskActivity.this, NewExplainActivity.class);
            intent_taskExplain.putExtra("part", partText.getText());
            intent_taskExplain.putExtra("partNum", partType());
            intent_taskExplain.putExtra("taskID", taskID);
            intent_taskExplain.putExtra("taskType", taskType);

            System.out.println("shibal"+taskID);
            intent_taskExplain.putExtra("loginToken", getLoginToken());
            System.out.println("가져온 텍스트 : "+partText.getText());
            startActivity(intent_taskExplain);
        }else if(taskType.equals("BOXCROP")){

        }
        else{
            intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
            intent_taskExplain.putExtra("taskType", taskType);
            startActivity(intent_taskExplain);
        }

    }

    public void setQuestList(String questString)
    {

        try {
            final TextView questText=findViewById(R.id.questText);
            JSONArray questList=new JSONArray(questString);
            JSONArray parsedQuestList=parseQuestList(questList);
            final JSONObject questName=(JSONObject) parsedQuestList.get(0);
            final JSONObject questReward=(JSONObject) parsedQuestList.get(1);
            if(questName.length()>0) {

                if((int)questReward.get(String.valueOf(currentIndex))!=0) {
                    questText.setText(questName.get(String.valueOf(currentIndex)).toString() + "+\uFFE6" + String.valueOf(questReward.get(String.valueOf(currentIndex))));
                }else{
                    questText.setText(questName.get(String.valueOf(currentIndex)).toString() );
                }

                questText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentIndex+1 < questName.length()) {
                            currentIndex++;
                        } else {
                            currentIndex = 0;
                        }
                        try {
                            if((int)questReward.get(String.valueOf(currentIndex))!=0) {
                                questText.setText(questName.get(String.valueOf(currentIndex)).toString() + "+\uFFE6" + String.valueOf(questReward.get(String.valueOf(currentIndex))));
                            }else{
                                questText.setText(questName.get(String.valueOf(currentIndex)).toString() );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String parseDailyQuest(String rawText) {
        if(rawText.contains("\n"))
        {
            rawText = rawText.replace("\n"," (");
            rawText = rawText + ")";
        }
        return rawText;
    }

}
