package com.selectstar.hwshin.cashmission.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.selectstar.hwshin.cashmission.Adapter.Explain.SlideAdapter_ExplainMain;
import com.selectstar.hwshin.cachemission.R;

public class ExplainActivity extends AppCompatActivity {

    private LinearLayout liner;
    private ViewPager viewpager;
    private SlideAdapter_ExplainMain myadapter;

    private TextView[] mdots;
    private Button prev, next;

    private int mCurrentPage;
    final int maxPage=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);

        viewpager=(ViewPager)findViewById(R.id.explainViewpager);
        liner=(LinearLayout)findViewById(R.id.dots);

        myadapter=new SlideAdapter_ExplainMain(this);
        viewpager.setAdapter(myadapter);
        adddots(0);

        viewpager.addOnPageChangeListener(viewlistener);

        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPage == maxPage - 1)
                    finish();
                else
                    viewpager.setCurrentItem(mCurrentPage+1);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(mCurrentPage-1);
            }
        });

    }


    public void adddots(int i){

        mdots=new TextView[maxPage];
        liner.removeAllViews();

        for (int x=0; x < mdots.length; x++){
            mdots[x]=new TextView(this);
            mdots[x].setText(Html.fromHtml("&#8226;"));
            mdots[x].setTextSize(50);
            mdots[x].setTextColor(Color.parseColor("#90FFFFFF"));

            liner.addView(mdots[x]);
        }
        if (mdots.length>0){
            mdots[i].setTextColor(Color.parseColor("#FFFFFF"));
        }

    }

    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            adddots(position);
            mCurrentPage =position;

            if (position==0){
                next.setEnabled(true);
                prev.setEnabled(false);
                prev.setVisibility(View.INVISIBLE);

                next.setBackground(ContextCompat.getDrawable(ExplainActivity.this, R.drawable.main_explain_right));
            }
            else if(position == mdots.length-1){

                next.setEnabled(true);
                prev.setEnabled(true);
                prev.setVisibility(View.VISIBLE);

                next.setBackground(ContextCompat.getDrawable(ExplainActivity.this, R.drawable.main_explain_check));
                prev.setBackground(ContextCompat.getDrawable(ExplainActivity.this, R.drawable.main_explain_left));

            }
            else {
                next.setEnabled(true);
                prev.setEnabled(true);
                prev.setVisibility(View.VISIBLE);

                prev.setBackground(ContextCompat.getDrawable(ExplainActivity.this, R.drawable.main_explain_left));
                next.setBackground(ContextCompat.getDrawable(ExplainActivity.this, R.drawable.main_explain_right));
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}
