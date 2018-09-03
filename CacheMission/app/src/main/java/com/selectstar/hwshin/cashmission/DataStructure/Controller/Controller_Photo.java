package com.selectstar.hwshin.cashmission.DataStructure.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;

import com.selectstar.hwshin.cashmission.R;

import java.io.File;
import java.io.IOException;

public class Controller_Photo extends Controller {


    public Controller_Photo() {
        controllerID = R.layout.controller_photo;
    }
    @Override
    public void setLayout(String id, View view, Context c, String tasktype, Intent in, String buttons) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        ConstraintLayout templayout = (ConstraintLayout) view;
        Button phototake=templayout.findViewById(R.id.phototake);
        phototake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takepictureintent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                parentActivity.startActivityForResult(takepictureintent,0);
            }
        });
    }
}
