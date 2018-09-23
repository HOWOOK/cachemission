package com.selectstar.hwshin.cachemission.Adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.DataStructure.RecyclerItem;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ChoiceImageAdapter extends RecyclerView.Adapter<ChoiceImageAdapter.ItemViewHolder> {
    ArrayList<Integer> checkedPhoto;
    GalleryImageAdapter adapter;
    RecyclerView mView;
    public ArrayList<String> getChoicePhoto()
    {
        ArrayList<String> photos = new ArrayList<>();
        for(int i=0;i<checkedPhoto.size();i++)
        {
            photos.add(adapter.basePath + File.separator + adapter.mImgs[checkedPhoto.get(i)]);
        }
        return photos;
    }
    public ChoiceImageAdapter(RecyclerView mView) {
        checkedPhoto = new ArrayList<>();
        this.mView = mView;
    }
    public void removePhoto(Integer integer)
    {
        int pos = checkedPhoto.indexOf(integer);
        checkedPhoto.remove(integer);
        adapter.checkList[integer] = false;
        GalleryImageAdapter.ItemViewHolder viewHolder = (GalleryImageAdapter.ItemViewHolder)mView.findViewHolderForAdapterPosition(integer);
        if(viewHolder != null)
            viewHolder.setFalse();
        notifyItemRemoved(pos);
        if(checkedPhoto.isEmpty()) {
            ((TextView) adapter.mActivity.findViewById(R.id.picselect)).setTextColor(Color.parseColor("#888888"));
            ((TextView) adapter.mActivity.findViewById(R.id.picselect)).setText("확인");
        }
        else{
            ((TextView) adapter.mActivity.findViewById(R.id.picselect)).setText(String.valueOf(checkedPhoto.size())+" 확인");

        }

    }
    public void addPhoto(Integer integer)
    {
        checkedPhoto.add(integer);
        RecyclerItem item = new RecyclerItem(String.valueOf(integer));
        notifyItemInserted(checkedPhoto.size()-1);
        int color = adapter.mActivity.getResources().getColor(R.color.fontColor1);
        ((TextView)adapter.mActivity.findViewById(R.id.picselect)).setTextColor(color);
        ((TextView) adapter.mActivity.findViewById(R.id.picselect)).setText(String.valueOf(checkedPhoto.size())+" 확인");

    }
    public void setGalleryAdapter(GalleryImageAdapter adapter)
    {
        this.adapter = adapter;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choicepageritem, parent, false);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        view.setLayoutParams(layoutParams);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ChoiceImageAdapter.ItemViewHolder holder, int pos) {
       // holder.delete.setOnClickListener();
        final int position = checkedPhoto.get(pos);
        new Thread() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //Bitmap bm = BitmapFactory.decodeFile(mBasePath + File.separator + mImgs[position], options);
                //options.inSampleSize = calculateInSampleSize(options, 100, 100);
                options.inSampleSize = 8;
                options.inJustDecodeBounds = false;
                final Bitmap bm = BitmapFactory.decodeFile(adapter.basePath + File.separator + adapter.mImgs[position], options);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.image.setImageBitmap(bm);
                            }
                        });
                    }
                }).start();
            }
        }.start();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePhoto(new Integer(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return checkedPhoto.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private Button delete;
        public ItemViewHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.itemdelete);
            image = itemView.findViewById(R.id.imageitem);
        }
    }
}