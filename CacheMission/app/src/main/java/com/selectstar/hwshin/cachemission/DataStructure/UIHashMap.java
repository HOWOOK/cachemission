package com.selectstar.hwshin.cachemission.DataStructure;

import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_2DBox;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_EditText;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Numbers;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_TwoPoint;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_2DBox;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_Buttons;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_EditText;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_TwoPoint;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_Voice;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Photo;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Voice;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_ZoomImage;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_Image;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithBox;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithLine;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_SwipeImage;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_Text;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_Voice;
import com.selectstar.hwshin.cachemission.R;

import java.util.HashMap;

public class UIHashMap {

    /* int[][] parameters 설명
     * parameters[0] -> TaskView의 Weight  (Default Weight : TaskView = 10)
     * parameters[1] -> TaskView의 Top을 어떤 Constraintlayout에 연결할지
     * parameters[2] -> TaskView의 Bottom을 어떤 Constraintlayout에 연결할지
     * parameters[3] -> Controller의 Top을 어떤 Constraintlayout에 연결할지
     * parameters[4] -> Controller의 Bottom을 어떤 Constraintlayout에 연결할지
     * parameters[5] -> Controller의 Weight  (Default Weight : Controller = 10)
     */
    int[][] parameters_OCR = new int[6][2];
    int[][] parameters_VIDEO = new int[6][2];
    int[][] parameters_RECORD = new int[6][2];
    int[][] parameters_DIRECTRECORD = new int[6][2];
    int[][] parameters_DICTATION = new int[6][2];
    int[][] parameters_NUMBEING = new int[6][2];
    int[][] parameters_DIALECT = new int[6][2];
    int[][] parameters_PHOTO = new int[6][2];
    int[][] parameters_BOXCROP = new int[6][2];
    int[][] parameters_TWOPOINT = new int[6][2];
    int[][] parameters_SUGGEST = new int[6][2];

    int[][] parameters_OCREXAM = new int[6][2];
    int[][] parameters_VIDEOEXAM = new int[6][2];
    int[][] parameters_RECORDEXAM = new int[6][2];
    int[][] parameters_DIRECTRECORDEXAM = new int[6][2];
    int[][] parameters_DICTATIONEXAM = new int[6][2];
    int[][] parameters_NUMBEINGEXAM = new int[6][2];
    int[][] parameters_DIALECTEXAM = new int[6][2];
    int[][] parameters_PHOTOEXAM = new int[6][2];
    int[][] parameters_BOXCROPEXAM = new int[6][2];
    int[][] parameters_TWOPOINTEXAM = new int[6][2];
    int[][] parameters_SUGGESTEXAM = new int[6][2];

    public HashMap<String, TaskView> taskViewHashMap = new HashMap();
    public HashMap<String, Controller> controllerHashMap = new HashMap();
    public HashMap<String, ExamView> examViewHashMap = new HashMap();
    public HashMap<String, int[][]> taskHashMap = new HashMap();

