package com.example.hwshin.cachemission.DataStructure.ExamView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.example.hwshin.cachemission.DataStructure.ExamView.ExamView;
import com.example.hwshin.cachemission.R;

public class ExamView_Voice extends ExamView {
    public ExamView_Voice() {
        ExamViewID = R.layout.examview_voice;
    }
    boolean isPlaying=false;
    @Override
    public void setLayout(String id, View view, final Context c, Intent in, String buttons, final String answer) {
        final Button mBtPlay=(Button) view;
        final MediaPlayer mPlayer=new MediaPlayer();
        mBtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying == false) {
                    try {
                        mPlayer.setDataSource(c, Uri.parse("http://18.222.204.84" + answer.substring(3)));
                        mPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.start();

                    isPlaying = true;
                    mBtPlay.setText("듣기중지");
                } else {
                    mPlayer.reset();

                    isPlaying = false;
                    mBtPlay.setText("들어보기");
                }
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.reset();
                isPlaying = false;
                mBtPlay.setText("들어보기");
            }
        });

    }
}
