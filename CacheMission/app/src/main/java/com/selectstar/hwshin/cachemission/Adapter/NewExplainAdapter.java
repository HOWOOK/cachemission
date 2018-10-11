package com.selectstar.hwshin.cachemission.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.Activity.NewExplainActivity;
import com.selectstar.hwshin.cachemission.DataStructure.ExplainHashMap;
import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;

public class NewExplainAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Integer> mImageList;

    public NewExplainAdapter(Context context,String taskType){
        this.context=context;
        ExplainHashMap explainHashMap =new ExplainHashMap();

        this.mImageList= explainHashMap.taskTypeLinkImages.get(taskType);
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(ConstraintLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.new_explain_task,container,false);

        ImageView img1 = (ImageView)view.findViewById(R.id.newExplainTaskImage);
        ImageView img2 = (ImageView)view.findViewById(R.id.newExplainTaskExit);
        TextView txt1 = (TextView)view.findViewById(R.id.newExplainCount);
       // TextView txt2 = (TextView)view.findViewById(R.id.explainTaskPage);

        Glide.with(context).load(mImageList.get(position)).into(img1);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==mImageList.size()-1){
                    ((Activity)context).finish();
                }else {
                    ((NewExplainActivity) context).viewpager.setCurrentItem(position + 1,false);
                }
            }
        });

        int position2 = position+1;
        txt1.setText(position2+"/"+getCount());

        container.addView(view);
        return view;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
