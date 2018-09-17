package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_PhotoView extends TaskView {
    private PhotoView myView;
    private float[][] answerCoordination;
    private String[] array1, array2, array3;

    public TaskView_PhotoView()
    {
        taskViewID = R.layout.taskview_photoview;
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
        //box 좌표 구해서 저장
        array1 = content.split("&");
        array2 = array1[1].split("\\(");
        answerCoordination = new float[array2.length][4];
        for(int i =0; i < array2.length; i++){
            array3 = array2[i].split(",");
            answerCoordination[i][0] = (float) Float.parseFloat(array3[1]);
            answerCoordination[i][1] = (float) Float.parseFloat(array3[2]);
            answerCoordination[i][2] = (float) Float.parseFloat(array3[3]);
            answerCoordination[i][3] = (float) Float.parseFloat(array3[4]);
        }
        myView = parentActivity.findViewById(R.id.srcview);
        myView.setScaleType(ImageView.ScaleType.FIT_CENTER);

               Glide.with(parentActivity)
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ array1[0]))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return true;
                    }
                })
                .thumbnail(0.1f)
                .into(myView)
        ;


        myView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {




                return true;
            }
        });




    }
}