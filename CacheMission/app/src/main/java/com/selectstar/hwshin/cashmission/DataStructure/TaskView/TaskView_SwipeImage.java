package com.selectstar.hwshin.cashmission.DataStructure.TaskView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.selectstar.hwshin.cashmission.Adapter.CircularPagerAdapter;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class TaskView_SwipeImage extends TaskView {
    private ViewPager mPager = null;

    private CircularPagerAdapter mPagerAdapter = null;

    public TaskView_SwipeImage() {
        taskViewID = R.layout.taskview_swipeimage;

    }

    @Override
    public void setContent(String id, String contentURI, Context context, String taskType, int examType, View... view) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        if(contentURI.equals("donthttp")==false) {

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

                                String mtaskID = resulttemp.get("baseID").toString();
                                settaskID(Integer.parseInt(mtaskID));
                                SharedPreferences iddd = parentActivity.getSharedPreferences("iddd", MODE_PRIVATE);
                                SharedPreferences.Editor editor = iddd.edit();
                                editor.putString("iddd", String.valueOf(taskID));
                                editor.commit();
                                Log.d("ssssss", String.valueOf(taskID));

                            } else {
                                if (parentActivity.getIntent().getIntExtra("from", 0) == 0) {
                                    Toast.makeText(parentActivity, "회원님이 선택하신 지역에 해당하는 과제가 더이상 없습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(parentActivity, "테스크를 모두 완료했습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                                }
                                parentActivity.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }.execute(parentActivity.getString(R.string.mainurl) + "/taskGet", param, logintoken);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ViewPager mPager = (ViewPager)view[0];
        int[] pages=new int[3];
        mPagerAdapter = new CircularPagerAdapter(mPager, pages, contentURI);

        mPager.setAdapter(mPagerAdapter);                     // ViewPager Adapter 설정

        mPager.setCurrentItem(3);



    }
}
