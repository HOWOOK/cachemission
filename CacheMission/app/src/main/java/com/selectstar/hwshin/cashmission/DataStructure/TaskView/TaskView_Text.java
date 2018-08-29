package com.selectstar.hwshin.cashmission.DataStructure.TaskView;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskView_Text extends TaskView {
    public TaskView_Text() {
        taskViewID = R.layout.taskview_text;

    }

    @Override
    public void setContent(String id, String contentURI, Context context, final View view) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        if (contentURI.equals("foobar")) {
            JSONObject param = new JSONObject();
            try {
                param.put("id", id);

                if(id.equals("4")){//RECORD일때는 지역을 같이 넣어서 요청해야함
                    String region;
                    SharedPreferences explain = parentActivity.getSharedPreferences("region", Context.MODE_PRIVATE);
                    region = explain.getString("region",null);
                    param.put("region", region);
                }

                new HttpRequest() {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        JSONObject resulttemp = null;
                        try {
                            resulttemp = new JSONObject(result);
Log.d("hahaha",result);
                            if ((boolean) resulttemp.get("success")) {

                                TextView textView = (TextView) view;
                                textView.setText(resulttemp.get("text").toString());

                                String taskID = resulttemp.get("baseID").toString();
                                settaskID(Integer.parseInt(taskID));

                            }
                            else{
                                Toast.makeText(parentActivity,"테스크를 모두 텍스트 완료했습니다. 테스크 리스트로 돌아갑니다.",Toast.LENGTH_SHORT).show();
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
            TextView textView = (TextView) view;
            textView.setText(contentURI);

        }
    }
}
