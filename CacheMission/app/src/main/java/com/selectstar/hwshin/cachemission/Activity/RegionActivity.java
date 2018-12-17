package com.selectstar.hwshin.cachemission.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.R;

public class RegionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr")) {
            Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
            t.setScreenName("RegionActivity");
            t.send(new HitBuilders.AppViewBuilder().build());
        }

        setContentView(R.layout.dialog_regionselect);

        ImageView iv1 = findViewById(R.id.chungbuk);
        ImageView iv2 = findViewById(R.id.kyeongbuk);
        ImageView iv3 = findViewById(R.id.jeonbuk);
        ImageView iv4 = findViewById(R.id.chungnam);
        ImageView iv5 = findViewById(R.id.kyeongnam);
        ImageView iv6 = findViewById(R.id.jeonnam);
        ImageView iv7 = findViewById(R.id.kangwon);
        ImageView iv8 = findViewById(R.id.jeju);
        String region;
        SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
        region = explain.getString("region",null);

        Intent intent;
        intent=getIntent();
        if(intent.getStringExtra("wanttochange").toString().equals("false") && region != null)
            finish();

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "충북";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "경북";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });

        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "전북";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });

        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "충남";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });

        iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "경남";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });

        iv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "전남";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });

        iv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "강원";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });

        iv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskActivity.region_dialect = "제주";
                SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
                SharedPreferences.Editor editor = explain.edit();
                editor.putString("region", TaskActivity.region_dialect);
                editor.commit();
                finish();
            }
        });


    }
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
