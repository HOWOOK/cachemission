package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;


public class TaskView_Video extends TaskView {

    public TaskView_Video() {
        taskViewID = R.layout.taskview_video;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    @Override
    public void setContent(String content) {
/*
        if (ContentURI.equals("foobar")) {
            JSONObject param = new JSONObject();
            try {
                param.put("id", id);

                new HurryHttpRequest(parentActivity) {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        JSONObject resulttemp = null;
                        try {
                            resulttemp = new JSONObject(result);

                            if ((boolean) resulttemp.get("success")) {

                                VideoView videoView = (VideoView) view[0];


                                MediaController mc = new MediaController(context);
                                videoView.setMediaController(mc);
                                videoView.setVideoURI(Uri.parse(parentActivity.getString(R.string.mainurl)+"/media/"+ ((String) resulttemp.get("url"))));
                                videoView.start();
                                String taskID = resulttemp.get("baseID").toString();

                            }else{
                                if(parentActivity.getIntent().getIntExtra("from",0)==0){
                                    Toast.makeText(parentActivity, "회원님이 선택하신 지역에 해당하는 과제가 더이상 없습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(parentActivity, "테스크를 모두 완료했습니다. 테스크 리스트로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                                }
                                parentActivity.finish();

                                TextView textView_nowgold = (TextView) view[2];
                                TextView textView_pregold = (TextView) view[3];
                                textView_nowgold.setText("현재 : " + "\uFFE6 "+String.valueOf(resulttemp.get("gold")));
                                textView_pregold.setText("예정 : " + "\uFFE6 "+String.valueOf(resulttemp.get("pending_gold")));
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
            VideoView videoView = (VideoView) view[0];
            MediaController mc = new MediaController(context);
            videoView.setMediaController(mc);
            videoView.setVideoURI(Uri.parse(parentActivity.getString(R.string.mainurl) +"/media/"+ ContentURI));
            videoView.start();

        }
    }
    */
    }
}
