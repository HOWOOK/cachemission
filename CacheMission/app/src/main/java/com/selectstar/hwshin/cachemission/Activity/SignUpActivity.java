package com.selectstar.hwshin.cachemission.Activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    Context context = this;
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
    ImageView backButton;
    String validId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        idText = findViewById(R.id.idvalue);
        pwText = findViewById(R.id.pwvalue);
        pw2Text = findViewById(R.id.pw2value);
        genderSpinner = (Spinner) findViewById(R.id.gendervalue);
       // nameText = findViewById(R.id.namevalue);
      //  regionSpinner = (Spinner) findViewById(R.id.wherevalue);
        ageSpinner = (Spinner) findViewById(R.id.agevalue);
       // descriptionText = findViewById(R.id.descriptionvalue);
        okButton = findViewById(R.id.okbutton);
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.signup_gender, R.layout.signup_si);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

       // ArrayAdapter whereAdapter = ArrayAdapter.createFromResource(this,R.array.signup_where, R.layout.signup_si);
       // whereAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // regionSpinner.setAdapter(whereAdapter);

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
               // String nameVal = nameText.getText().toString();
               // String regionVal = regionSpinner.getSelectedItem().toString();
                int ageVal = (int) Integer.parseInt(ageSpinner.getSelectedItem().toString());
                //String descriptionVal = descriptionText.getText().toString();
                if(errorCheck(idVal,pwVal,pw2Val,genderVal))
                    return;
                try {
                    JSONObject param = new JSONObject();
                    param.put("id", validId);
                    param.put("pw", pwVal);
                    param.put("gender", genderVal);
                    param.put("age", ageVal);
                    param.put("nickname","defNickName");
                    param.put("region","defRegion");
                    param.put("description", "defDescription");
                    new WaitHttpRequest(context){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                System.out.println(result);
                                JSONObject res = new JSONObject(result);
                                if (res.get("success").toString() == "true") {
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                }
                                else
                                    getDialog("회원가입 실패",res.get("message").toString());
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }.execute(getString(R.string.mainurl)+"/signup",param);
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
                boolean checkisemailformat = Pattern.matches("^(.+)(@)(.+)(\\.)(.+)$", idVal);
                if(!checkisemailformat)
                {
                    getDialog("회원가입 양식에 문제가 있습니다.","ID가 이메일 형식이 아닙니다.");
                    return;
                }
                try {
                    JSONObject param = new JSONObject();
                    param.put("id", idVal);
                    new WaitHttpRequest(context){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject res = new JSONObject(result);
                                if (res.get("success").toString() == "true") {
                                    getDialog("사용가능한 아이디 입니다.", "비밀번호를 지정해주세요.");
                                    validId = idVal;
                                }
                                else
                                    getDialog("이미 존재하는 아이디 입니다.", res.get("success").toString());
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }.execute(getString(R.string.mainurl)+"/signupCheckId",param);

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
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
    private Boolean errorCheck(String id1, String pw1, String pw2, String gender)
    {
        boolean checkisemailformat = Pattern.matches("^(.+)(@)(.+)(\\.)(.+)$", id1);
        //TODO : 비밀번호 문자와 숫자 조합인건 어떻게 확인할 수 있을까..
        if(!checkisemailformat)
        {
            getDialog("회원가입 양식에 문제가 있습니다.","ID가 이메일 형식이 아닙니다.");
            return true;
        }
        if(validId.equals(""))
        {
            getDialog("회원가입 양식에 문제가 있습니다.","아이디 중복체크를 해주세요.");
            return true;
        }
        if(!validId.equals(id1))
        {
            getDialog("회원가입 양식에 문제가 있습니다.","아이디 중복체크를 해주세요.");
            return true;
        }
        if(!pw1.equals(pw2))
        {
            getDialog("회원가입 양식에 문제가 있습니다.","비밀번호확인이 일치하지 않습니다.");
            return true;
        }
        if(gender.equals("[ 선택 ]"))
        {
            getDialog("회원가입 양식에 문제가 있습니다.","성별을 선택해주세요.");
            return true;
        }
        if(pw1.length() < 4)
        {
            getDialog("회원가입 양식에 문제가 있습니다.","4자 이상의 비밀번호를 설정해주세요.");
            return true;
        }

        return false;
    }
}