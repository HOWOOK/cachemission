package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoView;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller_2DBoxLight extends Controller {

    private Button sendButton, completeButton;
    private ImageView pinButton;
    private View centerImage;
    private PhotoView photoView;
    private float originWidth, originHeight, originLeftMargin, originTopMargin;
    private TaskView_PhotoView mtaskView_PhotoView;
    private float[][] answerCoordinationTemp;
    private int[] answerTypeTemp;
    private int answerCount = 0;
    private int drawAnswerCount = 0;
    public boolean pinFlag;
    private int getDeviceDpi;
    private float dpScale;
    private ConstraintLayout photoViewCL;

    public Controller_2DBoxLight() {
        controllerID = R.layout.controller_2dbox;
    }

    public ImageView getPinButton() {
        return pinButton;
    }

    @Override
    public void resetContent(final View view, final String taskID) {
        centerImage = view.findViewById(R.id.centerimage);
        mtaskView_PhotoView = (TaskView_PhotoView) parentActivity.getmTaskView();
        pinFlag = true;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDeviceDpi = displayMetrics.densityDpi;
        dpScale = (float) getDeviceDpi / 160f;
        photoViewCL = parentActivity.findViewById(R.id.photoViewCL);
        if(mtaskView_PhotoView.answerCoordination != null)
            answerCount = mtaskView_PhotoView.answerCoordination.length;
        System.out.println("서버에서 그려진 정답 수 : " + answerCount);

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
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TaskView_PhotoView) parentActivity.getmTaskView()).expandFlag) {
                    Toast.makeText(parentActivity, "먼저 물체를 찾아주세요", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject param = new JSONObject();
                    try {
                        param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                        param.put("taskID", taskID);

                        TaskView_PhotoView temp = (TaskView_PhotoView) parentActivity.getmTaskView();
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
                        final float leftPercent, topPercent, rightPercent, bottomPercent;
                        String submit;
                        leftPercent = (x4 - originLeftMargin) / originWidth;
                        topPercent = (y4 - originTopMargin) / originHeight;
                        rightPercent = (x5 - originLeftMargin) / originWidth;
                        bottomPercent = (y5 - originTopMargin) / originHeight;
                        submit = leftPercent + "," + topPercent + "," + rightPercent + "," + bottomPercent;
                        param.put("submit", submit);

                        new WaitHttpRequest(parentActivity) {
                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);

                                try {
                                    JSONObject resultTemp = new JSONObject(result);
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
                                        System.out.println("서버반응 : "+resultTemp.get("success").toString());

                                        drawAnswerCount++;
                                        System.out.println("그려져있던 수 : "+answerCount+" 내가 그린 수 : "+drawAnswerCount);
                                        answerCoordinationTemp = mtaskView_PhotoView.answerCoordination;
                                        answerTypeTemp = mtaskView_PhotoView.answerType;

                                        if (mtaskView_PhotoView.answerCoordination!=null) {
                                            System.out.println("---------추가 전 ----------");
                                            for (int i = 0; i < mtaskView_PhotoView.answerCoordination.length; i++) {
                                                System.out.print("(");
                                                for (int j = 0; j < 4; j++) {
                                                    System.out.print(mtaskView_PhotoView.answerCoordination[i][j] + ",");
                                                }
                                                System.out.print(" 타입 : " + mtaskView_PhotoView.answerType[i]);
                                                System.out.println(")");
                                            }
                                        }

                                        mtaskView_PhotoView.answerCoordination = new float[answerCount + drawAnswerCount][4];
                                        mtaskView_PhotoView.answerType = new int[answerCount + drawAnswerCount];

                                        if(answerCoordinationTemp != null) {
                                            for (int i = 0; i < answerCoordinationTemp.length; i++) {
                                                mtaskView_PhotoView.answerCoordination[i][0] = answerCoordinationTemp[i][0];
                                                mtaskView_PhotoView.answerCoordination[i][1] = answerCoordinationTemp[i][1];
                                                mtaskView_PhotoView.answerCoordination[i][2] = answerCoordinationTemp[i][2];
                                                mtaskView_PhotoView.answerCoordination[i][3] = answerCoordinationTemp[i][3];
                                                mtaskView_PhotoView.answerType[i] = answerTypeTemp[i];
                                            }
                                        }

                                        if (mtaskView_PhotoView.answerCoordination!=null) {
                                            System.out.println("---------복사 후----------");
                                            for (int i = 0; i < mtaskView_PhotoView.answerCoordination.length; i++) {
                                                System.out.print("(");
                                                for (int j = 0; j < 4; j++) {
                                                    System.out.print(mtaskView_PhotoView.answerCoordination[i][j] + ",");
                                                }
                                                System.out.print(" 타입 : " + mtaskView_PhotoView.answerType[i]);
                                                System.out.println(")");
                                            }
                                        }


                                        mtaskView_PhotoView.answerCoordination[answerCount + drawAnswerCount - 1][0] = leftPercent;
                                        mtaskView_PhotoView.answerCoordination[answerCount + drawAnswerCount - 1][1] = topPercent;
                                        mtaskView_PhotoView.answerCoordination[answerCount + drawAnswerCount - 1][2] = rightPercent;
                                        mtaskView_PhotoView.answerCoordination[answerCount + drawAnswerCount - 1][3] = bottomPercent;
                                        mtaskView_PhotoView.answerType[answerCount + drawAnswerCount - 1] = 1;
                                        mtaskView_PhotoView.changedCoordination = new float[mtaskView_PhotoView.answerCoordination.length][4];

                                        if (mtaskView_PhotoView.answerCoordination!=null) {
                                            System.out.println("---------추가 후----------");
                                            for (int i = 0; i < mtaskView_PhotoView.answerCoordination.length; i++) {
                                                System.out.print("(");
                                                for (int j = 0; j < 4; j++) {
                                                    System.out.print(mtaskView_PhotoView.answerCoordination[i][j] + ",");
                                                }
                                                System.out.print(" 타입 : " + mtaskView_PhotoView.answerType[i]);
                                                System.out.println(")");
                                            }
                                        }

                                        mtaskView_PhotoView.drawAnswer(mtaskView_PhotoView.answerCoordination);


                                        ConstraintLayout textDragCL = parentActivity.findViewById(R.id.textDragCL);
                                        Toast.makeText(parentActivity, "제출 완료! 계속 찾아주세요.", Toast.LENGTH_SHORT).show();
                                        boxCL.setVisibility(View.INVISIBLE);
                                        textDragCL.setVisibility(View.VISIBLE);
                                        textDragCL.bringToFront();
                                        pinFlag = true;
                                        photoView.setScale(1);
                                        ((TaskView_PhotoView) parentActivity.getmTaskView()).expandFlag = true;
                                        parentActivity.setGold(String.valueOf(resultTemp.get("gold")));
                                        parentActivity.setMaybe(String.valueOf(resultTemp.get("maybe")));
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
            }
        });


        completeButton = view.findViewById(R.id.completebtn);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
                    alertDialogBuilder.setTitle("모든 물체 제출 완료");
                    alertDialogBuilder.setMessage("정말 모든 물체를 찾아졌나요?");
                    alertDialogBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            JSONObject param = new JSONObject();
                            try {
                                param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                                param.put("taskID", taskID);

                                //보내야하는 데이타
                                String submit = "(allclear)";
                                param.put("submit", submit);

                                new WaitHttpRequest(parentActivity) {
                                    @Override
                                    protected void onPostExecute(Object o) {
                                        super.onPostExecute(o);
                                        try {
                                            JSONObject resultTemp = new JSONObject(result);
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
                                                ((TaskActivity)parentActivity).startTask();
                                                parentActivity.setGold(String.valueOf(resultTemp.get("gold")));
                                                parentActivity.setMaybe(String.valueOf(resultTemp.get("maybe")));
                                                answerCount=0;
                                                drawAnswerCount=0;
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
                    alertDialogBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
            }
        });

        final int maxsize = 150;
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

        youCantTouchAnymore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pinFlag)
                    return false;
                return true;
            }
        });

    }

    @Override
    public void setLayout(View view, String taskID) {

    }


}