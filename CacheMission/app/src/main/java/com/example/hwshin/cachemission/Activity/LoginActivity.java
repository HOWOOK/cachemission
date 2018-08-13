package com.example.hwshin.cachemission.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
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

public class LoginActivity extends AppCompatActivity {
    EditText idText;
    EditText pwText;
    Button loginButton;
    Button upButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId = auto.getString("inputId",null);
        String loginPw = auto.getString("inputPw",null);
        */
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
                                    getDialog("로그인 되었음", "축하폭하");

                                    SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = token.edit();
                                    editor.putString("logintoken", res.get("token").toString());
                                    editor.commit();

                                    startActivity(in);
                                    finish();
                                }
                                else
                                    getDialog("안됨",res.get("success").toString());
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
                getDialog("성공했어요","추카포카설포카");
            }
        }
    }

    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.show();
    }
}