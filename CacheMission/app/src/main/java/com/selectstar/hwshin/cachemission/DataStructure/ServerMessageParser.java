package com.selectstar.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.PatherActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerMessageParser {

    //TaskActivity에서 새로운 테스크를 받아오려고 할때 실패할경우
    public void taskSubmitFailParse(Context context, JSONObject result) throws JSONException {
        if (result.get("message").toString().equals("login")) {
            Intent in = new Intent(context, LoginActivity.class);
            context.startActivity(in);
            Toast.makeText(context, "로그인이 만료되었습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
        } else if (result.get("message").toString().equals("expired")) {
            Toast.makeText(context, "테스크가 만료되었습니다. 다시 들어와주세요", Toast.LENGTH_SHORT).show();
            ((PatherActivity) context).deleteWaitingTasks();
            ((PatherActivity) context).finish();
        } else if (result.get("message").toString().equals("nomore")) {
            Toast.makeText(context, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
            //((PatherActivity) context).finish();
        } else if (result.get("message").toString().equals("invalid")) {
            Toast.makeText(context, "현재 서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
            ((PatherActivity) context).finish();
        } else if (result.get("message").toString().equals("exceed")){
            Toast.makeText(context, "현재 랭크에서 할 수 있는 테스크의 수를 초과했습니다.", Toast.LENGTH_SHORT).show();
            ((PatherActivity) context).finish();
        } else if (result.get("message").toString().equals("needtest")){
            Toast.makeText(context, "먼저 테스트를 통과하셔야 진행할 수 있습니다.", Toast.LENGTH_SHORT).show();
        } else if(result.get("message").toString().equals("INVALID_ALL_ANSWER_STATUS")){
            ((PatherActivity) context).deleteWaitingTasks();
            ((PatherActivity) context).finish();
        } else{
            Toast.makeText(context, "false : " + result.get("message").toString(), Toast.LENGTH_SHORT).show();
            ((PatherActivity) context).finish();
        }
    }

    public void examSubmitFailParse(Context context, JSONObject result) throws JSONException{
        if (result.get("message").toString().equals("login")) {
            Intent in = new Intent(context, LoginActivity.class);
            context.startActivity(in);
            Toast.makeText(context, "로그인이 만료되었습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
        } else if (result.get("message").toString().equals("expired")) {
            Toast.makeText(context, "테스크가 만료되었습니다. 다른 테스크를 선택해주세요", Toast.LENGTH_SHORT).show();
            ((PatherActivity) context).deleteWaitingTasks();
        } else if (result.get("message").toString().equals("nomore")) {
            Toast.makeText(context, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
        } else if (result.get("message").toString().equals("invalid")) {
            Toast.makeText(context, "현재 서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
        } else if(result.get("message").toString().equals("INVALID_ALL_ANSWER_STATUS")){
            ((PatherActivity) context).deleteWaitingTasks();
        } else {
            Toast.makeText(context, "false : " + result.get("message").toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
