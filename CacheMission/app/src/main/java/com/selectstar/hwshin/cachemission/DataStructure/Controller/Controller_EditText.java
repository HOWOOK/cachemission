package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller_EditText extends Controller {
    public Controller_EditText(){
        controllerID=R.layout.controller_edittext;
    }

    private String result="foobar";
    @Override
    public void resetContent(final View view, final String taskID) {
        ConstraintLayout templayout = (ConstraintLayout) view;
        final EditText edit = templayout.findViewById(R.id.editText);
        edit.setText("");
        Button sendb=templayout.findViewById(R.id.sendbtn);

        sendb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit.getText().toString().length() < 3) {
                    Toast.makeText(parentActivity, "먼저 응답을 세 자 이상 기록해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edit.getText().toString().contains("(")){
                    Toast.makeText(parentActivity, "'(',')','&' 문자를 포함하지 말아주세요.", Toast.LENGTH_SHORT).show();
                    return;
                };
                if(edit.getText().toString().contains(")")){
                    Toast.makeText(parentActivity, "'(',')','&' 문자를 포함하지 말아주세요.", Toast.LENGTH_SHORT).show();
                    return;
                };
                if(edit.getText().toString().contains("&")){
                    Toast.makeText(parentActivity, "'(',')','&' 문자를 포함하지 말아주세요.", Toast.LENGTH_SHORT).show();
                    return;
                };
                JSONObject param2 = new JSONObject();
                try {
                    param2.put("answerID", ((TaskActivity)parentActivity).getAnswerID());
                    param2.put("taskID", taskID);
                    String submit = edit.getText().toString();
                    if(((TaskActivity)parentActivity).getTaskType().equals("DIALECT")) {//if task is dialect
                        String region;
                        SharedPreferences explain = parentActivity.getSharedPreferences("region", Context.MODE_PRIVATE);
                        region = explain.getString("region",null);
                        submit = "("+region+")"+submit;
                    }
                    param2.put("submit", submit);
                    new WaitHttpRequest(parentActivity) {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);

                            try {
                                JSONObject resultTemp = new JSONObject(result);
                                if (resultTemp.get("success").toString().equals("false")) {
                                    new ServerMessageParser().taskSubmitFailParse(parentActivity,resultTemp);
                                    parentActivity.finish();
                                } else {
                                    ((TaskActivity)parentActivity).startTask();
                                    parentActivity.goldSetting(String.valueOf(resultTemp.get("gold")));
                                    parentActivity.maybeSetting(String.valueOf(resultTemp.get("maybe")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }.execute(parentActivity.getString(R.string.mainurl)+"/testing/taskSubmit", param2,((TaskActivity)parentActivity).getLoginToken());
                }catch (JSONException e){
                    e.printStackTrace();

                }

            }
        });
    }

    @Override
    public void setLayout(View view, String taskID) {

    }
}