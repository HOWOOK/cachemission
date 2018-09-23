package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_PhotoView;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller_2DBox extends Controller {

    Button boxBotton, sendBotton, completeBotton;
    TextView testingText1, testingText2, testingText3;
    View centerImage;
    PhotoView photoView;
    float originWidth, originHeight, originLeftMargin, originTopMargin;
    TaskView_PhotoView mtaskView_PhotoView;
    //float[][] answerCoordination;

    public Controller_2DBox() {
        controllerID = R.layout.controller_2dbox;
    }

    @Override
    public void resetContent(final View view, final String taskID) {
        //GPU가속 끄기 (그래픽 랜더링 문제 때문에 합니다.)
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        centerImage = view.findViewById(R.id.centerimage);
        mtaskView_PhotoView = (TaskView_PhotoView) parentActivity.getmTaskView();
        //answerCoordination = mtaskView_PhotoView.getAnswerCoordination();

        //처음에는 box가 없어야 합니다.
        final ConstraintLayout boxCL = view.findViewById(R.id.boxCL);
        if (boxCL.getVisibility() == boxCL.VISIBLE)
            boxCL.setVisibility(View.INVISIBLE);

        boxBotton = view.findViewById(R.id.boxbtn);
        boxBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxCL.getVisibility() == boxCL.VISIBLE) {
                    boxCL.setVisibility(View.INVISIBLE);
                    boxBotton.setText("BOX");
                } else {
                    boxCL.setVisibility(View.VISIBLE);
                    boxBotton.setText("OFF");
                }
            }
        });

        sendBotton = view.findViewById(R.id.sendbtn);
        sendBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    testingText1 = view.findViewById(R.id.testingtext1);
                    testingText2 = view.findViewById(R.id.testingtext2);
                    testingText3 = view.findViewById(R.id.testingtext3);

                    /********************************
                     * (x1, y1) = crop box의 현재 좌상단 좌표
                     * (x2, y2) = crop box의 현재 우하단 좌표
                     * (x3, y3) = photoView를 담고있는 constraintlayout의 (0,0)의 좌표가 Scale = 1일 때는 뭔지 환산한 값
                     * (x4, y4) = crop box의 scale = 1일 대 좌상단 좌표 환산값
                     * (x5, y5) = crop box의 scale = 1일 대 우하단 좌표 환산값
                     *********************************/
                    float x1, x2, x3, x4, x5, y1, y2, y3, y4, y5;
                    x1 = centerImage.getX();
                    x2 = x1 + centerImage.getWidth();
                    y1 = centerImage.getY();
                    y2 = y1 + centerImage.getHeight();
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
                                   //((TaskActivity) parentActivity).startTask();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute(parentActivity.getString(R.string.mainurl) + "/taskSubmit", param, ((TaskActivity) parentActivity).getLoginToken());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        completeBotton = view.findViewById(R.id.completebtn);
        completeBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoView = mtaskView_PhotoView.getPhotoView();

                System.out.println("디스플레이 네모 : " + photoView.getDisplayRect());
                System.out.println("배율 : " + photoView.getScale());
                int widthCL = boxCL.getWidth();
                int heightCL = boxCL.getHeight();
                testingText1 = view.findViewById(R.id.testingtext1);
                testingText2 = view.findViewById(R.id.testingtext2);
                testingText3 = view.findViewById(R.id.testingtext3);

                /********************************
                 * (x1, y1) = crop box의 현재 좌상단 좌표
                 * (x2, y2) = crop box의 현재 우하단 좌표
                 * (x3, y3) = photoView를 담고있는 constraintlayout의 (0,0)의 좌표가 Scale = 1일 때는 뭔지 환산한 값
                 * (x4, y4) = crop box의 scale = 1일 대 좌상단 좌표 환산값
                 * (x5, y5) = crop box의 scale = 1일 대 우하단 좌표 환산값
                 */
                float x1, x2, x3, x4, x5, y1, y2, y3, y4, y5;
                x1 = centerImage.getX();
                x2 = x1 + centerImage.getWidth();
                y1 = centerImage.getY();
                y2 = y1 + centerImage.getHeight();
                originWidth = (photoView.getDisplayRect().right - photoView.getDisplayRect().left) / photoView.getScale();
                originHeight = (photoView.getDisplayRect().bottom - photoView.getDisplayRect().top) / photoView.getScale();
                originLeftMargin = (widthCL / 2.0f) - (originWidth) / 2.0f;
                originTopMargin = (heightCL / 2.0f) - (originHeight) / 2.0f;
                x3 = originLeftMargin - photoView.getDisplayRect().left / photoView.getScale();
                y3 = originTopMargin - photoView.getDisplayRect().top / photoView.getScale();
                testingText2.setText("현화면 (0,0) = \n(" + x3 + "," + y3 + ")");
                x4 = x3 + x1 / photoView.getScale();
                y4 = y3 + y1 / photoView.getScale();
                x5 = x3 + x2 / photoView.getScale();
                y5 = y3 + y2 / photoView.getScale();
                testingText3.setText("(" + x4 + "," + y4 + ")\n(" + x5 + "," + y5 + ")");

