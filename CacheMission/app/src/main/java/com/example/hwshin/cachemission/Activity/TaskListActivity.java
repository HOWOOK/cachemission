package com.example.hwshin.cachemission.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hwshin.cachemission.Adapter.ListviewAdapter;
import com.example.hwshin.cachemission.DataStructure.TaskListItem;
import com.example.hwshin.cachemission.DataStructure.UIHashmap;
import com.example.hwshin.cachemission.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskListActivity extends AppCompatActivity {

    private ListView lv_main;
    final static ArrayList<TaskListItem> mTaskList = new ArrayList<TaskListItem>();
    static ListviewAdapter adapter;
    private UIHashmap uiHashmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        uiHashmap = new UIHashmap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTaskList.clear();

        lv_main = (ListView) findViewById(R.id.taskList);
        TextView tv = findViewById(R.id.possibleTask);
        tv.setText("진행 가능한 작업");

        adapter = new ListviewAdapter(this, R.layout.task_lv, TaskListActivity.mTaskList);
        lv_main.setAdapter(adapter);

        //mTaskList에 현재 진행가능한 테스트들을 넣어줘야합니다. 현재는 테스트로 넣어봤습니다.
        mTaskList.add(new TaskListItem("labeling", "테스트입니다."));

        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //각 리스트를 클릭하여 TaskActivity로 넘어가게 되는데 거기에 intent될 데이터들을 구분해서 넘겨줘야합니다.
                //지금은 모든 리스트들이 OCR task를 할수있도록 taskview는 image를 controller는 edittext
                Intent intent_lv = new Intent(TaskListActivity.this, TaskActivity.class);

                intent_lv.putExtra("taskview", uiHashmap.taskViewHashMap.get(0));
                intent_lv.putExtra("controller", uiHashmap.controllerHashMap.get(1));
                intent_lv.putExtra("parameters", uiHashmap.taskHashMap.get(0));





                TaskListActivity.this.startActivity(intent_lv);

            }
        });
    }
}
