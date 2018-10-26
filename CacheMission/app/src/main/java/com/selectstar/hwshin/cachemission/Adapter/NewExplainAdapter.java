package com.selectstar.hwshin.cachemission.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.Activity.NewExplainActivity;
import com.selectstar.hwshin.cachemission.DataStructure.ExplainHashMap;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NewExplainAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Integer> mImageList;
     HashMap<String,Bitmap> bitmaps;
    String urlList;
    JSONArray urlArray;

    public NewExplainAdapter(final Context context, String taskType, String urlList ){
        this.context=context;
        Log.d("tasktypehere",taskType);
        ExplainHashMap explainHashMap =new ExplainHashMap();
        this.urlList=urlList;
        bitmaps = new HashMap<>();
        try {
            urlArray=new JSONArray(urlList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for (int i = 0; i < urlArray.length(); i++) {

                final String content = urlArray.get(i).toString();
                new Thread() {
                    public void run() {

                        Bitmap bitmap = getBitmap(context.getString(R.string.mainurl) + "/media/" + content);
                        bitmaps.put(content, bitmap);


                    }
                }.start();


            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        //this.mImageList= explainHashMap.taskTypeLinkImages.get(taskType);
    }

    @Override
    public int getCount() {
        return urlArray.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(ConstraintLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.new_explain_task,container,false);

        ImageView img1 = (ImageView)view.findViewById(R.id.newExplainTaskImage);
        ImageView img2 = (ImageView)view.findViewById(R.id.newExplainTaskExit);
        TextView txt1 = (TextView)view.findViewById(R.id.newExplainCount);
       // TextView txt2 = (TextView)view.findViewById(R.id.explainTaskPage);

        try {
            if(bitmaps.containsKey( urlArray.get(position).toString()))
                img1.setImageBitmap(bitmaps.get(urlArray.get(position).toString()));

            else if(urlArray.length()!=0)
            Glide.with(context).load(context.getString(R.string.mainurl)+"/media/"+(String)urlArray.get(position)).into(img1);
            else
                Glide.with(context).load(R.drawable.cashmissionlogo1).into(img1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==urlArray.length()-1){
                    ((Activity)context).finish();
                }else {
                    ((NewExplainActivity) context).viewpager.setCurrentItem(position + 1,false);
                }
            }
        });

        int position2 = position+1;
        txt1.setText(position2+"/"+getCount());

        container.addView(view);
        return view;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }
    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;

        try{
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(connection!=null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }
}
