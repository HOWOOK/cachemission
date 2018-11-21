package com.selectstar.hwshin.cachemission.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
import com.selectstar.hwshin.cachemission.Dialog.RegionSelectDialog;
import com.selectstar.hwshin.cachemission.Dialog.RegionSelectDialogListener;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class PatherActivity extends AppCompatActivity {

    protected int controllerID, taskViewID;
    protected JSONObject currentTask;
    public String answerID;
    protected String taskID;
    protected int[][] mParameter;
    public String getTaskID() {
        return taskID;
    }
    protected String taskTitle;
    protected String taskType;
    protected String taskDifficulty;
    public String getTaskType() {
        return taskType;
    }
    protected int examType;
    public int getExamType() { return  examType; }
    protected TaskView mTaskView;
    protected UIHashMap uiHashMap;
    private String loginToken;
    protected Intent intent;
    protected String gold;
    protected String maybe;
    protected ArrayList<JSONObject> waitingTasks;
    protected TextView nowGold;
    protected TextView pendingGold;
    protected int upGold;
    protected int partNum=-1;
    String questString="";
    int currentIndex=0;

    public int getPartNum() {
        return partNum;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }

    public TaskView getmTaskView() {
        return this.mTaskView;
    }

    public String getTaskDifficulty(){return  this.taskDifficulty;}

    public int getUpGold()
    {
        return upGold;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        if(loginToken == null)
            loginToken = "";
        this.loginToken = loginToken;
    }

    public abstract void startTask();
    public abstract void getNewTask();

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences explain = getSharedPreferences("region", Context.MODE_PRIVATE);
        SharedPreferences tasktoken = getSharedPreferences("taskToken", MODE_PRIVATE);
        if((taskType.equals("DIALECT") || taskType.equals("RECORD") || (taskType.equals("RECORDEXAM") && examType == 2) || taskType.equals("DIRECTRECORD"))
                && tasktoken.getInt(taskType + "taskToken", 0) == 100){// <- 내가 이 조건은 왜 넣은걸까?
            if(explain.getString("region","").equals("")){
                regionDialogShow((TextView) findViewById(R.id.optionText));
            }else{
                ((TextView) findViewById(R.id.optionText)).setText(explain.getString("region",""));
            }
        }
    }

    public void regionDialogShow(TextView optionText) {
        final TextView optionTextTemp = optionText;
        RegionSelectDialog dialog = new RegionSelectDialog(this, R.style.AppTheme_Transparent_Dialog);
        dialog.setDialogListener(new RegionSelectDialogListener() {
            @Override
            public void onPartChungBukClicked() {
                optionTextTemp.setText("충북");
                startTask();
            }

            @Override
            public void onPartChungNamClicked() {
                optionTextTemp.setText("충남");
                startTask();
            }

            @Override
            public void onPartKyeongBukClicked() {
                optionTextTemp.setText("경북");
                startTask();
            }

            @Override
            public void onPartKyeongNamClicked() {
                optionTextTemp.setText("경남");
                startTask();
            }

            @Override
            public void onPartJeonBukClicked() {
                optionTextTemp.setText("전북");
                startTask();
            }

            @Override
            public void onPartJeonNamClicked() {
                optionTextTemp.setText("전남");
                startTask();
            }

            @Override
            public void onPartKangwonClicked() {
                optionTextTemp.setText("강원");
                startTask();
            }

            @Override
            public void onPartJejuClicked() {
                optionTextTemp.setText("제주");
                startTask();
            }
        });
        dialog.show();
    }

    public int partType() {
        int answer = -1;
        if((taskType.equals("BOXCROP") || taskType.equals("BOXCROPEXAM")) && taskDifficulty.equals("EASY"))
            answer = 100;
        if((taskType.equals("BOXCROP") || taskType.equals("BOXCROPEXAM")) && taskDifficulty.equals("NORMAL"))
            answer = 200;
        if(taskType.equals("CLASSIFICATION") && taskDifficulty.equals("1"))
            answer = 1001;
        if(taskType.equals("CLASSIFICATION") && taskDifficulty.equals("2"))
            answer = 1002;
        if(taskType.equals("CLASSIFICATION") && taskDifficulty.equals("3"))
            answer = 1003;

        TextView partType = findViewById(R.id.optionText);

        if(partType.getText().toString().equals("전봇대 부품들"))
            answer += 2;
        else if(partType.getText().toString().equals("전봇대"))
            answer += 3;
        else if(partType.getText().toString().equals("나무"))
            answer += 4;
        else if(partType.getText().toString().equals("변압기"))
            answer += 5;
        else if(partType.getText().toString().equals("부품 A"))
            answer += 6;
        else if(partType.getText().toString().equals("부품 B"))
            answer += 7;
        else if(partType.getText().toString().equals("부품 C"))
            answer += 8;
        else if(partType.getText().toString().equals("부품 D"))
            answer += 9;
        else if(partType.getText().toString().equals("부품 E"))
            answer += 10;
        else if(partType.getText().toString().equals("부품 G"))
            answer += 1;
        else if(taskType.equals("CLASSIFICATION"))
            ;//nothing to change
        else
            answer = -1;

        System.out.println("파트파입 함수 answer : " +answer);

        return answer;
    }

    protected void partDialogShow(TextView optionText) {
        final TextView partTextTemp = optionText;
        com.selectstar.hwshin.cachemission.Dialog.PartSelectDialog dialog = new com.selectstar.hwshin.cachemission.Dialog.PartSelectDialog(this, R.style.AppTheme_Transparent_Dialog, taskID, taskType, examType, taskDifficulty);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(partTextTemp.getText().toString().equals(""))
                    finish();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(partTextTemp.getText().toString().equals(""))
                    finish();
                startTask();
            }
        });
        dialog.show();
    }

    public String getNumberInString(String rawText)
    {
        return rawText.replaceAll("\\D+","");
    }

    //현재 돈이 즉각적으로 올라가는 UI구현을 위해서 서버와 통신하지 않고 클라이언트 단에서 돈을 올려준다.
    //Controller 2dbox, EditText, Numbers, Photo, Voice 이랑 ExamActivity 에서 사용중
    public void goldSetting(String value){
        if(value == "-1"){
            value = String.valueOf(Integer.parseInt(gold) + upGold);
        }
        gold = value;
        nowGold.setText("현재 : \uFFE6 " + gold);
    }

    //현재 돈이 즉각적으로 올라가는 UI구현을 위해서 서버와 통신하지 않고 클라이언트 단에서 돈을 올려준다.
    //Controller 2dbox, EditText, Numbers, Photo, Voice 이랑 ExamActivity 에서 사용중
    public void maybeSetting(String value){
        maybe = value;
        pendingGold.setText("예정 : \uFFE6 " + maybe);
    }

    //Task를 제출하면 동전 애니메이션과 돈 애니메이션
    public void showAnimation(int animID, int gold){
        ImageView view = findViewById(R.id.imageAnimation);
        TextView tView = findViewById(R.id.textAnimation);
        view.bringToFront();
        tView.bringToFront();
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if(animID == R.drawable.three_coin_anim_list) {
            lp.height = 800;
            lp.width = 800;
            view.setLayoutParams(lp);
        }
        else {
            lp.height = 400;
            lp.width = 400;
            view.setLayoutParams(lp);
        }
        view.setBackgroundResource(animID);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.goldtranslate);
        System.out.println("왜 안뜨냐 골드 : "+String.valueOf(gold));
        tView.setText("+ " + String.valueOf(gold) + " \uFFE6");
        tView.startAnimation(animation);
        AnimationDrawable spinnerAnim = (AnimationDrawable) view.getBackground();
        spinnerAnim.stop();
        spinnerAnim.start();
    }

    public void deleteWaitingTasks(){
        System.out.println("FHFH"+partType());
        String key = taskID;
        if(taskType.contains("BOXCROP"))
            key = key +"/"+ String.valueOf(partType());
        else
            key = key + "/-1";
        if(taskType.contains("EXAM"))
            key = key + "/" + String.valueOf(examType);
        else
            key = key + "/-1";
        savePreference("waitingTasks", key, new JSONArray().toString());
    }

    public void updateWaitingTasks(){
        waitingTasks.remove(waitingTasks.size() - 1);
        String key = taskID;
        if(taskType.contains("BOXCROP"))
            key = key +"/"+ String.valueOf(partType());
        else
            key = key + "/-1";
        if(taskType.contains("EXAM"))
            key = key + "/" + String.valueOf(examType);
        else
            key = key + "/-1";
        savePreference("waitingTasks",key,ARRAYtoJSON(waitingTasks).toString());
    }

    protected JSONArray ARRAYtoJSON(ArrayList<JSONObject> list){
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < list.size(); i++)
            jsonArray.put(list.get(i));
        return jsonArray;
    }

    protected static Date addMinutesToDate(int minutes, Date beforeTime){
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }

    public String getAnswerID() {
        try {
            Log.d("notnull?",currentTask.toString());
            if(currentTask==null){
                Log.d("null?",currentTask.toString());
            }
            return currentTask.get("id").toString();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            return "-1";
        }
    }

    protected ArrayList<JSONObject> JSONtoArray(JSONArray jsonArray){
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

    public String getPreference(String title,String key){
        SharedPreferences SPF = getSharedPreferences(title, MODE_PRIVATE);
        return SPF.getString(key, (new ArrayList<JSONObject>()).toString());
    }

    public void savePreference(String title, String key, String value){

        SharedPreferences SPF = getSharedPreferences(title, MODE_PRIVATE);
        SharedPreferences.Editor editor = SPF.edit();
        editor.putString(key, value);
        editor.commit();

    }

    protected boolean timeCheck(String x){
        System.out.println(StringToDate(x));
        System.out.println(new Date());
        if(StringToDate(x).after(new Date()))
            return true;
        System.out.println("??");
        return false;
    }

    protected String DateToString(Date from) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String to = transFormat.format(from);
        return to;
    }

    protected Date StringToDate(String from){
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

    public JSONArray parseQuestList( JSONArray questList){
        JSONArray result=new JSONArray();
        JSONObject resultNameElem=new JSONObject();
        JSONObject resultRewardElem=new JSONObject();
        JSONObject[] questItem=new JSONObject[questList.length()];
        String[] questName=new String[questList.length()];
        int[] questReward=new int[questList.length()];

        try {
            if (questList.length() > 0) {
                for (int i = 0; i < questList.length(); i++) {
                    questItem[i] = (JSONObject) questList.get(i);
                    if ((boolean) questItem[i].get("isClear")) {
                        questName[i] = ((String) questItem[i].get("name")) + "(완료)";
                        questReward[i] = (Integer) questItem[i].get("reward");

                    } else {
                        questName[i] = ((String) questItem[i].get("name")) + "[" + String.valueOf(questItem[i].get("questDone")) + "/" + String.valueOf(questItem[i].get("questTotal")) + "]";
                        questReward[i] = (Integer) questItem[i].get("reward");
                    }

                    resultNameElem.put(String.valueOf(i),questName[i]);

                    resultRewardElem.put(String.valueOf(i),questReward[i]);

                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        result.put(resultNameElem);
        result.put(resultRewardElem);
        return result;
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

    public void forcedShowDescription(String taskName){

        SharedPreferences firstTimeExplain = getSharedPreferences("firstTimeExplain", MODE_PRIVATE);
        if(firstTimeExplain.getString(taskName,"").equals("notFirst"))
            return;
        Intent intent_taskExplain;
        TextView partText = findViewById(R.id.optionText);
        Log.d("boxbox",taskType);
        if(taskType.equals("BOXCROP")){
            intent_taskExplain = new Intent(PatherActivity.this, NewExplainActivity.class);
            intent_taskExplain.putExtra("part", partText.getText());
            intent_taskExplain.putExtra("partNum", partType());
            intent_taskExplain.putExtra("taskID", taskID);
            System.out.println("shibal"+taskID);
            intent_taskExplain.putExtra("loginToken", getLoginToken());
            System.out.println("가져온 텍스트 : "+partText.getText());
        }else{
            intent_taskExplain = new Intent(PatherActivity.this, TaskExplainActivity.class);
        }
        intent_taskExplain.putExtra("taskType", taskType);
        startActivity(intent_taskExplain);
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
    //해당 task가 처음이라면 설명서 띄워주는 것
    public void showDescription(){
        SharedPreferences taskToken = getSharedPreferences("taskToken", MODE_PRIVATE);
        if(taskToken.getInt(taskType + "taskToken",0) == 100)
            return;
        Intent intent_taskExplain;
        TextView partText = findViewById(R.id.optionText);
        Log.d("boxbox",taskType);
        if(taskType.equals("PHOTO")){
            intent_taskExplain = new Intent(PatherActivity.this, NewExplainActivity.class);
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
            intent_taskExplain = new Intent(PatherActivity.this, TaskExplainActivity.class);
            intent_taskExplain.putExtra("taskType", taskType);
            startActivity(intent_taskExplain);
        }

    }

}
