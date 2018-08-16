package com.example.hwshin.cachemission.DataStructure.TaskView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hwshin.cachemission.Activity.TaskListActivity;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.example.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskView_Voice extends TaskView {
    boolean isPlaying=false;

    public TaskView_Voice() {
        taskViewID = R.layout.taskview_voice;
    }
    @Override
    public void setContent(String id, final String contentURI, final Context context, final View view) {


        final Button mBtPlay=(Button) view;
        final MediaPlayer mPlayer=new MediaPlayer();
        if (contentURI.equals("foobar")) {
            JSONObject param = new JSONObject();
            try {
                param.put("id", id);

                new HttpRequest() {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        JSONObject resulttemp = null;
                        try {
                            resulttemp = new JSONObject(result);
                            Log.d("hey2", resulttemp.toString());
                            if ((boolean) resulttemp.get("success")) {


                                Log.d("hey", "http://18.222.204.84/" + resulttemp.get("url"));
                                final String url = "http://18.222.204.84" + ((String) resulttemp.get("url")).substring(3);

                                mBtPlay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (isPlaying == false) {
                                            try {
                                                mPlayer.setDataSource(context, Uri.parse(url));
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
                                String taskID = resulttemp.get("baseID").toString();
                                Log.d("baseid", taskID);
                                settaskID(Integer.parseInt(taskID));

                            }
                            else{
                                Toast.makeText(parentActivity,"테스크를 모두 완료했습니다. 테스크 리스트로 돌아갑니다.",Toast.LENGTH_SHORT);
                                Intent in = new Intent(parentActivity,TaskListActivity.class);
                                parentActivity.startActivity(in);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute("http://18.222.204.84/taskURI", param);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            mBtPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isPlaying == false) {
                        try {
                            mPlayer.setDataSource(context, Uri.parse("http://18.222.204.84" + contentURI.substring(3)));
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


}
