package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Adapter.numbergridadapter;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Controller_Numbers extends Controller {
    public Controller_Numbers() {controllerID = R.layout.controller_numbers;    }
    private String result="foobar";
    private GridView gv;
    private AnimationDrawable frameAnimation;

    public void showAnimation()
    {
        ImageView view = parentActivity.findViewById(R.id.imageAnimation);
        TextView tView = parentActivity.findViewById(R.id.textAnimation);
        view.bringToFront();
        view.setBackgroundResource(R.drawable.coin_animation_list);
        Animation animation = AnimationUtils.loadAnimation(parentActivity,R.anim.goldtranslate);
        tView.setText("+ " + String.valueOf(parentActivity.getUpGold()) + " \uFFE6");
        tView.startAnimation(animation);

        AnimationDrawable spinnerAnim = (AnimationDrawable) view.getBackground();
        spinnerAnim.stop();
        spinnerAnim.start();
    }
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
                    parentActivity.setGold("-1");
                    parentActivity.setDailyQuest("-1");
                    param.put("answerID", parentActivity.getAnswerID());
                    param.put("taskID", taskID);
                    param.put("submit", Integer.parseInt(result));
                    parentActivity.updateWaitingTasks();
                    parentActivity.startTask();
                    showAnimation();
                    new HurryHttpRequest(parentActivity) {
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
                                        parentActivity.deleteWaitingTasks();
                                        parentActivity.finish();
                                    } else {
                                        Toast.makeText(parentActivity, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();
                                    }
                                    return;

                                } else {
                                    parentActivity.setGold(String.valueOf(resulttemp.get("gold")));
                                    parentActivity.setMaybe(String.valueOf(resulttemp.get("maybe")));
                                    parentActivity.setDailyQuest(String.valueOf(resulttemp.get("dailyQuest")));
                                    int bonus = Integer.parseInt(String.valueOf(resulttemp.get("bonus_up")));
                                    System.out.println(bonus);
                                    System.out.println("----");
                                    if(bonus > 0)
                                    {
                                        Toast.makeText(parentActivity,"일일 퀘스트 완료! 추가 보상 \uFFE6" + String.valueOf(bonus),Toast.LENGTH_SHORT).show();
                                    }


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
    public void setLayout(View view,String taskID)
    {
        gv = ((ConstraintLayout)view).findViewById(R.id.gridViewff);
        gv.setNumColumns(10);
        ArrayList<ImageView> as = new ArrayList<>();
        for(int i=0;i<10;i++) {
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