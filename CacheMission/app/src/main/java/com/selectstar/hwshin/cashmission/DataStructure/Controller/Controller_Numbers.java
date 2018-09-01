package com.selectstar.hwshin.cashmission.DataStructure.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.selectstar.hwshin.cashmission.Activity.LoginActivity;
import com.selectstar.hwshin.cashmission.Adapter.numbergridadapter;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Controller_Numbers extends Controller {
    public Controller_Numbers() {
        controllerID = R.layout.controller_numbers;
    }
    private String result="foobar";

    @Override
    public void setLayout(final String id, View view, Context c, Intent in, String buttons) {

        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        ConstraintLayout templayout =(ConstraintLayout) view;
        Button numberbutton=templayout.findViewById(R.id.numberbutton);

        //final RadioGroup radioGroup=templayout.findViewById(R.id.numberradio);
       // radioGroup.setOrientation(RadioGroup.HORIZONTAL);
        //RadioGroup.LayoutParams params;
        JSONArray res = null;

        try {
            res = new JSONArray(buttons);

            GridView gv=templayout.findViewById(R.id.gridViewff);
            //gv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            gv.setNumColumns(Integer.parseInt(res.get(0).toString()));
            ArrayList<ImageView> as=new ArrayList<>();
            for(int i=0; i<Integer.parseInt(res.get(0).toString());i++){
            ImageView iv =new ImageView(parentActivity);
            as.add(iv);
            }
            numbergridadapter adapter=new numbergridadapter(c,R.layout.number_button_item,as);
            gv.setAdapter(adapter);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long ids) {
                    result=String.valueOf(position+1);
                }
            });
            for(int i=0; i<Integer.parseInt(res.get(0).toString());i++){


            }



          //  cl.addView(IMGS[i]);




            //cl.addView(IMGS[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       numberbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (result.equals("foobar")) {
                Toast.makeText(parentActivity,"먼저 호감도를 선택해 주세요",Toast.LENGTH_SHORT).show();

               } else {
                   JSONObject param2 = new JSONObject();

                   try {

                       param2.put("answerID", mtaskview.gettaskID());
                       param2.put("taskID", id);
                       param2.put("submit", Integer.parseInt(result));
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

                                   } else {
                                       parentIntent.putExtra("from",1);
                                       parentActivity.startActivity(parentIntent);
                                       parentActivity.finish();

                                   }
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }


                           }
                       }.execute(parentActivity.getString(R.string.mainurl) + "/taskSubmit", param2, logintoken);
                   } catch (JSONException e) {
                       e.printStackTrace();

                   }
               }
           }
       });

    }
}
