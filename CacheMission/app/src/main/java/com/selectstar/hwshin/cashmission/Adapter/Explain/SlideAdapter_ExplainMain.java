package com.selectstar.hwshin.cashmission.Adapter.Explain;

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

public class SlideAdapter_ExplainMain extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter_ExplainMain(Context context){
        this.context=context;
    }


    public int[] list_images={
            R.drawable.explain_main1,
            R.drawable.explain_main2,
            R.drawable.explain_main3,
            R.drawable.explain_main4,
    };
    public String[] list_description={
            "\"캐시미션\"은\n음성 녹음 사진 속 사람찾기 등\n간단하고 다양한 미션들을 수행하고\n보상을 받는 앱입니다!",
            "참여한 미션은 바로 확인에 들어갑니다.\n미션을 정확히 수행하셔야\n보상을 얻으실 수 있습니다!",
            "미션에 많이 참여할 수록 랭크가 올라갑니다.\n높은 랭크를 받아\n더욱 많은 해택을 받아가세요!",
            "그럼 지금부터 캐시 미션과 함께\n지저분한 광고 없이\n틈틈이 용돈 많이 벌어가세요 >_<"
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
        View view = inflater.inflate(R.layout.explain_main,container,false);

        ImageView img1 = (ImageView)view.findViewById(R.id.explainImage);
        TextView txt1 = (TextView)view.findViewById(R.id.explaintext);

        Glide.with(context).load(list_images[position]).into(img1);
        txt1.setText(list_description[position]);

        container.addView(view);
        return view;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
