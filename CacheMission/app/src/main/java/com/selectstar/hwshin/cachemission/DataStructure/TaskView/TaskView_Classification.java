package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.Adapter.ClassificationAdapter;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_Classification extends TaskView {

    public ClassificationAdapter mAdapter;
    private PhotoView photoView;
    private RecyclerView classificationrv;
    private String[] arrayExpand, arrayURI, arrayClass;
    public ArrayList<Integer> idList;
    public ArrayList<Boolean> checkList;

    public TaskView_Classification(){
        taskViewID = R.layout.taskview_classification;
    }

    @Override
    public void setContent(String content) {
        photoView = parentActivity.findViewById(R.id.srcview);
        classificationrv = parentActivity.findViewById(R.id.classRecyclerView);

        System.out.println("콘텐츠 : " + content);
        arrayExpand = content.split(">");
        arrayURI = arrayExpand[1].split("\\*");
        arrayClass = arrayURI[1].split(",");

        idList = new ArrayList<>();
        checkList = new ArrayList<>();
        mAdapter = new ClassificationAdapter(parentActivity, idList, checkList);

        classificationrv.setLayoutManager(new GridLayoutManager(parentActivity, 4));
        classificationrv.setAdapter(mAdapter);

        //Adapter에 아이템 넣어주기
        for(int i = 0; i < arrayClass.length; i++){
            if(arrayClass[i].equals("1"))
                idList.add(R.drawable.part_g);
            if(arrayClass[i].equals("4"))
                idList.add(R.drawable.part_tree);
            if(arrayClass[i].equals("5"))
                idList.add(R.drawable.part_transformer);
            if(arrayClass[i].equals("6"))
                idList.add(R.drawable.part_a);
            if(arrayClass[i].equals("7"))
                idList.add(R.drawable.part_b);
            if(arrayClass[i].equals("8"))
                idList.add(R.drawable.part_c);
            if(arrayClass[i].equals("9"))
                idList.add(R.drawable.part_d);
            if(arrayClass[i].equals("10"))
                idList.add(R.drawable.part_e);
        }

        for(int i = 0; i < arrayClass.length; i++){
            checkList.add(false);
        }

        //source 띄우기
        Glide.with(parentActivity)
                .asBitmap()
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+arrayURI[0]))
                .into(photoView);
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
