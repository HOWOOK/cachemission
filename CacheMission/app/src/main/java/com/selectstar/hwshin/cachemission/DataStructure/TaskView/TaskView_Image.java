package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskView_Image extends TaskView {
    private ImageView myView;
    private ImageView[] myViews;
    private HashMap<String,Bitmap> bitmaps;
    public TaskView_Image()
    {
        taskViewID = R.layout.taskview_image;
        bitmaps = new HashMap<>();
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

    @Override
    public boolean isEmpty() {
        return bitmaps.isEmpty();
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list)
    {
        try {
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.get(i);
                final String content = jsonObject.get("content").toString();
                new Thread() {
                    public void run() {
                        Bitmap bitmap = getBitmap(parentActivity.getString(R.string.mainurl) + "/media/" + content);
                        bitmaps.put(content, bitmap);
                    }
                }.start();
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void setContent(String content) {

        myView = parentActivity.findViewById(R.id.srcview);
        myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if(bitmaps.containsKey(content))
            myView.setImageBitmap(bitmaps.get(content));
        else
        Glide.with(parentActivity)
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ content))
                .thumbnail(0.1f)
                .into(myView);
//    }
    }
}
