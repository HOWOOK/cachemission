package com.selectstar.hwshin.cachemission.Adapter;


import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;


public class ClassificationAdapter extends RecyclerView.Adapter<ClassificationAdapter.ItemViewHolder>{
    PatherActivity mActivity;
    ArrayList<Integer> idList;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        public ItemViewHolder(View itemView){
            super(itemView);
        }
    }

    public ClassificationAdapter(PatherActivity mActivity){
        this.mActivity = mActivity;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassificationAdapter.ItemViewHolder holder, int position) {
        holder.image.setImageDrawable(ContextCompat.getDrawable(mActivity, idList.get(position)));

    }

    @Override
    public int getItemCount() {
        return 0;
    }



}
