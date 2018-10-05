package com.selectstar.hwshin.cachemission.LoginHelper;


import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;


import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskListActivity;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;


public class TotalLoginActivity extends AppCompatActivity {

    private Context mContext;


    private Button btn_cashmission_login;
    private Button btn_kakao_login;
    private Button btn_facebook_login;

    Session session;
    SessionCallback sessionCallback=new SessionCallback();




    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_total_login);

        mContext = getApplicationContext();

        btn_cashmission_login=(Button) findViewById(R.id.btn_cashmission_login);

        btn_cashmission_login.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent cashmissionLoginIntent = new Intent(TotalLoginActivity.this,LoginActivity.class);

                startActivity(cashmissionLoginIntent);

                finish();

            }

        });

        btn_kakao_login = (Button) findViewById(R.id.btn_kakao_login);

        btn_kakao_login.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                session = Session.getCurrentSession();

                session.addCallback(sessionCallback);

                session.open(AuthType.KAKAO_LOGIN_ALL, TotalLoginActivity.this);

                try {
                    JSONObject param = new JSONObject();
                    JSONObject information=new JSONObject();
                    information.put("userEmail",sessionCallback.userEmail);
                    information.put("userName",sessionCallback.userName);
                    param.put("type", "kakao");
                    param.put("info", information.toString());
                    new WaitHttpRequest(view.getContext()){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject res = new JSONObject(result);
                                System.out.println(res);
                                Intent in=new Intent(TotalLoginActivity.this, TaskListActivity.class);
                                if (res.get("success").toString() == "true"){
                                    SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = token.edit();
                                    //logintoken이라는 key값으로 token을 저장한다.
                                    editor.putString("loginToken", res.get("token").toString());
                                    editor.commit();
                                    startActivity(in);
                                    finish();
                                }
                                else
                                    getDialog("로그인에 실패하였습니다.","아이디와 비밀번호를 확인해주세요.");
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }.execute(getString(R.string.mainurl)+"/signin",param);
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }



            }

        });




    }

    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TotalLoginActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
}
