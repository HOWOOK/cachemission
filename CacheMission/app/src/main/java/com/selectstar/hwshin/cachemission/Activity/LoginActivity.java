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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    Button loginButton;
    Button upButton;
    Button findid,findpw;
    Guideline guideline;
    ImageView backbutton;
    final int P_RECORD_AUDIO=77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  ){

            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO,WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},
                    P_RECORD_AUDIO);

        }
        setContentView(R.layout.activity_login);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("LoginActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        viewGuideline = this.findViewById(R.id.guideline);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int getDeviceHeight_Pixel = displayMetrics.heightPixels;
        viewGuideline.setGuidelineBegin(getDeviceHeight_Pixel / 2);

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


        idText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guideline.setGuidelinePercent(0);
            }
        });
        pwText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // guideline.setGuidelinePercent(0);
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

                int id = view.getId();
                Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);

                if(id == R.id.button_in){
                    t.send(new HitBuilders.EventBuilder().setCategory("LoginActivity").setAction("Press Button").setLabel("LoginButton Click").build());

                }

                String idVal = idText.getText().toString();
                String pwVal = pwText.getText().toString();
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
                                System.out.println("로그인 리스폰스 : "+res);
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
                                }
                                else
                                    getDialog("로그인에 실패하였습니다.","아이디와 비밀번호를 확인해주세요.");
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }.execute(getString(R.string.mainurl)+"/testing/login",param);
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
//
//        findid=findViewById(R.id.button_findid);
//        findpw=findViewById(R.id.button_findpw);
//        findid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"알파테스트에서 구현되지 않은 사항입니다. 앱 추천인에게 문의하세요!",Toast.LENGTH_SHORT).show();
//            }
//        });
//        findpw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"알파테스트에서 구현되지 않은 사항입니다. 앱 추천인에게 문의하세요!",Toast.LENGTH_SHORT).show();
//            }
//        });


    }
    @Override
    protected void onStart(){
            super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);


    }
    @Override
    protected void onStop(){
        super.onStop();
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