package com.selectstar.hwshin.cachemission.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MyPageActivity extends AppCompatActivity {
    Context mContext=this;
    TextView nowMoney;
    TextView certainMoney;
    TextView delayedMoney;
    TextView totalMoney;
    TextView userName;
    TextView userLevel;

    TextView withdrawButton;
    ImageView myPageBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        nowMoney=findViewById(R.id.nowMoney);
        certainMoney=findViewById(R.id.certainMoney);
        delayedMoney=findViewById(R.id.delayedMoney);
        totalMoney=findViewById(R.id.totalMoney);
        userName=findViewById(R.id.myPageUserName);
        userLevel=findViewById(R.id.mypageUserLevel);
        myPageBack=findViewById(R.id.myPageBack);
        withdrawButton=findViewById(R.id.withdrawButton);

        myPageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
                final String loginToken = token.getString("loginToken","");
                JSONObject param = new JSONObject();
                WaitHttpRequest asyncTask=new WaitHttpRequest(mContext) {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        try {
                            if (result == "")
                                return;
                            JSONObject resultTemp = new JSONObject(result);
                            String url=resultTemp.get("url").toString();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);



                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }
                };
                //CountDownTimer adf= new AsyncTaskCancelTimerTask(asyncTask,Integer.parseInt(getString(R.string.hTTPTimeOut)),1000,true,this).start();
                asyncTask.execute(getString(R.string.mainurl) + "/testing/exchange", param, loginToken);
                //Toast.makeText(getApplicationContext(),"알파테스트에서 구현되지 않은 사항입니다! 챔피언의 충실한 보조 조하영 화이팅!",Toast.LENGTH_SHORT).show();
            }
        });

        JSONObject userInfo=new JSONObject();
        int totalM=0;
        int nowM=0;
        int certainM=0;
        int delayedM=0;
        String userN="";
        int userL=0;
        String[] parsedUserN=new String[2];
        SharedPreferences userInfoPreference = getSharedPreferences("userInfo",MODE_PRIVATE);
        try {
            userInfo=new JSONObject(userInfoPreference.getString("userInfo",""));
            certainM=(int)userInfo.get("gold");
            delayedM=(int)userInfo.get("maybe");
            nowM=certainM+delayedM;
            totalM=certainM+delayedM;
            userN=(String)userInfo.get("name");
            parsedUserN=userN.split("@");
            userL=(int)userInfo.get("rank");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        nowMoney.setText("\uFFE6"+String.valueOf(nowM));
        certainMoney.setText("\uFFE6"+String.valueOf(certainM));
        delayedMoney.setText("\uFFE6"+String.valueOf(delayedM));
        totalMoney.setText("\uFFE6"+String.valueOf(totalM));
        userName.setText(parsedUserN[0]+"님의 통장");
        if(userL==1) {

            userLevel.setText("Lv.01");
        }
        else if(userL==2) {
            userLevel.setText("Lv.02");
        }
        else if(userL==3) {

            userLevel.setText("Lv.03");
        }
        else if(userL==151){

            userLevel.setText("Lv.99");
        }
        else {

            userLevel.setText("Lv.00");
        }

    }
}
