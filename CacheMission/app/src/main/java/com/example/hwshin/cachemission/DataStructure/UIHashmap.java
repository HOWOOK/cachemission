package com.example.hwshin.cachemission.DataStructure;

import com.example.hwshin.cachemission.R;

import java.util.ArrayList;
import java.util.HashMap;

public class UIHashmap {

    /* int[][] parameters 설명
    * parameters[0] -> TaskView의 Weight  (Defualt Weight : TaskView = 10, Controller = 10)
    * parameters[1] -> TaskView의 Top을 어떤 Constraintlayout에 연결할지
    * parameters[2] -> TaskView의 Bottom을 어떤 Constraintlayout에 연결할지
    * parameters[3] -> Controller의 Top을 어떤 Constraintlayout에 연결할지
    * parameters[4] -> Controller의 Bottom을 어떤 Constraintlayout에 연결할지
    */
    int[][] parameters = new int[5][2];

    public HashMap<Integer, TaskView> taskViewHashMap = new HashMap();
    public HashMap<Integer, Controller> controllerHashMap = new HashMap();
    public HashMap<Integer, int[][]> taskHashMap = new HashMap();

    public UIHashmap() {
        //TaskView hashmap 종류들
        taskViewHashMap.put(0, new TaskView_Image());
        taskViewHashMap.put(1, new TaskView_Video());

        //Controller hashmap 종류들
        controllerHashMap.put(0, new Controller_EditText());
        controllerHashMap.put(1, new Controller_Buttons());
        //OCR view-controller connect
        parameters[0][0] = 5;
        parameters[1][0] = R.id.taskConstLayout; parameters[1][1] = 3;
        parameters[2][0] = R.id.controller; parameters[2][1] = 3;
        parameters[3][0] = R.id.taskview; parameters[3][1] = 4;
        parameters[4][0] = R.id.taskConstLayout; parameters[4][1] = 4;
        taskHashMap.put(0, parameters);
    }

}
