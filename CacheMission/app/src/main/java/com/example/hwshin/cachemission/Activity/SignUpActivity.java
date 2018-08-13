package com.example.hwshin.cachemission.Activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class SignUpActivity extends AppCompatActivity {
    EditText idText;
    EditText pwText;
    EditText pw2Text;
    Button checkButton;
    Button okButton;
    Button noButton;
    String validId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        idText = findViewById(R.id.idvalue);
        pwText = findViewById(R.id.pwvalue);
        pw2Text = findViewById(R.id.pw2value);
        okButton = findViewById(R.id.okbutton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idVal = idText.getText().toString();
                String pwVal = pwText.getText().toString();
                String pw2Val = pw2Text.getText().toString();
                if(errorCheck(idVal,pwVal,pw2Val))
                    return;
                try {
                    JSONObject param = new JSONObject();
                    param.put("id", validId);
                    param.put("pw", pwVal);

                    new HttpRequest(){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                System.out.println(result);
                                JSONObject res = new JSONObject(result);
                                if (res.get("success").toString() == "true") {
                                    Intent returnIntent = new Intent();
                                    //returnIntent.putExtra("result", result);
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                }
                                else
                                    getDialog("안됨",res.get("success").toString());
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }.execute("http://18.222.204.84/signup",param);
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        checkButton = findViewById(R.id.idcheck);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String idVal = idText.getText().toString();
                System.out.println("가자");
                try {
                    JSONObject param = new JSONObject();
                    param.put("id", idVal);
                    new HttpRequest(){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            System.out.println(result);
                            try {
                                JSONObject res = new JSONObject(result);
                                if (res.get("success").toString() == "true") {
                                    getDialog("굳 ID", "비번 치3");
                                    validId = idVal;
                                }
                                else
                                    getDialog("이미 있음",res.get("success").toString());
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }.execute("http://18.222.204.84/signupCheckId",param);
                    /*
                    new HttpRequest(){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject res = new JSONObject(result);
                                if (res.get("success") == true) {
                                    getDialog("굳 ID", "비번 치3");
                                    validId = idVal;
                                }
                                else
                                    getDialog("이미 있음",res.get("success").toString());
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }.execute("http://18.222.204.84/signupCheckId",param);*/
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        noButton = findViewById(R.id.nobutton);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.show();
    }
    private Boolean errorCheck(String id1, String pw1, String pw2)
    {
        if(validId.equals(""))
        {
            getDialog("확인좀","아이디 중복인지 확인 plz");
            return true;
        }
        if(!validId.equals(id1))
        {

            getDialog("확인좀","님이 지금 친 ID 다시 체크 고고");
            return true;
        }
        if(!pw1.equals(pw2))
        {

            getDialog("확인좀","비번 두 개 다른데?");
            return true;
        }
        if(pw1.length() < 4)
        {

            getDialog("확인좀","비번 4자 이상으로 해주셔야 해요");
            return true;
        }
        return false;
    }
}