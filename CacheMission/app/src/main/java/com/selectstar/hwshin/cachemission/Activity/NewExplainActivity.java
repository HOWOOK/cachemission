package com.selectstar.hwshin.cachemission.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainMain;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Dialect;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_DirectRecord;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_None;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Numbering;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Photo;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record_ExamType1;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record_ExamType2;
import com.selectstar.hwshin.cachemission.Adapter.NewExplainAdapter;
import com.selectstar.hwshin.cachemission.R;

public class NewExplainActivity extends AppCompatActivity {
    Intent intent;
    String taskType="";
    String part="";
    int examType = 0;

    public ViewPager viewpager;
    private PagerAdapter myadapter;
    private ImageView exit;
    private TextView pagecnt;
    private int mCurrentPage;
    final int maxPage=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_explain);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("NewExplainActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        viewpager=(ViewPager)findViewById(R.id.newExplainViewpager);
        intent = getIntent();
        taskType = intent.getStringExtra("taskType");
        part = intent.getStringExtra("part");
        part = partTranslation(part);
        System.out.println("변환한 텍스트 : "+part);

        SharedPreferences taskToken = getSharedPreferences("taskToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = taskToken.edit();
        editor.putInt(taskType + "taskToken", 100);
        if(taskType.equals("RECORDEXAM")) {
            examType = intent.getIntExtra("examType", 0);
            editor.putInt(taskType + examType + "taskToken", 100);
        }
        editor.commit();

        myadapter= findAdaptingTaskExplain(taskType);
        viewpager.setAdapter(myadapter);
        viewpager.addOnPageChangeListener(viewlistener);


    }

    private String partTranslation(String part) {
        String result ="";
        if(part.equals("전신주"))
            result = "pole";
        if(part.equals("나무"))
            result = "tree";
        if(part.equals("변압기"))
            result = "transformer";
        return  result;
    }

    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            mCurrentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private PagerAdapter findAdaptingTaskExplain(String tasktype) {

        return new NewExplainAdapter(this,taskType+part);
    }
    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);


    }
    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


}
