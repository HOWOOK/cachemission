package com.selectstar.hwshin.cashmission.Adapter.Explain;

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
import com.selectstar.hwshin.cashmission.R;

public class SlideAdapter_ExplainTask_Record_ExamType1 extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter_ExplainTask_Record_ExamType1(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.explain_task_dialect7
    };
    public String[] list_description={
            "재생 버튼을 눌러 음성을 들어주세요.\n주어진 문장을 정확히 읽었다면 O표를\n아니라면 X를 누르고 제출을 눌러주세요."
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

        Glide.with(context).load(list_images[position]).into(img1);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });
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
