package com.example.hwshin.cachemission.DataStructure.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hwshin.cachemission.Activity.LoginActivity;
import com.example.hwshin.cachemission.Activity.TaskListActivity;
import com.example.hwshin.cachemission.DataStructure.Controller.Controller;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.R;

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
                    Log.d("idinfo", String.valueOf(mtaskview.gettaskID()));
                    param2.put("baseID", mtaskview.gettaskID());
                    param2.put("taskID", id);
                    param2.put("submit", edit.getText().toString());
                    new HttpRequest() {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);

                            try {
                                JSONObject resulttemp = new JSONObject(result);
                                if (resulttemp.get("success").toString().equals("false")) {
                                    if (resulttemp.get("message").toString().equals("login")) {
                                        Intent in = new Intent(parentActivity, LoginActivity.class);
                                        parentActivity.startActivity(in);
                                        Toast.makeText(parentActivity, "로그인이 만료되었습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT);
                                    } else if (resulttemp.get("message").toString().equals("task")) {
                                        Intent in = new Intent(parentActivity, TaskListActivity.class);
                                        parentActivity.startActivity(in);
                                        Toast.makeText(parentActivity, "테스크가 만료되었습니다. 다른 테스크를 선택해주세요", Toast.LENGTH_SHORT);

                                    }

                                } else {
                                    parentActivity.startActivity(parentIntent);
                                    parentActivity.finish();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }.execute("http://18.222.204.84/textSubmit", param2, logintoken);
                }catch (JSONException e){
                    e.printStackTrace();

                }

            }
        });




    }


}
