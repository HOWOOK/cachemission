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

public class SlideAdapter_ExplainTask_DirectRecord extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter_ExplainTask_DirectRecord(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.explain_task_dialect1,
            R.drawable.explain_task_photo8,
            R.drawable.explain_task_photo8
    };
    public String[] list_description={
            "1. 구사 가능한 사투리를 선택해주세요.\n(다른 사투리 미션에서 설정했다면\n자동으로 설정됩니다.)",
            "2. 주어진 상황을 보고 그에 알맞는 표현을 해당 사투리로 자연스럽게 녹음해주세요.",
            "3. 실수해도 언제든 다시 녹음할 수 있답니다.\n4. 보상은 적립 예정금에 쌓이며, 녹음 검사 후 적립 확정됩니다."

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
        constraintSet.connect(R.id.explainTaskText, ConstraintSet.TOP, R.id.taskExplainContLayout, ConstraintSet.TOP);
        constraintSet.connect(R.id.explainTaskText, ConstraintSet.START, R.id.taskExplainContLayout,ConstraintSet.START);
        constraintSet.connect(R.id.explainTaskText, ConstraintSet.END, R.id.taskExplainContLayout,ConstraintSet.END);
        constraintSet.connect(R.id.explainTaskText, ConstraintSet.BOTTOM, R.id.taskExplainContLayout, ConstraintSet.BOTTOM);

        if(position==0)
            Glide.with(context).load(list_images[position]).into(img1);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });

        if(position==1 || position==2)
            constraintSet.applyTo(cl);
        txt1.setText(list_description[position]);

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
