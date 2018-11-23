package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithLine;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;


public class Controller_TwoPoint extends Controller {

    public Controller_TwoPoint() {
        controllerID = R.layout.controller_twopoint;
    }

    //두개의 포인트를 이어줄 선을 그리기 위한 innerClass
    //포인트들의 좌표를 Path Array형태로 지정해주고 draw를 하면 그려준다.
    public class LineView extends View{
        private Paint paint = new Paint();
        private float[] Lines = new float[4];
        DisplayMetrics displayMetrics = new DisplayMetrics();

        public Paint getPaint() {
            return paint;
        }

        public float[] getLines() {
            return Lines;
        }

        public LineView(Context context){//one Answer
            super(context);
            parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            getDeviceDpi = displayMetrics.densityDpi;
            dpScale = (float) getDeviceDpi / 160f;
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3f * dpScale);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(this.getResources().getColor(R.color.colorPrimary));
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            LineView.this.setLayoutParams(params);
        }

        public void onDraw(Canvas canvas){
            canvas.drawLines(Lines, paint);
        }

        public void pointReset(){
            Lines[0] = 0f;
            Lines[1] = 0f;
            Lines[2] = 0f;
            Lines[3] = 0f;
        }

        public void samePointSetting(float x, float y){
            Lines[0] = x;
            Lines[1] = y;
            Lines[2] = x;
            Lines[3] = y;
        }

        public void firstPointSetting(float x, float y){
            Lines[0] = x;
            Lines[1] = y;
        }

