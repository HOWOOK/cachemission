package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.Quiz2DBoxActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoWithBox;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller_2DBox extends Controller {

    private Button sendButton, completeButton;
    private ImageView pinButton;
    private View centerImage;
    private PhotoView photoView;
    private float originWidth, originHeight, originLeftMargin, originTopMargin;
    private TaskView_PhotoWithBox mTaskViewPhotoWithBox;
    public boolean pinFlag;
    private int getDeviceDpi;
    private float dpScale;
    private int testCount;
    private int testCountForGraduate=0;



    public Controller_2DBox() {
        controllerID = R.layout.controller_2dbox;
    }

    public ImageView getPinButton() {
        return pinButton;
    }

    @Override
    public void resetContent(final View view, final String taskID) {
        centerImage = view.findViewById(R.id.centerimage);
        mTaskViewPhotoWithBox = (TaskView_PhotoWithBox) parentActivity.getmTaskView();
        pinFlag = true;
        if(testFlag){
            testCount=BoxCropTestAnswer.length();
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDeviceDpi = displayMetrics.densityDpi;
        dpScale = (float) getDeviceDpi / 160f;

        //처음에는 box가 없어야 합니다.
        final ConstraintLayout boxCL = view.findViewById(R.id.boxCL);
        if (boxCL.getVisibility() == boxCL.VISIBLE)
            boxCL.setVisibility(View.INVISIBLE);

        pinButton = parentActivity.findViewById(R.id.pinbtn);
        pinButton.setBackgroundResource(R.drawable.twodbox_icon_pin_on);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinFlag){
                    pinButton.setBackgroundResource(R.drawable.twodbox_icon_pin_off);
                    pinFlag = false;
                    Toast.makeText(parentActivity, "사진위치를 조절해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    pinButton.setBackgroundResource(R.drawable.twodbox_icon_pin_on);
                    pinFlag = true;
                    Toast.makeText(parentActivity, "박스를 조절해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

            sendButton = view.findViewById(R.id.sendbtn);
            if(sendButton != null) {
                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TaskView_PhotoWithBox) parentActivity.getmTaskView()).expandFlag) {
                            Toast.makeText(parentActivity, "먼저 물체를 찾아주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject param = new JSONObject();
                            try {
                                if(!testFlag) {
                                    param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                                    param.put("taskID", taskID);
                                }
                                TaskView_PhotoWithBox temp = (TaskView_PhotoWithBox) parentActivity.getmTaskView();
                                photoView = temp.getPhotoView();

                                System.out.println("디스플레이 네모 : " + photoView.getDisplayRect());
                                System.out.println("배율 : " + photoView.getScale());
                                float widthCL = boxCL.getWidth();
                                float heightCL = boxCL.getHeight();
                                /********************************
                                 * (x1, y1) = crop box의 현재 좌상단 좌표
                                 * (x2, y2) = crop box의 현재 우하단 좌표
                                 * (x3, y3) = photoView를 담고있는 constraintlayout의 (0,0)의 좌표가 Scale = 1일 때는 뭔지 환산한 값
                                 * (x4, y4) = crop box의 scale = 1일 대 좌상단 좌표 환산값
                                 * (x5, y5) = crop box의 scale = 1일 대 우하단 좌표 환산값
                                 *********************************/
                                float x1, x2, x3, x4, x5, y1, y2, y3, y4, y5;
                                x1 = centerImage.getX();
                                x2 = centerImage.getX() + (float) centerImage.getWidth();
                                y1 = centerImage.getY();
                                y2 = centerImage.getY() + (float) centerImage.getHeight();
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
                                if (leftPercent < 0.013f)
                                    leftPercent = 0f;
                                if (rightPercent > 0.987f) //박스가 끝에 있을경우 1로 생각하도록
                                    rightPercent = 1f;
                                if (topPercent < 0.013f)
                                    topPercent = 0f;
                                if (bottomPercent > 0.987f) //박스가 끝에 있을경우 1로 생각하도록
                                    bottomPercent = 1f;
                                leftPercentSend = leftPercent;
                                topPercentSend = topPercent;
                                rightPercentSend = rightPercent;
                                bottomPercentSend = bottomPercent;
                                int ans = parentActivity.partType();
                                submit = "<" + ans + ">" + leftPercent + "," + topPercent + "," + rightPercent + "," + bottomPercent;
                                param.put("submit", submit);
                                //test인지 먼저 확인
                                if(!testFlag) {
                                    if (mTaskViewPhotoWithBox.similarityTest(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend)) {
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
                                                        new ServerMessageParser().taskSubmitFailParse(parentActivity, resultTemp);

                                                    } else {
                                                        System.out.println("서버반응 2: " + resultTemp.get("success").toString());

                                                        mTaskViewPhotoWithBox.addAnswer(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend);
                                                        completeButton.setText("모든 부품 제출 완료 +5원");

                                                        mTaskViewPhotoWithBox.drawAnswer(mTaskViewPhotoWithBox.answerCoordination);

                                                        ConstraintLayout textDragCL = parentActivity.findViewById(R.id.textDragCL);
                                                        Toast.makeText(parentActivity, "제출 완료! 계속 찾아주세요.", Toast.LENGTH_SHORT).show();
                                                        boxCL.setVisibility(View.INVISIBLE);
                                                        textDragCL.setVisibility(View.VISIBLE);
                                                        textDragCL.bringToFront();
                                                        pinFlag = true;
                                                        photoView.setScale(1);
                                                        ((TaskView_PhotoWithBox) parentActivity.getmTaskView()).expandFlag = true;
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
                                }
                                else {
                                    //test라면 이곳으로 들어옴
                                    if (testCountForGraduate >= 0) {
                                        TextView opt = parentActivity.findViewById(R.id.optionText);
                                        getDialog(opt.getText().toString() + "테스트 통과", "축하합니다. " + opt.getText().toString() + "테스트를 통과하셨습니다. 이제 " + opt.getText().toString() + "라벨링 작업을 시작할 수 있습니다.");
                                        SharedPreferences testFlag = parentActivity.getSharedPreferences("testFlag", parentActivity.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = testFlag.edit();
                                        editor.putBoolean("isTesting", true);
                                        editor.commit();

                                        JSONObject paramForRankUp = new JSONObject();
                                        paramForRankUp.put("option", parentActivity.partType());
                                        paramForRankUp.put("taskID", taskID);
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
                                                        new ServerMessageParser().taskSubmitFailParse(parentActivity, resultTemp);

                                                    } else {
                                                        System.out.println("서버반응 2: " + resultTemp.get("success").toString());


                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.execute(parentActivity.getString(R.string.mainurl) + "/testing/passTest", paramForRankUp, ((Quiz2DBoxActivity) parentActivity).getLoginToken());
                                    } else {
                                        TextView countText = parentActivity.findViewById(R.id.questText);
                                        if (testCount <= 0) {
                                            testCountForGraduate = 0;

                                            countText.setText(String.valueOf(testCountForGraduate) + "/10");
                                            getDialog("더이상 찾을 부품이 없습니다.", "테스트 통과 횟수가 초기화됩니다. 모든 부품 제출 완료 버튼을 눌러 주세요");
                                        } else {
                                            int candidate = mTaskViewPhotoWithBox.findCandidate(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend);
                                            System.out.println(String.valueOf(leftPercentSend));
                                            System.out.println(String.valueOf(topPercentSend));
                                            System.out.println(String.valueOf(rightPercentSend));
                                            System.out.println(String.valueOf(bottomPercentSend));
                                            if (mTaskViewPhotoWithBox.similarityTestForTest(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend, candidate) < (float) 0.02) {
                                                testCountForGraduate = 0;
                                                countText.setText(String.valueOf(testCountForGraduate) + "/10");
                                                getDialog("박스 안에 부품이 없습니다.", "다시 한번 확인하고 박스를 쳐 주세요.");
                                            } else if (mTaskViewPhotoWithBox.isIntersectionExistForTest(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend, candidate)) {
                                                testCountForGraduate = 0;
                                                countText.setText(String.valueOf(testCountForGraduate) + "/10");
                                                getDialog("부품이 잘렸습니다.", "박스를 좀더 크게 쳐 주세요.");
                                            } else if (mTaskViewPhotoWithBox.isBoundaryLimitExceededForTest(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend, candidate)) {
                                                testCountForGraduate = 0;
                                                countText.setText(String.valueOf(testCountForGraduate) + "/10");
                                                getDialog("박스가 너무 큽니다.", "부품의 경계에 맞게 박스를 쳐 주세요.");
                                            } else {
                                                mTaskViewPhotoWithBox.addAnswer(leftPercentSend, topPercentSend, rightPercentSend, bottomPercentSend);
                                                testCountForGraduate++;
                                                countText.setText(String.valueOf(testCountForGraduate) + "/10");

                                                if (testCountForGraduate >= 0) {
                                                    TextView opt = parentActivity.findViewById(R.id.optionText);
                                                    getDialog(opt.getText().toString() + "테스트 통과", "축하합니다. " + opt.getText().toString() + "테스트를 통과하셨습니다. 이제 " + opt.getText().toString() + "라벨링 작업을 시작할 수 있습니다.");
                                                    SharedPreferences testFlag = parentActivity.getSharedPreferences("testFlag", parentActivity.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = testFlag.edit();
                                                    editor.putBoolean("isTesting", true);
                                                    editor.commit();

                                                    JSONObject paramForRankUp = new JSONObject();
                                                    paramForRankUp.put("option", parentActivity.partType());
                                                    paramForRankUp.put("taskID", taskID);
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
                                                                    new ServerMessageParser().taskSubmitFailParse(parentActivity, resultTemp);

                                                                } else {
                                                                    System.out.println("서버반응 2: " + resultTemp.get("success").toString());


                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }.execute(parentActivity.getString(R.string.mainurl) + "/testing/passTest", paramForRankUp, ((Quiz2DBoxActivity) parentActivity).getLoginToken());
                                                } else {
                                                    getDialog("잘 하셨습니다!", "다음 부품을 찾아 주세요. 다 찾았다면 모든 부품 제출 완료 버튼을 눌러 주세요");
                                                    completeButton.setText("모든 부품 제출 완료 +5원");
                                                    testCount--;
                                                    mTaskViewPhotoWithBox.updateTestSet(candidate);

                                                    mTaskViewPhotoWithBox.drawAnswer(mTaskViewPhotoWithBox.answerCoordination);
                                                    ConstraintLayout textDragCL = parentActivity.findViewById(R.id.textDragCL);
                                                    Toast.makeText(parentActivity, "제출 완료! 계속 찾아주세요.", Toast.LENGTH_SHORT).show();
                                                    boxCL.setVisibility(View.INVISIBLE);
                                                    textDragCL.setVisibility(View.VISIBLE);
                                                    textDragCL.bringToFront();
                                                    pinFlag = true;
                                                    photoView.setScale(1);
                                                    ((TaskView_PhotoWithBox) parentActivity.getmTaskView()).expandFlag = true;
                                                    ConstraintSet constraintSet = new ConstraintSet();
                                                    constraintSet.clone((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                                    constraintSet.connect(parentActivity.findViewById(R.id.textAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (86 * dpScale));
                                                    constraintSet.connect(parentActivity.findViewById(R.id.imageAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (86 * dpScale));
                                                    constraintSet.applyTo((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                                }
                                            }

                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        completeButton = view.findViewById(R.id.completebtn);
            completeButton.setText("찾을 부품 없음 +5원");

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
                    alertDialogBuilder.setMessage("더 이상 찾아야 할 물체가 없나요?");
                    alertDialogBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!testFlag) {
                                dialog.dismiss();
                                final JSONObject param = new JSONObject();
                                try {
                                    param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                                    param.put("taskID", taskID);

                                    //보내야하는 데이타
                                    String submit = "<" + String.valueOf(parentActivity.partType()) + "><allclear>";
                                    System.out.println("올클리어 서브밋 : " + submit);
                                    param.put("submit", submit);
                                    new WaitHttpRequest(parentActivity) {
                                        @Override
                                        protected void onPostExecute(Object o) {
                                            super.onPostExecute(o);
                                            try {
                                                JSONObject resultTemp = new JSONObject(result);
                                                System.out.println("다찾음 제출 결과 : " + resultTemp);
                                                if (resultTemp.get("success").toString().equals("false")) {
                                                    new ServerMessageParser().taskSubmitFailParse(parentActivity, resultTemp);

                                                } else {
                                                    mTaskViewPhotoWithBox.removeAnswer();
                                                    parentActivity.updateWaitingTasks();
                                                    ((TaskActivity) parentActivity).startTask();
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
                            else{
                                dialog.dismiss();
                                if(testCount==0){
                                    mTaskViewPhotoWithBox.removeAnswer();
                                    parentActivity.updateWaitingTasks();
                                    parentActivity.startTask();

                                    ConstraintSet constraintSet = new ConstraintSet();
                                    constraintSet.clone((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                    constraintSet.connect(parentActivity.findViewById(R.id.textAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (43 * dpScale));
                                    constraintSet.connect(parentActivity.findViewById(R.id.imageAnimation).getId(), ConstraintSet.BOTTOM, parentActivity.findViewById(R.id.btnCL).getId(), ConstraintSet.BOTTOM, (int) (43 * dpScale));
                                    constraintSet.applyTo((ConstraintLayout) (parentActivity.findViewById(R.id.controllerCL)));
                                    //그냥 제출과 다찾음의 금액이 달라서 일단은 하드텍스트로 박아넣음 (수정요망)
                                }
                                else{
                                    getDialog("아직 찾지 못한 부품이 있습니다.","부품을 마저 찾아주세요.");
                                }

                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                }

        });

        final int maxsize = 75;
        final View top_line = view.findViewById(R.id.top_line);
        final View bottom_line = view.findViewById(R.id.bottom_line);
        final View left_line = view.findViewById(R.id.left_line);
        final View right_line = view.findViewById(R.id.right_line);
        final View topLeft_corner = view.findViewById(R.id.topleft_corner);
        final View topRight_corner = view.findViewById(R.id.topright_corner);
        final View bottomLeft_corner = view.findViewById(R.id.bottomleft_corner);
        final View bottomRight_corner = view.findViewById(R.id.bottomright_corner);
        final View centerImage = view.findViewById(R.id.centerimage);
        final View youCantTouchAnymore = view.findViewById(R.id.youcanttouchanymore);

        final View topLeftFade = view.findViewById(R.id.topLeftFade);
        final View topFade = view.findViewById(R.id.topFade);
        final View topRightFade = view.findViewById(R.id.topRightFade);
        final View leftFade = view.findViewById(R.id.leftFade);
        final View rightFade = view.findViewById(R.id.rightFade);
        final View bottomLeftFade = view.findViewById(R.id.bottomLeftFade);
        final View bottomFade = view.findViewById(R.id.bottomFade);
        final View bottomRightFade = view.findViewById(R.id.bottomRightFade);

        //top_line, bottom_line, left_line, right_line, topLeft_corner, topRight_corner, bottomLeft_corner, bottomRight_corner 리스너 사실 상 없애도 됨.

        top_line.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;

                    if (!(centerImage.getHeight() < maxsize && Y > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topLeft_corner.setLayoutParams(mLayoutParams2);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        topFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topLeft_corner.setLayoutParams(mLayoutParams2);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                    }
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottom_line.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomLeft_corner.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottomFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomLeft_corner.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        left_line.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.leftMargin += X;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams1);
                        topLeft_corner.setLayoutParams(mLayoutParams2);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        leftFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.leftMargin += X;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams1);
                        topLeft_corner.setLayoutParams(mLayoutParams2);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                    }
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        right_line.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.rightMargin -= X;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams1);
                        topRight_corner.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        rightFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.rightMargin -= X;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams1);
                        topRight_corner.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        topLeft_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams4.topMargin += Y;
                        mLayoutParams5.leftMargin += X;
                        top_line.setLayoutParams(mLayoutParams1);
                        left_line.setLayoutParams(mLayoutParams2);
                        topLeft_corner.setLayoutParams(mLayoutParams3);
                        topRight_corner.setLayoutParams(mLayoutParams4);
                        bottomLeft_corner.setLayoutParams(mLayoutParams5);
                    } else if ((centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams5.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams2);
                        topLeft_corner.setLayoutParams(mLayoutParams3);
                        bottomLeft_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y > 0) && (centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams4.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topLeft_corner.setLayoutParams(mLayoutParams3);
                        topRight_corner.setLayoutParams(mLayoutParams4);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        topLeftFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams4.topMargin += Y;
                        mLayoutParams5.leftMargin += X;
                        top_line.setLayoutParams(mLayoutParams1);
                        left_line.setLayoutParams(mLayoutParams2);
                        topLeft_corner.setLayoutParams(mLayoutParams3);
                        topRight_corner.setLayoutParams(mLayoutParams4);
                        bottomLeft_corner.setLayoutParams(mLayoutParams5);
                    } else if ((centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams5.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams2);
                        topLeft_corner.setLayoutParams(mLayoutParams3);
                        bottomLeft_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y > 0) && (centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams4.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topLeft_corner.setLayoutParams(mLayoutParams3);
                        topRight_corner.setLayoutParams(mLayoutParams4);
                    }
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        topRight_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams4.topMargin += Y;
                        mLayoutParams5.rightMargin -= X;
                        top_line.setLayoutParams(mLayoutParams1);
                        right_line.setLayoutParams(mLayoutParams2);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                        topLeft_corner.setLayoutParams(mLayoutParams4);
                        bottomRight_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y > 0) && (centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams4.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                        topLeft_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams5.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams2);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                        bottomRight_corner.setLayoutParams(mLayoutParams5);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        topRightFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams4.topMargin += Y;
                        mLayoutParams5.rightMargin -= X;
                        top_line.setLayoutParams(mLayoutParams1);
                        right_line.setLayoutParams(mLayoutParams2);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                        topLeft_corner.setLayoutParams(mLayoutParams4);
                        bottomRight_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y > 0) && (centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams4.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                        topLeft_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerImage.getHeight() < maxsize && Y > 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams5.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams2);
                        topRight_corner.setLayoutParams(mLayoutParams3);
                        bottomRight_corner.setLayoutParams(mLayoutParams5);
                    }
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottomLeft_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams4.bottomMargin -= Y;
                        mLayoutParams5.leftMargin += X;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        left_line.setLayoutParams(mLayoutParams2);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                        bottomRight_corner.setLayoutParams(mLayoutParams4);
                        topLeft_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y < 0) && (centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams4.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                        bottomRight_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams5.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams2);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                        topLeft_corner.setLayoutParams(mLayoutParams5);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottomLeftFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams4.bottomMargin -= Y;
                        mLayoutParams5.leftMargin += X;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        left_line.setLayoutParams(mLayoutParams2);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                        bottomRight_corner.setLayoutParams(mLayoutParams4);
                        topLeft_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y < 0) && (centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams4.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                        bottomRight_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams5.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams2);
                        bottomLeft_corner.setLayoutParams(mLayoutParams3);
                        topLeft_corner.setLayoutParams(mLayoutParams5);
                    }
                    lastX = curX;
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottomRight_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams4.bottomMargin -= Y;
                        mLayoutParams5.rightMargin -= X;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        right_line.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                        bottomLeft_corner.setLayoutParams(mLayoutParams4);
                        topRight_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y < 0) && (centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams4.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                        bottomLeft_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams5.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                        topRight_corner.setLayoutParams(mLayoutParams5);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottomRightFade.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams4.bottomMargin -= Y;
                        mLayoutParams5.rightMargin -= X;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        right_line.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                        bottomLeft_corner.setLayoutParams(mLayoutParams4);
                        topRight_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerImage.getHeight() < maxsize && Y < 0) && (centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams4.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                        bottomLeft_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerImage.getHeight() < maxsize && Y < 0) && !(centerImage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams5.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams2);
                        bottomRight_corner.setLayoutParams(mLayoutParams3);
                        topRight_corner.setLayoutParams(mLayoutParams5);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        centerImage.setOnTouchListener(new View.OnTouchListener() {
            private void Xchange(float x, ConstraintLayout.LayoutParams... params) {
                params[2].leftMargin += x;
                params[3].rightMargin -= x;
                params[4].leftMargin += x;
                params[5].rightMargin -= x;
                params[6].leftMargin += x;
                params[7].rightMargin -= x;
                left_line.setLayoutParams(params[2]);
                right_line.setLayoutParams(params[3]);
                topLeft_corner.setLayoutParams(params[4]);
                topRight_corner.setLayoutParams(params[5]);
                bottomLeft_corner.setLayoutParams(params[6]);
                bottomRight_corner.setLayoutParams(params[7]);
            }

            private void Ychange(float y, ConstraintLayout.LayoutParams... params) {
                params[0].topMargin += y;
                params[1].bottomMargin -= y;
                params[4].topMargin += y;
                params[5].topMargin += y;
                params[6].bottomMargin -= y;
                params[7].bottomMargin -= y;
                top_line.setLayoutParams(params[0]);
                bottom_line.setLayoutParams(params[1]);
                topLeft_corner.setLayoutParams(params[4]);
                topRight_corner.setLayoutParams(params[5]);
                bottomLeft_corner.setLayoutParams(params[6]);
                bottomRight_corner.setLayoutParams(params[7]);
            }

            int lastX;
            int lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams6 = (ConstraintLayout.LayoutParams) topRight_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams7 = (ConstraintLayout.LayoutParams) bottomLeft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams8 = (ConstraintLayout.LayoutParams) bottomRight_corner.getLayoutParams();

                int action = event.getAction();
                int curX = (int) event.getX();
                int curY = (int) event.getY();
                //System.out.println("처음 X값(curX) : "+curX);
                //System.out.println("처음 Y값(curY) : "+curY);
                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                    //System.out.println("움직임 X값(lastX) : "+lastX);
                    //System.out.println("움직임 Y값(lastY) : "+lastY);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    int X = curX - lastX;
                    int Y = curY - lastY;
                    //System.out.println("계산한 X값(X) : "+X);
                    //System.out.println("계산한 Y값(Y) : "+Y);
                    int deviceW = boxCL.getWidth();
                    int deviceH = boxCL.getHeight();

                    if ((top_line.getY() <= 0 && Y < 0)
                            && !(bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && (left_line.getX() <= 0 && X < 0)
                            && !(right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //좌상단코더 도달시 못움직임
                    } else if ((top_line.getY() <= 0 && Y < 0)
                            && !(bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && !(left_line.getX() <= 0 && X < 0)
                            && (right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //우상단코더 도달시 못움직임
                    } else if (!(top_line.getY() <= 0 && Y < 0)
                            && (bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && (left_line.getX() <= 0 && X < 0)
                            && !(right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //좌하단코더 도달시 못움직임
                    } else if (!(top_line.getY() <= 0 && Y < 0)
                            && (bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && !(left_line.getX() <= 0 && X < 0)
                            && (right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //우하단코더 도달시 못움직임
                    } else if ((top_line.getY() <= 0 && Y < 0)
                            && !(bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && !(left_line.getX() <= 0 && X < 0)
                            && !(right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //상단도달시 좌우만 움직임
                        Xchange(X, mLayoutParams1, mLayoutParams2, mLayoutParams3, mLayoutParams4, mLayoutParams5, mLayoutParams6, mLayoutParams7, mLayoutParams8);
                    } else if (!(top_line.getY() <= 0 && Y < 0)
                            && (bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && !(left_line.getX() <= 0 && X < 0)
                            && !(right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //하단도달시 좌우만 움직임
                        Xchange(X, mLayoutParams1, mLayoutParams2, mLayoutParams3, mLayoutParams4, mLayoutParams5, mLayoutParams6, mLayoutParams7, mLayoutParams8);
                    } else if (!(top_line.getY() <= 0 && Y < 0)
                            && !(bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && (left_line.getX() <= 0 && X < 0)
                            && !(right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //좌단도달시 상하만 움직임
                        Ychange(Y, mLayoutParams1, mLayoutParams2, mLayoutParams3, mLayoutParams4, mLayoutParams5, mLayoutParams6, mLayoutParams7, mLayoutParams8);
                    } else if (!(top_line.getY() <= 0 && Y < 0)
                            && !(bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && !(left_line.getX() <= 0 && X < 0)
                            && (right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //우단도달시 상하만 움직임
                        Ychange(Y, mLayoutParams1, mLayoutParams2, mLayoutParams3, mLayoutParams4, mLayoutParams5, mLayoutParams6, mLayoutParams7, mLayoutParams8);
                    } else if (!(top_line.getY() <= 0 && Y < 0)
                            && !(bottom_line.getY() + bottom_line.getHeight() >= deviceH && Y > 0)
                            && !(left_line.getX() <= 0 && X < 0)
                            && !(right_line.getX() + right_line.getWidth() >= deviceW && X > 0)) {
                        //아무것도 아닐땐 자유~~
                        Xchange(X, mLayoutParams1, mLayoutParams2, mLayoutParams3, mLayoutParams4, mLayoutParams5, mLayoutParams6, mLayoutParams7, mLayoutParams8);
                        Ychange(Y, mLayoutParams1, mLayoutParams2, mLayoutParams3, mLayoutParams4, mLayoutParams5, mLayoutParams6, mLayoutParams7, mLayoutParams8);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        //boxcrop controller가 활성화된 이후에는 포토뷰가 더이상 수정될 수 없어야 합니다.
        youCantTouchAnymore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                return true;
            }
        });


        settingThumbnailIV();
    }

    private void settingThumbnailIV() {
        ImageView thumbNailIV = parentActivity.findViewById(R.id.thumbnailImage);
        int partNum = parentActivity.getPartNum();
        Log.d("파트 번호", ((Integer)partNum).toString());
        if(partNum == 1)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_g));
        if(partNum == 2)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(android.R.color.transparent));
        if(partNum == 3)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_pole));
        if(partNum == 4)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_tree));
        if(partNum == 5)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_transformer));
        if(partNum == 6)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_a));
        if(partNum == 7)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_b));
        if(partNum == 8)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_c));
        if(partNum == 9)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_d));
        if(partNum == 10)
            thumbNailIV.setImageDrawable(parentActivity.getDrawable(R.drawable.part_e));
        thumbNailIV.bringToFront();
    }

    @Override
    public void setLayout(View view, String taskID) {

    }
    private void getDialog(final String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView opt=parentActivity.findViewById(R.id.optionText);
                if(title.equals(opt.getText().toString()+"테스트 통과")){


                    dialog.dismiss();
                    parentActivity.finish();
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }


}
