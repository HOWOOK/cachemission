package com.selectstar.hwshin.cachemission.DataStructure;

import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_2DBox;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_EditText;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Numbers;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_Buttons;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_EditText;
import com.selectstar.hwshin.cachemission.DataStructure.ExamView.ExamView_Voice;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Photo;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller_Voice;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_Image;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoView;
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
    int[][] parameters_VOICE = new int[6][2];
    int[][] parameters_DICTATION = new int[6][2];
    int[][] parameters_NUMBERING = new int[6][2];
    int[][] parameters_DIALECT = new int[6][2];
    int[][] parameters_PHOTO = new int[6][2];
    int[][] parameters_BOXCROP = new int[6][2];

    int[][] parameters_OCREXAM = new int[6][2];
    int[][] parameters_VIDEOEXAM = new int[6][2];
    int[][] parameters_VOICEEXAM = new int[6][2];
    int[][] parameters_DICTATIONEXAM = new int[6][2];
    int[][] parameters_NUMBERINGEXAM = new int[6][2];
    int[][] parameters_DIALECTEXAM = new int[6][2];
    int[][] parameters_BOXCROPEXAM = new int[6][2];

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
        taskViewHashMap.put("photoview", new TaskView_PhotoView());
        //Controller hashmap 종류들
        controllerHashMap.put("edittext", new Controller_EditText());
        controllerHashMap.put("2dbox", new Controller_2DBox());
        //controllerHashMap.put("buttons", new Controller_Buttons());
        controllerHashMap.put("record", new Controller_Voice());
        controllerHashMap.put("numbers", new Controller_Numbers());
        controllerHashMap.put("photo", new Controller_Photo());

        examViewHashMap.put("edittext", new ExamView_EditText());
        examViewHashMap.put("buttons", new ExamView_Buttons());
        examViewHashMap.put("record", new ExamView_Voice());

        //OCR view-controller connect
        parameters_OCR[0][0] = 10;
        parameters_OCR[1][0] = R.id.tasktitle; parameters_OCR[1][1] = 4;
        parameters_OCR[2][0] = R.id.taskConstLayout; parameters_OCR[2][1] = 4;
        parameters_OCR[3][0] = R.id.taskConstLayout; parameters_OCR[3][1] = 3;
        parameters_OCR[4][0] = R.id.taskConstLayout; parameters_OCR[4][1] = 4;
        parameters_OCR[5][0] = 0;
        taskHashMap.put("OCR", parameters_OCR);

        parameters_OCREXAM[0][0] = 5;
        parameters_OCREXAM[1][0] = R.id.tasktitleexam; parameters_OCREXAM[1][1] = 4;
        parameters_OCREXAM[2][0] = R.id.examview; parameters_OCREXAM[2][1] = 3;
        parameters_OCREXAM[3][0] = R.id.taskviewexam; parameters_OCREXAM[3][1] = 4;
        parameters_OCREXAM[4][0] = R.id.taskConstLayoutexam; parameters_OCREXAM[4][1] = 4;
        parameters_OCREXAM[5][0] = 10;
        taskHashMap.put("OCREXAM", parameters_OCREXAM);

        parameters_VIDEO[0][0] = 5;
        parameters_VIDEO[1][0] = R.id.tasktitle; parameters_VIDEO[1][1] = 4;
        parameters_VIDEO[2][0] = R.id.controller; parameters_VIDEO[2][1] = 3;
        parameters_VIDEO[3][0] = R.id.taskview; parameters_VIDEO[3][1] = 4;
        parameters_VIDEO[4][0] = R.id.taskConstLayout; parameters_VIDEO[4][1] = 4;
        parameters_VIDEO[5][0] = 10;
        taskHashMap.put("VIDEO", parameters_VIDEO);

        parameters_VIDEOEXAM[0][0] = 5;
        parameters_VIDEOEXAM[1][0] = R.id.tasktitleexam; parameters_VIDEOEXAM[1][1] = 4;
        parameters_VIDEOEXAM[2][0] = R.id.examview; parameters_VIDEOEXAM[2][1] = 3;
        parameters_VIDEOEXAM[3][0] = R.id.taskviewexam; parameters_VIDEOEXAM[3][1] = 4;
        parameters_VIDEOEXAM[4][0] = R.id.taskConstLayoutexam; parameters_VIDEOEXAM[4][1] = 4;
        parameters_VIDEOEXAM[5][0] = 10;
        taskHashMap.put("VIDEOEXAM", parameters_VIDEOEXAM);

        parameters_VOICE[0][0] = 20;
        parameters_VOICE[1][0] = R.id.tasktitle; parameters_VOICE[1][1] = 4;
        parameters_VOICE[2][0] = R.id.controller; parameters_VOICE[2][1] = 3;
        parameters_VOICE[3][0] = R.id.taskview; parameters_VOICE[3][1] = 4;
        parameters_VOICE[4][0] = R.id.taskConstLayout; parameters_VOICE[4][1] = 4;
        parameters_VOICE[5][0] = 10;
        taskHashMap.put("RECORD", parameters_VOICE);

        parameters_VOICEEXAM[0][0] = 25;
        parameters_VOICEEXAM[1][0] = R.id.tasktitleexam; parameters_VOICEEXAM[1][1] = 4;
        parameters_VOICEEXAM[2][0] = R.id.examview; parameters_VOICEEXAM[2][1] = 3;
        parameters_VOICEEXAM[3][0] = R.id.taskviewexam; parameters_VOICEEXAM[3][1] = 4;
        parameters_VOICEEXAM[4][0] = R.id.taskConstLayoutexam; parameters_VOICEEXAM[4][1] = 4;
        parameters_VOICEEXAM[5][0] = 10;
        taskHashMap.put("RECORDEXAM", parameters_VOICEEXAM);

        parameters_DICTATION[0][0] = 5;
        parameters_DICTATION[1][0] = R.id.tasktitle; parameters_DICTATION[1][1] = 4;
        parameters_DICTATION[2][0] = R.id.taskConstLayout; parameters_DICTATION[2][1] = 4;
        parameters_DICTATION[3][0] = R.id.taskConstLayout; parameters_DICTATION[3][1] = 3;
        parameters_DICTATION[4][0] = R.id.taskConstLayout; parameters_DICTATION[4][1] = 4;
        parameters_DICTATION[5][0] = 10;
        taskHashMap.put("DICTATION", parameters_DICTATION);

        parameters_DICTATIONEXAM[0][0] = 5;
        parameters_DICTATIONEXAM[1][0] = R.id.tasktitleexam; parameters_DICTATIONEXAM[1][1] = 4;
        parameters_DICTATIONEXAM[2][0] = R.id.examview; parameters_DICTATIONEXAM[2][1] = 3;
        parameters_DICTATIONEXAM[3][0] = R.id.taskviewexam; parameters_DICTATIONEXAM[3][1] = 4;
        parameters_DICTATIONEXAM[4][0] = R.id.taskConstLayoutexam; parameters_DICTATIONEXAM[4][1] = 4;
        parameters_DICTATIONEXAM[5][0] = 10;
        taskHashMap.put("DICTATIONEXAM", parameters_DICTATIONEXAM);

        parameters_NUMBERING[0][0] = 10;
        parameters_NUMBERING[1][0] = R.id.option; parameters_NUMBERING[1][1] = 4;
        parameters_NUMBERING[2][0] = R.id.controller; parameters_NUMBERING[2][1] = 3;
        parameters_NUMBERING[3][0] = R.id.taskview; parameters_NUMBERING[3][1] = 4;
        parameters_NUMBERING[4][0] = R.id.taskConstLayout; parameters_NUMBERING[4][1] = 4;
        parameters_NUMBERING[5][0] = 5;
        taskHashMap.put("NUMBERING", parameters_NUMBERING);

        parameters_NUMBERINGEXAM[0][0] = 5;
        parameters_NUMBERINGEXAM[1][0] = R.id.tasktitleexam; parameters_NUMBERINGEXAM[1][1] = 4;
        parameters_NUMBERINGEXAM[2][0] = R.id.examview; parameters_NUMBERINGEXAM[2][1] = 3;
        parameters_NUMBERINGEXAM[3][0] = R.id.taskviewexam; parameters_NUMBERINGEXAM[3][1] = 4;
        parameters_NUMBERINGEXAM[4][0] = R.id.taskConstLayoutexam; parameters_NUMBERINGEXAM[4][1] = 4;
        parameters_NUMBERINGEXAM[5][0] = 10;
        taskHashMap.put("NUMBERINGEXAM", parameters_NUMBERINGEXAM);

        parameters_DIALECT[0][0] = 5;
        parameters_DIALECT[1][0] = R.id.option; parameters_DIALECT[1][1] = 4;
        parameters_DIALECT[2][0] = R.id.taskConstLayout; parameters_DIALECT[2][1] = 4;
        parameters_DIALECT[3][0] = R.id.taskConstLayout; parameters_DIALECT[3][1] = 3;
        parameters_DIALECT[4][0] = R.id.taskConstLayout; parameters_DIALECT[4][1] = 4;
        parameters_DIALECT[5][0] = 10;
        taskHashMap.put("DIALECT", parameters_DIALECT);

        parameters_DIALECTEXAM[0][0] = 5;
        parameters_DIALECTEXAM[1][0] = R.id.tasktitleexam; parameters_DIALECTEXAM[1][1] = 4;
        parameters_DIALECTEXAM[2][0] = R.id.examview; parameters_DIALECTEXAM[2][1] = 3;
        parameters_DIALECTEXAM[3][0] = R.id.taskviewexam; parameters_DIALECTEXAM[3][1] = 4;
        parameters_DIALECTEXAM[4][0] = R.id.taskConstLayoutexam; parameters_DIALECTEXAM[4][1] = 4;
        parameters_DIALECTEXAM[5][0] = 10;
        taskHashMap.put("DIALECTEXAM", parameters_DIALECTEXAM);

        parameters_PHOTO[0][0] = 5;
        parameters_PHOTO[1][0] = R.id.tasktitle; parameters_PHOTO[1][1] = 4;
        parameters_PHOTO[2][0] = R.id.controller; parameters_PHOTO[2][1] = 3;
        parameters_PHOTO[3][0] = R.id.taskview; parameters_PHOTO[3][1] = 4;
        parameters_PHOTO[4][0] = R.id.taskConstLayout; parameters_PHOTO[4][1] = 4;
        parameters_PHOTO[5][0] = 10;
        taskHashMap.put("PHOTO", parameters_PHOTO);

        parameters_BOXCROP[0][0] = 10;
        parameters_BOXCROP[1][0] = R.id.tasktitle; parameters_BOXCROP[1][1] = 4;
        parameters_BOXCROP[2][0] = R.id.taskConstLayout; parameters_BOXCROP[2][1] = 4;
        parameters_BOXCROP[3][0] = R.id.tasktitle; parameters_BOXCROP[3][1] = 4;
        parameters_BOXCROP[4][0] = R.id.taskConstLayout; parameters_BOXCROP[4][1] = 4;
        parameters_BOXCROP[5][0] = 10;
        taskHashMap.put("BOXCROP", parameters_BOXCROP);

    }

}