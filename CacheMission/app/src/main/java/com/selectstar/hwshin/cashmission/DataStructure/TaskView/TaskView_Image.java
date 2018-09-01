package com.selectstar.hwshin.cashmission.DataStructure.TaskView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cashmission.Activity.TaskListActivity;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskView_Image extends TaskView {

    public TaskView_Image() {
        taskViewID = R.layout.taskview_image;

}

    //protected int taskViewID = R.layout.taskview_image;
    @Override
    public void setContent(String id, String contentURI, final Context context, final String taskType, int examType, final View... view) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

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

                                ImageView imageView = (ImageView) view[0];
                                if(taskType.equals("NUMBERING"))
                                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                Glide.with(context).load(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ ((String) resulttemp.get("url")))).into((ImageView) view[0]);


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
            ImageView imageView = (ImageView) view[0];
            Glide.with(context).load(Uri.parse(parentActivity.getString(R.string.mainurl) + "/media"+contentURI.substring(3))).into((ImageView) view[0]);


        }

    }

}
