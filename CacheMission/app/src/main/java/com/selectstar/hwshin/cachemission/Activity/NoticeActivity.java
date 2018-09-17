
package com.selectstar.hwshin.cachemission.Activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.selectstar.hwshin.cachemission.Adapter.NoticeviewAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.NoticeItem;
import com.selectstar.hwshin.cachemission.DataStructure.WaitHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeActivity extends AppCompatActivity {

    Context context = this;
    ImageView quitButton;
    ListView listView;
    static NoticeviewAdapter adapter;
    final static ArrayList<NoticeItem> mNoticeList = new ArrayList<NoticeItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        quitButton = findViewById(R.id.popupback);
        listView = findViewById(R.id.noticelist);
        mNoticeList.clear();
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        JSONObject param = new JSONObject();
        new WaitHttpRequest(context){
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                try {
                    JSONObject res = new JSONObject(result);
                    if(res.get("success").toString().equals(("true")))
                    {
                        JSONArray noticeList = (JSONArray)res.get("notices");
                        for (int i = 0; i < noticeList.length(); i++) {

                            JSONObject temp = (JSONObject) noticeList.get(i);
                            mNoticeList.add(new NoticeItem( temp.get("title").toString(), temp.get("date").toString(),temp.get("content").toString()) );
                        }
                        adapter = new NoticeviewAdapter(getApplicationContext(), R.layout.notice_lv, NoticeActivity.mNoticeList);
                        listView.setAdapter(adapter);

                    }
                    else
                    {
                        getDialog("통신 에러","확인");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(getString(R.string.mainurl)+"/notice",param, "");
    }

    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NoticeActivity.this);
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