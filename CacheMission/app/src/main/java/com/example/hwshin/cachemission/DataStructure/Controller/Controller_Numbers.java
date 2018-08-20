package com.example.hwshin.cachemission.DataStructure.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hwshin.cachemission.Activity.LoginActivity;
import com.example.hwshin.cachemission.Activity.TaskListActivity;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Controller_Numbers extends Controller {
    public Controller_Numbers() {
        controllerID = R.layout.controller_numbers;
    }
    @Override
    public void setLayout(final String id, View view, Context c, Intent in, String buttons) {

        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        ConstraintLayout templayout =(ConstraintLayout) view;
        Button numberbutton=templayout.findViewById(R.id.numberbutton);
        final RadioGroup radioGroup=new RadioGroup(parentActivity);
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);
        RadioGroup.LayoutParams params;
        //buttons="[1111]";
        JSONArray res = null;
        try {
            res = new JSONArray(buttons);



        for(int i=0; i<Integer.parseInt(res.get(0).toString());i++){
            RadioButton rb= new RadioButton(parentActivity);
            rb.setText(String.valueOf(i+1));
            rb.setTextSize(40);
            rb.setPadding(20,0,20,0);
            rb.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("foo","ba");
                rb.setButtonDrawable(null);

                //Drawable d=parentActivity.getDrawable(R.drawable.recordstart);
rb.setBackground(parentActivity.getDrawable(R.drawable.radiobutton));
                //rb.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.radiobutton,0,0);
            }

            params=new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.MATCH_PARENT);
            radioGroup.addView(rb,params);

        }

        templayout.addView(radioGroup);

        } catch (JSONException e) {
            e.printStackTrace();
        }
       numberbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               JSONObject param2 = new JSONObject();
               Log.d("subsub",String.valueOf(radioGroup.getCheckedRadioButtonId()));
               try {
                   Log.d("idinfo", String.valueOf(mtaskview.gettaskID()));
                   param2.put("baseID", mtaskview.gettaskID());
                   param2.put("taskID", id);
                   param2.put("submit", radioGroup.getCheckedRadioButtonId());
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
                                       parentActivity.finish();
                                   } else if (resulttemp.get("message").toString().equals("task")) {
                                       Intent in = new Intent(parentActivity, TaskListActivity.class);
                                       parentActivity.startActivity(in);
                                       Toast.makeText(parentActivity, "테스크가 만료되었습니다. 다른 테스크를 선택해주세요", Toast.LENGTH_SHORT);
                                       parentActivity.finish();
                                   }
                                   else{
                                       Intent in = new Intent(parentActivity, TaskListActivity.class);
                                       parentActivity.startActivity(in);
                                       Toast.makeText(parentActivity,"남은 테스크가 없습니다.",Toast.LENGTH_SHORT);
                                       parentActivity.finish();

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
