package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.Matrix;
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
    private ConstraintLayout photoViewCL;
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

        photoViewCL = parentActivity.findViewById(R.id.photoViewCL);

        //box 좌표 구해서 저장
        array1 = content.split("&");
        if(array1.length==2) {//좌표를 찾은적이 있는 놈이라면..
            array2 = array1[1].split("\\(");
            answerCoordination = new float[array2.length-1][4];
            changedCoordination = new float[array2.length-1][4];
            coordinationParsing(array2);
        }

        photoView = parentActivity.findViewById(R.id.srcview);
        photoView.setMaximumScale(10);
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
            float initX = 0;
            float initY = 0;
            float pvRatio = 0;
            int space = 50;
            float saveWidth = 0;
            float saveHeight = 0;
            float saveX = 0;
            float saveY = 0;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                photoView.getAttacher().onTouch(v, event);
                if(expandFlag) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN :
                            initX = event.getX();
                            initY = event.getY();
                            pvRatio = (float) photoViewCL.getWidth() / (float) photoViewCL.getHeight();
                            System.out.println("포토뷰 비율 : "+ pvRatio );

                            expandView = new ImageView(parentActivity);
                            expandViewParams = new ConstraintLayout.LayoutParams(0, 0);
                            expandView.setId(View.generateViewId());
                            expandView.setBackgroundResource(R.drawable.box_2dbox);
                            expandView.setLayoutParams(expandViewParams);
                            photoViewCL.addView(expandView);
                            updateConstraintSet(expandView, (int) initX, (int) initY);

                            expandView2 = new ImageView(parentActivity);
                            expandView2Params = new ConstraintLayout.LayoutParams(0,0);
                            expandView2.setId(View.generateViewId());
                            expandView2.setBackgroundResource(R.drawable.box_2dbox2);
                            expandView2.setLayoutParams(expandViewParams);
                            photoViewCL.addView(expandView2);
                            updateConstraintSet(expandView2, (int) initX, (int) initY);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            int width = (int)(event.getX() - initX);
                            int height = (int)(event.getY() - initY);
                            float topMargin = 0;
                            float leftMargin = 0;
                            int width2 = 0;
                            int height2 = 0;
                            float expandBoxRatio = (float) width/(float) height;

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
                            if(expandBoxRatio < 0)
                                expandBoxRatio = -expandBoxRatio;
                            if(expandBoxRatio > pvRatio) {
                                width2 = width + space;
                                height2 = (int) ((float)width2 / pvRatio);
                            }else{// ratio < 1
                                height2 = height + space;
                                width2 = (int) ((float)height2 * pvRatio);
                            }

                            saveWidth = (float) width2;
                            saveHeight = (float) height2;
                            saveX = (float) expandView2.getX();
                            saveY = (float) expandView2.getY();

                            if(expandView != null && expandView2 != null) {
                                expandViewParams = new ConstraintLayout.LayoutParams(width, height);
                                expandView.setLayoutParams(expandViewParams);
                                updateConstraintSet(expandView, (int)leftMargin, (int) topMargin);

                                expandView2Params = new ConstraintLayout.LayoutParams(width2, height2);
                                expandView2.setLayoutParams(expandView2Params);
                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(photoViewCL);
                                constraintSet.connect(expandView2.getId(), ConstraintSet.START, expandView.getId(), ConstraintSet.START);
                                constraintSet.connect(expandView2.getId(), ConstraintSet.END, expandView.getId(), ConstraintSet.END);
                                constraintSet.connect(expandView2.getId(), ConstraintSet.TOP, expandView.getId(), ConstraintSet.TOP);
                                constraintSet.connect(expandView2.getId(), ConstraintSet.BOTTOM, expandView.getId(), ConstraintSet.BOTTOM);
                                constraintSet.applyTo(photoViewCL);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            Matrix newSuppMatrix = new Matrix();
                            updateSuppMatrix(newSuppMatrix, saveWidth, saveHeight, saveX, saveY);

                            photoViewCL.removeView(expandView);
                            photoViewCL.removeView(expandView2);
                            expandFlag = false;
                            break;
                    }
                }
                return true;

            }
        });

    }

    //photoViewCL의 ConstraintSet을 업데이트한다.
    private void updateConstraintSet(ImageView targetView, int leftMargin, int topMargin) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(photoViewCL);
        constraintSet.connect(targetView.getId(), ConstraintSet.START, photoViewCL.getId(), ConstraintSet.START, leftMargin);
        constraintSet.connect(targetView.getId(), ConstraintSet.TOP, photoViewCL.getId(), ConstraintSet.TOP, topMargin);
        constraintSet.applyTo(photoViewCL);
    }

    //확대영역을 결정하기 위해 mSuppMatrix를 업데이트 해준다.
    private void updateSuppMatrix(Matrix newSuppMatrix, float saveWidth, float saveHeight, float saveX, float saveY) {
        Matrix hackMatrix1 = new Matrix();  //mBaseMatrix
        float[] var = new float[9];
        float[] hackVar1 = new float[9];
        float scale = 1;
        float left, top;

        scale = (float) photoViewCL.getWidth() / saveWidth;
        left = (photoView.getDisplayRect().left - saveX) * scale;
        top = (photoView.getDisplayRect().top - saveY) * scale;
        hackMatrix1 = photoView.getAttacher().mBaseMatrix;
        hackMatrix1.getValues(hackVar1);

        var[0] = scale;
        var[1] = 0;
        if(hackVar1[2] != 0)
            var[2] = left - (scale * hackVar1[2]);
        else
            var[2] = left;
        var[3] = 0;
        var[4] = scale;
        if(hackVar1[5] != 0)
            var[5] = top - (scale * hackVar1[5]);
        else
            var[5] = top;
        var[6] = 0;
        var[7] = 0;
        var[8] = 1;

        newSuppMatrix.setValues(var);
        photoView.setSuppMatrix(newSuppMatrix);
    }

    //좌표를 토대로 photoview에 라벨링된 데이터를 그려준다.
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
                photoViewCL.addView(answerList[i]);
            }
        }

        for(int i = 0; i < answerList.length; i++) {
            ConstraintLayout.LayoutParams squareParams = new ConstraintLayout.LayoutParams(
                    (int) (changedCoordination[i][2] - changedCoordination[i][0]),
                    (int) (changedCoordination[i][3] - changedCoordination[i][1]));
            answerList[i].setLayoutParams(squareParams);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(photoViewCL);
            constraintSet.connect(answerList[i].getId(), ConstraintSet.START, photoViewCL.getId(), ConstraintSet.START, (int) changedCoordination[i][0]);
            constraintSet.connect(answerList[i].getId(), ConstraintSet.TOP, photoViewCL.getId(), ConstraintSet.TOP, (int) changedCoordination[i][1]);
            constraintSet.applyTo(photoViewCL);
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