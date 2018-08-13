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
    int[][] parameters_OCR = new int[5][2];
    int[][] parameters_VIDEO = new int[5][2];

    public HashMap<String, TaskView> taskViewHashMap = new HashMap();
    public HashMap<String, Controller> controllerHashMap = new HashMap();
    public HashMap<String, int[][]> taskHashMap = new HashMap();

    public UIHashmap() {
        //TaskView hashmap 종류들
        taskViewHashMap.put("image", new TaskView_Image());
        taskViewHashMap.put("video", new TaskView_Video());

        //Controller hashmap 종류들
        controllerHashMap.put("edittext", new Controller_EditText());
        controllerHashMap.put("buttons", new Controller_Buttons());
        controllerHashMap.put("record", new Controller_Voice());
        //OCR view-controller connect
        parameters_OCR[0][0] = 5;
        parameters_OCR[1][0] = R.id.tasktitle; parameters_OCR[1][1] = 4;
        parameters_OCR[2][0] = R.id.controller; parameters_OCR[2][1] = 3;
        parameters_OCR[3][0] = R.id.taskview; parameters_OCR[3][1] = 4;
        parameters_OCR[4][0] = R.id.taskConstLayout; parameters_OCR[4][1] = 4;
        taskHashMap.put("OCR", parameters_OCR);

        parameters_VIDEO[0][0] = 5;
        parameters_VIDEO[1][0] = R.id.tasktitle; parameters_VIDEO[1][1] = 4;
        parameters_VIDEO[2][0] = R.id.controller; parameters_VIDEO[2][1] = 3;
        parameters_VIDEO[3][0] = R.id.taskview; parameters_VIDEO[3][1] = 4;
        parameters_VIDEO[4][0] = R.id.taskConstLayout; parameters_VIDEO[4][1] = 4;
        taskHashMap.put("VIDEO", parameters_VIDEO);

    }

}
