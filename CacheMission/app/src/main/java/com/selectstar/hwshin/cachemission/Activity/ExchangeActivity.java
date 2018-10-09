package com.selectstar.hwshin.cachemission.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ExchangeActivity extends AppCompatActivity {

    Context context = this;
    EditText exchangemain;
    Button send_btn;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("ExchangeActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        exchangemain = findViewById(R.id.exchangemain);
        send_btn = findViewById(R.id.sendbtn);


        backButton = findViewById(R.id.exchangeback);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog2();
            }
        });
    }

    private void getDialog(String title, String value, String positive)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ExchangeActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialogBuilder.show();
    }


    private void getDialog2 ()
    {
        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        String stringToken="";
        stringToken = token.getString("loginToken",null);
        final String finalStringToken = stringToken;

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ExchangeActivity.this);
        alertDialogBuilder.setTitle("환전요청 전송");
        alertDialogBuilder.setMessage("입력사항을 보내시겠습니까?");

        alertDialogBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject param = new JSONObject();
                final String exchangeVal = exchangemain.getText().toString();
                try {
                    param.put("exchange", exchangeVal);

                    new WaitHttpRequest(context){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject res = new JSONObject(result);
                                if(exchangeVal.equals(""))
                                    getDialog("입력 된 문장이 없습니다.", "글을 입력해 주세요.", "확인");
                                else if(res.get("success").toString().equals("true"))
                                    getDialog("전송 되었습니다.", "요청이 처리되면 포인트가 차감됩니다.", "확인");
                                else
                                    getDialog("전송에 실패 하였습니다.", "서버에 문제가 있나보네요ㅠㅠ 다음에 다시 시도해주세요!", "확인");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute(getString(R.string.mainurl)+"/exchange",param, finalStringToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialogBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }
    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);


    }
    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

}
