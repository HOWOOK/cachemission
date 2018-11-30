package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
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
                        try {
                            param.put("answerID", ((TaskActivity) parentActivity).getAnswerID());
                            param.put("taskID", taskID);

                            //보내야하는 데이타
                            ArrayList<Integer> partList = ((TaskView_Classification)parentActivity.getmTaskView()).partList;
                            ArrayList<Boolean> checkList = ((TaskView_Classification)parentActivity.getmTaskView()).checkList;

                            System.out.println("----파트리스트----");
                            for (int i = 0; i < partList.size(); i++){
                                System.out.print(partList.get(i)+" ");
                            }
                            System.out.println("\n------------------");

                            System.out.println("----체크리스트----");
                            for (int i = 0; i < checkList.size(); i++){
                                System.out.print(checkList.get(i)+" ");
                            }
                            System.out.println("\n------------------");

                            String submitString = "<group>";

                            int partTemp = 0;
                            for(int i = 0; i < partList.size(); i++){
                                if (checkList.get(i) == true) {
                                    partTemp = 0;
                                    if(parentActivity.taskDifficulty.equals("1") || parentActivity.taskDifficulty.equals("2")) {
                                        partTemp = 100;
                                    }
                                    if(parentActivity.taskDifficulty.equals("3")) {
                                        partTemp = 200;
                                    }
                                    partTemp += partList.get(i);
                                    submitString += ((Integer)partTemp).toString() + ",";
                                }
                            }

                            submitString = submitString.substring(0,submitString.length()-1);

                            System.out.println("classification 서브밋 데이타 : " + submitString);
                            param.put("submit", submitString);
                            new WaitHttpRequest(parentActivity) {
                                @Override
                                protected void onPostExecute(Object o) {
                                    super.onPostExecute(o);
                                    try {
                                        JSONObject resultTemp = new JSONObject(result);
                                        if (resultTemp.get("success").toString().equals("false")) {
                                            new ServerMessageParser().taskSubmitFailParse(parentActivity,resultTemp);

                                        } else {
                                            parentActivity.goldSetting(String.valueOf(resultTemp.get("gold")));
                                            parentActivity.maybeSetting(String.valueOf(resultTemp.get("maybe")));
                                            parentActivity.setQuestList(String.valueOf(resultTemp.get("questList")));
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
