package com.selectstar.hwshin.cachemission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ListView;

import com.selectstar.hwshin.cachemission.Adapter.ButtonListAdapter;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ExamView_Buttons extends ExamView {
    public ExamView_Buttons() {
        ExamViewID = R.layout.examview_buttons;
    }

    @Override
    public void setContent(String content, String taskID) {

    }

    @Override
    public void setLayout(String id, View view, Context c, Intent in, String buttons, String answer) {
        ConstraintLayout templayout = (ConstraintLayout) view;
        ListView lv = templayout.findViewById(R.id.buttons_list_exam);
        ArrayList<String> textarray=new ArrayList<String>();

        try {

            JSONArray res = new JSONArray(buttons);
            for (int i = 0; i < res.length(); i++) {

                String temp = res.get(i).toString();
                textarray.add(temp);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        ButtonListAdapter adapter= new ButtonListAdapter(c, R.layout.examview_buttons_item, textarray,answer);
        lv.setAdapter(adapter);
    }
}
