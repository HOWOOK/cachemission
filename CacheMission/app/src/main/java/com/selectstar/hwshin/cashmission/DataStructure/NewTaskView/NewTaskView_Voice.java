package com.selectstar.hwshin.cashmission.DataStructure.NewTaskView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.Activity.TaskListActivity;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.DataStructure.NewTaskView.NewTaskView;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class NewTaskView_Voice extends NewTaskView {
    boolean isPlaying=false;
    Button mBtPlay;
    MediaPlayer mPlayer;
    public NewTaskView_Voice() {
        taskViewID = R.layout.taskview_voice;
        mPlayer = new MediaPlayer();
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
