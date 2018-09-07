package com.selectstar.hwshin.cashmission.Activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.selectstar.hwshin.cashmission.Adapter.NoticeviewAdapter;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.DataStructure.NoticeItem;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticePopUpActivity extends AppCompatActivity {

    Context context = this;
    String title;
    String date;
    String content;
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticepopup);
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String content = getIntent().getStringExtra("content");
        TextView titleView = findViewById(R.id.popuptitle);
        titleView.setText(title);
        TextView contentView = findViewById(R.id.popupcontent);
        contentView.setText(content);
        backButton = findViewById(R.id.popupback);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}