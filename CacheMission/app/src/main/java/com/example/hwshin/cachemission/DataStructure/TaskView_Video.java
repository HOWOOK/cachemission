package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TaskView_Video extends TaskView {

    public TaskView_Video() {
        taskViewID = R.layout.taskview_video;
    }
    //SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
    //String logintoken = token.getString("logintoken",null);

    @Override
    public void setContent(String id, String ContentURI, final Context context, final View view) {


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
                        Log.d("hey2",resulttemp.toString());
                        if((boolean)resulttemp.get("success")){

                            VideoView videoView= (VideoView) view;
                            Log.d("hey","http://18.222.204.84/"+resulttemp.get("url"));

                            MediaController mc=new MediaController(context);
                            videoView.setMediaController(mc);
                            videoView.setVideoURI(Uri.parse("http://18.222.204.84"+((String)resulttemp.get("url")).substring(3)));
                            videoView.start();
                            String taskID = resulttemp.get("baseID").toString();
                            Log.d("baseid",taskID);
                            settaskID(Integer.parseInt(taskID));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }
            }.execute("http://18.222.204.84/taskURI", param);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        /*
        VideoView videoView= (VideoView) view;
        videoView.setVideoURI(Uri.parse("https://www.youtube.com/watch?v=xsVBM4HFcak"));
        MediaController mc = new MediaController(context);
        videoView.setMediaController(mc);
        videoView.requestFocus();
        videoView.start();*/
        //vv.setVideoURI(Uri.parse(ContentURI));
       // vv.setVideoPath("https://youtu.be/xsVBM4HFcak");

    }
}
