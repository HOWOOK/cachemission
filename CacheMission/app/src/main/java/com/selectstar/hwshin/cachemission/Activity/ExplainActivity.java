package com.selectstar.hwshin.cachemission.Activity;

import android.content.Intent;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainMain;
import com.selectstar.hwshin.cachemission.R;

public class ExplainActivity extends AppCompatActivity {

    private LinearLayout liner;
    private ViewPager viewPager;
    private SlideAdapter_ExplainMain myAdapter;

    private TextView[] mDots;
    private Button prev, next;

    private int mCurrentPage;
    final int maxPage=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr")) {
            Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
            t.setScreenName("ExplainActivity");
            t.send(new HitBuilders.AppViewBuilder().build());
        }


            viewPager = (ViewPager) findViewById(R.id.explainViewpager);
            liner = (LinearLayout) findViewById(R.id.dots);

            myAdapter = new SlideAdapter_ExplainMain(this);
            viewPager.setAdapter(myAdapter);
            adddots(0);

            viewPager.addOnPageChangeListener(viewListener);

            prev = findViewById(R.id.prev);
            next = findViewById(R.id.next);

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentPage == maxPage - 1)
                        finish();
                    else
                        viewPager.setCurrentItem(mCurrentPage + 1);
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(mCurrentPage - 1);
                }
            });


    }


    public void adddots(int i){

        mDots=new TextView[maxPage];
        liner.removeAllViews();

        for (int x=0; x < mDots.length; x++){
            mDots[x]=new TextView(this);
            mDots[x].setText(Html.fromHtml("&#8226;"));
            mDots[x].setTextSize(50);
            mDots[x].setTextColor(Color.parseColor("#90FFFFFF"));

            liner.addView(mDots[x]);
        }
        if (mDots.length>0){
            mDots[i].setTextColor(Color.parseColor("#FFFFFF"));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
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
            else if(position == mDots.length-1){

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
    @Override
    protected void onStart(){
        super.onStart();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStart(this);


    }
    @Override
    protected void onStop(){
        super.onStop();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


}
