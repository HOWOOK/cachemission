package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.Adapter.ClassificationAdapter;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskView_Classification extends TaskView {

    public ClassificationAdapter mAdapter;
    private PhotoView photoView;
    private RecyclerView classificationrv;
    private String[] arrayExpand, arrayURI, arrayClass;
    private int[] arrayintClass;
    private HashMap<String, Bitmap> bitmaps;
    public ArrayList<Integer> idList, partList;
    public ArrayList<Boolean> checkList;

    public TaskView_Classification(){
        taskViewID = R.layout.taskview_classification;
        bitmaps = new HashMap<>();
    }

    //사진 url을 서버에서 받아와서 미리 로딩해놓는 함수이다.
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
    public void setPreviewContents(ArrayList<JSONObject> list)
    {
        try {
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.get(i);
                final String content = jsonObject.get("content").toString();
                System.out.println("TaskView_Classification, setPreviewContents, 컨텐츠 : " + content);
                String[] preArray0 = content.split(">");
                final String[] preArray1 = preArray0[1].split("\\*");
                Log.d("arrayset",preArray0[1]);
                new Thread() {
                    public void run() {
                        Bitmap bitmap = getBitmap(parentActivity.getString(R.string.mainurl) + "/media/" + preArray1[0]);
                        bitmaps.put(preArray1[0], bitmap);
                    }
                }.start();

            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setContent(String content) {
        photoView = parentActivity.findViewById(R.id.srcview);
        classificationrv = parentActivity.findViewById(R.id.classRecyclerView);

        System.out.println("콘텐츠 : " + content);
        arrayExpand = content.split(">");
        arrayURI = arrayExpand[1].split("\\*");
        arrayClass = arrayURI[1].split(",");
        arrayintClass = new int[arrayClass.length];

        for (int i = 0; i < arrayClass.length; i++)
            arrayintClass[i] = Integer.parseInt(arrayClass[i]) % 100;

        idList = new ArrayList<>();
        partList = new ArrayList<>();
        checkList = new ArrayList<>();

        mAdapter = new ClassificationAdapter(parentActivity, idList, partList, checkList);

        classificationrv.setLayoutManager(new GridLayoutManager(parentActivity, 4));
        classificationrv.setAdapter(mAdapter);

        //Adapter에 아이템 넣어주기
        for(int i = 0; i < arrayintClass.length; i++){
            if(arrayintClass[i] == 1) {
                idList.add(R.drawable.part_g);
                partList.add(1);
            }
            if(arrayintClass[i] == 4) {
                idList.add(R.drawable.part_tree);
                partList.add(4);
            }
            if(arrayintClass[i] == 5){
                idList.add(R.drawable.part_transformer);
                partList.add(5);
            }
            if(arrayintClass[i] == 6) {
                idList.add(R.drawable.part_a);
                partList.add(6);
            }
            if(arrayintClass[i] == 7) {
                idList.add(R.drawable.part_b);
                partList.add(7);
            }
            if(arrayintClass[i] == 8) {
                idList.add(R.drawable.part_c);
                partList.add(8);
            }
            if(arrayintClass[i] == 9) {
                idList.add(R.drawable.part_d);
                partList.add(9);
            }
            if(arrayintClass[i] == 10) {
                idList.add(R.drawable.part_e);
                partList.add(10);
            }
        }

        for(int i = 0; i < arrayClass.length; i++){
            checkList.add(false);
        }

        if(bitmaps.containsKey(arrayURI[0])) {
            System.out.println("TaskView_Classification, Http 통신안함, arrayURI[0] : " + arrayURI[0].toString());

            Glide.with(parentActivity)
                    .asBitmap()
                    .load(bitmaps.get(arrayURI[0]))
                    .into(photoView);

        }else{
            System.out.println("TaskView_Classification, Http 통신함, arrayURI[0] : " + arrayURI[0].toString());

            Glide.with(parentActivity)
                    .asBitmap()
                    .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+arrayURI[0]))
                    .into(photoView);

        }


    }

}
