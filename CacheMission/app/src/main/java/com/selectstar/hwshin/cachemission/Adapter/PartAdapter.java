package com.selectstar.hwshin.cachemission.Adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.selectstar.hwshin.cachemission.Activity.GalleryActivity;
import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.Dialog.PartSelectDialog;
import com.selectstar.hwshin.cachemission.DataStructure.RecyclerItem;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.ItemViewHolder> {
    Context context;
    ArrayList<Integer> idList;
    ArrayList<String> nameList;
    PartSelectDialog mDialog;
    PatherActivity mActivity;
    public PartAdapter(PatherActivity mActivity, ArrayList<Integer> idList, ArrayList<String> nameList, PartSelectDialog mDialog) {
        this.context = context;
        this.idList = idList;
        this.nameList = nameList;
        this.mDialog = mDialog;
        this.mActivity = mActivity;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull PartAdapter.ItemViewHolder holder, int pos) {
        final int position = pos;
        holder.image.setImageDrawable(ContextCompat.getDrawable(mActivity,idList.get(pos)));
        holder.name.setText(nameList.get(pos));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = mActivity.findViewById(R.id.partText);
                tv.setText(nameList.get(position));
                mDialog.dismiss();
                System.out.println("bbbbb");
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
                System.out.println("aaaaaa");
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
        public ItemViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.part_thumb);
            name = itemView.findViewById(R.id.part_text);
        }
    }
}