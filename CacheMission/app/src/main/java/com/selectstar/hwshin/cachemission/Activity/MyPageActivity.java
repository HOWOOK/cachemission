package com.selectstar.hwshin.cachemission.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
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
    Guideline lowerBoundary;
    TextView withdrawButton;
    TextView imoticon,gifticon,cash;
    TextView withdrawGuide1,withdrawGuide2;
    EditText info1,info2;
    EditText gold;
    TextView sendBtn;
    ImageView myPageBack;
    String requestType="imoticon";
    String info1String="";
    String info2String="";


    boolean isWithdrawSelected=false;
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
        lowerBoundary=findViewById(R.id.guideLineMyPageHo2);
        imoticon=findViewById(R.id.imoticon);
        gifticon=findViewById(R.id.gifticon);
        cash=findViewById(R.id.cash);
        withdrawGuide1=findViewById(R.id.withdrawText);
        withdrawGuide2=findViewById(R.id.warningMessage);
        info1=findViewById(R.id.personalInfo1);
        info2=findViewById(R.id.personalInfo2);
        gold=findViewById(R.id.personalInfoGold);
        sendBtn=findViewById(R.id.withdrawSend);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float getDeviceDpi = displayMetrics.densityDpi;
        final float dpScale = (float) getDeviceDpi / 160f;

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
            totalM=(int)userInfo.get("exchange");
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
       // totalMoney.setText("\uFFE6"+"0");
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
        myPageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               requestType="imoticon";
               imoticon.setBackground(getResources().getDrawable(R.drawable.selected_rounded_withdrawbutton));
               gifticon.setBackground(getResources().getDrawable(R.drawable.rounded_withdrawbutton));
               cash.setBackground(getResources().getDrawable(R.drawable.rounded_withdrawbutton));
               imoticon.setTextColor(getResources().getColor(R.color.colorWhite));
               gifticon.setTextColor(getResources().getColor(R.color.colorDarkWhiteText));
               cash.setTextColor(getResources().getColor(R.color.colorDarkWhiteText));
               info1.setVisibility(View.VISIBLE);
               info1.setText("");
               info1.setHint("이모티콘명");

               info2.setText("");
               info2.setHint("카카오톡 아이디");

               gold.setText("");
               gold.setHint("이모티콘금액");
            }
        });
        gifticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestType="gifticon";
                imoticon.setBackground(getResources().getDrawable(R.drawable.rounded_withdrawbutton));
                gifticon.setBackground(getResources().getDrawable(R.drawable.selected_rounded_withdrawbutton));
                cash.setBackground(getResources().getDrawable(R.drawable.rounded_withdrawbutton));
                imoticon.setTextColor(getResources().getColor(R.color.colorDarkWhiteText));
                gifticon.setTextColor(getResources().getColor(R.color.colorWhite));
                cash.setTextColor(getResources().getColor(R.color.colorDarkWhiteText));
                info1.setVisibility(View.VISIBLE);
                info1.setText("");
                info1.setHint("기프티콘명");

                info2.setText("");
                info2.setHint("카카오톡 아이디");

                gold.setText("");
                gold.setHint("기프티콘금액");
            }
        });
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestType="cash";
                imoticon.setBackground(getResources().getDrawable(R.drawable.rounded_withdrawbutton));
                gifticon.setBackground(getResources().getDrawable(R.drawable.rounded_withdrawbutton));
                cash.setBackground(getResources().getDrawable(R.drawable.selected_rounded_withdrawbutton));
                imoticon.setTextColor(getResources().getColor(R.color.colorDarkWhiteText));
                gifticon.setTextColor(getResources().getColor(R.color.colorDarkWhiteText));
                cash.setTextColor(getResources().getColor(R.color.colorWhite));
                gold.setText("");
                gold.setHint("금액");

                info2.setText("");
                info2.setHint("계좌번호");
                info1.setVisibility(View.INVISIBLE);
            }
        });
        info1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String curMoneyArray[]=certainMoney.getText().toString().split("\uFFE6");
                String curMoney=curMoneyArray[1];
                if(requestType.equals("cash")) {
                    if (!info1.getText().toString().equals("")) {
                        if (Integer.parseInt(info1.getText().toString()) > Integer.parseInt(curMoney)) {
                            info1.setText(curMoney);
                            getDialog("최대금액제한", "현재 적립금액보다 적은 금액만 환전 가능합니다.");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        gold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String curMoneyArray[]=certainMoney.getText().toString().split("\uFFE6");
                String curMoney=curMoneyArray[1];
                if(!gold.getText().toString().equals("")) {
                    if (Integer.parseInt(gold.getText().toString()) > Integer.parseInt(curMoney)) {
                        gold.setText(curMoney);
                        getDialog("최대금액제한", "현재 적립금액보다 적은 금액만 환전 가능합니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentInfo1,currentInfo2,currentGold;
                currentInfo1=info1.getText().toString();
                currentInfo2=info2.getText().toString();
                currentGold=gold.getText().toString();
                if(currentInfo1.equals("")){
                    getDialog("상품정보 없음","먼저 상품정보를 입력해 주세요.");
                    return;
                }
                if(currentInfo2.equals("")){
                    getDialog("상품전달 인적사항 없음","먼저 상품전달에 필요한 인적사항을 입력해 주세요.");
                    return;
                }
                if(currentGold.equals("")){
                    getDialog("금액정보 없음","먼저 상품금액을 입력해 주세요.");
                    return;
                }
                if(currentInfo1.length()>30){
                    getDialog("상품정보 길이제한","30자 이내로 입력해 주세요.");
                    return;
                }
                if(currentInfo2.length()>30){
                    getDialog("인적사항 길이제한","30자 이내로 입력해 주세요.");
                    return;
                }
                if(currentGold.length()>30){
                    getDialog("금액 길이제한","30자 이내로 입력해 주세요.");
                    return;
                }
                if(Integer.parseInt(currentGold)<2000){
                    getDialog("금액 액수제한","2000원 이상의 금액부터 환전할 수 있습니다.");
                    return;
                }
//                String curMoneyArray[]=certainMoney.getText().toString().split("\uFFE6");
//                String curMoney=curMoneyArray[1];
//                if(requestType.equals("cash")&&Integer.parseInt(currentInfo1)>Integer.parseInt(curMoney)){
//                    getDialog("현재적립금액 초과","현재 적립금액보다 같거나 적은 액수만 환전 가능합니다.");
//                    return;
//                }
                SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
                final String loginToken = token.getString("loginToken","");
                JSONObject param = new JSONObject();
                try {
                    param.put("type",requestType);
                    param.put("content",currentInfo1);
                    param.put("path",currentInfo2);
                    param.put("gold",Integer.parseInt(currentGold));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                WaitHttpRequest asyncTask=new WaitHttpRequest(mContext) {
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        try {
                            if (result == "")
                                return;
                            JSONObject resultTemp = new JSONObject(result);
                            String success=resultTemp.get("success").toString();
                            if(success.equals("true")){
                                getDialog("환전신청 접수 완료","환전신청이 성공적으로 접수되었습니다.");
                                totalMoney.setText("\uFFE6"+resultTemp.get("exchange_gold").toString());
                                certainMoney.setText("\uFFE6"+resultTemp.get("gold").toString());
                                nowMoney.setText("\uFFE6"+String.valueOf(Integer.parseInt(resultTemp.get("pending_gold").toString())+Integer.parseInt(resultTemp.get("gold").toString())));
//                                nowMoney.setText(String.valueOf(Integer.parseInt(nowMoney.getText().toString())-Integer.parseInt(resultTemp.get("exchange_gold").toString())));
//                                certainMoney.setText(String.valueOf(Integer.parseInt(certainMoney.getText().toString())-Integer.parseInt(resultTemp.get("exchange_gold").toString())));
                            }
                            else {
                                getDialog("환전신청 접수 실패","환전신청이 접수되지 않았습니다. 잠시 후에 다시 시도해주세요.");
                            }



                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }
                };
                //CountDownTimer adf= new AsyncTaskCancelTimerTask(asyncTask,Integer.parseInt(getString(R.string.hTTPTimeOut)),1000,true,this).start();
                asyncTask.execute(getString(R.string.mainurl) + "/testing/newExchange", param, loginToken);
                //Toast.makeText(getApplicationContext(),"알파테스트에서 구현되지 않은 사항입니다! 챔피언의 충실한 보조 조하영 화이팅!",Toast.LENGTH_SHORT).show();
            }
        });
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isWithdrawSelected) {
                    isWithdrawSelected=true;

                    lowerBoundary.setGuidelineBegin((int)(500*dpScale));//기존 289

                    imoticon.setVisibility(View.VISIBLE);
                    gifticon.setVisibility(View.VISIBLE);
                    cash.setVisibility(View.VISIBLE);
                    withdrawGuide1.setVisibility(View.VISIBLE);
                    withdrawGuide2.setVisibility(View.VISIBLE);
                    info1.setVisibility(View.VISIBLE);
                    info2.setVisibility(View.VISIBLE);
                    gold.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.VISIBLE);
                    withdrawButton.setText("▲ 환전신청 접기");
                }
                else{
                    isWithdrawSelected=false;
                    lowerBoundary.setGuidelineBegin((int)(289*dpScale));//기존 289
                    imoticon.setVisibility(View.GONE);
                    gifticon.setVisibility(View.GONE);
                    cash.setVisibility(View.GONE);
                    withdrawGuide1.setVisibility(View.GONE);
                    withdrawGuide2.setVisibility(View.GONE);
                    info1.setVisibility(View.GONE);
                    info2.setVisibility(View.GONE);
                    gold.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.GONE);
                    withdrawButton.setText("▼ 환전신청");
                }
//                SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
//                final String loginToken = token.getString("loginToken","");
//                JSONObject param = new JSONObject();
//                WaitHttpRequest asyncTask=new WaitHttpRequest(mContext) {
//                    @Override
//                    protected void onPostExecute(Object o) {
//                        super.onPostExecute(o);
//
//                        try {
//                            if (result == "")
//                                return;
//                            JSONObject resultTemp = new JSONObject(result);
//                            String url=resultTemp.get("url").toString();
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setData(Uri.parse(url));
//                            startActivity(intent);
//
//
//
//                        }
//                        catch(JSONException e)
//                        {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                };
//                //CountDownTimer adf= new AsyncTaskCancelTimerTask(asyncTask,Integer.parseInt(getString(R.string.hTTPTimeOut)),1000,true,this).start();
//                asyncTask.execute(getString(R.string.mainurl) + "/testing/exchange", param, loginToken);
//                //Toast.makeText(getApplicationContext(),"알파테스트에서 구현되지 않은 사항입니다! 챔피언의 충실한 보조 조하영 화이팅!",Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyPageActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
}
