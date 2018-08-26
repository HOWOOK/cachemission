package com.example.hwshin.cachemission.DataStructure.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hwshin.cachemission.Activity.LoginActivity;
import com.example.hwshin.cachemission.Activity.TaskListActivity;
import com.example.hwshin.cachemission.Adapter.ButtonListAdapter;
import com.example.hwshin.cachemission.DataStructure.Controller.Controller;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.widget.Toast;

public class Controller_Buttons extends Controller {
    Context cont=null;
    int highlightflag=-1;



    public Controller_Buttons() {
        controllerID = R.layout.controller_buttons;
    }

    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.show();
    }

    @Override
    public void setLayout(final String id, View view, final Context c, final Intent in, String buttons) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);
        cont=c;

        ArrayList<String> textarray=new ArrayList<String>();
                    try {

                        JSONArray res = new JSONArray(buttons);
                        for(int i=0;i<res.length();i++) {

                            String temp =  res.get(i).toString();
                            Log.d("dataget", temp.toString());
                            textarray.add(temp);

                        }
                        ConstraintLayout templayout = (ConstraintLayout) view;
                        ListView lv = templayout.findViewById(R.id.buttons_list);
                        ButtonListAdapter adapter= new ButtonListAdapter(c, R.layout.controller_buttons_item, textarray);
                        lv.setAdapter(adapter);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long ids) {

                                if (position != highlightflag) {
                                    view.setBackgroundColor(Color.RED);
                                    highlightflag=position;
                                } else{
                                    String submit = adapterView.getItemAtPosition(position).toString();
                                JSONObject param2 = new JSONObject();
                                try {
                                    Log.d("idinfo",String.valueOf(mtaskview.gettaskID()));
                                    param2.put("answerID", mtaskview.gettaskID());
                                    param2.put("taskID", id);
                                    param2.put("submit", submit);


                                    new HttpRequest() {
                                        @Override
                                        protected void onPostExecute(Object o) {
                                            super.onPostExecute(o);

                                            try {
                                                JSONObject resulttemp = new JSONObject(result);
                                                if(resulttemp.get("success").toString().equals("false")){
                                                    if(resulttemp.get("message").toString().equals("login")){
                                                        Intent in = new Intent(parentActivity, LoginActivity.class);
                                                        parentActivity.startActivity(in);
                                                        Toast.makeText(parentActivity,"로그인이 만료되었습니다. 다시 로그인해주세요",Toast.LENGTH_SHORT).show();
                                                        parentActivity.finish();
                                                    }
                                                    else if(resulttemp.get("message").toString().equals("task")){

                                                        Toast.makeText(parentActivity,"테스크가 만료되었습니다. 다른 테스크를 선택해주세요",Toast.LENGTH_SHORT).show();
                                                        parentActivity.finish();
                                                    }else{

                                                        Toast.makeText(parentActivity,"남은 테스크가 없습니다.",Toast.LENGTH_SHORT).show();
                                                        parentActivity.finish();
                                                    }

                                                }
                                                else{
                                                    parentActivity.startActivity(parentIntent);
                                                    parentActivity.finish();

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }




                                        }
                                    }.execute("http://18.222.204.84/taskSubmit", param2,logintoken);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //Intent nextvideo=new Intent(c, TaskActivity.class);
                            }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    /*
                    Message msg= new Message();
                    msg.arg1=0;
                    msg.obj=result;
                    handler.sendMessage(msg);
*/













    }
}
