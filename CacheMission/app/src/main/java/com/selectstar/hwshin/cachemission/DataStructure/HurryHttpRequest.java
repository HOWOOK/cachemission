package com.selectstar.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HurryHttpRequest extends AsyncTask{
    Context parent = null;
    String img_binary = null;
    public String result = "";

    private Context mContext;

    public HurryHttpRequest(Context context){
        mContext= context;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String url;
        InputStream is = null;
        //inputStream  = 바이트 단위로 데이터를 읽는다. 외부로부터 읽어 들이는기능관련 클래스들
        //outputStream = 외부로 데이터를 전송합니다. 외부로 데이터를 전송하는 기능 관련 클래스들
        try {
            String token="";
            URL urlCon = new URL(objects[0].toString());
            HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();
            if(objects.length == 3)
            {
                token = objects[2].toString();
            }
            //서버 response data를 json 형식의 타입으로 요청
            //httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            httpCon.setRequestProperty("X-CSRF-Token","Fetch");
            httpCon.setRequestProperty("Content-Type", "application/xml");
            httpCon.setRequestProperty("TOKEN", token);//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MzQxODU4MjEsImVtYWlsIjoiIiwidXNlcl9pZCI6MSwidXNlcm5hbWUiOiJhc2RmIn0.oGIBuWPo2qw0wciDqGqo3MCoiGNZFP7zX5lqpU3xPyM");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);
            String json = objects[1].toString();
            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("utf-8"));
            os.flush();

            if(httpCon.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if (is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Something is wrong";

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                httpCon.disconnect();

            }

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

            System.out.println("InputStream" + e.getLocalizedMessage());

        }
        return result;
    }


    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        // while ((line = bufferedReader.readLine()) != null)
        line = bufferedReader.readLine();
        result += line;
        inputStream.close();
        return result;

    }



}
