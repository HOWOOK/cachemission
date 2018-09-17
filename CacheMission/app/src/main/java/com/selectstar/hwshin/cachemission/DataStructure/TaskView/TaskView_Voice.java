package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_Voice extends TaskView {
    boolean isPlaying=false;
    Button mBtPlay;
    MediaPlayer mPlayer;
    public TaskView_Voice() {
        taskViewID = R.layout.taskview_voice;
        mPlayer = new MediaPlayer();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public void setContent(String content)
    {
        final String url = parentActivity.getString(R.string.mainurl) +"/media/"+ content;
        mBtPlay = parentActivity.findViewById(R.id.srcview);
        mBtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying == false) {
                    isPlaying = true;
                    mBtPlay.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.voicestopbtn));
                    try {
                        mPlayer.setDataSource(parentActivity, Uri.parse(url));
                        mPlayer.prepare();
                    }
                    catch (Exception e) {
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

                isPlaying = false;
                mBtPlay.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.voiceplaybtn));
                mPlayer.reset();

            }
        });
    }
}
