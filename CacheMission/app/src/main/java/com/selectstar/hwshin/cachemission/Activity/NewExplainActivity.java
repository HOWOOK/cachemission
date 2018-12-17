package com.selectstar.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainMain;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Dialect;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_DirectRecord;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_None;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Numbering;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Photo;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record_ExamType1;
import com.selectstar.hwshin.cachemission.Adapter.Explain.SlideAdapter_ExplainTask_Record_ExamType2;
import com.selectstar.hwshin.cachemission.Adapter.NewExplainAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class NewExplainActivity extends AppCompatActivity {
    Intent intent;
    String taskType="";
    String part="";
   int partNum;
    String urlList="";
    int examType = 0;
    String taskID="";
    String loginToken="";
    int version=0;

    public ViewPager viewpager;
    private PagerAdapter myadapter;
    private ImageView exit;
    private TextView pagecnt;
    private int mCurrentPage;
    final int maxPage=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_explain);
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr")) {
            Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
            t.setScreenName("NewExplainActivity");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
        viewpager=(ViewPager)findViewById(R.id.newExplainViewpager);
        intent = getIntent();
        taskType = intent.getStringExtra("taskType");
        part = intent.getStringExtra("part");
        part = partTranslation(part);
        partNum=intent.getIntExtra("partNum",0);
        taskID=intent.getStringExtra("taskID");
        loginToken=intent.getStringExtra("loginToken");

        System.out.println("테스크" + taskID);
        getUrlList();
        SharedPreferences explainImageSet=getSharedPreferences(taskID+"explainImageSet"+String.valueOf(partNum),MODE_PRIVATE);
        String urlListFinal=explainImageSet.getString("imageSet","");

        try {
            JSONObject urlObjectFinal=new JSONObject(urlListFinal);
            if(!urlListFinal.equals("")) {
                urlList = (String) urlObjectFinal.get("imageList").toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("urlList?",urlList);
        System.out.println("변환한 텍스트 : "+part);

        SharedPreferences taskToken = getSharedPreferences("taskToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = taskToken.edit();
        editor.putInt(taskType + "taskToken", 100);
        if(taskType.equals("RECORDEXAM")) {
            examType = intent.getIntExtra("examType", 0);
            editor.putInt(taskType + examType + "taskToken", 100);
        }
        editor.commit();




    }

    private String partTranslation(String part) {
        String result ="";
        if(part.equals("전봇대 부품들"))
            result = "preProcess";
        if(part.equals("전봇대"))
            result = "pole";
        if(part.equals("나무"))
            result = "tree";
        if(part.equals("변압기"))
            result = "transformer";
        if(part.equals("부품 A"))
            result = "partA";
        if(part.equals("부품 B"))
            result = "partB";
        if(part.equals("부품 C"))
            result = "partC";
        if(part.equals("부품 D"))
            result = "partD";
        if(part.equals("부품 E"))
            result = "partE";
        if(part.equals("부품 G"))
            result = "partG";

        return  result;
    }
    private void getUrlList(){

        SharedPreferences explainImageSet=getSharedPreferences(taskID+"explainImageSet"+String.valueOf(partNum),MODE_PRIVATE);
        String murlList=explainImageSet.getString("imageSet","");
        final SharedPreferences.Editor editor=explainImageSet.edit();


        JSONObject param = new JSONObject();

        try {

            JSONObject urlObject;
            if(!murlList.equals("")){
                urlObject=new JSONObject(murlList);
            version= urlObject.getInt("version");
            }
            param.put("taskID", taskID);
            param.put("option", partNum % 100);
            param.put("version", version);


            new HurryHttpRequest(this) {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    JSONObject resultTemp = null;
                    try {
                        resultTemp = new JSONObject(result);
                        System.out.println("리절리절"+result);
                        JSONArray imageList = (JSONArray) resultTemp.get("imageList");
                        if(imageList.length()!=0){
                            urlList=imageList.toString();
                            editor.putString("imageSet",result);
                            editor.commit();
                        }
                        myadapter= findAdaptingTaskExplain(taskType);
                        viewpager.setAdapter(myadapter);
                        viewpager.addOnPageChangeListener(viewlistener);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }.execute(getString(R.string.mainurl) + "/explainGet", param, loginToken);
        } catch(JSONException e)
        {
            e.printStackTrace();

        }



    }

    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            mCurrentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private PagerAdapter findAdaptingTaskExplain(String tasktype) {
        try {
            JSONArray urlJSONArray = new JSONArray(urlList);

            return new NewExplainAdapter(this,tasktype+part,urlJSONArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new NewExplainAdapter(this,tasktype+part,"[]");
    }
    @Override
    protected void onStart(){
        super.onStart();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStart(this);


    }
    @Override
    protected void onStop(){
        super.onStop();
        if(getString(R.string.mainurl).equals("https://www.selectstar.co.kr"))
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


}
