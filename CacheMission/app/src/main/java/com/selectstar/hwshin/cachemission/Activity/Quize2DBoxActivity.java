package com.selectstar.hwshin.cachemission.Activity;

import android.app.Dialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.Activity.GlobalApplication;
import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;

public class Quize2DBoxActivity extends PatherActivity {
    View controllerView;
    Controller mController;
    String buttons;
    Uri photoUri;
    Dialog explainDialog;
    ImageView backButton;
    ArrayList<String> pic=new ArrayList<>();
    //사투리특별전용옵션
    static String region_dialect;
    String questString="";
    int currentIndex=0;
    private TextView answerIDtv;

    public TaskView getmTaskView() {
        return this.mTaskView;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Quiz2DBoxActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        setContentView(R.layout.activity_quize_2dbox);
    }

    @Override
    public void startTask() {

    }

    @Override
    public void getNewTask() {

    }
}
