package com.selectstar.hwshin.cashmission.DataStructure.NewController;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.Activity.LoginActivity;
import com.selectstar.hwshin.cashmission.Adapter.numbergridadapter;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewController_Numbers extends NewController {
    public NewController_Numbers() {controllerID = R.layout.controller_numbers;    }
    private String result="foobar";
    private GridView gv;

    @Override
    public void resetContent(final View view, final String taskID)
    {
        gv.clearChoices();
        gv.clearFocus();
        result="foobar";
        Button numberButton = view.findViewById(R.id.numberbutton);
        numberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.equals("foobar")) {
                    Toast.makeText(parentActivity, "먼저 호감도를 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject param = new JSONObject();

                try {
                    System.out.println(parentActivity.getAnswerID());
                    System.out.println(taskID);
                    System.out.println(Integer.parseInt(result));
                    param.put("answerID", parentActivity.getAnswerID());
                    param.put("taskID", taskID);
                    param.put("submit", Integer.parseInt(result));
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
                                    } else {

                                        Toast.makeText(parentActivity, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();

                                    }
                                    return;

                                } else {
                                    parentActivity.startTask();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }.execute(parentActivity.getString(R.string.mainurl)+"/taskSubmit", param, parentActivity.getLoginToken());
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }
    @Override
    public void setLayout(View view,Context context,String taskID)
    {
        gv = ((ConstraintLayout)view).findViewById(R.id.gridViewff);
        gv.setNumColumns(10);
        ArrayList<ImageView> as = new ArrayList<>();
        for(int i=0;i<10;i++) {
            ImageView iv = new ImageView(parentActivity);
            as.add(iv);
        }
        numbergridadapter adapter = new numbergridadapter(context,R.layout.number_button_item,as);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                result = String.valueOf(position+1);
            }
        });
        resetContent(view,taskID);
    }
}
