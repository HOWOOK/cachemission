package com.selectstar.hwshin.cashmission.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.hwshin.cachemission.R;

public class RegionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        ImageView iv1 = findViewById(R.id.region1);
        ImageView iv2 = findViewById(R.id.region2);
        ImageView iv3 = findViewById(R.id.region3);
        ImageView iv4 = findViewById(R.id.region4);
        ImageView iv5 = findViewById(R.id.region5);
        ImageView iv6 = findViewById(R.id.region6);
        ImageView iv7 = findViewById(R.id.region7);
        ImageView iv8 = findViewById(R.id.region8);

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
}
