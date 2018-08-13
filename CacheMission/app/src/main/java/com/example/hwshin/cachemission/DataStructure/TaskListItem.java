package com.example.hwshin.cachemission.DataStructure;

public class TaskListItem {

    private String id;
    private String taskName;
    private String taskType;
    private String taskView;
    private String controller;
    private String gold;

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


    public TaskListItem(String id, String taskName, String taskType, String taskView,  String controller, String gold ){

        this.id=id;
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskView = taskView;
        this.controller = controller;
        this.gold = gold;
    }

}
