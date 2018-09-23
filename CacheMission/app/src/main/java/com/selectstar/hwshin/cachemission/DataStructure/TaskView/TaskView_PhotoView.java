package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cachemission.DataStructure.MyEventListener;
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
        //private MyEventListener mListener;
        photoVIewCL = parentActivity.findViewById(R.id.photoViewCL);

        //box 좌표 구해서 저장
        array1 = content.split("&");
        if(array1.length==2) {//좌표를 찾은적이 있는 놈이라면..
            array2 = array1[1].split("\\(");
            answerCoordination = new float[array2.length-1][4];
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
                        System.out.println("나여기들어옴1");
                        if(answerCoordination != null) {

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



        photoView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                System.out.println(" 나 불렷음 ㅇㅇㅇㅇㅇㅇㅇㅇㅇ");
                drawAnswer(answerCoordination);
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

        for(int i = 0; i < answerCoordination.length; i++) {

            ImageView answer = new ImageView(parentActivity);
            ConstraintLayout.LayoutParams squareParams = new ConstraintLayout.LayoutParams(
                    (int) (answerCoordination[i][2] - answerCoordination[i][0]),
                    (int) (answerCoordination[i][3] - answerCoordination[i][1]));
            answer.setId(View.generateViewId());
            answer.setLayoutParams(squareParams);
            answer.setBackgroundResource(R.drawable.box_2dbox);
            photoVIewCL.addView(answer);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(photoVIewCL);
            constraintSet.connect(answer.getId(), ConstraintSet.START, photoVIewCL.getId(), ConstraintSet.START, (int) answerCoordination[i][0]);
            constraintSet.connect(answer.getId(), ConstraintSet.TOP, photoVIewCL.getId(), ConstraintSet.TOP, (int) answerCoordination[i][1]);
            constraintSet.applyTo(photoVIewCL);
        }

    }

    //퍼센트 좌표를 현재 photoview의 스케일에 맞게 절대 좌표로 변경해준다.
    private void coordinationChange(float[][] answerCoordination) {
        float photoWidth, photoHeight;
        photoWidth = photoView.getDisplayRect().right - photoView.getDisplayRect().left;
        photoHeight = photoView.getDisplayRect().bottom - photoView.getDisplayRect().top;
        for (int i = 0; i<answerCoordination.length; i++){
            answerCoordination[i][0] = photoView.getDisplayRect().left + answerCoordination[i][0] * photoWidth; //left
            answerCoordination[i][1] = photoView.getDisplayRect().top + answerCoordination[i][1] * photoHeight + 2; //top (+2는 box line의 두께)
            answerCoordination[i][2] = photoView.getDisplayRect().left + answerCoordination[i][2] * photoWidth; //right
            answerCoordination[i][3] = photoView.getDisplayRect().top + answerCoordination[i][3] * photoHeight + 2; //bottom (+2는 box line의 두께)
        }
    }

    //String 형태로 서버에서 넘어온 좌표를 float[][] 형식으로 바꾸어 answerCoordination에 넣어줌
    private void coordinationParsing(String[] array) {
        for(int i =1; i < array2.length; i++){
            System.out.println("나여기들어옴2");
            array3 = array2[i].split(",");
            answerCoordination[i-1][0] = (float) Float.parseFloat(array3[0]);
            answerCoordination[i-1][1] = (float) Float.parseFloat(array3[1]);
            answerCoordination[i-1][2] = (float) Float.parseFloat(array3[2]);
            answerCoordination[i-1][3] = (float) Float.parseFloat(array3[3]);
        }
    }




}