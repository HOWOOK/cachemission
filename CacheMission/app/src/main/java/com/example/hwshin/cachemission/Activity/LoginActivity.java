package com.example.hwshin.cachemission.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;



import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {
    EditText idText;
    EditText pwText;
    Button loginButton;
    Button upButton;
    final int P_RECORD_AUDIO=77;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)  ){

            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO,WRITE_EXTERNAL_STORAGE},
                    P_RECORD_AUDIO);

        }
        setContentView(R.layout.activity_login);

        //앱을 처음깔았다면 앱 설명띄워주는 것
        SharedPreferences explain = getSharedPreferences("explain", MODE_PRIVATE);
        if(explain.getString("first", null)!=null){
            //do nothing
        }else{
            Intent intent_mainExplain = new Intent(LoginActivity.this, ExplainActivity.class);
            SharedPreferences.Editor editor = explain.edit();
            editor.putString("first", "done");
            editor.commit();
            startActivity(intent_mainExplain);
        }

        idText = findViewById(R.id.edit_id);
        pwText = findViewById(R.id.edit_pw);
        loginButton = findViewById(R.id.button_in);
        System.out.println(loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idVal = idText.getText().toString();
                String pwVal = pwText.getText().toString();
                try {
                    JSONObject param = new JSONObject();
                    param.put("id", idVal);
                    param.put("pw", pwVal);
                    new HttpRequest(){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject res = new JSONObject(result);
                                System.out.println(res);
                                Intent in=new Intent(LoginActivity.this, TaskListActivity.class);
                                if (res.get("success").toString() == "true"){
                                    SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = token.edit();
                                    //logintoken이라는 key값으로 token을 저장한다.
                                    editor.putString("logintoken", res.get("token").toString());
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
                    }.execute("http://18.222.204.84/signin",param);
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        upButton = findViewById(R.id.button_up);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                getDialog("회원가입에 성공하였습니다.","가입한 아이디로 로그인해 주세요.");
            }
        }
    }

    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
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