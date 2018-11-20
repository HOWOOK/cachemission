package com.selectstar.hwshin.cachemission.Adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.Dialog.PartSelectDialog;
import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.ItemViewHolder> {
    Context context;
    ArrayList<Integer> idList;
    ArrayList<String> nameList;
    ArrayList<Integer> levelCountList; //유저가 해당 Rank에서 현재 진행한 테스크의 개수
    ArrayList<Integer> levelMaxList;  //해당 TaskRank에서 최대로 할수 있는 테스크의 개수
    PartSelectDialog mDialog;
    PatherActivity mActivity;

    public PartAdapter(PatherActivity mActivity, ArrayList<Integer> idList, ArrayList<String> nameList, ArrayList<Integer> levelCountList, ArrayList<Integer> levelMaxList, PartSelectDialog mDialog) {
        this.context = context;
        this.idList = idList;
        this.nameList = nameList;
        this.levelCountList = levelCountList;
        this.levelMaxList = levelMaxList;
        this.mDialog = mDialog;
        this.mActivity = mActivity;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_partitem, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull PartAdapter.ItemViewHolder holder, int pos) {
        final int position = pos;
        holder.image.setImageDrawable(ContextCompat.getDrawable(mActivity,idList.get(pos)));
        holder.name.setText(nameList.get(pos));
        holder.countText.setText("["+ levelCountList.get(pos)+"/"+ levelMaxList.get(pos)+"]");
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = mActivity.findViewById(R.id.optionText);
                tv.setText(nameList.get(position));
                mDialog.dismiss();

                //이하, 설명서 보여주기 위한 코드
                String taskName="a";
                if(nameList.get(position).equals("변압기"))
                    taskName="transformer";
                if(nameList.get(position).equals("나무"))
                    taskName="tree";
                if(nameList.get(position).equals("전신주"))
                    taskName="pole";
                if(nameList.get(position).equals("프리프로세스"))
                    taskName="preProcess";
                if(nameList.get(position).equals("부품 A"))
                    taskName="partA";
                if(nameList.get(position).equals("부품 B"))
                    taskName="partB";
                if(nameList.get(position).equals("부품 C"))
                    taskName="partC";
                if(nameList.get(position).equals("부품 D"))
                    taskName="partD";
                if(nameList.get(position).equals("부품 E"))
                    taskName="partE";
                if(nameList.get(position).equals("부품 G"))
                    taskName="partG";

                mActivity.forcedShowDescription(taskName);
                SharedPreferences firstTimeExplain = mActivity.getSharedPreferences("firstTimeExplain", MODE_PRIVATE);
                SharedPreferences.Editor editor=firstTimeExplain.edit();
                editor.putString(taskName,"notFirst");
                editor.commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView countText;
        public ItemViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.part_thumb);
            name = itemView.findViewById(R.id.part_text);
            countText = itemView.findViewById(R.id.part_countText);
        }
    }
}