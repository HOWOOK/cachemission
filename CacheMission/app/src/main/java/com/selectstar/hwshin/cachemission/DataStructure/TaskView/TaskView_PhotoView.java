package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_PhotoView extends TaskView {
    private PhotoView myView;
    private float[][] answerCoordination;
    private String[] array1, array2, array3;


    public PhotoView getPhotoView(){
        return  this.myView;
    }

    public TaskView_PhotoView()
    {
        taskViewID = R.layout.taskview_photoview;
    }

    public float[][] getAnswerCondination(){
        return  this.answerCoordination;
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public void setContent(String content)
    {
//        array = content.split("/");
//        array2 = array[1].split("\\(");
//        array3 = array2[1].split("\\)");
//        textView1.setText("["+array[0]+"]"+"\n"+array2[0]);
//        textView1.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.textview_custom1));
//        textView1.setTextSize(12);
//        textView2.setText("(" + array3[0] + ")" + "\n" + array3[1]);
//        textView2.setTextSize(18);

        array1 = content.split("&");
        array2 = array1[1].split("\\(");
        for(int i =0; i < array2.length; i++){
            array3 = array2[i].split(",");
        }


        myView = parentActivity.findViewById(R.id.srcview);
        myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(parentActivity)
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ array1[0]))
                .thumbnail(0.1f)
                .into(myView);
    }
}