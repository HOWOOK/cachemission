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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Adapter.ListviewAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;
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
    public String getTaskType() {
        return taskType;
    }
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
    protected int partNum;

    public int getPartNum() {
        return partNum;
    }
    public int partType() {
        int answer = -1;
        TextView partType = findViewById(R.id.partText);
        System.out.println(partType.getText().toString());
        if(partType.getText().toString().equals("프리프로세스"))
            answer = 2;
        if(partType.getText().toString().equals("전신주"))
            answer = 3;
        if(partType.getText().toString().equals("나무"))
            answer = 4;
        if(partType.getText().toString().equals("변압기"))
            answer = 5;
        if(partType.getText().toString().equals("부품 A"))
            answer = 6;
        if(partType.getText().toString().equals("부품 B"))
            answer = 7;
        if(partType.getText().toString().equals("부품 C"))
            answer = 8;
        if(partType.getText().toString().equals("부품 D"))
            answer = 9;
        if(partType.getText().toString().equals("부품 E"))
            answer = 10;
        if(partType.getText().toString().equals("부품 G"))
            answer = 1;
        System.out.println(answer);
        return answer;
    }
    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }
    protected void partDialogShow(TextView partText) {
        final TextView partTextTemp = partText;
        com.selectstar.hwshin.cachemission.Dialog.PartSelectDialog dialog = new com.selectstar.hwshin.cachemission.Dialog.PartSelectDialog(this, R.style.AppTheme_Transparent_Dialog);
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
    public void showAnimation(int animID, int gold)
    {
        ImageView view = findViewById(R.id.imageAnimation);
        TextView tView = findViewById(R.id.textAnimation);
        view.bringToFront();
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
        tView.setText("+ " + String.valueOf(gold) + " \uFFE6");
        tView.startAnimation(animation);
        AnimationDrawable spinnerAnim = (AnimationDrawable) view.getBackground();
        spinnerAnim.stop();
        spinnerAnim.start();
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
    public abstract void startTask();
    public abstract void getNewTask();
    public void setLoginToken(String loginToken) {if(loginToken==null)loginToken="";this.loginToken = loginToken;}
    public String getLoginToken() {
        return loginToken;
    }
    protected JSONArray ARRAYtoJSON(ArrayList<JSONObject> list)
    {
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<list.size();i++)
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
    protected ArrayList<JSONObject> JSONtoArray(JSONArray jsonArray)
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

    public String getPreference(String title,String key)
    {
        SharedPreferences SPF = getSharedPreferences(title, MODE_PRIVATE);
        return SPF.getString(key, (new ArrayList<JSONObject>()).toString());
    }

    public void savePreference(String title, String key, String value)
    {

        SharedPreferences SPF = getSharedPreferences(title, MODE_PRIVATE);
        SharedPreferences.Editor editor = SPF.edit();
        editor.putString(key, value);
        editor.commit();

    }

    protected boolean timeCheck(String x)
    {
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
    protected Date StringToDate(String from)
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

    public JSONArray parseQuestList( JSONArray questList)
    {
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

    public void forcedShowDescription(String taskName)
    {

        SharedPreferences firstTimeExplain = getSharedPreferences("firstTimeExplain", MODE_PRIVATE);
        if(firstTimeExplain.getString(taskName,"").equals("notFirst"))
            return;
        Intent intent_taskExplain;
        TextView partText = findViewById(R.id.partText);
        Log.d("boxbox",taskType);
        if(taskType.equals("BOXCROP")){
            System.out.println("3333");
            intent_taskExplain = new Intent(PatherActivity.this, NewExplainActivity.class);
            intent_taskExplain.putExtra("part", partText.getText());
            System.out.println("가져온 텍스트 : "+partText.getText());
        }else{
            intent_taskExplain = new Intent(PatherActivity.this, TaskExplainActivity.class);
        }
        intent_taskExplain.putExtra("taskType", taskType);
        startActivity(intent_taskExplain);
    }
}
