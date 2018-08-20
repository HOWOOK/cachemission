package com.example.hwshin.cachemission.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hwshin.cachemission.DataStructure.HttpRequest;
import com.example.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SuggestionActivity extends AppCompatActivity {


    EditText suggestionmain;
    Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        suggestionmain = findViewById(R.id.suggestionmain);
        send_btn = findViewById(R.id.sendbtn);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog2();
            }
        });
    }

    private void getDialog(String title, String value, String positive)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SuggestionActivity.this);
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
        String stringtoken="";
        stringtoken = token.getString("logintoken",null);
        final String finalStringtoken = stringtoken;

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(SuggestionActivity.this);
        alertDialogBuilder.setTitle("건의사항 전송");
        alertDialogBuilder.setMessage("입력사항을 보내시겠습니까?");

        alertDialogBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject param = new JSONObject();
                final String suggestionVal = suggestionmain.getText().toString();
                try {
                    param.put("suggestion", suggestionVal);

                    new HttpRequest(){
                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            try {
                                JSONObject res = new JSONObject(result);
                                if(suggestionVal.equals(""))
                                    getDialog("입력 된 문장이 없습니다.", "글을 입력해 주세요.", "확인");
                                else if(res.get("success").toString().equals("true"))
                                    getDialog("전송 되었습니다.", "관심 가져주셔서 감사합니다♡", "확인");
                                else
                                    getDialog("전송에 실패 하였습니다.", "서버에 문제가 있나보네요ㅠㅠ 다음에 다시 시도해주세요!", "확인");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute("http://18.222.204.84/suggestion",param, finalStringtoken);
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
}
