package com.selectstar.hwshin.cashmission.DataStructure;

import org.json.JSONArray;

public class TaskListItem {

    private String id;
    private String taskName;
    private String taskType;
    private String taskView;
    private String controller;
    private String gold;
    private String dailyMission;
    private JSONArray buttons;
    private int taskFlag;
    private int examType;

    public String getId(){return id;}
    public String getTaskName() {
        return taskName;
    }
    public String getTaskType() {
        return taskType;
    }
    public String getTaskView() {
        return taskView;
    }
    public String getController() {
        return controller;
    }
    public String getGold() {
        return gold;
    }
    public String getDailyMission() {
        return dailyMission;
    }

    public JSONArray getButtons(){return buttons;}
    public int getTaskFlag(){
        return taskFlag;
    }
    public int getExamType(){return examType;}

    public String setId(String id){
        return this.id = id;
    }
    public String setTaskName(String taskName){
        return this.taskName = taskName;
    }
    public String setTaskType(String taskType){
        return this.taskType = taskType;
    }
    public String setTaskView(String taskView){
        return this.taskView = taskView;
    }
    public String setController(String controller){ return this.controller = controller; }
    public String setGold(String gold){
        return this.gold = gold;
    }
    public String setDailyMission(){
        return this.dailyMission= dailyMission;
    }
    public JSONArray setButtons(JSONArray buttons){return this.buttons=buttons;}
    public int setTaskFlag(int taskFlag){
        return this.taskFlag = taskFlag;
    }
    public int setExamType(int examType){return this.examType=examType;}


    public TaskListItem(String id, String taskName, String taskType, String taskView, String controller, String gold, String dailyMission, JSONArray buttons, int taskFlag){

        this.id=id;
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskView = taskView;
        this.controller = controller;
        this.gold = gold;
        this.dailyMission =dailyMission;
        this.buttons=buttons;
        this.taskFlag = taskFlag;
    }
    public TaskListItem(String id, String taskName, String taskType, String taskView, String controller, String gold, String dailyMission, JSONArray buttons, int taskFlag, int examType){

        this.id=id;
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskView = taskView;
        this.controller = controller;
        this.gold = gold;
        this.dailyMission =dailyMission;
        this.buttons=buttons;
        this.taskFlag = taskFlag;
        this.examType = examType;
    }

}