    public UIHashMap() {
        //TaskView hashmap 종류들
        taskViewHashMap.put("image", new TaskView_Image());
        //taskViewHashMap.put("video", new TaskView_Video());
        taskViewHashMap.put("text", new TaskView_Text());
        taskViewHashMap.put("voice", new TaskView_Voice());
        taskViewHashMap.put("swipeimage", new TaskView_SwipeImage());
        taskViewHashMap.put("photoview", new TaskView_PhotoWithBox());
        taskViewHashMap.put("photowithline", new TaskView_PhotoWithLine());

        //Controller hashmap 종류들
        controllerHashMap.put("edittext", new Controller_EditText());
        controllerHashMap.put("2dbox", new Controller_2DBox());
        //controllerHashMap.put("buttons", new Controller_Buttons());
        controllerHashMap.put("record", new Controller_Voice());
        controllerHashMap.put("numbers", new Controller_Numbers());
        controllerHashMap.put("photo", new Controller_Photo());
        controllerHashMap.put("twopoint", new Controller_TwoPoint());

        examViewHashMap.put("edittext", new ExamView_EditText());
        examViewHashMap.put("buttons", new ExamView_Buttons());
        examViewHashMap.put("record", new ExamView_Voice());
        examViewHashMap.put("2dbox", new ExamView_2DBox());
        examViewHashMap.put("photo", new ExamView_ZoomImage());
        examViewHashMap.put("twopoint", new ExamView_TwoPoint());

        //OCR view-controller connect
        parameters_OCR[0][0] = 10;
        parameters_OCR[1][0] = R.id.title; parameters_OCR[1][1] = 4;
        parameters_OCR[2][0] = R.id.taskConstLayout; parameters_OCR[2][1] = 4;
        parameters_OCR[3][0] = R.id.taskConstLayout; parameters_OCR[3][1] = 3;
        parameters_OCR[4][0] = R.id.taskConstLayout; parameters_OCR[4][1] = 4;
        parameters_OCR[5][0] = 0;
        taskHashMap.put("OCR", parameters_OCR);

        parameters_OCREXAM[0][0] = 5;
        parameters_OCREXAM[1][0] = R.id.title; parameters_OCREXAM[1][1] = 4;
        parameters_OCREXAM[2][0] = R.id.controllerExam; parameters_OCREXAM[2][1] = 3;
        parameters_OCREXAM[3][0] = R.id.taskViewExam; parameters_OCREXAM[3][1] = 4;
        parameters_OCREXAM[4][0] = R.id.taskConstLayoutExam; parameters_OCREXAM[4][1] = 4;
        parameters_OCREXAM[5][0] = 10;
        taskHashMap.put("OCREXAM", parameters_OCREXAM);

        parameters_VIDEO[0][0] = 5;
        parameters_VIDEO[1][0] = R.id.title; parameters_VIDEO[1][1] = 4;
        parameters_VIDEO[2][0] = R.id.controller; parameters_VIDEO[2][1] = 3;
        parameters_VIDEO[3][0] = R.id.taskview; parameters_VIDEO[3][1] = 4;
        parameters_VIDEO[4][0] = R.id.taskConstLayout; parameters_VIDEO[4][1] = 4;
        parameters_VIDEO[5][0] = 10;
        taskHashMap.put("VIDEO", parameters_VIDEO);

        parameters_VIDEOEXAM[0][0] = 5;
        parameters_VIDEOEXAM[1][0] = R.id.title; parameters_VIDEOEXAM[1][1] = 4;
        parameters_VIDEOEXAM[2][0] = R.id.controllerExam; parameters_VIDEOEXAM[2][1] = 3;
        parameters_VIDEOEXAM[3][0] = R.id.taskViewExam; parameters_VIDEOEXAM[3][1] = 4;
        parameters_VIDEOEXAM[4][0] = R.id.taskConstLayoutExam; parameters_VIDEOEXAM[4][1] = 4;
        parameters_VIDEOEXAM[5][0] = 10;
        taskHashMap.put("VIDEOEXAM", parameters_VIDEOEXAM);

        parameters_RECORD[0][0] = 20;
        parameters_RECORD[1][0] = R.id.option; parameters_RECORD[1][1] = 4;
        parameters_RECORD[2][0] = R.id.controller; parameters_RECORD[2][1] = 3;
        parameters_RECORD[3][0] = R.id.taskview; parameters_RECORD[3][1] = 4;
        parameters_RECORD[4][0] = R.id.taskConstLayout; parameters_RECORD[4][1] = 4;
        parameters_RECORD[5][0] = 10;
        taskHashMap.put("RECORD", parameters_RECORD);

        parameters_DIRECTRECORD[0][0] = 20;
        parameters_DIRECTRECORD[1][0] = R.id.option; parameters_DIRECTRECORD[1][1] = 4;
        parameters_DIRECTRECORD[2][0] = R.id.controller; parameters_DIRECTRECORD[2][1] = 3;
        parameters_DIRECTRECORD[3][0] = R.id.taskview; parameters_DIRECTRECORD[3][1] = 4;
        parameters_DIRECTRECORD[4][0] = R.id.taskConstLayout; parameters_DIRECTRECORD[4][1] = 4;
        parameters_DIRECTRECORD[5][0] = 10;
        taskHashMap.put("DIRECTRECORD", parameters_DIRECTRECORD);

        parameters_RECORDEXAM[0][0] = 25;
        parameters_RECORDEXAM[1][0] = R.id.title; parameters_RECORDEXAM[1][1] = 4;
        parameters_RECORDEXAM[2][0] = R.id.controllerExam; parameters_RECORDEXAM[2][1] = 3;
        parameters_RECORDEXAM[3][0] = R.id.taskViewExam; parameters_RECORDEXAM[3][1] = 4;
        parameters_RECORDEXAM[4][0] = R.id.taskConstLayoutExam; parameters_RECORDEXAM[4][1] = 4;
        parameters_RECORDEXAM[5][0] = 10;
        taskHashMap.put("RECORDEXAM", parameters_RECORDEXAM);

        parameters_DIRECTRECORDEXAM[0][0] = 25;
        parameters_DIRECTRECORDEXAM[1][0] = R.id.title; parameters_DIRECTRECORDEXAM[1][1] = 4;
        parameters_DIRECTRECORDEXAM[2][0] = R.id.controllerExam; parameters_DIRECTRECORDEXAM[2][1] = 3;
        parameters_DIRECTRECORDEXAM[3][0] = R.id.taskViewExam; parameters_DIRECTRECORDEXAM[3][1] = 4;
        parameters_DIRECTRECORDEXAM[4][0] = R.id.taskConstLayoutExam; parameters_DIRECTRECORDEXAM[4][1] = 4;
        parameters_DIRECTRECORDEXAM[5][0] = 10;
        taskHashMap.put("DIRECTRECORDEXAM", parameters_DIRECTRECORDEXAM);

        parameters_DICTATION[0][0] = 5;
        parameters_DICTATION[1][0] = R.id.title; parameters_DICTATION[1][1] = 4;
        parameters_DICTATION[2][0] = R.id.taskConstLayout; parameters_DICTATION[2][1] = 4;
        parameters_DICTATION[3][0] = R.id.taskConstLayout; parameters_DICTATION[3][1] = 3;
        parameters_DICTATION[4][0] = R.id.taskConstLayout; parameters_DICTATION[4][1] = 4;
        parameters_DICTATION[5][0] = 10;
        taskHashMap.put("DICTATION", parameters_DICTATION);

        parameters_DICTATIONEXAM[0][0] = 5;
        parameters_DICTATIONEXAM[1][0] = R.id.title; parameters_DICTATIONEXAM[1][1] = 4;
        parameters_DICTATIONEXAM[2][0] = R.id.controllerExam; parameters_DICTATIONEXAM[2][1] = 3;
        parameters_DICTATIONEXAM[3][0] = R.id.taskViewExam; parameters_DICTATIONEXAM[3][1] = 4;
        parameters_DICTATIONEXAM[4][0] = R.id.taskConstLayoutExam; parameters_DICTATIONEXAM[4][1] = 4;
        parameters_DICTATIONEXAM[5][0] = 10;
        taskHashMap.put("DICTATIONEXAM", parameters_DICTATIONEXAM);

        parameters_NUMBEING[0][0] = 10;
        parameters_NUMBEING[1][0] = R.id.option; parameters_NUMBEING[1][1] = 4;
        parameters_NUMBEING[2][0] = R.id.controller; parameters_NUMBEING[2][1] = 3;
        parameters_NUMBEING[3][0] = R.id.taskview; parameters_NUMBEING[3][1] = 4;
        parameters_NUMBEING[4][0] = R.id.taskConstLayout; parameters_NUMBEING[4][1] = 4;
        parameters_NUMBEING[5][0] = 5;
        taskHashMap.put("NUMBERING", parameters_NUMBEING);

        parameters_NUMBEINGEXAM[0][0] = 5;
        parameters_NUMBEINGEXAM[1][0] = R.id.title; parameters_NUMBEINGEXAM[1][1] = 4;
        parameters_NUMBEINGEXAM[2][0] = R.id.controllerExam; parameters_NUMBEINGEXAM[2][1] = 3;
        parameters_NUMBEINGEXAM[3][0] = R.id.taskViewExam; parameters_NUMBEINGEXAM[3][1] = 4;
        parameters_NUMBEINGEXAM[4][0] = R.id.taskConstLayoutExam; parameters_NUMBEINGEXAM[4][1] = 4;
        parameters_NUMBEINGEXAM[5][0] = 10;
        taskHashMap.put("NUMBERINGEXAM", parameters_NUMBEINGEXAM);

        parameters_DIALECT[0][0] = 5;
        parameters_DIALECT[1][0] = R.id.option; parameters_DIALECT[1][1] = 4;
        parameters_DIALECT[2][0] = R.id.taskConstLayout; parameters_DIALECT[2][1] = 4;
        parameters_DIALECT[3][0] = R.id.taskConstLayout; parameters_DIALECT[3][1] = 3;
        parameters_DIALECT[4][0] = R.id.taskConstLayout; parameters_DIALECT[4][1] = 4;
        parameters_DIALECT[5][0] = 10;
        taskHashMap.put("DIALECT", parameters_DIALECT);

        parameters_DIALECTEXAM[0][0] = 5;
        parameters_DIALECTEXAM[1][0] = R.id.title; parameters_DIALECTEXAM[1][1] = 4;
        parameters_DIALECTEXAM[2][0] = R.id.controllerExam; parameters_DIALECTEXAM[2][1] = 3;
        parameters_DIALECTEXAM[3][0] = R.id.taskViewExam; parameters_DIALECTEXAM[3][1] = 4;
        parameters_DIALECTEXAM[4][0] = R.id.taskConstLayoutExam; parameters_DIALECTEXAM[4][1] = 4;
        parameters_DIALECTEXAM[5][0] = 10;
        taskHashMap.put("DIALECTEXAM", parameters_DIALECTEXAM);

        parameters_PHOTO[0][0] = 20;
        parameters_PHOTO[1][0] = R.id.option; parameters_PHOTO[1][1] = 4;
        parameters_PHOTO[2][0] = R.id.controller; parameters_PHOTO[2][1] = 3;
        parameters_PHOTO[3][0] = R.id.taskview; parameters_PHOTO[3][1] = 4;
        parameters_PHOTO[4][0] = R.id.taskConstLayout; parameters_PHOTO[4][1] = 4;
        parameters_PHOTO[5][0] = 10;
        taskHashMap.put("PHOTO", parameters_PHOTO);

        parameters_PHOTOEXAM[0][0] = 1;
        parameters_PHOTOEXAM[1][0] = R.id.title; parameters_PHOTOEXAM[1][1] = 4;
        parameters_PHOTOEXAM[2][0] = R.id.controllerExam; parameters_PHOTOEXAM[2][1] = 3;
        parameters_PHOTOEXAM[3][0] = R.id.taskViewExam; parameters_PHOTOEXAM[3][1] = 4;
        parameters_PHOTOEXAM[4][0] = R.id.taskConstLayoutExam; parameters_PHOTOEXAM[4][1] = 4;
        parameters_PHOTOEXAM[5][0] = 50;
        taskHashMap.put("PHOTOEXAM",parameters_PHOTOEXAM);

        parameters_BOXCROP[0][0] = 10;
        parameters_BOXCROP[1][0] = R.id.option; parameters_BOXCROP[1][1] = 4;
        parameters_BOXCROP[2][0] = R.id.taskConstLayout; parameters_BOXCROP[2][1] = 4;
        parameters_BOXCROP[3][0] = R.id.option; parameters_BOXCROP[3][1] = 4;
        parameters_BOXCROP[4][0] = R.id.taskConstLayout; parameters_BOXCROP[4][1] = 4;
        parameters_BOXCROP[5][0] = 10;
        taskHashMap.put("BOXCROP", parameters_BOXCROP);

        parameters_BOXCROPEXAM[0][0] = 10;
        parameters_BOXCROPEXAM[1][0] = R.id.option; parameters_BOXCROPEXAM[1][1] = 4;
        parameters_BOXCROPEXAM[2][0] = R.id.taskConstLayoutExam; parameters_BOXCROPEXAM[2][1] = 4;
        parameters_BOXCROPEXAM[3][0] = R.id.option; parameters_BOXCROPEXAM[3][1] = 4;
        parameters_BOXCROPEXAM[4][0] = R.id.taskConstLayoutExam; parameters_BOXCROPEXAM[4][1] = 4;
        parameters_BOXCROPEXAM[5][0] = 10;
        taskHashMap.put("BOXCROPEXAM", parameters_BOXCROPEXAM);

        parameters_TWOPOINT[0][0] = 10;
        parameters_TWOPOINT[1][0] = R.id.option; parameters_TWOPOINT[1][1] = 4;
        parameters_TWOPOINT[2][0] = R.id.taskConstLayout; parameters_TWOPOINT[2][1] = 4;
        parameters_TWOPOINT[3][0] = R.id.option; parameters_TWOPOINT[3][1] = 4;
        parameters_TWOPOINT[4][0] = R.id.taskConstLayout; parameters_TWOPOINT[4][1] = 4;
        parameters_TWOPOINT[5][0] = 10;
        taskHashMap.put("TWOPOINT", parameters_TWOPOINT);

        parameters_TWOPOINTEXAM[0][0] = 10;
        parameters_TWOPOINTEXAM[1][0] = R.id.option; parameters_TWOPOINTEXAM[1][1] = 4;
        parameters_TWOPOINTEXAM[2][0] = R.id.taskConstLayoutExam; parameters_TWOPOINTEXAM[2][1] = 4;
        parameters_TWOPOINTEXAM[3][0] = R.id.option; parameters_TWOPOINTEXAM[3][1] = 4;
        parameters_TWOPOINTEXAM[4][0] = R.id.taskConstLayoutExam; parameters_TWOPOINTEXAM[4][1] = 4;
        parameters_TWOPOINTEXAM[5][0] = 10;
        taskHashMap.put("TWOPOINTEXAM", parameters_TWOPOINTEXAM);

        parameters_SUGGEST[0][0] = 10;
        parameters_SUGGEST[1][0] = R.id.title; parameters_SUGGEST[1][1] = 4;
        parameters_SUGGEST[2][0] = R.id.controller; parameters_SUGGEST[2][1] = 4;
        parameters_SUGGEST[3][0] = R.id.taskview; parameters_SUGGEST[3][1] = 3;
        parameters_SUGGEST[4][0] = R.id.taskConstLayout; parameters_SUGGEST[4][1] = 4;
        parameters_SUGGEST[5][0] = 0;
        taskHashMap.put("SUGGEST", parameters_SUGGEST);

        parameters_SUGGESTEXAM[0][0] = 5;
        parameters_SUGGESTEXAM[1][0] = R.id.title; parameters_SUGGESTEXAM[1][1] = 4;
        parameters_SUGGESTEXAM[2][0] = R.id.controllerExam; parameters_SUGGESTEXAM[2][1] = 3;
        parameters_SUGGESTEXAM[3][0] = R.id.taskViewExam; parameters_SUGGESTEXAM[3][1] = 4;
        parameters_SUGGESTEXAM[4][0] = R.id.taskConstLayoutExam; parameters_SUGGESTEXAM[4][1] = 4;
        parameters_SUGGESTEXAM[5][0] = 10;
        taskHashMap.put("SUGGESTEXAM", parameters_SUGGESTEXAM);

    }

}
