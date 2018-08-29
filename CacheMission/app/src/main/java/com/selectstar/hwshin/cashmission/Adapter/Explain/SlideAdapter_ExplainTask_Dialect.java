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
import com.example.hwshin.cachemission.R;

public class SlideAdapter_ExplainTask_Dialect extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter_ExplainTask_Dialect(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.explain_task_dialect1,
            R.drawable.explain_task_dialect2,
            R.drawable.explain_task_dialect3
    };
    public String[] list_description={
            "1.구사 가능한 사투리를 선택해주세요.\n(다른 사투리 미션에서 설정했다면\n자동으로 설정됩니다.)",
            "2.주어진 상황 설명을 보고\n그에 맞는 문장을 사투리로 적고\n제출을 눌러주세요!",
            "3.다른 사람이 같은 문장을\n먼저 제출했거나, 표현이 어색할 경우\n보상을 못 드릴 수도 있습니다ㅠㅠ"
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
