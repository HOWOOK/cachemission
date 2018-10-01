package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.RectF;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.selectstar.hwshin.cachemission.Photoview.OnMatrixChangedListener;
import com.selectstar.hwshin.cachemission.R;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_PhotoView extends TaskView {
    private PhotoView photoView;
    private float[][] answerCoordination;
    private float[][] changedCoordination;
    private String[] array1, array2, array3;
    private ConstraintLayout photoVIewCL;
    private ImageView[] answerList;
    private boolean expandFlag, isExamFlag;

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
        //플래그 처리
        isExamFlag = false;
        expandFlag = true;
        if(parentActivity.getTaskType().equals("BOXCROPEXAM"))
            isExamFlag = true;
        if(isExamFlag == true)
            expandFlag = false;

        photoVIewCL = parentActivity.findViewById(R.id.photoViewCL);

        //box 좌표 구해서 저장
        array1 = content.split("&");
        if(array1.length==2) {//좌표를 찾은적이 있는 놈이라면..
            array2 = array1[1].split("\\(");
            answerCoordination = new float[array2.length-1][4];
            changedCoordination = new float[array2.length-1][4];
            coordinationParsing(array2);
        }

        photoView = parentActivity.findViewById(R.id.srcview);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(parentActivity)
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ array1[0]))
                .into(new SimpleTarget<Drawable>(){
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        photoView.setImageDrawable(resource);
                        if(answerCoordination != null && isExamFlag) {

//                            System.out.print("-----안비어있지롱-----");
//                            System.out.println("길이 : "+answerCoordination.length);
//                            for (int i = 0; i<answerCoordination.length; i++){
//                                System.out.println("ㅁ"+i+"+1번째ㅁ");
//                                for (int j = 0; j<answerCoordination[i].length; j++) {
//                                    System.out.print(answerCoordination[i][j]+", ");
//                                }
//                                System.out.print("\n");
//                            }
//                            System.out.println("----------------------");

                            drawAnswer(answerCoordination);
                        }
                    }
                });


        photoView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if(answerCoordination!= null && isExamFlag)
                    drawAnswer(answerCoordination);
            }
        });


        photoView.setOnTouchListener(new View.OnTouchListener() {
            ImageView expandView = null;
            ConstraintLayout.LayoutParams expandViewParams;
            ImageView expandView2 = null;
            ConstraintLayout.LayoutParams expandView2Params;
            ConstraintSet constraintSet;
            float initX = 0;
            float initY = 0;
            float ratio = 0;
            float ratio2 = 0;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                photoView.getAttacher().onTouch(v, event);
                if(expandFlag) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN :
                            initX = event.getX();
                            initY = event.getY();
                            ratio = (float) photoVIewCL.getWidth() / (float) photoVIewCL.getHeight();

                            expandView = new ImageView(parentActivity);
                            expandViewParams = new ConstraintLayout.LayoutParams(0, 0);
                            expandView.setId(View.generateViewId());
                            expandView.setBackgroundResource(R.drawable.box_2dbox);
                            expandView.setLayoutParams(expandViewParams);
                            constraintSet = new ConstraintSet();
                            constraintSet.clone(photoVIewCL);
                            constraintSet.connect(expandView.getId(), ConstraintSet.START, photoVIewCL.getId(), ConstraintSet.START, (int) initX);
                            constraintSet.connect(expandView.getId(), ConstraintSet.TOP, photoVIewCL.getId(), ConstraintSet.TOP, (int) initY);
                            constraintSet.applyTo(photoVIewCL);
                            photoVIewCL.addView(expandView);

                            expandView2 = new ImageView(parentActivity);
                            expandView2Params = new ConstraintLayout.LayoutParams(0,0);
                            expandView2.setId(View.generateViewId());
                            expandView2.setBackgroundResource(R.drawable.box_2dbox2);
                            expandView2.setLayoutParams(expandViewParams);
                            constraintSet = new ConstraintSet();
                            constraintSet.clone(photoVIewCL);
                            constraintSet.connect(expandView2.getId(), ConstraintSet.START, photoVIewCL.getId(), ConstraintSet.START, (int) initX);
                            constraintSet.connect(expandView2.getId(), ConstraintSet.TOP, photoVIewCL.getId(), ConstraintSet.TOP, (int) initY);
                            constraintSet.applyTo(photoVIewCL);
                            photoVIewCL.addView(expandView2);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            int width = (int)(event.getX() - initX);
                            int height = (int)(event.getY() - initY);
                            int width2 = 0;
                            int height2 = 0;
                            float ratio2 = (float) width/(float) height;
                            float topMargin = 0;
                            float leftMargin = 0;
                            float topMargin2 = 0;
                            float leftMargin2 = 0;

                            //expandView 마진 설정
                            if(width >= 0 && height < 0){
                                height = -height;
                                topMargin = event.getY();
                                leftMargin = initX;
                            }else if(width < 0 && height < 0 ){
                                height = -height;
                                width = -width;
                                topMargin = event.getY();
                                leftMargin = event.getX();
                            }else if(width < 0 && height >= 0 ){
                                width = -width;
                                topMargin = initY;
                                leftMargin = event.getX();
                            }else if(width >= 0 && height >= 0 ){
                                topMargin = initY;
                                leftMargin = initX;
                            }

                            //expandView2 마진 설정
                            if(ratio2 < 0)
                                ratio2 = -ratio2;
                            if(ratio2 > ratio) { // 높이가 더 긴 디바이스
                                width2 = width;
                                height2 = (int) ((float)width2 / ratio);
                                leftMargin2  = leftMargin;
                                topMargin2 = topMargin - (height2 - height) / 2;
                            }else{// ratio < 1 // 너비가 더 긴 디바이스
                                height2 = height;
                                width2 = (int) ((float)height2 * ratio);
                                leftMargin2 = leftMargin - (width2 - width) / 2;
                                topMargin2 = topMargin;
                            }


                            if(expandView != null && expandView2 != null) {
                                expandViewParams = new ConstraintLayout.LayoutParams(width, height);
                                expandView.setLayoutParams(expandViewParams);

                                expandView2Params = new ConstraintLayout.LayoutParams(width2, height2);
                                expandView2.setLayoutParams(expandView2Params);

                                constraintSet = new ConstraintSet();
                                constraintSet.clone(photoVIewCL);
                                constraintSet.connect(expandView.getId(), ConstraintSet.START, photoVIewCL.getId(), ConstraintSet.START, (int) leftMargin);
                                constraintSet.connect(expandView.getId(), ConstraintSet.TOP, photoVIewCL.getId(), ConstraintSet.TOP, (int) topMargin);
                                constraintSet.connect(expandView2.getId(), ConstraintSet.START, photoVIewCL.getId(), ConstraintSet.START, (int) leftMargin2);
                                constraintSet.connect(expandView2.getId(), ConstraintSet.TOP, photoVIewCL.getId(), ConstraintSet.TOP, (int) topMargin2);
                                constraintSet.applyTo(photoVIewCL);

                            }
                            break;
                        case MotionEvent.ACTION_UP:

                            photoVIewCL.removeView(expandView);
                            photoVIewCL.removeView(expandView2);


                            break;
                    }
                }
                return true;

            }
        });


    }

    private void drawAnswer(float[][] answerCoordination) {
        coordinationChange(answerCoordination);

//        System.out.print("-----변환된 좌표-----");
//        System.out.println("길이 : "+answerCoordination.length);
//        for (int i = 0; i<answerCoordination.length; i++){
//            System.out.println("ㅁ"+i+"+1번째ㅁ");
//            for (int j = 0; j<answerCoordination[i].length; j++) {
//                System.out.print(answerCoordination[i][j]+", ");
//            }
//            System.out.print("\n");
//        }

        if(answerList == null) {
            answerList = new ImageView[changedCoordination.length];
            for(int i = 0; i < changedCoordination.length; i++){
                ImageView answer = new ImageView(parentActivity);
                answerList[i] = answer;
                ConstraintLayout.LayoutParams squareParams = new ConstraintLayout.LayoutParams(
                        (int) (changedCoordination[i][2] - changedCoordination[i][0]),
                        (int) (changedCoordination[i][3] - changedCoordination[i][1]));
                answerList[i].setId(View.generateViewId());
                answerList[i].setLayoutParams(squareParams);
                answerList[i].setBackgroundResource(R.drawable.box_2dbox);
                photoVIewCL.addView(answerList[i]);
            }
        }

        for(int i = 0; i < answerList.length; i++) {
            ConstraintLayout.LayoutParams squareParams = new ConstraintLayout.LayoutParams(
                    (int) (changedCoordination[i][2] - changedCoordination[i][0]),
                    (int) (changedCoordination[i][3] - changedCoordination[i][1]));
            answerList[i].setLayoutParams(squareParams);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(photoVIewCL);
            constraintSet.connect(answerList[i].getId(), ConstraintSet.START, photoVIewCL.getId(), ConstraintSet.START, (int) changedCoordination[i][0]);
            constraintSet.connect(answerList[i].getId(), ConstraintSet.TOP, photoVIewCL.getId(), ConstraintSet.TOP, (int) changedCoordination[i][1]);
            constraintSet.applyTo(photoVIewCL);
        }

    }

    //퍼센트 좌표를 현재 photoview의 스케일에 맞게 절대 좌표로 변경해준다.
    private void coordinationChange(float[][] answerCoordination) {
        float photoWidth, photoHeight;
        photoWidth = photoView.getDisplayRect().right - photoView.getDisplayRect().left;
        photoHeight = photoView.getDisplayRect().bottom - photoView.getDisplayRect().top;
        for (int i = 0; i<answerCoordination.length; i++){
            changedCoordination[i][0] = photoView.getDisplayRect().left + answerCoordination[i][0] * photoWidth; //left
            changedCoordination[i][1] = photoView.getDisplayRect().top + answerCoordination[i][1] * photoHeight + 2; //top (+2는 box line의 두께)
            changedCoordination[i][2] = photoView.getDisplayRect().left + answerCoordination[i][2] * photoWidth; //right
            changedCoordination[i][3] = photoView.getDisplayRect().top + answerCoordination[i][3] * photoHeight + 2; //bottom (+2는 box line의 두께)
        }
    }

    //String 형태로 서버에서 넘어온 좌표를 float[][] 형식으로 바꾸어 answerCoordination에 넣어줌
    private void coordinationParsing(String[] array) {
        for(int i =1; i < array2.length; i++){
            array3 = array2[i].split(",");
            answerCoordination[i-1][0] = (float) Float.parseFloat(array3[0]);
            answerCoordination[i-1][1] = (float) Float.parseFloat(array3[1]);
            answerCoordination[i-1][2] = (float) Float.parseFloat(array3[2]);
            answerCoordination[i-1][3] = (float) Float.parseFloat(array3[3]);
        }
    }




}