package com.selectstar.hwshin.cashmission.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cashmission.Activity.NoticePopUpActivity;
import com.selectstar.hwshin.cashmission.DataStructure.TaskListItem;
import com.selectstar.hwshin.cashmission.DataStructure.TaskView.NoticeItem;
import com.selectstar.hwshin.cashmission.R;

import java.util.ArrayList;

public class NoticetviewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<NoticeItem> mNoticeList;
    private int layout;

    public NoticetviewAdapter(Context context, int layout, ArrayList<NoticeItem> noticeList){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mNoticeList=noticeList;
        this.layout=layout;
    }


    @Override
    public int getCount() {
        return mNoticeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNoticeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(layout, parent, false);
        final NoticeItem noticeItem = mNoticeList.get(position);
        TextView titleView = (TextView) convertView.findViewById(R.id.title2);
        titleView.setText(noticeItem.getTitle());
        TextView dateView = (TextView) convertView.findViewById(R.id.date);
        dateView.setText(noticeItem.getDate());
        final Context context = convertView.getContext();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NoticePopUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title",noticeItem.getTitle());
                intent.putExtra("date",noticeItem.getDate());
                intent.putExtra("content",noticeItem.getContent());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
