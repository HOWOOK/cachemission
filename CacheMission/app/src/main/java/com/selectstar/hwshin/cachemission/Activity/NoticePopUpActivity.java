package com.selectstar.hwshin.cachemission.Activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.R;

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
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}