package com.selectstar.hwshin.cachemission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

public class ExamView_ZoomImage extends ExamView{
    public ExamView_ZoomImage() {
        ExamViewID = R.layout.examview_zoomimage;
    }
    @Override
    public void setContent(String content, String taskID) {
        PhotoView photoView = (PhotoView)parentActivity.findViewById(R.id.zoomzoom);
        Glide.with(parentActivity)
                .load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ content))
                .thumbnail(0.1f)
                .into(photoView);
        photoView.setMaximumScale(15.0f);
    }

    @Override
    public void setLayout(String id, View view, Context c, Intent in, String buttons, String Answer) {
    }
}
