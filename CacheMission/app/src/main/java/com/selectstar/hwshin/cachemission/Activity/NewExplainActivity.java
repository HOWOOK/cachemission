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

import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainMain;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Dialect;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_DirectRecord;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_None;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Numbering;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Photo;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record_ExamType1;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record_ExamType2;
import com.selectstar.hwshin.cachemission.R;

public class NewExplainActivity extends AppCompatActivity {
    Intent intent;
    String taskType="";
    int examType = 0;

    private ViewPager viewpager;
    private PagerAdapter myadapter;
    private ImageView exit;
    private TextView pagecnt;
    private int mCurrentPage;
    final int maxPage=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_explain);

        viewpager=(ViewPager)findViewById(R.id.newExplainViewpager);
        intent = getIntent();
        taskType = intent.getStringExtra("taskType");

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

        if(tasktype.equals("OCR"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("OCREXAM"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("VIDEO"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("VIDEOEXAM"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("RECORD"))
            return new SlideAdapter_ExplainTask_Record(this);
        else if (tasktype.equals("RECORDEXAM") && examType == 1)
            return new SlideAdapter_ExplainTask_Record_ExamType1(this);
        else if (tasktype.equals("RECORDEXAM") && examType == 2)
            return new SlideAdapter_ExplainTask_Record_ExamType2(this);
        else if (tasktype.equals("DIRECTRECORD"))
            return new SlideAdapter_ExplainTask_DirectRecord(this);
        else if (tasktype.equals("DICTATION"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("DICTATIONEXAM"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("NUMBERING"))
            return new SlideAdapter_ExplainTask_Numbering(this);
        else if (tasktype.equals("DIALECT"))
            return new SlideAdapter_ExplainTask_Dialect(this);
        else if (tasktype.equals("PHOTO"))
            return new SlideAdapter_ExplainTask_Photo(this);
        else
            return new SlideAdapter_ExplainTask_None(this);
    }

}
