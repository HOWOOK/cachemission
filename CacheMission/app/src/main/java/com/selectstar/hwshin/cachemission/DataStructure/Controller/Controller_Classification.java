package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.selectstar.hwshin.cachemission.R;

public class Controller_Classification extends Controller {

    RecyclerView classificationrv;

    public Controller_Classification(){
        controllerID = R.layout.controller_classification;
    }

    @Override
    public void resetContent(View view, String taskID) {

        classificationrv = parentActivity.findViewById(R.id.classRecyclerView);

    }

    @Override
    public void setLayout(View view, String taskID) {

    }
}
