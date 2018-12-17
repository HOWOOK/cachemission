package com.selectstar.hwshin.cachemission.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {
    Guideline viewGuideline;
    EditText idText;
    EditText pwText;
    TextView loginButton;
    TextView upButton;
    TextView findid,findpw;
    Guideline guideline;
    ImageView backbutton;
    CheckBox IDRemember,PWRemember;
    final int P_RECORD_AUDIO=77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

            ActivityCompat.requestPermissions(this,
                    new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                    P_RECORD_AUDIO);
        }
        setContentView(R.layout.activity_login);
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr")) {
            Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
            t.setScreenName("LoginActivity");
            t.send(new HitBuilders.AppViewBuilder().build());
        }

        IDRemember=findViewById(R.id.IDRemember);
        PWRemember=findViewById(R.id.PWRemember);

        viewGuideline = this.findViewById(R.id.guideline);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int getDeviceHeight_Pixel = displayMetrics.heightPixels;
       // viewGuideline.setGuidelineBegin(getDeviceHeight_Pixel / 2);

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
        SharedPreferences IDPWRemember = getSharedPreferences("IDPWRemember", MODE_PRIVATE);
        String rememberedID="";
        String rememberedPW="";
        rememberedID=IDPWRemember.getString("loginID","");
        rememberedPW=IDPWRemember.getString("loginPW","");
        if(!rememberedID.equals(""))
            idText.setText(rememberedID);
        if(!rememberedPW.equals(""))
            pwText.setText(rememberedPW);

        if(IDPWRemember.getString("loginIDRemember","").equals("true"))
            IDRemember.setChecked(true);
        if(IDPWRemember.getString("loginPWRemember","").equals("true"))
            PWRemember.setChecked(true);
        guideline=findViewById(R.id.guideline);
       // InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        backbutton=findViewById(R.id.loginBackButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"최고의 개발자 신호욱 화이팅!",Toast.LENGTH_SHORT).show();
            }
        });

        pwText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        loginButton = findViewById(R.id.button_in);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
                SharedPreferences.Editor editor = token.edit();
                //logintoken이라는 key값으로 token을 저장한다.
                editor.putString("loginID", idText.getText().toString());
                editor.apply();
                SharedPreferences IDPWRemember = getSharedPreferences("IDPWRemember", MODE_PRIVATE);
                SharedPreferences.Editor IDPWeditor = IDPWRemember.edit();
                if(IDRemember.isChecked()){
                IDPWeditor.putString("loginID", idText.getText().toString());
                IDPWeditor.putString("loginIDRemember", "true");
                IDPWeditor.putString("loginPWRemember", "");

                }
                else {
                    IDPWeditor.putString("loginID", "");
                    IDPWeditor.putString("loginIDRemember", "");
                    IDPWeditor.putString("loginPWRemember", "");
                }
                if(PWRemember.isChecked()) {
                    IDPWeditor.putString("loginID", idText.getText().toString());
                    IDPWeditor.putString("loginPW", pwText.getText().toString());
                    IDPWeditor.putString("loginIDRemember", "true");
                    IDPWeditor.putString("loginPWRemember", "true");

                }
                else {
                    IDPWeditor.putString("loginPW", "");
                }
                IDPWeditor.apply();
                int id = view.getId();
                Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);

                if(id == R.id.button_in){
                    t.send(new HitBuilders.EventBuilder().setCategory("LoginActivity").setAction("Press Button").setLabel("LoginButton Click").build());

                }
                String idVal = idText.getText().toString().trim();
                String pwVal = pwText.getText().toString().trim();
                try {
                    JSONObject param = new JSONObject();
                    param.put("id", idVal);
                    param.put("pw", pwVal);
                    new WaitHttpRequest(view.getContext()){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject res = new JSONObject(result);
                                Intent in=new Intent(LoginActivity.this, TaskListActivity.class);
                                if (res.get("success").toString() == "true"){
                                    SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = token.edit();
                                    //logintoken이라는 key값으로 token을 저장한다.
                                    editor.putString("loginToken", res.get("token").toString());
                                    editor.commit();
                                    onUserSignIn();
                                    startActivity(in);
                                    finish();
                                }else
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

        upButton = findViewById(R.id.button_up);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent,1);
            }
        });

        final InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);

        //아이디 입력 후 엔터 누르면 패스워드 입력
        idText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    imm.hideSoftInputFromWindow(idText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    pwText.requestFocus();
                    imm.hideSoftInputFromWindow(pwText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    return true;
                }
                return false;
            }
        });

        //아이디 입력 후 엔터 누르면 로그인 버튼으로 포커스
        pwText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    imm.hideSoftInputFromWindow(pwText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    loginButton.requestFocus();
                    return true;
                }
                return false;
            }
        });


    }
    @Override
    protected void onStart(){
            super.onStart();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStart(this);


    }
    @Override
    protected void onStop(){
        super.onStop();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
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
    public void onUserSignIn() {

        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);

        // You only need to set User ID on a tracker once. By setting it on the tracker, the ID will be
        // sent with all subsequent hits.
        SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
        String loginID=token.getString("loginID","");
        t.set("&uid", loginID);

        // This hit will be sent with the User ID value and be visible in User-ID-enabled views (profiles).
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("User Sign In")
                .build()
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        guideline.setGuidelinePercent((float) 0.42);

    }
}