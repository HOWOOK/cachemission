package com.selectstar.hwshin.cachemission.Activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.R;


public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        class LineView extends View{
            private Paint paint = new Paint();
            private Path[] paths;

            public LineView(Context context){//one Answer
                super(context);
                paint.setAntiAlias(true);
                paint.setStrokeWidth(3f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setColor(this.getResources().getColor(R.color.colorPoint1));
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(500,500);
                LineView.this.setLayoutParams(params);
            }


            public void onDraw(Canvas canvas){
                for(int i = 0; i < paths.length; i++){
                    canvas.drawPath(paths[i], paint);
                }
            }

        }
        ConstraintLayout testCL = findViewById(R.id.testCL);
        Canvas canvas = new Canvas();
        LineView answerLineView = new LineView(this);
        LineView drawLineView = new LineView(this);
        answerLineView.setId(View.generateViewId());
        drawLineView.setId(View.generateViewId());
        testCL.addView(answerLineView);
        testCL.addView(drawLineView);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) answerLineView.getLayoutParams();
        layoutParams.topMargin = 150;
        layoutParams.leftMargin = 150;
        answerLineView.setLayoutParams(layoutParams);
        answerLineView.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));

        layoutParams = (ConstraintLayout.LayoutParams) drawLineView.getLayoutParams();
        layoutParams.topMargin = 700;
        layoutParams.leftMargin = 150;
        drawLineView.setLayoutParams(layoutParams);
        drawLineView.setBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark));


        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(testCL);
        constraintSet.connect(answerLineView.getId(), ConstraintSet.TOP, testCL.getId(), ConstraintSet.TOP);
        constraintSet.connect(answerLineView.getId(), ConstraintSet.LEFT, testCL.getId(), ConstraintSet.LEFT);
        constraintSet.connect(drawLineView.getId(), ConstraintSet.TOP, testCL.getId(), ConstraintSet.TOP);
        constraintSet.connect(drawLineView.getId(), ConstraintSet.LEFT, testCL.getId(), ConstraintSet.LEFT);
        constraintSet.applyTo(testCL);



        Path[] answerPaths = new Path[2];
        for(int i = 0; i < answerPaths.length; i++){
            answerPaths[i] = new Path();
        }
        answerPaths[0].moveTo(600,600);
        answerPaths[0].lineTo(700,700);
        answerPaths[1].moveTo(150,100);
        answerPaths[1].lineTo(200,250);

        Path[] drawPaths = new Path[1];
        for(int i = 0; i < drawPaths.length; i++){
            drawPaths[i] = new Path();
        }
        drawPaths[0].moveTo(-100,-100);
        drawPaths[0].lineTo(-200,-200);


        answerLineView.paths = answerPaths;
        answerLineView.draw(canvas);

        drawLineView.paths = drawPaths;
        drawLineView.draw(canvas);














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
