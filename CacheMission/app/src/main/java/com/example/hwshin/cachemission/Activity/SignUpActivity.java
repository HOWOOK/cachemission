package com.example.hwshin.cachemission.Activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
    Spinner genderSpinner;
    EditText nameText;
    Spinner regionSpinner;
    Spinner ageSpinner;
    EditText descriptionText;
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
        genderSpinner = (Spinner) findViewById(R.id.gendervalue);
        nameText = findViewById(R.id.namevalue);
        regionSpinner = (Spinner) findViewById(R.id.wherevalue);
        ageSpinner = (Spinner) findViewById(R.id.agevalue);
        descriptionText = findViewById(R.id.descriptionvalue);
        okButton = findViewById(R.id.okbutton);


        final ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.signup_gender, R.layout.signup_si);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter whereAdapter = ArrayAdapter.createFromResource(this,R.array.signup_where, R.layout.signup_si);
        whereAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(whereAdapter);

        ArrayAdapter ageAdapter = ArrayAdapter.createFromResource(this,R.array.signup_age, R.layout.signup_si);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);
        ageSpinner.setSelection(90);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idVal = idText.getText().toString();
                String pwVal = pwText.getText().toString();
                String pw2Val = pw2Text.getText().toString();
                String genderVal ="[ 선택 ]";
                 if (genderSpinner.getSelectedItem().toString().equals("남자"))
                     genderVal = "MALE";
                 else if(genderSpinner.getSelectedItem().toString().equals("여자"))
                     genderVal = "FEMALE";
                String nameVal = nameText.getText().toString();
                String regionVal = regionSpinner.getSelectedItem().toString();
                int ageVal = (int) Integer.parseInt(ageSpinner.getSelectedItem().toString());
                String descriptionVal = descriptionText.getText().toString();
                if(errorCheck(idVal,pwVal,pw2Val,genderVal,nameVal,regionVal,descriptionVal))
                    return;
                try {
                    JSONObject param = new JSONObject();
                    param.put("id", validId);
                    param.put("pw", pwVal);
                    param.put("gender", genderVal);
                    param.put("age", ageVal);
                    param.put("nickname",nameVal);
                    param.put("region",regionVal);
                    param.put("description", descriptionVal);
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
    private Boolean errorCheck(String id1, String pw1, String pw2, String gender, String name, String region, String description)
    {
        if(!id1.contains("@"))
        {
            getDialog("확인좀","ID가 이메일 형식이 아닙니다.");
            return true;
        }
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
        if(gender.equals("[ 선택 ]"))
        {
            getDialog("확인좀","성별 선택 안됨");
            return true;
        }
        if(pw1.length() < 4)
        {

            getDialog("확인좀","비번 4자 이상으로 해주셔야 해요");
            return true;
        }
        if(name.equals(""))
        {
            getDialog("확인좀","이름이뭐에요");
            return true;
        }
        if(region.equals("[ 선택 ]"))
        {
            getDialog("확인좀","어디사시나요");
            return true;
        }
        if(description.equals(""))
        {
            getDialog("확인좀","누구의 소개로...?");
            return true;
        }
        return false;
    }
}