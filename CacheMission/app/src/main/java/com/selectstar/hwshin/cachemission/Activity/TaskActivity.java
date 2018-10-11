package com.selectstar.hwshin.cachemission.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
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
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Photo;
import com.selectstar.hwshin.cachemission.DataStructure.PartSelectDialog;
import com.selectstar.hwshin.cachemission.DataStructure.PartSelectDialogListener;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoView;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class TaskActivity extends PatherActivity {

    View controllerView;
    Controller mController;
    String buttons;
    Uri photoUri;
    Dialog explainDialog;
    ImageView backButton;
    public TextView taskCount;
    ArrayList<String> pic=new ArrayList<>();
    //사투리특별전용옵션
    static String region_dialect;
    int currentIndex=0;



    public TaskView getmTaskView() {
        return mTaskView;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //사투리선택해야하는 테스크면 선택하게 만들어야함
        String region;
        SharedPreferences tasktoken = getSharedPreferences("taskToken", MODE_PRIVATE);
        SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
        region = explain.getString("region", null);
        if ((taskType.equals("DIALECT") || taskType.equals("RECORD") || taskType.equals("DIRECTRECORD")) && tasktoken.getInt(taskType + "taskToken", 0) == 100 && region == null) {
            Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
            intent_region.putExtra("wanttochange", "false");
            startActivity(intent_region);
        }
    }

    private void partDialogShow(TextView partText) {
        final TextView partTextTemp = partText;
        PartSelectDialog dialog = new PartSelectDialog(this, R.style.AppTheme_Transparent_Dialog);
        dialog.setDialogListener(new PartSelectDialogListener() {
            @Override
            public void onPartPoleClicked() {
                partTextTemp.setText("전신주");
                startTask();
            }

            @Override
            public void onPartTreeClicked() {
                partTextTemp.setText("나무");
                startTask();
            }

            @Override
            public void onPartTransformerClicked() {
                partTextTemp.setText("변압기");
                startTask();
            }
        });
        dialog.show();
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
                questText.setText(questName.get(String.valueOf(0)).toString() + String.valueOf(questReward.get(String.valueOf(0))));


                questText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentIndex+1 < questName.length()) {
                            currentIndex++;
                        } else {
                            currentIndex = 0;
                        }
                        try {
                            questText.setText(questName.get(String.valueOf(currentIndex)).toString() + String.valueOf(questReward.get(String.valueOf(currentIndex))));
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
    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);

        //지역 재 선택을 위한 인터페이스
        if (taskType.equals("DIALECT") || taskType.equals("RECORD") || taskType.equals("DIRECTRECORD")) {
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
        setQuestList(intent.getStringExtra("questList"));


    }

    public void getNewTask()
    {
        JSONObject param = new JSONObject();
        try {
            param.put("taskID", taskID);
            if(taskType.equals("BOXCROP")){//BOXCROP에서는 파트를 넣어서 요청해야함
                int ans = partType();
                param.put("partNum",null);
            }
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
                            System.out.println("컨텐츠 : "+ currentTask.get("content"));
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
            }.execute(getString(R.string.mainurl) + "/testing/taskGet", param, getLoginToken());
        } catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private int partType() {
        int answer = -1;
        TextView partType = findViewById(R.id.partText);
        if(partType.getText().equals("전신주"))
            answer = 2;
        if(partType.getText().equals("나무"))
            answer = 3;
        if(partType.getText().equals("변압기"))
            answer = 4;

        return answer;
    }

    @Override
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

    //데일리 퀘스트 관련
    public int getTaskCount()
    {
        String string = taskCount.getText().toString();
        int startPoint=-1;
        int midPoint=-1;
        int endPoint=-1;
        for(int i=0;i<string.length();i++)
        {
            if(string.charAt(i)=='[')
                startPoint=i;
            if(string.charAt(i)=='/')
                midPoint=i;
            if(string.charAt(i)==']')
                endPoint=i;
        }
        int a = Integer.parseInt(string.substring(startPoint+1,midPoint));
        int b = Integer.parseInt(string.substring(midPoint+1,endPoint));
        return b-a;
    }


    //해당 task가 처음이라면 설명서 띄워주는 것
    public void showDescription()
    {
        SharedPreferences taskToken = getSharedPreferences("taskToken", MODE_PRIVATE);
        if(taskToken.getInt(taskType + "taskToken",0) == 100)
            return;
        Intent intent_taskExplain;
        TextView partText = findViewById(R.id.partText);
        Log.d("boxbox",taskType);
        if(taskType.equals("BOXCROP")){
            intent_taskExplain = new Intent(TaskActivity.this, NewExplainActivity.class);
            intent_taskExplain.putExtra("part", partText.getText());
            System.out.println("가져온 텍스트 : "+partText.getText());
        }else{
            intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
        }
        intent_taskExplain.putExtra("taskType", taskType);
        startActivity(intent_taskExplain);
    }


    public int setDailyQuest(String rawText)
    {
        if(rawText.equals("-1")) {
            int startPoint = -1, endPoint = -1, midPoint = -1,bonusPoint = -1;
            String a = taskCount.getText().toString();
            rawText = a;
            for (int i = 0; i < rawText.length(); i++) {
                if (rawText.charAt(i) == '[')
                    startPoint = i;
                if (rawText.charAt(i) == ']')
                    endPoint = i;
                if (rawText.charAt(i) == '/')
                    midPoint = i;
                if (rawText.charAt(i) == '(')
                    bonusPoint = i;
            }
            System.out.println("~!~!!~!");
            System.out.println(startPoint);
            System.out.println(endPoint);
            System.out.println(midPoint);
            System.out.println(bonusPoint);

            if (startPoint == -1 || endPoint == -1 || midPoint == -1 || bonusPoint == -1)
                return 0;

            if (startPoint >= endPoint)
                return 0;
            int bonusGold = Integer.parseInt(getNumberInString(rawText.substring(bonusPoint+1)));
            int count = Integer.parseInt(rawText.substring(startPoint+1,midPoint));
            int allCount = Integer.parseInt(rawText.substring(midPoint+1,endPoint));
            if(count + 1 == allCount)
                return bonusGold+getUpGold();
            else
                rawText.replace(String.valueOf(count),String.valueOf(count+1));

        }
        taskCount.setText(parseDailyQuest(rawText));
        return 0;
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
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("TaskActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
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
        setLoginToken(token.getString("loginToken",null));

        /*
         *intent로 부터 받아와야할 것 :   1. 어떤 controller를 사용하는지
         * 2. 어떤 taskview를 사용하는지  3. 두개의 constraint 관계는 어떤지
         */
        explainDialog = new Dialog(this);
        intent = getIntent();
        upGold = Integer.parseInt(intent.getStringExtra("upGold").substring(1));
        gold =intent.getStringExtra("goldNow");
        maybe = intent.getStringExtra("goldPre");
        nowGold.setText("현재 : \uFFE6 " + gold);
        pendingGold.setText("예정 : \uFFE6 " + maybe);
        if(intent.hasExtra("daily"))
            taskCount.setText(parseDailyQuest(intent.getStringExtra("daily")));
        uiHashMap = new UIHashMap();
        taskID = (String)intent.getStringExtra("taskId");
        mTaskView =  uiHashMap.taskViewHashMap.get(intent.getStringExtra("taskView"));
        mTaskView.setParentActivity(this);
        mController =  uiHashMap.controllerHashMap.get(intent.getStringExtra("controller"));
        mParameter =  (int[][]) uiHashMap.taskHashMap.get(intent.getStringExtra("taskType"));
        taskTitle = intent.getStringExtra("taskTitle");
        if(intent.hasExtra("buttons"))
            buttons= intent.getStringExtra("buttons");
        TextView mTaskTitle = findViewById(R.id.tasktitletext);
        mTaskTitle.setText(taskTitle);
        taskViewID = mTaskView.taskViewID;
        controllerID = mController.controllerID;
//        taskViewID = mTaskView.taskViewID;
//        controllerID = mController.controllerID;

        taskType = intent.getStringExtra("taskType");

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

        controllerView = findViewById(R.id.controller);
        mController.setParentActivity(this);
        mController.setLayout(controllerView,  taskID);
        if(!taskType.equals("BOXCROP")) //boxcrop이면 파트 선택되고나서 로딩해야함
            startTask();

        final TextView partText = findViewById(R.id.partText);
        if((taskType.equals("BOXCROP"))){
            findViewById(R.id.option).setBackgroundColor(this.getResources().getColor(R.color.colorBlack2));
            partDialogShow(partText);
        }

        //물음표버튼누르면 설명서 띄워주는것
        ImageView howbtn = findViewById(R.id.howbtn);
        howbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_taskExplain;
                Log.d("boxbox",taskType);
                if(taskType.equals("BOXCROP")){
                    intent_taskExplain = new Intent(TaskActivity.this, NewExplainActivity.class);
                    intent_taskExplain.putExtra("part", partText.getText());
                    System.out.println("가져온 텍스트 : "+partText.getText());
                }else{
                    intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
                }

                intent_taskExplain.putExtra("taskType", taskType);
                startActivity(intent_taskExplain);
            }
        });
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
            TaskView_PhotoView temp = (TaskView_PhotoView) mTaskView;
            Controller_2DBox temp2 = (Controller_2DBox) mController;
            if(!temp.expandFlag && temp.getPhotoView()!=null){
                temp.expandFlag = true;
                temp2.getPinButton().setBackgroundResource(R.drawable.twodbox_icon_pin_on);
                temp2.pinFlag = true;
                temp.getPhotoView().setScale(1);
                findViewById(R.id.boxCL).setVisibility(View.INVISIBLE);
                findViewById(R.id.textDragCL).setVisibility(View.VISIBLE);
                findViewById(R.id.textDragCL).bringToFront();
            } else{
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

}
