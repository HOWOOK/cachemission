package com.selectstar.hwshin.cashmission.Adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;

import com.selectstar.hwshin.cashmission.R;

public class CircularPagerAdapter extends PagerAdapter {

    private ViewPager mPager = null;

    private int[] mPagerIDsArray;

    private int mCount;

    private Context mContext;



    public CircularPagerAdapter(final ViewPager pager, int[] pageArray) {

        super();



        mPager = pager;

// 순환하는 형태의 ViewPager를 구현하기 위해 View들의 양 끝에 임시 View 추가

        int actualNoOfIDs = pageArray.length;

        mCount = actualNoOfIDs ;

        mPagerIDsArray = new int[mCount];



        for (int i = 0; i < actualNoOfIDs; i++) {

            mPagerIDsArray[i ] = pageArray[i];

        }

      //  mPagerIDsArray[0] = pageArray[actualNoOfIDs - 1];

       // mPagerIDsArray[mCount - 1] = pageArray[0];



// Page를 순환하여 Navigate할 수 있도록 구현 (일종의 눈속임)

        mPager.setOnPageChangeListener(new OnPageChangeListener() {
/*
            @Override

            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_IDLE) {              // 좌우 Flicking이 끝나는 시점

                    int pageCount = getCount();

                    if (mPager.getCurrentItem() == 0) {               // 왼쪽끝 임시 View로 이동한 경우

// 실제 View들 중 마지막 View로 Animation없이 이동

                        mPager.setCurrentItem(pageCount - 2, false);

                    } else if (mPager.getCurrentItem() == mCount - 1) { // 오른쪽끝 임시 View로 이동한 경우

// 실제 View들 중 첫번째 View로 Animation없이 이동

                        mPager.setCurrentItem(1, false);

                    }

                }

            }
            */



            @Override

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }



            @Override

            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

    }



    public int getCount() {

        return mCount;

    }




    public Object instantiateItem(View pager, int position) {                // position 별로 pager에 view들을 등록

        mContext = pager.getContext();

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.pagerimage1, null);                // ViewPager에 inflate 시킬 layout 연결

        ((ViewPager) pager).addView(view, 0);

        return view;

    }



    @Override

    public void destroyItem(View view, int position, Object obj) {

        ((ViewPager) view).removeView((View) obj);

    }



    @Override

    public boolean isViewFromObject(View view, Object obj) {

        return view == (View) obj;

    }



    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}

    @Override public Parcelable saveState() { return null; }

    @Override public void startUpdate(View arg0) {}

    @Override public void finishUpdate(View arg0) {}

}
