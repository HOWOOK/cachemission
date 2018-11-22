package com.selectstar.hwshin.cachemission.Adapter;


import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;


public class ClassificationAdapter extends RecyclerView.Adapter<ClassificationAdapter.ItemViewHolder>{
    PatherActivity mActivity;
    ArrayList<Integer> idList;
    ArrayList<Integer> partList;
    ArrayList<Boolean> checkList;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private CheckBox checkBox;

        public ItemViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.classification_image);
            checkBox = itemView.findViewById(R.id.classification_checkbox);
        }
    }

    public ClassificationAdapter(PatherActivity mActivity, ArrayList<Integer> idList, ArrayList<Integer> partList, ArrayList<Boolean> checkList){
        this.mActivity = mActivity;
        this.idList = idList;
        this.partList = partList;
        this.checkList = checkList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskview_classificiation_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClassificationAdapter.ItemViewHolder holder, final int position) {
        holder.image.setImageDrawable(ContextCompat.getDrawable(mActivity, idList.get(position)));
        holder.checkBox.setChecked(false);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    checkList.set(position, false);
                }else{
                    holder.checkBox.setChecked(true);
                    checkList.set(position, true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return idList.size();
    }



}
