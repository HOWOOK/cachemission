package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.Adapter.ClassificationAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.TaskView.TaskView_Classification;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Controller_Classification extends Controller {

    public Controller_Classification(){
        controllerID = R.layout.controller_classification;
    }

    @Override
    public void resetContent(View view, final String taskID) {
        Button sendbtn = parentActivity.findViewById(R.id.sendbtn);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
                alertDialogBuilder.setMessage("정답을 제출 할까요?");
                alertDialogBuilder.setPositiveButton("네",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        JSONObject param = new JSONObject();
                        JSONObject parmaMap = new JSONObject();
                        try {
                            param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                            param.put("taskID", taskID);

                            //보내야하는 데이타
                            ArrayList<Integer> idList = ((TaskView_Classification)parentActivity.getmTaskView()).idList;
                            ArrayList<Boolean> checkList = ((TaskView_Classification)parentActivity.getmTaskView()).checkList;

                            for(int i = 0; i < idList.size(); i++){
                                parmaMap.put(idList.get(i).toString(), checkList.get(i).toString());
                            }

                            System.out.println("파라미터 매치 : " + parmaMap);
                            param.put("submit", parmaMap);

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
                                                parentActivity.deleteWaitingTasks();
                                                parentActivity.finish();
                                            } else {
                                                Toast.makeText(parentActivity, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
                                                parentActivity.finish();
                                            }
                                        } else {
                                            parentActivity.updateWaitingTasks();
                                            ((TaskActivity) parentActivity).startTask();
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

    @Override
    public void setLayout(View view, String taskID) {

    }
}