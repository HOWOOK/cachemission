package com.selectstar.hwshin.cachemission.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.R;


public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        boolean check;
        RelativeLayout testL = findViewById(R.id.testL);

        check  = testL.isHardwareAccelerated();
        System.out.println("체크 : "+check);

        testL.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        System.out.println("체크 : "+check);




    }
}
