package com.selectstar.hwshin.cashmission.DataStructure.TaskView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.Activity.TaskListActivity;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskView_Voice extends TaskView {
    boolean isPlaying=false;

    public TaskView_Voice() {
        taskViewID = R.layout.taskview_voice;
    }
    @Override
    public void setContent(String id, final String contentURI, final Context context, String taskType, int examType, final View... view) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);


        final Button mBtPlay=(Button) view[0];
        final MediaPlayer mPlayer=new MediaPlayer();
        if (contentURI.equals("foobar")) {
            JSONObject param = new JSONObject();
            try {
                param.put("id", id);

                new HttpRequest(parentActivity) {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        JSONObject resulttemp = null;
                        try {
                            resulttemp = new JSONObject(result);

                            if ((boolean) resulttemp.get("success")) {
                                final String url = parentActivity.getString(R.string.mainurl) +"/media/"+ ((String) resulttemp.get("url"));
Log.d("shiba",url);
                                mBtPlay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (isPlaying == false) {
                                            isPlaying = true;
                                            mBtPlay.setBackground(ContextCompat.getDrawable(context, R.drawable.voicestopbtn));
                                            try {
                                                mPlayer.setDataSource(context, Uri.parse(url));
                                                mPlayer.prepare();
                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            mPlayer.start();

                                        } else {

                                            isPlaying = false;
                                            mBtPlay.setBackground(ContextCompat.getDrawable(context, R.drawable.voiceplaybtn));
                                            mPlayer.reset();

                                        }
                                    }
                                });
                                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mp) {

                                        isPlaying = false;
                                        mBtPlay.setBackground(ContextCompat.getDrawable(context, R.drawable.voiceplaybtn));
                                        mPlayer.reset();

                                    }
                                });
                                String taskID = resulttemp.get("baseID").toString();
                                settaskID(Integer.parseInt(taskID));

                            }
                            else{
                                if(parentActivity.getIntent().getIntExtra("from",0)==0){
                                    Toast.makeText(parentActivity, "회원님이 선택하신 지역에 해당하는 과제가 더이상 없습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(parentActivity, "테스크를 모두 완료했습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                                }
                                parentActivity.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(parentActivity.getString(R.string.mainurl)+"/taskGet", param,logintoken);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            mBtPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isPlaying == false) {

                        isPlaying = true;
                        mBtPlay.setBackground(ContextCompat.getDrawable(context, R.drawable.voicestopbtn));
                        try {
                            mPlayer.setDataSource(context, Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/" + contentURI));
                            Log.d("shishi",parentActivity.getString(R.string.mainurl)+"/media/" + contentURI);
                            mPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mPlayer.start();

                    } else {
                        isPlaying = false;
                        mBtPlay.setBackground(ContextCompat.getDrawable(context, R.drawable.voiceplaybtn));
                        mPlayer.reset();


                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    mBtPlay.setBackground(ContextCompat.getDrawable(context, R.drawable.voiceplaybtn));
                    mPlayer.reset();

                }
            });

        }


    }


}
