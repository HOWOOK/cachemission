package com.selectstar.hwshin.cachemission.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.selectstar.hwshin.cachemission.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        TextView exchange = findViewById(R.id.exchangebtn);
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_exchange = new Intent(SettingActivity.this, ExchangeActivity.class);
                startActivity(intent_exchange);
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

        Switch pushOnOff = findViewById(R.id.pushonoffbtn);
        final SharedPreferences push = getSharedPreferences("push", MODE_PRIVATE);
        String OnOff = push.getString("push", null);
        boolean isOn = false;
        if(OnOff!=null) {
            if (OnOff.equals("true"))
                isOn = true;
            else
                isOn = false;
        }
        pushOnOff.setChecked(isOn);
        pushOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences.Editor editor = push.edit();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("TestPush");
                    editor.putString("push","true");
                    editor.commit();
                    Toast.makeText(SettingActivity.this, "푸시알람을 허용합니다.", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("TestPush");
                    editor.putString("push","false");
                    editor.commit();
                    Toast.makeText(SettingActivity.this, "푸시알람을 허용을 해제합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
