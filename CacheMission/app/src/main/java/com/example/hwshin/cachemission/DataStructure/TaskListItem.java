package com.example.hwshin.cachemission.DataStructure;

public class TaskListItem {
    private String taskType;
    private String taskName;


    public String getTaskType() {
        return taskType;
    }
    public String getTaskName() {
        return taskName;
    }

    public String setTaskType(String taskType){
        return this.taskType = taskType;
    }
    public String setTaskName(String taskName){
        return this.taskName = taskName;
    }

    public TaskListItem(String taskType, String taskName){
        this.taskType = taskType;
        this.taskName = taskName;
    }
}
