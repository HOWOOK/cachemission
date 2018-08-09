package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.hwshin.cachemission.Activity.TaskListActivity;
import com.example.hwshin.cachemission.Adapter.ButtonListAdapter;
import com.example.hwshin.cachemission.Adapter.ListviewAdapter;
import com.example.hwshin.cachemission.R;

import java.util.ArrayList;

public class Controller_Buttons extends Controller {

    public Controller_Buttons() {
        controllerID = R.layout.controller_buttons;
    }

    @Override
    public String getAnswer() {
        return null;
    }

    @Override
    public void setLayout(View view, Context c) {
        ArrayList<String> textarray=new ArrayList<String>();
        textarray.add("에바");
        textarray.add("참치");
        textarray.add("꽁치");
        ConstraintLayout templayout = (ConstraintLayout) view;

        ListView lv = templayout.findViewById(R.id.buttons_list);
        ButtonListAdapter adapter;

        adapter = new ButtonListAdapter(c, R.layout.controller_buttons_item, textarray);
        lv.setAdapter(adapter);
    }
}
