package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Adapter.numbergridadapter;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Controller_Numbers extends Controller {
    public Controller_Numbers() {controllerID = R.layout.controller_numbers;    }
    private String result="foobar";
    private GridView gv;
    private AnimationDrawable frameAnimation;

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
                    parentActivity.goldSetting("-1");
                    param.put("answerID", parentActivity.getAnswerID());
                    param.put("taskID", taskID);
                    param.put("submit", Integer.parseInt(result));
                    parentActivity.updateWaitingTasks();
                    parentActivity.startTask();
                    new HurryHttpRequest(parentActivity) {
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject resultTemp = new JSONObject(result);
                                System.out.println("resultTemp : "+resultTemp);
                                if (resultTemp.get("success").toString().equals("false")) {
                                    new ServerMessageParser().taskGetFailParse(parentActivity,resultTemp);
                                    parentActivity.finish();
                                } else {
                                    parentActivity.goldSetting(String.valueOf(resultTemp.get("gold")));
                                    parentActivity.maybeSetting(String.valueOf(resultTemp.get("maybe")));
                                    parentActivity.setQuestList(String.valueOf(resultTemp.get("questList")));
                                    int bonus = Integer.parseInt(String.valueOf(resultTemp.get("bonus_up")));
                                    System.out.println(bonus);
                                    System.out.println("----");
                                    if(bonus > 0)
                                    {
                                        Toast.makeText(parentActivity,"퀘스트 완료! 추가 보상 \uFFE6" + String.valueOf(bonus),Toast.LENGTH_SHORT).show();
                                    }
                                    parentActivity.showAnimation(R.drawable.coin_animation_list,parentActivity.getUpGold());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }.execute(parentActivity.getString(R.string.mainurl)+"/testing/taskSubmit", param, parentActivity.getLoginToken());
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }
    @Override
    public void setLayout(View view,String taskID)
    {
        gv = ((ConstraintLayout)view).findViewById(R.id.gridViewff);
        gv.setNumColumns(10);
        ArrayList<ImageView> as = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            ImageView iv = new ImageView(parentActivity);
            as.add(iv);
        }
        numbergridadapter adapter = new numbergridadapter(parentActivity,R.layout.number_button_item,as);
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
