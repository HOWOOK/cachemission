package com.selectstar.hwshin.cachemission.Adapter.Explain;

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
import com.selectstar.hwshin.cachemission.R;

public class SlideAdapter_ExplainTask_Photo extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter_ExplainTask_Photo(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.explain_task_photo1,
            R.drawable.explain_task_photo2,
            R.drawable.explain_task_photo3,
            R.drawable.explain_task_photo4,
            R.drawable.explain_task_photo5,
            R.drawable.explain_task_photo6,
            R.drawable.explain_task_photo7,
            R.drawable.explain_task_photo8,
            R.drawable.explain_task_photo9,
            R.drawable.explain_task_photo10
    };
    public String[] list_description={
            "1.다음 예시 사진과 같이 2개 이상의 바코드 또는 QR 코드를 한번에 찍어주세요. 사진을 여러 장 찍어 한 번에 제출할 수 있습니다!",
            "2.같은 물건, 다른 배경 반복 OK! 하지만, 촬영 각도와 거리가 바뀌어야합니다.",
            "3.다른 물건, 같은 배경 반복도 OK! 하지만, 촬영 각도와 거리가 바뀌어야합니다.",
            "4.필터없이 촬영하셔야 하며, 사진이 흔들리면 안됩니다.",
            "5.물체와 카메라 사이 거리는 60cm 이하 (팔을 뻗었을 때 닿는 거리) 입니다.",
            "6.사진 촬영을 너무 비스듬히 (45도 이상) 하시면 안됩니다.",
            "7.사진을 미리 찍어두고 갤러리로 업로드 해주셔도 됩니다.",
            "8.단, 인터넷에서 다운받은 사진은 보상이 압수됩니다!\n9.보상은 적립 예정금에 쌓이며, 사진 검사 후 적립 확정됩니다.",
            "(예시사진)",
            "(예시사진)"
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
