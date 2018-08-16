package com.example.hwshin.cachemission.DataStructure;

import com.example.hwshin.cachemission.DataStructure.Controller.Controller;
import com.example.hwshin.cachemission.DataStructure.Controller.Controller_Buttons;
import com.example.hwshin.cachemission.DataStructure.Controller.Controller_EditText;
import com.example.hwshin.cachemission.DataStructure.Controller.Controller_Voice;
import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView_Buttons;
import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView_EditText;
import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView_Voice;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView_Image;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView_Text;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView_Video;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView_Voice;
import com.example.hwshin.cachemission.R;

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
    int[][] parameters_VOICE = new int[5][2];
    int[][] parameters_DICTATION = new int[5][2];
    int[][] parameters_OCREXAM = new int[5][2];
    int[][] parameters_VIDEOEXAM = new int[5][2];
    int[][] parameters_VOICEEXAM = new int[5][2];
    int[][] parameters_DICTATIONEXAM = new int[5][2];

    public HashMap<String, TaskView> taskViewHashMap = new HashMap();
    public HashMap<String, Controller> controllerHashMap = new HashMap();
    public HashMap<String, ExamView> examviewHashMap = new HashMap();
    public HashMap<String, int[][]> taskHashMap = new HashMap();

    public UIHashmap() {
        //TaskView hashmap 종류들
        taskViewHashMap.put("image", new TaskView_Image());
        taskViewHashMap.put("video", new TaskView_Video());
        taskViewHashMap.put("text", new TaskView_Text());
        taskViewHashMap.put("voice", new TaskView_Voice());

        //Controller hashmap 종류들
        controllerHashMap.put("edittext", new Controller_EditText());
        controllerHashMap.put("buttons", new Controller_Buttons());
        controllerHashMap.put("record", new Controller_Voice());


        examviewHashMap.put("edittext", new ExamView_EditText());
        examviewHashMap.put("buttons", new ExamView_Buttons());
        examviewHashMap.put("record", new ExamView_Voice());
        //OCR view-controller connect
        parameters_OCR[0][0] = 5;
        parameters_OCR[1][0] = R.id.tasktitle; parameters_OCR[1][1] = 4;
        parameters_OCR[2][0] = R.id.controller; parameters_OCR[2][1] = 3;
        parameters_OCR[3][0] = R.id.taskview; parameters_OCR[3][1] = 4;
        parameters_OCR[4][0] = R.id.taskConstLayout; parameters_OCR[4][1] = 4;
        taskHashMap.put("OCR", parameters_OCR);

        parameters_OCREXAM[0][0] = 5;
        parameters_OCREXAM[1][0] = R.id.tasktitleexam; parameters_OCREXAM[1][1] = 4;
        parameters_OCREXAM[2][0] = R.id.examview; parameters_OCREXAM[2][1] = 3;
        parameters_OCREXAM[3][0] = R.id.taskviewexam; parameters_OCREXAM[3][1] = 4;
        parameters_OCREXAM[4][0] = R.id.taskConstLayoutexam; parameters_OCREXAM[4][1] = 4;
        taskHashMap.put("OCREXAM", parameters_OCREXAM);

        parameters_VIDEO[0][0] = 5;
        parameters_VIDEO[1][0] = R.id.tasktitle; parameters_VIDEO[1][1] = 4;
        parameters_VIDEO[2][0] = R.id.controller; parameters_VIDEO[2][1] = 3;
        parameters_VIDEO[3][0] = R.id.taskview; parameters_VIDEO[3][1] = 4;
        parameters_VIDEO[4][0] = R.id.taskConstLayout; parameters_VIDEO[4][1] = 4;
        taskHashMap.put("VIDEO", parameters_VIDEO);

        parameters_VIDEOEXAM[0][0] = 5;
        parameters_VIDEOEXAM[1][0] = R.id.tasktitleexam; parameters_VIDEOEXAM[1][1] = 4;
        parameters_VIDEOEXAM[2][0] = R.id.examview; parameters_VIDEOEXAM[2][1] = 3;
        parameters_VIDEOEXAM[3][0] = R.id.taskviewexam; parameters_VIDEOEXAM[3][1] = 4;
        parameters_VIDEOEXAM[4][0] = R.id.taskConstLayoutexam; parameters_VIDEOEXAM[4][1] = 4;
        taskHashMap.put("VIDEOEXAM", parameters_VIDEO);

        parameters_VOICE[0][0] = 5;
        parameters_VOICE[1][0] = R.id.tasktitle; parameters_VOICE[1][1] = 4;
        parameters_VOICE[2][0] = R.id.controller; parameters_VOICE[2][1] = 3;
        parameters_VOICE[3][0] = R.id.taskview; parameters_VOICE[3][1] = 4;
        parameters_VOICE[4][0] = R.id.taskConstLayout; parameters_VOICE[4][1] = 4;
        taskHashMap.put("RECORD", parameters_VOICE);

        parameters_VOICEEXAM[0][0] = 5;
        parameters_VOICEEXAM[1][0] = R.id.tasktitleexam; parameters_VOICEEXAM[1][1] = 4;
        parameters_VOICEEXAM[2][0] = R.id.examview; parameters_VOICEEXAM[2][1] = 3;
        parameters_VOICEEXAM[3][0] = R.id.taskviewexam; parameters_VOICEEXAM[3][1] = 4;
        parameters_VOICEEXAM[4][0] = R.id.taskConstLayoutexam; parameters_VOICEEXAM[4][1] = 4;
        taskHashMap.put("RECORDEXAM", parameters_VOICEEXAM);

        parameters_DICTATION[0][0] = 5;
        parameters_DICTATION[1][0] = R.id.tasktitle; parameters_DICTATION[1][1] = 4;
        parameters_DICTATION[2][0] = R.id.controller; parameters_DICTATION[2][1] = 3;
        parameters_DICTATION[3][0] = R.id.taskview; parameters_DICTATION[3][1] = 4;
        parameters_DICTATION[4][0] = R.id.taskConstLayout; parameters_DICTATION[4][1] = 4;
        taskHashMap.put("DICTATION", parameters_DICTATION);

        parameters_DICTATIONEXAM[0][0] = 5;
        parameters_DICTATIONEXAM[1][0] = R.id.tasktitleexam; parameters_DICTATIONEXAM[1][1] = 4;
        parameters_DICTATIONEXAM[2][0] = R.id.examview; parameters_DICTATIONEXAM[2][1] = 3;
        parameters_DICTATIONEXAM[3][0] = R.id.taskviewexam; parameters_DICTATIONEXAM[3][1] = 4;
        parameters_DICTATIONEXAM[4][0] = R.id.taskConstLayoutexam; parameters_DICTATIONEXAM[4][1] = 4;
        taskHashMap.put("DICTATIONEXAM", parameters_DICTATIONEXAM);

    }

}
