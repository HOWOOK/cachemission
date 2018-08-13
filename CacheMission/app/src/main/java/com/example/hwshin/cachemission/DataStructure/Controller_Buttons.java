package com.example.hwshin.cachemission.DataStructure;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.hwshin.cachemission.Activity.TaskActivity;
import com.example.hwshin.cachemission.Activity.TaskListActivity;
import com.example.hwshin.cachemission.Adapter.ButtonListAdapter;
import com.example.hwshin.cachemission.Adapter.ListviewAdapter;
import com.example.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import android.os.Handler;

public class Controller_Buttons extends Controller {
    Context cont=null;


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
    public void setLayout(final String id, final View view, final Context c, final Intent in) {
        cont=c;
        ArrayList<String> textarray=new ArrayList<String>();
        textarray.add("천사다");
        textarray.add("천사당");
        textarray.add("천사담");
/*
        final Handler handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg)
            {

            }
        };

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                ArrayList<String> textarray=new ArrayList<String>();
                String result = msg.obj.toString();
                if(msg.what == 0){   // Message id 가 0 이면
                    try {
                        JSONObject resulttemp = new JSONObject(result);
                        JSONArray res = (JSONArray) resulttemp.get("options");
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

                                String submit= adapterView.getItemAtPosition(position).toString();
                                JSONObject param2 = new JSONObject();
                                try {
                                    param2.put("id", Integer.parseInt(id));
                                    param2.put("submit", submit);


                                    new HttpRequest() {
                                        @Override
                                        protected void onPostExecute(Object o) {
                                            super.onPostExecute(o);


                                        }
                                    }.execute("http://18.222.204.84/videoSubmit", param2);

                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //Intent nextvideo=new Intent(c, TaskActivity.class);
                                cont.startActivity(in);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
*/


       // ArrayList<String> textarray=new ArrayList<String>();
        JSONObject param = new JSONObject();
        try {
            param.put("id", Integer.parseInt(id));
            param.put("requestOption", "buttons");


            new HttpRequest() {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    ArrayList<String> textarray=new ArrayList<String>();
                    try {
                        JSONObject resulttemp = new JSONObject(result);
                        JSONArray res = (JSONArray) resulttemp.get("options");
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

                                String submit= adapterView.getItemAtPosition(position).toString();
                                JSONObject param2 = new JSONObject();
                                try {
                                    param2.put("id", Integer.parseInt(id));
                                    param2.put("submit", submit);


                                    new HttpRequest() {
                                        @Override
                                        protected void onPostExecute(Object o) {
                                            super.onPostExecute(o);
                                            Intent x = new Intent();
                                            String mId=(String)in.getStringExtra("taskid");
                                            TaskView mTaskView = (TaskView) in.getSerializableExtra("taskview");
                                            Controller mController = (Controller) in.getSerializableExtra("controller");
                                            int[][] mParameter =  (int[][]) in.getSerializableExtra("tasktype");
                                            String tasktitle = in.getStringExtra("tasktitle");
                                            x.putExtra("taskid",mId);
                                            x.putExtra("taskview",mTaskView);
                                            x.putExtra("controller",mController);
                                            x.putExtra("tasktype",mParameter);
                                            x.putExtra("tasktitle",tasktitle);

                                            parentActivity.startActivity(parentIntent);


                                        }
                                    }.execute("http://18.222.204.84/videoSubmit", param2);

                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //Intent nextvideo=new Intent(c, TaskActivity.class);

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
            }.execute("http://18.222.204.84/video", param);

        }
         catch (JSONException e) {
            e.printStackTrace();
        }








    }
}
