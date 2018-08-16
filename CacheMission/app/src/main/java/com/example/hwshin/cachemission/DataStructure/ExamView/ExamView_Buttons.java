package com.example.hwshin.cachemission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.hwshin.cachemission.Adapter.ButtonListAdapter;
import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.example.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ExamView_Buttons extends ExamView {
    @Override
    public void setLayout(String id, View view, Context c, Intent in, String buttons, String answer) {
        ConstraintLayout templayout = (ConstraintLayout) view;
        ListView lv = templayout.findViewById(R.id.buttons_list);
        ArrayList<String> textarray=new ArrayList<String>();
        try {

            JSONArray res = new JSONArray(buttons);
            for (int i = 0; i < res.length(); i++) {

                String temp = res.get(i).toString();
                Log.d("dataget", temp.toString());
                textarray.add(temp);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        ButtonListAdapter adapter= new ButtonListAdapter(c, R.layout.controller_buttons_item, textarray);
        lv.setAdapter(adapter);
    }
}
