package com.selectstar.hwshin.cachemission.Adapter.Explain;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.R;

public class SlideAdapter_ExplainTask_Classification extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter_ExplainTask_Classification(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.so_stressful,
    };
    public String[] list_description={
            "",
    };


    @Override
    public int getCount() {
        return list_description.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(ConstraintLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.explain_task,container,false);

        ImageView img1 = (ImageView)view.findViewById(R.id.newExplainTaskImage);
        ImageView img2 = (ImageView)view.findViewById(R.id.explainTaskExit);
        TextView txt1 = (TextView)view.findViewById(R.id.explainTaskText);
        TextView txt2 = (TextView)view.findViewById(R.id.explainTaskPage);

        ConstraintLayout cl = (ConstraintLayout) view.findViewById(R.id.taskExplainContLayout);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(cl);
        constraintSet.connect(R.id.newExplainTaskImage, ConstraintSet.TOP, R.id.taskExplainContLayout, ConstraintSet.TOP);
        constraintSet.connect(R.id.newExplainTaskImage, ConstraintSet.START, R.id.taskExplainContLayout,ConstraintSet.START);
        constraintSet.connect(R.id.newExplainTaskImage, ConstraintSet.END, R.id.taskExplainContLayout,ConstraintSet.END);
        constraintSet.connect(R.id.newExplainTaskImage, ConstraintSet.BOTTOM, R.id.taskExplainContLayout, ConstraintSet.BOTTOM);
        constraintSet.applyTo(cl);

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });

        Glide.with(context).load(list_images[position]).into(img1);

        int position2 = position+1;
        txt2.setText(position2+"/"+getCount());

        container.addView(view);
        return view;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