        public void secondPointSetting(float x, float y){
            Lines[2] = x;
            Lines[3] = y;
        }

    }
    public boolean firstPointFlag = true;
    public boolean secondPointFlag = true;
    public View firstPoint, secondPoint, touchView, firstPointTouch, secondPointTouch;
    public TextView sendbtn, completebtn;
    public LineView pointLine;
    private TaskView_PhotoWithLine mTaskViewPhotoWithLine;
    private PhotoView photoView;
    private ConstraintLayout boxCL;
    private int getDeviceDpi;
    private float dpScale;
    private Canvas canvas;
    private float originWidth, originHeight, originLeftMargin, originTopMargin;

    //상태바 높이 구하기
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void resetContent(View view, final String taskID) {
        mTaskViewPhotoWithLine = (TaskView_PhotoWithLine) parentActivity.getmTaskView();
        firstPointFlag = true;
        secondPointFlag = false;
        firstPoint = parentActivity.findViewById(R.id.firstPoint);
        secondPoint = parentActivity.findViewById(R.id.secondPoint);
        firstPointTouch = parentActivity.findViewById(R.id.firstPointTouch);
        secondPointTouch = parentActivity.findViewById(R.id.secondPointTouch);
        touchView = parentActivity.findViewById(R.id.touchView);
        sendbtn = parentActivity.findViewById(R.id.sendbtn);
        completebtn = parentActivity.findViewById(R.id.completebtn);
        boxCL = parentActivity.findViewById(R.id.boxCL);
        photoView = mTaskViewPhotoWithLine.getPhotoView();
        canvas = new Canvas();
        final float expandSize = 2f;

        //뷰를 추가하고나면 point들이 뒤에 있어서 클릭할수가 없어서 앞으로 가져온다.
        pointLine = new LineView(parentActivity);
        pointLine.setId(View.generateViewId());
        boxCL.addView(pointLine);
        firstPoint.bringToFront();
        secondPoint.bringToFront();

        //dp크기 가져오기
        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDeviceDpi = displayMetrics.densityDpi;
        dpScale = (float) getDeviceDpi / 160f;

        //처음에는 두개의 포인트가 없어야한다.
        if (firstPoint.getVisibility() == firstPoint.VISIBLE)
            firstPoint.setVisibility(View.INVISIBLE);
        if (firstPointTouch.getVisibility() == firstPointTouch.VISIBLE)
            firstPointTouch.setVisibility(View.INVISIBLE);
        if (secondPoint.getVisibility() == secondPoint.VISIBLE)
            secondPoint.setVisibility(View.INVISIBLE);
        if (secondPointTouch.getVisibility() == secondPointTouch.VISIBLE)
            secondPointTouch.setVisibility(View.INVISIBLE);

        touchView.setOnTouchListener(new View.OnTouchListener() {
            ConstraintLayout textDragCL = parentActivity.findViewById(R.id.textDragCL);
            TextView textDrag = parentActivity.findViewById(R.id.textDrag);
            View targetPoint, targetPointTouch;
            float curX = 0;
            float curY = 0;
            float pvRatio = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                curX = event.getX();
                curY = event.getY();

                if (firstPointFlag || secondPointFlag) {
                    if (mTaskViewPhotoWithLine.ImageReady) {

                        //OutOfBound 처리
                        curX = setXOutOfBound(curX);
                        curY = setYOutOfBound(curY);

                        //expandView의 위치변경
                        setExpandViewPos(curX, curY);

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (firstPointFlag && !secondPointFlag) {
                                    targetPoint = firstPoint;
                                    targetPointTouch = firstPointTouch;
                                }
                                if (!firstPointFlag && secondPointFlag) {
                                    targetPoint = secondPoint;
                                    targetPointTouch = secondPointTouch;
                                }

                                pvRatio = (float) boxCL.getWidth() / (float) boxCL.getHeight();
                                updateConstraintSet1(targetPoint, (int) curX, (int) curY);

                                if (targetPoint.getVisibility() == View.INVISIBLE)
                                    targetPoint.setVisibility(View.VISIBLE);
                                if (targetPointTouch.getVisibility() == View.INVISIBLE)
                                    targetPointTouch.setVisibility(View.VISIBLE);

                                if (targetPoint == firstPoint) {
                                    pointLine.samePointSetting(curX, curY);
                                    pointLine.invalidate();
                                }
                                if (targetPoint == secondPoint) {
                                    pointLine.secondPointSetting(curX, curY);
                                    pointLine.invalidate();
                                }
                                textDragCL.setVisibility(View.INVISIBLE);

                                //expandView 설정
                                if (parentActivity.findViewById(R.id.expandViewCL).getVisibility() == View.INVISIBLE)
                                    parentActivity.findViewById(R.id.expandViewCL).setVisibility(View.VISIBLE);
                                mTaskViewPhotoWithLine.expandBitmapSetting(curX, curY, expandSize);
                                break;

                            case MotionEvent.ACTION_MOVE:

                                updateConstraintSet1(targetPoint, (int) curX, (int) curY);
                                if (targetPoint == firstPoint) {
                                    pointLine.samePointSetting(curX, curY);
                                    pointLine.invalidate();
                                }
                                if (targetPoint == secondPoint) {
                                    pointLine.secondPointSetting(curX, curY);
                                    pointLine.invalidate();
                                }

                                //expandView 설정
                                mTaskViewPhotoWithLine.expandBitmapSetting(curX, curY, expandSize);
                                break;

                            case MotionEvent.ACTION_UP:

                                updateConstraintSet1(targetPoint, (int) curX, (int) curY);
                                if (targetPoint == firstPoint) {
                                    pointLine.samePointSetting(curX, curY);
                                    pointLine.invalidate();
                                    firstPointFlag = false;
                                    secondPointFlag = true;
                                    textDragCL.setVisibility(View.VISIBLE);
                                    textDrag.setText("라인의 끝점을 지정해 주세요.");
                                }
                                if (targetPoint == secondPoint) {
                                    pointLine.secondPointSetting(curX, curY);
                                    pointLine.invalidate();
                                    firstPointFlag = false;
                                    secondPointFlag = false;
                                }

                                //expandView 설정
                                if (parentActivity.findViewById(R.id.expandViewCL).getVisibility() == View.VISIBLE)
                                    parentActivity.findViewById(R.id.expandViewCL).setVisibility(View.INVISIBLE);
                                mTaskViewPhotoWithLine.expandBitmapSetting(curX, curY, expandSize);
                                break;
                        }
                    }

                    return true;
                }
                return false;
            }

        });

        firstPointTouch.setOnTouchListener(new View.OnTouchListener() {
            float curX = 0;
            float curY = 0;
            float pvRatio = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float YCorrectionHeight = (float) parentActivity.findViewById(R.id.title).getHeight() + (float) parentActivity.findViewById(R.id.option).getHeight();
                System.out.println("Controller_TwoPoint, YCorrectionHeight : " + YCorrectionHeight);
                curX = event.getRawX();
                curY = event.getRawY() - YCorrectionHeight * dpScale - getStatusBarHeight(parentActivity);

                //OutOfBound 처리
                curX = setXOutOfBound(curX);
                curY = setYOutOfBound(curY);

                //expandView의 위치변경
                setExpandViewPos(curX, curY);

                if (mTaskViewPhotoWithLine.ImageReady) {
                    pvRatio = (float) boxCL.getWidth() / (float) boxCL.getHeight();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //expandView 설정
                            if (parentActivity.findViewById(R.id.expandViewCL).getVisibility() == View.INVISIBLE)
                                parentActivity.findViewById(R.id.expandViewCL).setVisibility(View.VISIBLE);
                        case MotionEvent.ACTION_MOVE:
                            mTaskViewPhotoWithLine.expandBitmapSetting(curX, curY, expandSize);
                            updateConstraintSet1(firstPoint, (int) curX, (int) curY);
                            if (secondPointFlag)
                                pointLine.samePointSetting(curX, curY);
                            else
                                pointLine.firstPointSetting(curX, curY);
                            pointLine.invalidate();
                            break;

                        case MotionEvent.ACTION_UP:
                            updateConstraintSet1(firstPoint, (int) curX, (int) curY);
                            if (secondPointFlag)
                                pointLine.samePointSetting(curX, curY);
                            else
                                pointLine.firstPointSetting(curX, curY);
                            pointLine.invalidate();
                            //expandView 설정
                            if (parentActivity.findViewById(R.id.expandViewCL).getVisibility() == View.VISIBLE)
                                parentActivity.findViewById(R.id.expandViewCL).setVisibility(View.INVISIBLE);
                            mTaskViewPhotoWithLine.expandBitmapSetting(curX, curY, expandSize);
                            break;
                    }
                }
                return true;
            }
        });

        secondPointTouch.setOnTouchListener(new View.OnTouchListener() {
            float curX = 0;
            float curY = 0;
            float pvRatio = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                curX = event.getRawX();
                curY = event.getRawY() - 70f * dpScale - getStatusBarHeight(parentActivity); //70 : taskActivity의 title와 option CL의 크기

                //OutOfBound 처리
                curX = setXOutOfBound(curX);
                curY = setYOutOfBound(curY);

                //expandView의 위치변경
                setExpandViewPos(curX, curY);

                if (mTaskViewPhotoWithLine.ImageReady) {
                    pvRatio = (float) boxCL.getWidth() / (float) boxCL.getHeight();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //expandView 설정
                            if (parentActivity.findViewById(R.id.expandViewCL).getVisibility() == View.INVISIBLE)
                                parentActivity.findViewById(R.id.expandViewCL).setVisibility(View.VISIBLE);
                        case MotionEvent.ACTION_MOVE:
                            mTaskViewPhotoWithLine.expandBitmapSetting(curX, curY, expandSize);
                            updateConstraintSet1(secondPoint, (int) curX, (int) curY);
                            pointLine.secondPointSetting(curX, curY);
                            pointLine.invalidate();
                            break;

                        case MotionEvent.ACTION_UP:
                            updateConstraintSet1(secondPoint, (int) curX, (int) curY);
                            pointLine.secondPointSetting(curX, curY);
                            pointLine.invalidate();
                            //expandView 설정
                            if (parentActivity.findViewById(R.id.expandViewCL).getVisibility() == View.VISIBLE)
                                parentActivity.findViewById(R.id.expandViewCL).setVisibility(View.INVISIBLE);
                            mTaskViewPhotoWithLine.expandBitmapSetting(curX, curY, expandSize);
                            break;
                    }
                }
                return true;
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!firstPointFlag && !secondPointFlag) {
                    JSONObject param = new JSONObject();
                    try {
                        param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                        param.put("taskID", taskID);

                        float widthCL = boxCL.getWidth();
                        float heightCL = boxCL.getHeight();
                        /********************************
                         * (x1, y1) = 첫번째 포인트의 현재 좌표
                         * (x2, y2) = 두번째 포인트의 현재 좌표
                         * (x3, y3) = photoView를 담고있는 constraintlayout의 (0,0)의 좌표가 Scale = 1일 때는 뭔지 환산한 값
                         * (x4, y4) = crop box의 scale = 1일 대 좌상단 좌표 환산값
                         * (x5, y5) = crop box의 scale = 1일 대 우하단 좌표 환산값
                         *********************************/
                        float x1, x2, x3, x4, x5, y1, y2, y3, y4, y5;
                        x1 = firstPoint.getX();
                        x2 = secondPoint.getX();
                        y1 = firstPoint.getY();
                        y2 = secondPoint.getY();
                        originWidth = (photoView.getDisplayRect().right - photoView.getDisplayRect().left) / photoView.getScale();
                        originHeight = (photoView.getDisplayRect().bottom - photoView.getDisplayRect().top) / photoView.getScale();
                        originLeftMargin = (widthCL / 2.0f) - (originWidth) / 2.0f;
                        originTopMargin = (heightCL / 2.0f) - (originHeight) / 2.0f;
                        x3 = originLeftMargin - photoView.getDisplayRect().left / photoView.getScale();
                        y3 = originTopMargin - photoView.getDisplayRect().top / photoView.getScale();
                        x4 = x3 + x1 / photoView.getScale();
                        y4 = y3 + y1 / photoView.getScale();
                        x5 = x3 + x2 / photoView.getScale();
                        y5 = y3 + y2 / photoView.getScale();

                        //보내야하는 데이타
                        float leftPercent, topPercent, rightPercent, bottomPercent;
                        final float leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend;
                        String submit;
                        leftPercent = (x4 - originLeftMargin) / originWidth;
                        topPercent = (y4 - originTopMargin) / originHeight;
                        rightPercent = (x5 - originLeftMargin) / originWidth;
                        bottomPercent = (y5 - originTopMargin) / originHeight;
                        if (leftPercent < 0f)
                            leftPercent = 0f;
                        if (rightPercent > 1f)
                            rightPercent = 1f;
                        if (topPercent < 0f)
                            topPercent = 0f;
                        if (bottomPercent > 1f)
                            bottomPercent = 1f;
                        leftPercentSend = leftPercent;
                        topPercentSend = topPercent;
                        rightPercentSend = rightPercent;
                        bottomPercentSend = bottomPercent;
                        int ans = 11; //전선
                        submit = "<" + ans + ">" + leftPercent + "," + topPercent + "," + rightPercent + "," + bottomPercent;
                        param.put("submit", submit);

                        if (mTaskViewPhotoWithLine.similarityTest(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend)) {
                            new WaitHttpRequest(parentActivity) {
                                @Override
                                protected void onPostExecute(Object o) {
                                    super.onPostExecute(o);
                                    System.out.println("나 여기 들어왔어");

                                    try {
                                        JSONObject resultTemp = new JSONObject(result);
                                        System.out.println("resultTemp : " + resultTemp);
                                        System.out.println("서버반응 : " + resultTemp.get("success").toString());
                                        if (resultTemp.get("success").toString().equals("false")) {
                                            if (resultTemp.get("message").toString().equals("login")) {
                                                Intent in = new Intent(parentActivity, LoginActivity.class);
                                                parentActivity.startActivity(in);
                                                Toast.makeText(parentActivity, "로그인이 만료되었습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
                                                parentActivity.finish();
                                            } else if (resultTemp.get("message").toString().equals("task")) {
                                                Toast.makeText(parentActivity, "테스크가 만료되었습니다. 다른 테스크를 선택해주세요", Toast.LENGTH_SHORT).show();
                                                parentActivity.finish();
                                            } else {
                                                Toast.makeText(parentActivity, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
                                                parentActivity.finish();
                                            }
                                        } else {
                                            System.out.println("서버반응 2: " + resultTemp.get("success").toString());

                                            mTaskViewPhotoWithLine.addAnswer(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend);
                                            completebtn.setText("모든 부품 제출 완료 +5원");

                                            mTaskViewPhotoWithLine.drawAnswer(mTaskViewPhotoWithLine.answerCoordination, mTaskViewPhotoWithLine.answerCoordinationSubmit);

                                            ConstraintLayout textDragCL = parentActivity.findViewById(R.id.textDragCL);
                                            Toast.makeText(parentActivity, "제출 완료! 계속 찾아주세요.", Toast.LENGTH_SHORT).show();
                                            textDragCL.setVisibility(View.VISIBLE);
                                            textDragCL.bringToFront();

                                            firstPointFlag = true;
                                            secondPointFlag = false;
                                            ((TextView) parentActivity.findViewById(R.id.textDrag)).setText("라인의 시작점을 지정해 주세요.");
                                            if (firstPoint.getVisibility() == View.VISIBLE)
                                                firstPoint.setVisibility(View.INVISIBLE);
                                            if (firstPointTouch.getVisibility() == View.VISIBLE)
                                                firstPointTouch.setVisibility(View.INVISIBLE);
                                            if (secondPoint.getVisibility() == View.VISIBLE)
                                                secondPoint.setVisibility(View.INVISIBLE);
                                            if (secondPointTouch.getVisibility() == View.VISIBLE)
                                                secondPointTouch.setVisibility(View.INVISIBLE);
                                            pointLine.Lines = new float[4];
                                            pointLine.invalidate();
                                            photoView.setScale(1);

                                            //에니메이션
                                            parentActivity.goldSetting(String.valueOf(resultTemp.get("gold")));
                                            parentActivity.maybeSetting(String.valueOf(resultTemp.get("maybe")));
                                            ConstraintSet constraintSet = new ConstraintSet();
                                            constraintSet.clone((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                            constraintSet.connect(parentActivity.findViewById(R.id.textAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (86 * dpScale));
                                            constraintSet.connect(parentActivity.findViewById(R.id.imageAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (86 * dpScale));
                                            constraintSet.applyTo((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                            parentActivity.showAnimation(R.drawable.coin_animation_list, parentActivity.getUpGold());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.execute(parentActivity.getString(R.string.mainurl) + "/testing/taskSubmit", param, ((TaskActivity) parentActivity).getLoginToken());
                        } else {
                            Toast.makeText(parentActivity, "이미 박스를 친 곳인 것 같네요 T^T", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(parentActivity, "먼저 물체를 찾아주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });

        completebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
                alertDialogBuilder.setMessage("더 이상 찾아야 할 물체가 없나요?");
                alertDialogBuilder.setPositiveButton("네",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        JSONObject param = new JSONObject();
                        try {
                            param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                            param.put("taskID", taskID);

                            //보내야하는 데이타
                            String submit = "<" + 11 + "><allclear>";
                            param.put("submit", submit);

                        new WaitHttpRequest(parentActivity) {
                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                try {
                                    JSONObject resultTemp = new JSONObject(result);
                                    if (resultTemp.get("success").toString().equals("false")) {
                                        new ServerMessageParser().taskSubmitFailParse(parentActivity,resultTemp);

                                    } else {
                                        mTaskViewPhotoWithLine.removeAnswer();
                                        parentActivity.updateWaitingTasks();
                                        ((TaskActivity) parentActivity).startTask();

                                        //에니메이션
                                        parentActivity.goldSetting(String.valueOf(resultTemp.get("gold")));
                                        parentActivity.maybeSetting(String.valueOf(resultTemp.get("maybe")));
                                        ConstraintSet constraintSet = new ConstraintSet();
                                        constraintSet.clone((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                        constraintSet.connect(parentActivity.findViewById(R.id.textAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (43 * dpScale));
                                        constraintSet.connect(parentActivity.findViewById(R.id.imageAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (43 * dpScale));
                                        constraintSet.applyTo((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                        //그냥 제출과 다찾음의 금액이 달라서 일단은 하드텍스트로 박아넣음 (수정요망)
                                        parentActivity.showAnimation(R.drawable.coin_animation_list, Integer.parseInt("5"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute(parentActivity.getString(R.string.mainurl) + "/testing/taskSubmit", param, ((TaskActivity) parentActivity).getLoginToken());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton("아니오",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick (DialogInterface dialog,int which){
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
            }
        });
    }

    private void setExpandViewPos(float curX, float curY) {
        float topBottomMargin = 44 * dpScale;
        float leftRightMargin = 26 * dpScale;
        ConstraintLayout photoWithLineCL = parentActivity.findViewById(R.id.photoWithLineCL);
        ConstraintLayout expandViewCL = parentActivity.findViewById(R.id.expandViewCL);
        float CLWidth = photoWithLineCL.getWidth();
        float CLHeight = photoWithLineCL.getHeight();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(photoWithLineCL);

        if(curX <= expandViewCL.getWidth() + leftRightMargin && curY <= expandViewCL.getHeight() + topBottomMargin) { //현재좌표가 좌상단, 포토뷰는 우하단으로
            Log.d("어디들어갔게", "좌상단" );
            constraintSetClear(expandViewCL, constraintSet);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.BOTTOM, photoWithLineCL.getId(), ConstraintSet.BOTTOM, (int) topBottomMargin);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.RIGHT, photoWithLineCL.getId(), ConstraintSet.RIGHT, (int) leftRightMargin);
        }else if(curX > CLWidth - (expandViewCL.getWidth() + leftRightMargin) && curY <= expandViewCL.getHeight() + topBottomMargin) { //현재좌표가 우상단, 포토뷰는 좌하단으로
            Log.d("어디들어갔게", "우상단" );
            constraintSetClear(expandViewCL, constraintSet);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.BOTTOM, photoWithLineCL.getId(), ConstraintSet.BOTTOM, (int) topBottomMargin);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.LEFT, photoWithLineCL.getId(), ConstraintSet.LEFT, (int) leftRightMargin);
        }else if(curX <= expandViewCL.getWidth() + leftRightMargin && curY > CLHeight - (expandViewCL.getHeight() + topBottomMargin)) { //현재좌표가 좌하단, 포토뷰는 우상단으로
            Log.d("어디들어갔게", "좌하단" );
            constraintSetClear(expandViewCL, constraintSet);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.TOP, photoWithLineCL.getId(), ConstraintSet.TOP, (int) topBottomMargin);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.RIGHT, photoWithLineCL.getId(), ConstraintSet.RIGHT, (int) leftRightMargin);
        }else if(curX > CLWidth - (expandViewCL.getWidth() + leftRightMargin) && curY > CLHeight - (expandViewCL.getHeight() + topBottomMargin)) { //현재좌표가 우하단, 포토뷰는 좌상단으로
            Log.d("어디들어갔게", "우하단" );
            constraintSetClear(expandViewCL, constraintSet);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.TOP, photoWithLineCL.getId(), ConstraintSet.TOP, (int) topBottomMargin);
            constraintSet.connect(expandViewCL.getId(), ConstraintSet.LEFT, photoWithLineCL.getId(), ConstraintSet.LEFT, (int) leftRightMargin);
        }
        constraintSet.applyTo(photoWithLineCL);
    }

    private void constraintSetClear(ConstraintLayout expandViewCL, ConstraintSet constraintSet) {
        constraintSet.clear(expandViewCL.getId(), ConstraintSet.TOP);
        constraintSet.clear(expandViewCL.getId(), ConstraintSet.BOTTOM);
        constraintSet.clear(expandViewCL.getId(), ConstraintSet.LEFT);
        constraintSet.clear(expandViewCL.getId(), ConstraintSet.RIGHT);
    }

    private float setXOutOfBound(float curX) {
        if(curX < mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().left)
            curX = mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().left;
        else if(curX > mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().right)
            curX = mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().right;
        return  curX;
    }

    private float setYOutOfBound(float curY){
        if(curY < mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().top)
            curY = mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().top;
        else if(curY > mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().bottom)
            curY = mTaskViewPhotoWithLine.getPhotoView().getDisplayRect().bottom;
        return  curY;
    }

    @Override
    public void setLayout(View view, String taskID) {

    }

    //photoViewCL의 ConstraintSet을 업데이트한다.
    private void updateConstraintSet1(View targetView, int leftMargin, int topMargin) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(boxCL);
        constraintSet.connect(targetView.getId(), ConstraintSet.START, boxCL.getId(), ConstraintSet.START, leftMargin);
        constraintSet.connect(targetView.getId(), ConstraintSet.TOP, boxCL.getId(), ConstraintSet.TOP, topMargin);
        constraintSet.applyTo(boxCL);
    }
}
