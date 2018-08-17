package com.example.hwshin.cachemission.Activity;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.hwshin.cachemission.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button logout = findViewById(R.id.logoutbtn);

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
                        editor.remove("logintoken");
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




    }

}
