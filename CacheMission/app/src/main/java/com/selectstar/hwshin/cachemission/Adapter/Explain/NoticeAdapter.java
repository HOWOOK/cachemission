package com.selectstar.hwshin.cachemission.Adapter.Explain;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.R;

import static android.content.Context.MODE_PRIVATE;

public class NoticeAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public NoticeAdapter(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.explain_main1,
            R.drawable.explain_main2,
            R.drawable.explain_main3,
            R.drawable.explain_main4,
    };
    public String[] list_description={

            "미션 성공률에 따라 미션 참여 가능 횟수가 정해집니다.\n" +
                    "\n" +
                    "미션 검사 후 성공률이 높다면\n" +
                    "참여 가능 횟수가 증가합니다.\n" +
                    "\n" +
                    "성공률이 25% 미만이면\n" +
                    "더이상 참여할 수 없으니 주의하세요!"

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
        View view = inflater.inflate(R.layout.notice_adapter,container,false);

        ImageView img1 = (ImageView)view.findViewById(R.id.explainTaskExitReal);
        final CheckBox Check=view.findViewById(R.id.noticeCheckBox);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Check.isChecked()){
                    SharedPreferences noticeFlag=context.getSharedPreferences("noticeFlag",MODE_PRIVATE);
                    SharedPreferences.Editor editor=noticeFlag.edit();
                    editor.putBoolean("noticeActivation",false);
                    editor.commit();
                }
                ((Activity)context).finish();
            }
        });


        container.addView(view);
        return view;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
