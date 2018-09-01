package com.selectstar.hwshin.cashmission.DataStructure.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.Activity.LoginActivity;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller_EditText extends Controller {

    public Controller_EditText() {
        controllerID = R.layout.controller_edittext;
    }


    @Override
    public void setLayout(final String id, View view, Context c, Intent in, String buttons) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        ConstraintLayout templayout = (ConstraintLayout) view;
        final EditText edit = templayout.findViewById(R.id.editText);
        Button sendb=templayout.findViewById(R.id.sendbtn);
        sendb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject param2 = new JSONObject();
                try {
                    param2.put("answerID", mtaskview.gettaskID());
                    param2.put("taskID", id);
                    String submit = edit.getText().toString();
                    if(id.equals("4")) {//if task is dialect
                        String region;
                        SharedPreferences explain = parentActivity.getSharedPreferences("region", Context.MODE_PRIVATE);
                        region = explain.getString("region",null);
                        submit = "("+region+")"+submit;
                    }
                    param2.put("submit", submit);
                    new HttpRequest(parentActivity) {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);

                            try {
                                JSONObject resulttemp = new JSONObject(result);
                                if (resulttemp.get("success").toString().equals("false")) {
                                    if (resulttemp.get("message").toString().equals("login")) {
                                        Intent in = new Intent(parentActivity, LoginActivity.class);
                                        parentActivity.startActivity(in);
                                        Toast.makeText(parentActivity, "로그인이 만료되었습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();
                                    } else if (resulttemp.get("message").toString().equals("task")) {

                                        Toast.makeText(parentActivity, "테스크가 만료되었습니다. 다른 테스크를 선택해주세요", Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();
                                    }
                                    else{

                                        Toast.makeText(parentActivity,"남은 테스크가 없습니다.",Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();

                                    }

                                } else {
                                    parentIntent.putExtra("from",1);
                                    parentActivity.startActivity(parentIntent);
                                    parentActivity.finish();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }.execute(parentActivity.getString(R.string.mainurl)+"/taskSubmit", param2, logintoken);
                }catch (JSONException e){
                    e.printStackTrace();

                }

            }
        });




    }


}
