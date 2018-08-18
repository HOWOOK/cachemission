package com.example.hwshin.cachemission.DataStructure.TaskView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.hwshin.cachemission.Activity.TaskListActivity;
import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.DataStructure.TaskView.TaskView;
import com.example.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;


public class TaskView_Video extends TaskView {

    public TaskView_Video() {
        taskViewID = R.layout.taskview_video;
    }


    @Override
    public void setContent(String id, String ContentURI, final Context context, final View view) {

        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        if (ContentURI.equals("foobar")) {
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

                                VideoView videoView = (VideoView) view;
                                Log.d("hey", "http://18.222.204.84/" + resulttemp.get("url"));

                                MediaController mc = new MediaController(context);
                                videoView.setMediaController(mc);
                                videoView.setVideoURI(Uri.parse("http://18.222.204.84" + ((String) resulttemp.get("url")).substring(3)));
                                videoView.start();
                                String taskID = resulttemp.get("baseID").toString();
                                Log.d("baseid", taskID);
                                settaskID(Integer.parseInt(taskID));

                            }else{
                                Toast.makeText(parentActivity,"테스크를 모두 완료했습니다. 테스크 리스트로 돌아갑니다.",Toast.LENGTH_SHORT);
                                Intent in = new Intent(parentActivity,TaskListActivity.class);
                                parentActivity.startActivity(in);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }.execute("http://18.222.204.84/taskURI", param,logintoken);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            VideoView videoView = (VideoView) view;
            MediaController mc = new MediaController(context);
            videoView.setMediaController(mc);
            videoView.setVideoURI(Uri.parse("http://18.222.204.84" + ContentURI.substring(3)));
            videoView.start();

        }
    }
}
