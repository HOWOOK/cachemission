package com.selectstar.hwshin.cachemission.Activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {
Context mContext=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr")) {
            Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
            t.setScreenName("SettingActivity");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
        setContentView(R.layout.activity_setting);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView logout = findViewById(R.id.logoutbtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder sure = new AlertDialog.Builder(SettingActivity.this);
                sure.setTitle("로그아웃");
                sure.setMessage("정말 로그아웃 하시겠습니까?");


                sure.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences test = getSharedPreferences("token", MODE_PRIVATE);
                        SharedPreferences.Editor editor = test.edit();
                        editor.remove("loginToken");
                        editor.commit();


                        SharedPreferences todayMoney=getSharedPreferences("todayMoney",MODE_PRIVATE);
                        SharedPreferences.Editor moneyEditor=todayMoney.edit();
                        moneyEditor.remove("todayDate");
                        moneyEditor.remove("todayStartMoney");
                        moneyEditor.commit();
                        finish();
                    }
                });

                sure.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                sure.show();
            }
        });

        TextView suggestion = findViewById(R.id.suggestionbtn);
        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_suggestion = new Intent(SettingActivity.this, SuggestionActivity.class);
                startActivity(intent_suggestion);
            }
        });

        //테스트 페이지로 들어가기 위한 어둠의 경로
        View testgogo = findViewById(R.id.testgogo);
        testgogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_testgogo = new Intent(SettingActivity.this, TestActivity.class);
                startActivity(intent_testgogo);
            }
        });

        Switch pushOnOff = findViewById(R.id.pushonoffbtn);
        final SharedPreferences push = getSharedPreferences("push", MODE_PRIVATE);
        String OnOff = push.getString("push", "true");
        pushOnOff.setChecked(OnOff.equals("true"));
        pushOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences.Editor editor = push.edit();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("RetentionPush");
                    editor.putString("push","true");
                    editor.commit();
                    Toast.makeText(SettingActivity.this, "푸시알람을 허용합니다.", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("RetentionPush");
                    editor.putString("push","false");
                    editor.commit();
                    Toast.makeText(SettingActivity.this, "푸시알람을 허용을 해제합니다.", Toast.LENGTH_SHORT).show();
                }

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


}
