package com.selectstar.hwshin.cachemission.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.transition.Transition;
import com.selectstar.hwshin.cachemission.Activity.ImagePopupActivity;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CustomImageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    String mBasePath="";
    Context mContext=null;
    String[] mImgs;
    ArrayList<Integer>selected=new ArrayList<>();
    GridView gridView;
    public CustomImageAdapter(Context context, String basepath, GridView v){
        this.mContext = context;
        this.mBasePath = basepath;
        this.gridView=v;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File file = new File(mBasePath); // 지정 경로의 directory를 File 변수로 받아
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.d(TAG, "failed to create directory");
            }
        }
        mImgs = file.list(); // file.list() method를 통해 directory 내 file 명들을 String[] 에 저장
        Log.d("ffffffff",String.valueOf(file.exists()));


    }

    public ArrayList<Integer> getSelected() {
        return selected;
    }

    @Override
    public int getCount() {
        return mImgs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public String getItemPath(int i){
        return mBasePath+ File.separator + mImgs[i];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.gallery_image, parent, false);
        final ImageView imageView=(ImageView) convertView.findViewById(R.id.imagecontent);
        final ImageView selectbutton =(ImageView) convertView.findViewById(R.id.chooseoption);
        Log.d("delta",mBasePath + File.separator + mImgs[position]);

        int gridviewH = gridView.getWidth() / 3;
/*
        Glide.with(mContext)
                .load(path)
                .asBitmap()
                .into(imageView);
        */

        Bitmap bm = BitmapFactory.decodeFile(mBasePath + File.separator + mImgs[position]);
        final Bitmap mThumbnail = ThumbnailUtils.extractThumbnail(bm, 300, 300);
        imageView.setPadding(8, 8, 8, 8);

        //imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        Glide.with(mContext)
                .asBitmap()
                .load(mBasePath + File.separator + mImgs[position])
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                        //할일

                    }
                });


imageView.setLayoutParams(new ConstraintLayout.LayoutParams(gridView.getWidth() / 3,gridView.getWidth() / 3));
        // imageView.setImageBitmap(mThumbnail);
        // final boolean[] itemselected = {false};
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(mContext, ImagePopupActivity.class);
                in.putExtra("image",mBasePath + File.separator + mImgs[position]);
                mContext.startActivity(in);
            }
        });
       selectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected.contains(position)){
                    selected.remove(selected.indexOf(position));
                   selectbutton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setting));

                    /*
                    for(int i=0;i<selected.size();i++){
                        if(selected.get(i)==position){


                        }

                    }*/
                }
                else{
                    selected.add(position);

                    selectbutton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cashmissionicon));

                }
            }
        });

        return convertView;
    }

}
