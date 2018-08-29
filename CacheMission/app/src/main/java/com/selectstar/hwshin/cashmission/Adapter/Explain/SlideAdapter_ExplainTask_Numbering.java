package com.selectstar.hwshin.cashmission.Adapter.Explain;

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
import com.selectstar.hwshin.cashmission.R;

public class SlideAdapter_ExplainTask_Numbering extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter_ExplainTask_Numbering(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.explain_task_numbering,
            R.drawable.explain_task_numbering,
    };
    public String[] list_description={
            "본 미션은 오직 연구를 위한 데이터 베이스\n구축이 목적이며, 미션 사진들은" +
                    "\n미국 테네시 대학과 홍콩 중문대학에서\n연구용으로 공개한 사진입니다." +
                    "\n\n미션 결과는 참여자를 알아볼 수 없는 형태로\n취합하니 솔직한 미션 참여 부탁드립니다!" +
                    "\n\n불성실한 응답은 향후 보상 지급에\n제한이 있을 수 있습니다.",
            "미션 사진을 보고, 본인이 생각하는\n외모 점수를 1부터 10에서 고르고\n제출을 눌러주세요!",
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

        ImageView img1 = (ImageView)view.findViewById(R.id.explainTaskImage);
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

        if(position==1)
            Glide.with(context).load(list_images[position]).into(img1);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });

        if(position==0)
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