//                System.out.println("(x1,y1),(x2,y2) ="+"(" + x1 + "," + y1 + "), (" + x2 + "," + y2 + ")");
//                System.out.println("(widthCL,heightCL) ="+"(" + widthCL + "," + heightCL + ")");
//                System.out.println("(originWidth,originHeight),(originLeftMargin,originTopMargin) ="+"(" + originWidth + "," + originHeight + "), (" + originLeftMargin + "," + originTopMargin + ")");

                //보내야하는 데이타
                float leftPercent, topPercent, rightPercent, bottomPercent;
                String submit;
                leftPercent = (x4 - originLeftMargin) / originWidth;
                topPercent = (y4 - originTopMargin) / originHeight;
                rightPercent = (x5 - originLeftMargin) / originWidth;
                bottomPercent = (y5 - originTopMargin) / originHeight;
                testingText1.setText(leftPercent + "," + topPercent + ",\n" + rightPercent + "," + bottomPercent);




            }
        });

        final int maxsize = 150;
        final View top_line = view.findViewById(R.id.top_line);
        final View bottom_line = view.findViewById(R.id.bottom_line);
        final View left_line = view.findViewById(R.id.left_line);
        final View right_line = view.findViewById(R.id.right_line);
        final View topleft_corner = view.findViewById(R.id.topleft_corner);
        final View topright_corner = view.findViewById(R.id.topright_corner);
        final View bottomleft_corner = view.findViewById(R.id.bottomleft_corner);
        final View bottomright_corner = view.findViewById(R.id.bottomright_corner);
        final View centerimage = view.findViewById(R.id.centerimage);
        final View youcanttouchanymore = view.findViewById(R.id.youcanttouchanymore);

        top_line.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topright_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getHeight() < maxsize && Y > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topleft_corner.setLayoutParams(mLayoutParams2);
                        topright_corner.setLayoutParams(mLayoutParams3);
                    }
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
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) bottomleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomright_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getHeight() < maxsize && Y < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomleft_corner.setLayoutParams(mLayoutParams2);
                        bottomright_corner.setLayoutParams(mLayoutParams3);
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
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomleft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.leftMargin += X;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams1);
                        topleft_corner.setLayoutParams(mLayoutParams2);
                        bottomleft_corner.setLayoutParams(mLayoutParams3);
                    }
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
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) topright_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomright_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.rightMargin -= X;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams1);
                        topright_corner.setLayoutParams(mLayoutParams2);
                        bottomright_corner.setLayoutParams(mLayoutParams3);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        topleft_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) topright_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) bottomleft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getHeight() < maxsize && Y > 0) && !(centerimage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams4.topMargin += Y;
                        mLayoutParams5.leftMargin += X;
                        top_line.setLayoutParams(mLayoutParams1);
                        left_line.setLayoutParams(mLayoutParams2);
                        topleft_corner.setLayoutParams(mLayoutParams3);
                        topright_corner.setLayoutParams(mLayoutParams4);
                        bottomleft_corner.setLayoutParams(mLayoutParams5);
                    } else if ((centerimage.getHeight() < maxsize && Y > 0) && !(centerimage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams5.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams2);
                        topleft_corner.setLayoutParams(mLayoutParams3);
                        bottomleft_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerimage.getHeight() < maxsize && Y > 0) && (centerimage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams4.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topleft_corner.setLayoutParams(mLayoutParams3);
                        topright_corner.setLayoutParams(mLayoutParams4);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        topright_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) topright_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) topleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) bottomright_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getHeight() < maxsize && Y > 0) && !(centerimage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams4.topMargin += Y;
                        mLayoutParams5.rightMargin -= X;
                        top_line.setLayoutParams(mLayoutParams1);
                        right_line.setLayoutParams(mLayoutParams2);
                        topright_corner.setLayoutParams(mLayoutParams3);
                        topleft_corner.setLayoutParams(mLayoutParams4);
                        bottomright_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerimage.getHeight() < maxsize && Y > 0) && (centerimage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.topMargin += Y;
                        mLayoutParams3.topMargin += Y;
                        mLayoutParams4.topMargin += Y;
                        top_line.setLayoutParams(mLayoutParams1);
                        topright_corner.setLayoutParams(mLayoutParams3);
                        topleft_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerimage.getHeight() < maxsize && Y > 0) && !(centerimage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams5.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams2);
                        topright_corner.setLayoutParams(mLayoutParams3);
                        bottomright_corner.setLayoutParams(mLayoutParams5);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottomleft_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) bottomright_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topleft_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getHeight() < maxsize && Y < 0) && !(centerimage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams4.bottomMargin -= Y;
                        mLayoutParams5.leftMargin += X;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        left_line.setLayoutParams(mLayoutParams2);
                        bottomleft_corner.setLayoutParams(mLayoutParams3);
                        bottomright_corner.setLayoutParams(mLayoutParams4);
                        topleft_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerimage.getHeight() < maxsize && Y < 0) && (centerimage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams4.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomleft_corner.setLayoutParams(mLayoutParams3);
                        bottomright_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerimage.getHeight() < maxsize && Y < 0) && !(centerimage.getWidth() < maxsize && X > 0)) {
                        mLayoutParams2.leftMargin += X;
                        mLayoutParams3.leftMargin += X;
                        mLayoutParams5.leftMargin += X;
                        left_line.setLayoutParams(mLayoutParams2);
                        bottomleft_corner.setLayoutParams(mLayoutParams3);
                        topleft_corner.setLayoutParams(mLayoutParams5);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        bottomright_corner.setOnTouchListener(new View.OnTouchListener() {
            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) bottomright_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) bottomleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topright_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    if (!(centerimage.getHeight() < maxsize && Y < 0) && !(centerimage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams4.bottomMargin -= Y;
                        mLayoutParams5.rightMargin -= X;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        right_line.setLayoutParams(mLayoutParams2);
                        bottomright_corner.setLayoutParams(mLayoutParams3);
                        bottomleft_corner.setLayoutParams(mLayoutParams4);
                        topright_corner.setLayoutParams(mLayoutParams5);
                    } else if (!(centerimage.getHeight() < maxsize && Y < 0) && (centerimage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams1.bottomMargin -= Y;
                        mLayoutParams3.bottomMargin -= Y;
                        mLayoutParams4.bottomMargin -= Y;
                        bottom_line.setLayoutParams(mLayoutParams1);
                        bottomright_corner.setLayoutParams(mLayoutParams3);
                        bottomleft_corner.setLayoutParams(mLayoutParams4);
                    } else if ((centerimage.getHeight() < maxsize && Y < 0) && !(centerimage.getWidth() < maxsize && X < 0)) {
                        mLayoutParams2.rightMargin -= X;
                        mLayoutParams3.rightMargin -= X;
                        mLayoutParams5.rightMargin -= X;
                        right_line.setLayoutParams(mLayoutParams2);
                        bottomright_corner.setLayoutParams(mLayoutParams3);
                        topright_corner.setLayoutParams(mLayoutParams5);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

        centerimage.setOnTouchListener(new View.OnTouchListener() {
            private void Xchange(float x, ConstraintLayout.LayoutParams... params) {
                params[2].leftMargin += x;
                params[3].rightMargin -= x;
                params[4].leftMargin += x;
                params[5].rightMargin -= x;
                params[6].leftMargin += x;
                params[7].rightMargin -= x;
                left_line.setLayoutParams(params[2]);
                right_line.setLayoutParams(params[3]);
                topleft_corner.setLayoutParams(params[4]);
                topright_corner.setLayoutParams(params[5]);
                bottomleft_corner.setLayoutParams(params[6]);
                bottomright_corner.setLayoutParams(params[7]);
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
                topleft_corner.setLayoutParams(params[4]);
                topright_corner.setLayoutParams(params[5]);
                bottomleft_corner.setLayoutParams(params[6]);
                bottomright_corner.setLayoutParams(params[7]);
            }

            float lastX;
            float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams mLayoutParams1 = (ConstraintLayout.LayoutParams) top_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams2 = (ConstraintLayout.LayoutParams) bottom_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams3 = (ConstraintLayout.LayoutParams) left_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams4 = (ConstraintLayout.LayoutParams) right_line.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams5 = (ConstraintLayout.LayoutParams) topleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams6 = (ConstraintLayout.LayoutParams) topright_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams7 = (ConstraintLayout.LayoutParams) bottomleft_corner.getLayoutParams();
                ConstraintLayout.LayoutParams mLayoutParams8 = (ConstraintLayout.LayoutParams) bottomright_corner.getLayoutParams();

                int action = event.getAction();
                float curX = event.getX();
                float curY = event.getY();
                //System.out.println("처음 X값(curX) : "+curX);
                //System.out.println("처음 Y값(curY) : "+curY);
                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = curX;
                    lastY = curY;
                    //System.out.println("움직임 X값(lastX) : "+lastX);
                    //System.out.println("움직임 Y값(lastY) : "+lastY);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float X = curX - lastX;
                    float Y = curY - lastY;
                    //System.out.println("계산한 X값(X) : "+X);
                    //System.out.println("계산한 Y값(Y) : "+Y);
                    int deviceW = boxCL.getWidth();
                    int deviceH = boxCL.getHeight();
                    float temp1 = bottom_line.getY() + bottom_line.getHeight();
                    float temp3 = right_line.getX() + right_line.getWidth();

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

        youcanttouchanymore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    @Override
    public void setLayout(View view, String taskID) {

    }

    public void drawAnswer(){

    }


}
