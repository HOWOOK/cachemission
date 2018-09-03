package com.selectstar.hwshin.cashmission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.selectstar.hwshin.cashmission.R;

public class ExamView_Voice extends ExamView {
    public ExamView_Voice() {
        ExamViewID = R.layout.examview_voice;
    }
    boolean isPlaying=false;
    @Override
    public void setLayout(String id, View view, final Context c, Intent in, String buttons, final String answer) {
        ConstraintLayout voicelayout=(ConstraintLayout) view;

        final Button mBtPlay=(Button)voicelayout.findViewById(R.id.examviewvoicebutton) ;
        final MediaPlayer mPlayer=new MediaPlayer();
        mBtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying == false) {
                    mBtPlay.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.voicestopbtn));
                    isPlaying = true;
                    try {
                        mPlayer.setDataSource(c, Uri.parse(parentActivity.getString(R.string.mainurl) + "/media/"+answer));
                        mPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.start();



                } else {
                    isPlaying = false;
                    mBtPlay.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.voiceplaybtn));
                    mPlayer.reset();


                }
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mBtPlay.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.voiceplaybtn));
                mPlayer.reset();
                isPlaying = false;

            }
        });

    }
}
