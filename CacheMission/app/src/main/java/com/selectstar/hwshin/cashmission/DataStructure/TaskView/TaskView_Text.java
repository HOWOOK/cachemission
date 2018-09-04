package com.selectstar.hwshin.cashmission.DataStructure.TaskView;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
    public void setContent(String id, String contentURI, final Context context, final String taskType, int examType, final View... view) {
        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);

        if (contentURI.equals("foobar")) {
            JSONObject param = new JSONObject();
            try {
                param.put("id", id);

                if(taskType.equals("RECORD")){//RECORD일때는 지역을 같이 넣어서 요청해야함
                    String region;
                    SharedPreferences explain = parentActivity.getSharedPreferences("region", Context.MODE_PRIVATE);
                    region = explain.getString("region",null);
                    param.put("region", region);
                }

                new HttpRequest(parentActivity) {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        JSONObject resulttemp = null;
                        try {
                            resulttemp = new JSONObject(result);
                            if ((boolean) resulttemp.get("success")) {
                                String responeText;
                                responeText = resulttemp.get("text").toString();

                                //Text parsing /를 기준으로 나눠서 TextVIew에 넣어준다.
                                TextView textView1 = (TextView) view[0];
                                TextView textView2 = (TextView) view[1];
                                String[] array;
                                String[] array2;
                                String[] array3;

                                if(taskType.equals("DIALECT")){
                                    array = responeText.split("/");
                                    textView1.setText("["+array[0]+"]");
                                    textView1.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_custom2));
                                    textView1.setTextSize(18);
                                    textView2.setText(array[1]+"\n\n※ 억양만 사투리여도 그대로 적으시고, 원하는 호칭이나 이름으로 자연스럽게 써주세요.");
                                    textView2.setTextColor(ContextCompat.getColor(context, R.color.fontColorActive));
                                    textView2.setTextSize(18);
                                }else if(taskType.equals("RECORD")){
                                    array = responeText.split("/");
                                    array2 = array[1].split("\\(");
                                    array3 = array2[1].split("\\)");
                                    textView1.setText("["+array[0]+"]"+"\n"+array2[0]);
                                    textView1.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_custom1));
                                    textView1.setTextSize(12);
                                    textView2.setText("(" + array3[0] + ")" + "\n" + array3[1]);
                                    textView2.setTextSize(18);
                                }
                                String taskID = resulttemp.get("baseID").toString();
                                settaskID(Integer.parseInt(taskID));

                                TextView textView_nowgold = (TextView) view[2];
                                TextView textView_pregold = (TextView) view[3];
                                textView_nowgold.setText("현재 : " + "\uFFE6 "+String.valueOf(resulttemp.get("gold")));
                                textView_pregold.setText("예정 : " + "\uFFE6 "+String.valueOf(resulttemp.get("pending_gold")));

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
        else{//examining
            //text를 /와 (와 )를 기준으로 나눠서 각각 텍스트뷰에 넣어준다.
            TextView textView1 = (TextView) view[0];
            TextView textView2 = (TextView) view[1];
            String[] array;
            String[] array2;
            String[] array3;

            if(examType==1){//문장만 제대로 읽었는지 검수
                array = contentURI.split("/");
                array2 = array[1].split("\\(");
                array3 = array2[1].split("\\)");
                textView1.setText("["+array[0]+"]"+"\n"+array2[0]);
                textView1.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_custom1));
                textView1.setTextSize(12);
                textView2.setText(array3[1]);
                textView2.setTextSize(18);
            }else if(examType==2){//사투리 발음까지 제대로 했는지 검수
                array = contentURI.split("/");
                array2 = array[1].split("\\(");
                array3 = array2[1].split("\\)");
                textView1.setText("["+array[0]+"]"+"\n"+array2[0]);
                textView1.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_custom1));
                textView1.setTextSize(12);
                textView2.setText("("+array3[0]+")"+"\n"+array3[1]);
                textView2.setTextSize(18);
            }

//            String[] array = contentURI.split("/");
//            String[] array2 = array[1].split("\\(");
//            String[] array3 = array2[1].split("\\)");
//
//            TextView textView1 = (TextView) view[0];
//            TextView textView2 = (TextView) view[1];
//            textView1.setText("<"+array[0]+">"+"\n"+array2[0]);
//            textView2.setText("("+array3[0]+")"+"\n"+array3[1]);

        }
    }
}
