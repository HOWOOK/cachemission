package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.selectstar.hwshin.cachemission.Photoview.OnMatrixChangedListener;

import com.selectstar.hwshin.cachemission.R;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.BitmapTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

public class TaskView_PhotoView extends TaskView {
    private PhotoView photoView;
    public float[][] answerCoordination;
    public float[][] changedCoordination;
    public int[] answerType; //0이면 서버에서 보내준거, 1이면 유저가 그린거
    private String[] array0, array1, array2, array3;
    private ConstraintLayout photoViewCL;
    public ConstraintLayout[] answerList;
    public View[][] answerEdges;
    public boolean expandFlag, isExamFlag, ImageReady;
    private int getDeviceDpi;
    private float dpScale;
    private int answerCount = 0;
    private int drawAnswerCount = 0;
    private HashMap<String,Bitmap> bitmaps;

    public TaskView_PhotoView()
    {
        taskViewID = R.layout.taskview_photoview;
        bitmaps = new HashMap<>();
    }

    public PhotoView getPhotoView() {
        return this.photoView;
    }

    //사진 url을 서버에서 받아와서 미리 로딩해놓는 함수이다.
    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;


        try{
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(connection!=null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list)
    {
        try {
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.get(i);
                final String content = jsonObject.get("content").toString();
                String[] preArray0 = content.split(">");
                final String[] preArray1 = preArray0[1].split("\\*");
                Log.d("arrayset",preArray0[1]);
                new Thread() {
                    public void run() {

                        Bitmap bitmap = getBitmap(parentActivity.getString(R.string.mainurl) + "/media/" + preArray1[0]);
                        bitmaps.put(preArray1[0], bitmap);


                    }
                }.start();



            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    public void setContent(String content)
    {
        Log.d("contenttttt",content);
        //플래그 처리
        ImageReady = false;
        isExamFlag = false;
        expandFlag = true;
        if(parentActivity.getTaskType().equals("BOXCROPEXAM"))
            isExamFlag = true;
        if(isExamFlag == true)
            expandFlag = false;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDeviceDpi = displayMetrics.densityDpi;
        dpScale = (float) getDeviceDpi / 160f;

        photoViewCL = parentActivity.findViewById(R.id.photoViewCL);
        if(parentActivity.getPartNum() == 2){ // 프리프로세싱은 레이아웃이 좀 달라야함
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT);
            params.bottomMargin= (int) (43 * dpScale);
            photoViewCL.setLayoutParams(params);
        }


        if(isExamFlag)
            parentActivity.findViewById(R.id.textDragCL).setVisibility(View.INVISIBLE);

        //box 좌표 구해서 저장
        // *content의 양식 =>    확대좌표>URI*<답<답<답...
        //                      f,f,f,f>String*<f,f,f,f<f,f,f,f<f,f,f,f....
        array0 = content.split(">");
        array1 = array0[1].split("\\*");
        if(array1.length == 2) {//좌표를 찾은적이 있는 놈이라면..
            array2 = array1[1].split("<");
            if (!(array2.length == 0)) {
                answerCoordination = new float[array2.length - 1][4];
                changedCoordination = new float[answerCoordination.length][4];
                answerType = new int[answerCoordination.length];
                answerCount = answerCoordination.length;
                coordinationParsing(array2);
            }
        }

        photoView = parentActivity.findViewById(R.id.srcview);
        photoView.setMaximumScale(10);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        System.out.println("뭐누아ㅣㅁ너ㅜ이ㅏ"+parentActivity.getString(R.string.mainurl)+"/media/"+array1[0]);

        if(bitmaps.containsKey(array1[0])) {
            Log.d("imin",array1[0]);
            Glide.with(parentActivity)
                    .asBitmap()
                    .load(bitmaps.get(array1[0]))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            resource = cropBitmap(resource, array0[0]);
                            photoView.setImageBitmap(resource);
                            ImageReady = true;

                            if (answerCoordination != null) {
                                System.out.println("그릴때");
                                for (int i = 0; i < answerCoordination.length; i++) {
                                    System.out.print("(");
                                    for (int j = 0; j < 4; j++) {
                                        System.out.print(answerCoordination[i][j] + ",");
                                    }
                                    System.out.print(" 타입 : " + answerType[i]);
                                    System.out.println(")");
                                }
                                drawAnswer(answerCoordination);
                                parentActivity.findViewById(R.id.textDragCL).bringToFront();
                            }
                            else{
                                Log.d("aswr:null","shit");
                            }
                        }
                    });
        }
        else {

            Glide.with(parentActivity)
                    .asBitmap()
                    .load(Uri.parse(parentActivity.getString(R.string.mainurl) + "/media/" + array1[0]))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            resource = cropBitmap(resource, array0[0]);
                            photoView.setImageBitmap(resource);
                            ImageReady = true;

                            if (answerCoordination != null) {
                                System.out.println("그릴때");
                                for (int i = 0; i < answerCoordination.length; i++) {
                                    System.out.print("(");
                                    for (int j = 0; j < 4; j++) {
                                        System.out.print(answerCoordination[i][j] + ",");
                                    }
                                    System.out.print(" 타입 : " + answerType[i]);
                                    System.out.println(")");
                                }
                                drawAnswer(answerCoordination);
                                parentActivity.findViewById(R.id.textDragCL).bringToFront();
                            }
                            else{
                                Log.d("aswr:null","shit");
                            }
                        }
                    });
        }

        photoView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if(answerCoordination!= null) {
                    drawAnswer(answerCoordination);
                    parentActivity.findViewById(R.id.textDragCL).bringToFront();
                }
            }
        });
        photoView.setOnTouchListener(new View.OnTouchListener() {
            ImageView expandView = parentActivity.findViewById(R.id.expandView);
            ConstraintLayout.LayoutParams expandViewParams;
            ImageView expandView2 = parentActivity.findViewById(R.id.expandView2);
            ConstraintLayout.LayoutParams expandView2Params;
            float initX = 0;
            float initY = 0;
            float pvRatio = 0;
            int space = 100;
            float saveWidth = 0;
            float saveHeight = 0;
            float saveX = 0;
            float saveY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout testDragCL = parentActivity.findViewById(R.id.textDragCL);
                if(expandFlag && ImageReady) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN :
                            initX = event.getX();
                            initY = event.getY();
                            pvRatio = (float) photoViewCL.getWidth() / (float) photoViewCL.getHeight();
                            System.out.println("포토뷰 비율 : "+ pvRatio );

                            expandView.setBackgroundResource(R.drawable.box_2dbox3);
                            updateConstraintSet1(expandView, (int) initX, (int) initY);

                            //expandView2.setBackgroundResource(R.drawable.box_2dbox2);
                            updateConstraintSet1(expandView2, (int) initX, (int) initY);
                            testDragCL.setVisibility(View.INVISIBLE);
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
                                updateConstraintSet1((View) expandView, (int)leftMargin, (int) topMargin);

                                expandView2Params = new ConstraintLayout.LayoutParams(width2, height2);
                                expandView2.setLayoutParams(expandView2Params);
                                updateConstraintSet2(expandView2, expandView);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            Matrix newSuppMatrix = new Matrix();
                            float[] deltaSettingValue = new float[4];
                            deltaSettingValue = CalDeltaSettingValue(expandView, expandView2);
                            updateSuppMatrix(newSuppMatrix, saveWidth, saveHeight, saveX, saveY);
                            boxSetting(expandView, expandView2, deltaSettingValue);

                            expandViewParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                            expandView.setLayoutParams(expandViewParams);
                            expandView.setBackgroundColor(parentActivity.getResources().getColor(R.color.photoViewExpandHackColor));
                            expandView2Params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                            expandView2.setLayoutParams(expandView2Params);
                            expandView2.setBackgroundColor(parentActivity.getResources().getColor(R.color.photoViewExpandHackColor));
                            expandFlag = false;
                            break;
                    }
                }else{
                    photoView.getAttacher().onTouch(v, event);
                }
                return true;

            }
        });
    }

    //확대 후에도 뷰에 여백이 남는경우 남은 여백이 쌍방으로 균등하게 배분되는데, 그로인한 cropBox 제조정을 위해 이동 델타 값을 구하는 함수
    private float[] CalDeltaSettingValue(View expandView, View expandView2) {
        ConstraintLayout.LayoutParams expandViewParams = (ConstraintLayout.LayoutParams) expandView.getLayoutParams();
        ConstraintLayout.LayoutParams expandView2Params = (ConstraintLayout.LayoutParams) expandView2.getLayoutParams();
        float topSpace, bottomSpace, leftSpace, rightSpace;
        float deltaValTop = 0;
        float deltaValBottom = 0;
        float deltaValLeft = 0;
        float deltaValRight = 0;
        float[] returnVal;

        returnVal = new float[4];
        topSpace = photoView.getDisplayRect().top - ((float)expandViewParams.topMargin + (float)expandViewParams.height / 2.0f - (float)expandView2Params.height / 2.0f);
        bottomSpace =  - photoView.getDisplayRect().bottom + ((float)expandViewParams.topMargin + (float)expandViewParams.height / 2.0f + (float)expandView2Params.height / 2.0f);
        leftSpace = photoView.getDisplayRect().left - ((float)expandViewParams.leftMargin + (float)expandViewParams.width / 2.0f - (float)expandView2Params.width / 2.0f);
        rightSpace = - photoView.getDisplayRect().right + ((float)expandViewParams.leftMargin + (float)expandViewParams.width / 2.0f + (float)expandView2Params.width / 2.0f);
        System.out.println("위 여백 : " + topSpace + " 아래 여백 : " + bottomSpace + " 왼 여백 : " + leftSpace + " 오른 여백 : " + rightSpace);

        if(topSpace > 0){
            deltaValTop = - topSpace;
            deltaValBottom = topSpace;
        }

        if(bottomSpace > 0){
            deltaValTop =  bottomSpace;
            deltaValBottom = - bottomSpace;
        }

        if(leftSpace > 0){
            deltaValLeft = - leftSpace;
            deltaValRight = leftSpace;
        }

        if(rightSpace > 0){
            deltaValLeft = rightSpace;
            deltaValRight = - rightSpace;
        }

        if(topSpace + bottomSpace > 0){
            deltaValTop = ((bottomSpace - topSpace) / 2.0f);
            deltaValBottom = - ((bottomSpace - topSpace) / 2.0f);
        }

        if(leftSpace + rightSpace > 0){
            deltaValLeft = ((rightSpace - leftSpace) / 2.0f);
            deltaValRight = - ((rightSpace - leftSpace) / 2.0f);
        }

        returnVal[0] = deltaValLeft;
        returnVal[1] = deltaValTop;
        returnVal[2] = deltaValRight;
        returnVal[3] = deltaValBottom;

        return  returnVal;
    }

    //Answer를 바탕으로 박스를 그릴 때 어디에 그릴지 셋팅해주는 함수
    private void boxSetting(View expandView, View expandView2, float[] deltaSettingValue) {
        ConstraintLayout boxCL = parentActivity.findViewById(R.id.boxCL);
        ConstraintLayout.LayoutParams expandViewParams = (ConstraintLayout.LayoutParams) expandView.getLayoutParams();
        ConstraintLayout.LayoutParams expandView2Params = (ConstraintLayout.LayoutParams) expandView2.getLayoutParams();
        float scale;
        View topLine, bottomLine, leftLine, rightLine, topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner;
        ConstraintLayout.LayoutParams topLineParams, bottomLineParams, leftLineParams, rightLineParams, topLeftCornerParams, topRightCornerParams, bottomLeftCornerParams, bottomRightCornerParams;
        float setValTop, setValBottom, setValLeft, setValRight;

        topLine = parentActivity.findViewById(R.id.top_line);
        bottomLine = parentActivity.findViewById(R.id.bottom_line);
        leftLine = parentActivity.findViewById(R.id.left_line);
        rightLine = parentActivity.findViewById(R.id.right_line);
        topLeftCorner = parentActivity.findViewById(R.id.topleft_corner);
        topRightCorner = parentActivity.findViewById(R.id.topright_corner);
        bottomLeftCorner = parentActivity.findViewById(R.id.bottomleft_corner);
        bottomRightCorner = parentActivity.findViewById(R.id.bottomright_corner);
        topLineParams = (ConstraintLayout.LayoutParams) topLine.getLayoutParams();
        bottomLineParams = (ConstraintLayout.LayoutParams) bottomLine.getLayoutParams();
        leftLineParams = (ConstraintLayout.LayoutParams) leftLine.getLayoutParams();
        rightLineParams = (ConstraintLayout.LayoutParams) rightLine.getLayoutParams();
        topLeftCornerParams = (ConstraintLayout.LayoutParams) topLeftCorner.getLayoutParams();
        topRightCornerParams = (ConstraintLayout.LayoutParams) topRightCorner.getLayoutParams();
        bottomLeftCornerParams = (ConstraintLayout.LayoutParams) bottomLeftCorner.getLayoutParams();
        bottomRightCornerParams = (ConstraintLayout.LayoutParams) bottomRightCorner.getLayoutParams();

        scale = photoView.getScale();
        setValTop = ((expandView2Params.height - expandViewParams.height) / 2.0f) * scale + deltaSettingValue[1] * scale;
        setValBottom = ((expandView2Params.height - expandViewParams.height) / 2.0f) * scale + deltaSettingValue[3] * scale;
        setValLeft = ((expandView2Params.width - expandViewParams.width) / 2.0f) * scale + deltaSettingValue[0] * scale;
        setValRight = ((expandView2Params.width - expandViewParams.width) / 2.0f) * scale + deltaSettingValue[2] * scale;
        System.out.println("위 델타 : " + deltaSettingValue[1] + " 아래 델타 : " + deltaSettingValue[3] + " 왼 델타 : " + deltaSettingValue[0] + " 오른 델타 : " + deltaSettingValue[2]);
        System.out.println("위 셋벨 : " + setValTop + " 아래 셋벨 : " + setValBottom + " 왼 셋벨 : " + setValLeft + " 오른 셋벨 : " + setValRight);

        float cor1 = 0f * dpScale; // 라인 두께로 인해 보정
        float cor2 = 0f * dpScale; // 코너 두께로 인해 보정
        float cor3 = 3.0f *dpScale; // 확대하는 뷰 라인 두께로 인한 보정

        topLineParams.setMargins(0, (int) (setValTop - cor1 + cor3) ,0,0);
        bottomLineParams.setMargins(0, 0 ,0,(int) (setValBottom - cor1 + cor3));
        leftLineParams.setMargins((int) (setValLeft - cor1 + cor3), 0 ,0,0);
        rightLineParams.setMargins(0, 0,(int) (setValRight - cor1 + cor3),0);
        topLeftCornerParams.setMargins((int) (setValLeft - cor2 + cor3), (int) (setValTop - cor2 + cor3),0,0);
        topRightCornerParams.setMargins(0, (int) (setValTop - cor2 + cor3),(int) (setValRight - cor2 + cor3),0);
        bottomLeftCornerParams.setMargins((int) (setValLeft - cor2 + cor3), 0,0,(int) (setValBottom - cor2 + cor3));
        bottomRightCornerParams.setMargins(0, 0,(int) (setValRight - cor2 + cor3),(int) (setValBottom - cor2 + cor3));

        //아무나 하나 이렇게 자극을 줘야 레이아웃이 변경되더라고 ;;
        topLine.requestLayout();

        boxCL.setVisibility(View.VISIBLE);
    }

    //photoViewCL의 ConstraintSet을 업데이트한다.
    private void updateConstraintSet1(View targetView, int leftMargin, int topMargin) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(photoViewCL);
        constraintSet.connect(targetView.getId(), ConstraintSet.START, photoViewCL.getId(), ConstraintSet.START, leftMargin);
        constraintSet.connect(targetView.getId(), ConstraintSet.TOP, photoViewCL.getId(), ConstraintSet.TOP, topMargin);
        constraintSet.applyTo(photoViewCL);
    }

    //photoViewCL의 ConstraintSet을 업데이트한다.
    private void updateConstraintSet2(View targetView, View linkView) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(photoViewCL);
        constraintSet.connect(targetView.getId(), ConstraintSet.TOP, linkView.getId(), ConstraintSet.TOP);
        constraintSet.connect(targetView.getId(), ConstraintSet.BOTTOM, linkView.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(targetView.getId(), ConstraintSet.START, linkView.getId(), ConstraintSet.START);
        constraintSet.connect(targetView.getId(), ConstraintSet.END, linkView.getId(), ConstraintSet.END);
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
    public void drawAnswer(float[][] answerCoordination) {
        coordinationChange(answerCoordination);

        if(answerList == null || (answerList.length != changedCoordination.length)) {
            //일전에 그려져있던건 싹 지워야한다.
            System.out.println("지운다.");
            if(answerList != null){
                for (int i = 0; i < answerList.length; i++){
                    photoViewCL.removeView(answerList[i]);
                    photoViewCL.removeView(answerEdges[i][0]);
                    photoViewCL.removeView(answerEdges[i][1]);
                    photoViewCL.removeView(answerEdges[i][2]);
                    photoViewCL.removeView(answerEdges[i][3]);
                }
            }

            answerList = new ConstraintLayout[changedCoordination.length];
            answerEdges = new View[changedCoordination.length][4];

            //각 정답박스을 생성해주고 width와 height값 설정, 그리고 박스 라인들을 해당 박스의 상하좌우에 연결
            for(int i = 0; i < changedCoordination.length; i++){
                ConstraintLayout answer = new ConstraintLayout(parentActivity);
                answerList[i] = answer;
                ConstraintLayout.LayoutParams squareParams = new ConstraintLayout.LayoutParams(
                        (int) (changedCoordination[i][2] - changedCoordination[i][0]),
                        (int) (changedCoordination[i][3] - changedCoordination[i][1]));
                answerList[i].setId(View.generateViewId());
                answerList[i].setLayoutParams(squareParams);
                photoViewCL.addView(answerList[i]);

                ViewGroup.LayoutParams topBottomParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(3*dpScale));
                ViewGroup.LayoutParams leftRightParams = new ViewGroup.LayoutParams((int)(3*dpScale), ViewGroup.LayoutParams.MATCH_PARENT);

                View answerTopLine = new View(parentActivity);
                View answerBottomLine = new View(parentActivity);
                View answerLeftLine = new View(parentActivity);
                View answerRightLine = new View(parentActivity);

                answerTopLine.setId(View.generateViewId());
                answerBottomLine.setId(View.generateViewId());
                answerLeftLine.setId(View.generateViewId());
                answerRightLine.setId(View.generateViewId());

                answerTopLine.setLayoutParams(topBottomParams);
                answerBottomLine.setLayoutParams(topBottomParams);
                answerLeftLine.setLayoutParams(leftRightParams);
                answerRightLine.setLayoutParams(leftRightParams);

                System.out.println("엔서 타입 : " +answerType[i]);
                if(answerType[i] == 0) {
                    answerTopLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint2));
                    answerBottomLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint2));
                    answerLeftLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint2));
                    answerRightLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint2));
                }else if(answerType[i] == 1){
                    answerTopLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint1));
                    answerBottomLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint1));
                    answerLeftLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint1));
                    answerRightLine.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorPoint1));
                }

                answer.addView(answerTopLine);
                answer.addView(answerBottomLine);
                answer.addView(answerLeftLine);
                answer.addView(answerRightLine);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(answer);
                constraintSet.connect(answerTopLine.getId(), ConstraintSet.TOP, answer.getId(), ConstraintSet.TOP);
                constraintSet.connect(answerBottomLine.getId(), ConstraintSet.BOTTOM, answer.getId(), ConstraintSet.BOTTOM);
                constraintSet.connect(answerLeftLine.getId(), ConstraintSet.LEFT, answer.getId(), ConstraintSet.LEFT);
                constraintSet.connect(answerRightLine.getId(), ConstraintSet.RIGHT, answer.getId(), ConstraintSet.RIGHT);
                constraintSet.applyTo(answer);

                answerEdges[i][0] = answerLeftLine;
                answerEdges[i][1] = answerTopLine;
                answerEdges[i][2] = answerRightLine;
                answerEdges[i][3] = answerBottomLine;
            }
        }

        // 위에 반복문에서 만들어진 정답박스들의 탑마진과 아래마진을 설정해준다.
        // 내가 왜 width height랑 같이 설정 안해줬는진 이유가 있었던거같은게 기억이안난다.
        // 처음엔 그랬어야했는데 코드를 수정하다보니 그럴필요가 없어진거 같기도한데... 여튼 그냥 이렇게 둔다.
        for(int i = 0; i < answerList.length; i++) {
            ConstraintLayout.LayoutParams squareParams = null;

            //뷰가 왼쪽 위로 올라가면 박스의 라인들이 안보이는 것처럼 보여야한다.
            if(changedCoordination[i][0] < 0 && changedCoordination[i][1] < 0){
                squareParams = new ConstraintLayout.LayoutParams(
                        (int) (changedCoordination[i][2]),
                        (int) (changedCoordination[i][3]));
            }else if(changedCoordination[i][1] < 0) {
                squareParams = new ConstraintLayout.LayoutParams(
                        (int) (changedCoordination[i][2] - changedCoordination[i][0]),
                        (int) (changedCoordination[i][3]));
            }else if(changedCoordination[i][0] < 0){
                squareParams = new ConstraintLayout.LayoutParams(
                        (int) (changedCoordination[i][2]),
                        (int) (changedCoordination[i][3] - changedCoordination[i][1]));
            }else{
                squareParams = new ConstraintLayout.LayoutParams(
                        (int) (changedCoordination[i][2] - changedCoordination[i][0]),
                        (int) (changedCoordination[i][3] - changedCoordination[i][1]));
            }
            answerList[i].setLayoutParams(squareParams);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(photoViewCL);
            constraintSet.connect(answerList[i].getId(), ConstraintSet.START, photoViewCL.getId(), ConstraintSet.START, (int) (changedCoordination[i][0]));
            constraintSet.connect(answerList[i].getId(), ConstraintSet.TOP, photoViewCL.getId(), ConstraintSet.TOP, (int) (changedCoordination[i][1]));
            constraintSet.applyTo(photoViewCL);

            ConstraintLayout.LayoutParams setViewVisible = (ConstraintLayout.LayoutParams) answerList[i].getLayoutParams();

            if(setViewVisible.leftMargin < 0 && setViewVisible.width < 0){
                answerEdges[i][0].setVisibility(View.INVISIBLE);
                answerEdges[i][1].setVisibility(View.INVISIBLE);
                answerEdges[i][2].setVisibility(View.INVISIBLE);
                answerEdges[i][3].setVisibility(View.INVISIBLE);
            }else{
                if(setViewVisible.leftMargin < 0)
                    answerEdges[i][0].setVisibility(View.INVISIBLE);
                if(setViewVisible.topMargin < 0)
                    answerEdges[i][1].setVisibility(View.INVISIBLE);
                if(setViewVisible.width < 0)
                    answerEdges[i][2].setVisibility(View.INVISIBLE);
                if(setViewVisible.height < 0)
                    answerEdges[i][3].setVisibility(View.INVISIBLE);
                if(setViewVisible.leftMargin >= 0)
                    answerEdges[i][0].setVisibility(View.VISIBLE);
                if(setViewVisible.topMargin >= 0)
                    answerEdges[i][1].setVisibility(View.VISIBLE);
                if(setViewVisible.width >= 0)
                    answerEdges[i][2].setVisibility(View.VISIBLE);
                if(setViewVisible.height >= 0)
                    answerEdges[i][3].setVisibility(View.VISIBLE);
            }

        }

    }

    //퍼센트 좌표를 현재 photoview의 스케일에 맞게 절대 좌표로 변경해준다.
    private void coordinationChange(float[][] answerCoordination) {
        float photoWidth, photoHeight;
        photoWidth = photoView.getDisplayRect().right - photoView.getDisplayRect().left;
        photoHeight = photoView.getDisplayRect().bottom - photoView.getDisplayRect().top;
        float cor = 3 * dpScale / photoView.getScale(); //박스 라인의 두께로 인한 보정
        for (int i = 0; i<answerCoordination.length; i++){
            changedCoordination[i][0] = photoView.getDisplayRect().left + answerCoordination[i][0] * photoWidth - cor; //left
            changedCoordination[i][1] = photoView.getDisplayRect().top + answerCoordination[i][1] * photoHeight - cor; //top
            changedCoordination[i][2] = photoView.getDisplayRect().left + answerCoordination[i][2] * photoWidth + cor; //right
            changedCoordination[i][3] = photoView.getDisplayRect().top + answerCoordination[i][3] * photoHeight + cor; //bottom
        }
    }

    //String 형태로 서버에서 넘어온 좌표를 float[][] 형식으로 바꾸어 answerCoordination에 넣어줌
    private void coordinationParsing(String[] array) {

        for(int i =1; i < array.length; i++){
            Log.d("answercoordinationhere",array[i]);
            array3 = array[i].split(",");
            answerCoordination[i-1][0] = (float) Float.parseFloat(array3[0]);
            answerCoordination[i-1][1] = (float) Float.parseFloat(array3[1]);
            answerCoordination[i-1][2] = (float) Float.parseFloat(array3[2]);
            answerCoordination[i-1][3] = (float) Float.parseFloat(array3[3]);
            answerType[i-1] = 0;
        }
    }

    //서버에서 보내준 확대 좌표를 바탕으로 bitmap을 잘라준다.
    private Bitmap cropBitmap(Bitmap original, String cropCoordination) {
        System.out.println("------------");
        System.out.println(cropCoordination);
        String[] cropCoord = cropCoordination.split(",");
        float[] cropCoordParse = new float[4];

        System.out.print("확대좌표 (");
        for(int i = 0; i < cropCoord.length; i++){
            cropCoordParse[i] = Float.parseFloat(cropCoord[i]);
            System.out.print(cropCoordParse[i]+",");
        }
        System.out.println(")");
        System.out.println("------------");
        if(cropCoordParse[0] < 0f)
            cropCoordParse[0] = 0f;
        if(cropCoordParse[1] < 0f)
            cropCoordParse[1] = 0f;
        if(cropCoordParse[2] > 1f)
            cropCoordParse[2] = 1f;
        if(cropCoordParse[3] > 1f)
            cropCoordParse[3] = 1f;
        original = Bitmap.createBitmap(original
                , (int)(original.getWidth() * cropCoordParse[0]) //X 시작위치
                , (int)(original.getHeight() * cropCoordParse[1]) //Y 시작위치
                , (int)(original.getWidth() * (cropCoordParse[2]-cropCoordParse[0])) // 넓이
                , (int)(original.getHeight() * (cropCoordParse[3]-cropCoordParse[1]))); // 높이
        return original;
    }

    public void addAnswer(float left, float top, float right, float bottom){
        drawAnswerCount++;
        float[][] answerCoordinationTemp = answerCoordination;
        int[] answerTypeTemp = answerType;
        answerCoordination = new float[answerCount + drawAnswerCount][4];
        answerType = new int[answerCount + drawAnswerCount];

        if(answerCoordinationTemp != null) {
            for (int i = 0; i < answerCoordinationTemp.length; i++) {
                answerCoordination[i][0] = answerCoordinationTemp[i][0];
                answerCoordination[i][1] = answerCoordinationTemp[i][1];
                answerCoordination[i][2] = answerCoordinationTemp[i][2];
                answerCoordination[i][3] = answerCoordinationTemp[i][3];
                answerType[i] = answerTypeTemp[i];

            }
        }


        answerCoordination[answerCount + drawAnswerCount - 1][0] = left;
        answerCoordination[answerCount + drawAnswerCount - 1][1] = top;
        answerCoordination[answerCount + drawAnswerCount - 1][2] = right;
        answerCoordination[answerCount + drawAnswerCount - 1][3] = bottom;
        answerType[answerCount + drawAnswerCount - 1] = 1;
        changedCoordination = new float[answerCoordination.length][4];
    }

    public void removeAnswer(){
        answerCount= 0;
        drawAnswerCount = 0;
        answerType = null;
        answerCoordination = null;
        if(answerList != null) {
            for (int i = 0; i < answerList.length; i++) {
                photoViewCL.removeView(answerList[i]);
                photoViewCL.removeView(answerEdges[i][0]);
                photoViewCL.removeView(answerEdges[i][1]);
                photoViewCL.removeView(answerEdges[i][2]);
                photoViewCL.removeView(answerEdges[i][3]);
            }
        }
        answerList = null;
    }

    //size도 함께 생각해야한다........ 겹치는 퍼센트에 따라 P/F 하는거로 수정 요망
    public boolean similarityTest(float left, float top, float right, float bottom){
        Boolean rtnVal = true;
        float error = 0.015f;
        if(answerCoordination!=null) {
            for (int i = 0; i < answerCoordination.length; i++) {
                if (Math.abs(answerCoordination[i][0] - left) < error &&
                        Math.abs(answerCoordination[i][1] - top) < error &&
                        Math.abs(answerCoordination[i][2] - right) < error &&
                        Math.abs(answerCoordination[i][3] - bottom) < error)
                    rtnVal = false;
            }
        }
        return  rtnVal;
    };





}