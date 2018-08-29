package com.selectstar.hwshin.cashmission.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.selectstar.hwshin.cashmission.DataStructure.RecyclerItem;
import com.selectstar.hwshin.cashmission.R;

import java.util.ArrayList;

public class NumberRecyclerAdapter extends RecyclerView.Adapter<NumberRecyclerAdapter.ItemViewHolder>
{
    ArrayList<RecyclerItem> mItems;

    public NumberRecyclerAdapter(ArrayList<RecyclerItem> items){
        mItems = items;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.controller_numbers_item,parent,false);
        return new ItemViewHolder(view);
    }





    @Override
    public void onBindViewHolder(@NonNull NumberRecyclerAdapter.ItemViewHolder holder, int position) {
        holder.mNameTv.setText(mItems.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView mNameTv;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mNameTv = (TextView) itemView.findViewById(R.id.itemNameTv);
        }
    }

}


