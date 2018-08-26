package com.example.hwshin.cachemission.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hwshin.cachemission.Adapter.SlideAdapter;
import com.example.hwshin.cachemission.R;

public class ExplainActivity extends AppCompatActivity {

    private LinearLayout liner;
    private ViewPager viewpager;
    private SlideAdapter myadapter;

    private TextView[] mdots;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);

        viewpager=(ViewPager)findViewById(R.id.explainViewpager);
        liner=(LinearLayout)findViewById(R.id.dots);

        myadapter=new SlideAdapter(this);
        viewpager.setAdapter(myadapter);
        adddots(0);

        viewpager.addOnPageChangeListener(viewlistener);

    }


    public void adddots(int i){

        mdots=new TextView[4];
        liner.removeAllViews();

        for (int x=0;x<mdots.length;x++){
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
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}
