package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_PhotoView extends TaskView {
    private PhotoView photoView;
    private float[][] answerCoordination;
    private String[] array1, array2, array3;
    private ConstraintLayout photoVIewCL;

    public TaskView_PhotoView()
    {
        taskViewID = R.layout.taskview_photoview;
    }

    public PhotoView getPhotoView() {
        return this.photoView;
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public void setContent(String content)
    {
        photoVIewCL = parentActivity.findViewById(R.id.photoViewCL);
        //box 좌표 구해서 저장
        array1 = content.split("&");
        if(array1.length==2) {//좌표를 찾은적이 있는 놈이라면..
            array2 = array1[1].split("\\(");
            answerCoordination = new float[array2.length][4];
            coordinationParsing(array2);
        }

        photoView = parentActivity.findViewById(R.id.srcview);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(parentActivity)
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ array1[0]))
                .thumbnail(0.1f)
                .into(new SimpleTarget<Drawable>(){
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        photoView.setImageDrawable(resource);
                        System.out.println("나여기들어옴1");
                        if(answerCoordination!=null) {
                            System.out.println("안비어있지롱 : "+answerCoordination);
                            coordinationChange(answerCoordination);
                            drawAnswer(answerCoordination);
                        }
                    }
                });


        photoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {




                return true;
            }
        });

    }

    private void drawAnswer(float[][] answerCoordination) {
        ConstraintLayout answerCL = new ConstraintLayout(parentActivity);
        answerCL.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
        ConstraintLayout mAnswerCL = (ConstraintLayout) answerCL;
        photoVIewCL.addView(mAnswerCL);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mAnswerCL);
        constraintSet.connect(answerCL.getId(), ConstraintSet.TOP, photoVIewCL.getId(), ConstraintSet.TOP);
        constraintSet.connect(answerCL.getId(), ConstraintSet.START, photoVIewCL.getId(),ConstraintSet.START);
        constraintSet.applyTo(mAnswerCL);

        ImageView answer = new ImageView(parentActivity);
        answer.setLayoutParams(new ConstraintLayout.LayoutParams(30, 30));
        answer.setBackgroundResource(R.drawable.box_2dbox);
        answerCL.addView(answer);


    }

    //퍼센트 좌표를 절대 좌표로 변경해준다.
    private void coordinationChange(float[][] answerCoordination) {
        float photoWidth, photoHeight;
        photoWidth = photoView.getDisplayRect().right - photoView.getDisplayRect().left;
        photoHeight = photoView.getDisplayRect().bottom - photoView.getDisplayRect().top;
        for (int i = 0; i<answerCoordination.length; i++){
            System.out.println("나여기들어옴3");
            answerCoordination[i][0] = answerCoordination[i][0] * photoWidth; //left
            answerCoordination[i][1] = answerCoordination[i][1] * photoHeight; //top
            answerCoordination[i][2] = answerCoordination[i][2] * photoWidth; //right
            answerCoordination[i][3] = answerCoordination[i][3] * photoHeight; //bottom
        }
    }

    //String 형태로 서버에서 넘어온 좌표를 float[][] 형식으로 바꾸어 answerCoordination에 넣어줌
    private void coordinationParsing(String[] array) {
        for(int i =1; i < array2.length; i++){
            System.out.println("나여기들어옴2");
            array3 = array2[i].split(",");
            answerCoordination[i-1][0] = (float) Float.parseFloat(array3[1]);
            answerCoordination[i-1][1] = (float) Float.parseFloat(array3[2]);
            answerCoordination[i-1][2] = (float) Float.parseFloat(array3[3]);
            answerCoordination[i-1][3] = (float) Float.parseFloat(array3[4]);
        }
    }
}