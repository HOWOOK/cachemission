package com.example.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Dialect;
import com.example.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_None;
import com.example.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Numbering;
import com.example.hwshin.cachemission.R;

public class TaskExplainActivity extends AppCompatActivity {

    Intent intent;
    String tasktype="";

    private ViewPager viewpager;
    private PagerAdapter myadapter;
    private ImageView exit;
    private TextView pagecnt;
    private int mCurrentPage;
    final int maxPage=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskexplain);

        viewpager=(ViewPager)findViewById(R.id.explainViewpager);
        intent = getIntent();
        tasktype = intent.getStringExtra("tasktype");

        myadapter= findAdaptingTaskExplain(tasktype);
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
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("RECORDEXAM"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("DICTATION"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("DICTATIONEXAM"))
            return new SlideAdapter_ExplainTask_None(this);
        else if (tasktype.equals("NUMBERING"))
            return new SlideAdapter_ExplainTask_Numbering(this);
        else if (tasktype.equals("DIALECT"))
            return new SlideAdapter_ExplainTask_Dialect(this);
        else
            return new SlideAdapter_ExplainTask_None(this);
    }

}
