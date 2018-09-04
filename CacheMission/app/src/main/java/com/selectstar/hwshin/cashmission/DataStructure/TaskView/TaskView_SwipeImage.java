package com.selectstar.hwshin.cashmission.DataStructure.TaskView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.selectstar.hwshin.cashmission.Adapter.CircularPagerAdapter;
import com.selectstar.hwshin.cashmission.R;

public class TaskView_SwipeImage extends TaskView {
    private ViewPager mPager = null;

    private CircularPagerAdapter mPagerAdapter = null;

    public TaskView_SwipeImage() {
        taskViewID = R.layout.taskview_swipeimage;

    }

    @Override
    public void setContent(String id, String contentURI, Context context, String taskType, int examType, View... view) {
        ViewPager mPager = (ViewPager)view[0];
        int[] pages=new int[3];
        mPagerAdapter = new CircularPagerAdapter(mPager, pages, contentURI);

        mPager.setAdapter(mPagerAdapter);                     // ViewPager Adapter 설정

        mPager.setCurrentItem(3);



    }
}
