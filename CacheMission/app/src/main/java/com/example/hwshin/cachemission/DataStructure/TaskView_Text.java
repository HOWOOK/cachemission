package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskView_Text extends TaskView{
    public TaskView_Text() {
        taskViewID = R.layout.taskview_text;

    }

    @Override
    public void setContent(String id, String contentURI, Context context, final View view) {
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
                           //Log.d("hey","http://18.222.204.84/"+resulttemp.get("url"));
                            TextView textView= (TextView) view;
                           // Glide.with(context).load(Uri.parse("http://18.222.204.84"+((String)resulttemp.get("url")).substring(3))).into((ImageView) view);
                            textView.setText(resulttemp.get("text").toString());


                            String taskID = resulttemp.get("baseID").toString();
                            Log.d("baseid",taskID);
                            settaskID(Integer.parseInt(taskID));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }
            }.execute("http://18.222.204.84/taskText", param);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
