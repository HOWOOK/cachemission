package com.selectstar.hwshin.cachemission.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.Adapter.PartAdapter;
import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;


public class PartSelectDialog extends Dialog{

    private Context context;

    private ImageView cancelbtn;
    private ImageView partPole;
    private ImageView partTree;
    private ImageView partTransformer;
    private ConstraintLayout dialogCL;

    public PartSelectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public PartSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_partselect);
        getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
        dialogCL = findViewById(R.id.dialogCL);
        cancelbtn = findViewById(R.id.cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PartSelectDialog.this.dismiss();
            }
        });
        RecyclerView partRecycler = findViewById(R.id.partRecycler);
        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        idList.add(R.drawable.part_pole);
        nameList.add("전봇대");
        idList.add(R.drawable.part_a);
        nameList.add("부품 A");
        idList.add(R.drawable.part_b);
        nameList.add("부품 B");
        idList.add(R.drawable.part_c);
        nameList.add("부품 C");
        idList.add(R.drawable.part_d);
        nameList.add("부품 D");
        idList.add(R.drawable.part_tree);
        nameList.add("나무");
        idList.add(R.drawable.part_transformer);
        nameList.add("변압기");
        idList.add(R.drawable.part_e);
        nameList.add("부품 E");
        idList.add(R.drawable.part_g);
        nameList.add("부품 G");
        idList.add(R.drawable.part_pre);
        nameList.add("전봇대 부품들");
        PartAdapter mAdapter = new PartAdapter((PatherActivity)context, idList,nameList,this);
        partRecycler.setLayoutManager(new GridLayoutManager(context,2));
        partRecycler.setAdapter(mAdapter);




    }

}


