package com.selectstar.hwshin.cashmission.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.selectstar.hwshin.cashmission.R;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        TextView textView = findViewById(R.id.bombom);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.goldtranslate);
        textView.startAnimation(animation);

    }
}
