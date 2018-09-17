package com.selectstar.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.UIHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class PatherActivity extends AppCompatActivity {

    protected int controllerID, taskViewID;
    protected JSONObject currentTask;
    protected String answerID;
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
}
