package com.selectstar.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class FileHttpRequest extends AsyncTask {

    public String myResult;
    PatherActivity parentActivity;
    public MyProgressDialog httpDialog;
    public MyProgressDialog httpDialogSomethingOptimizationFailed;
    public void setMyResult(String string)
    {
        myResult = string;
    }
    public FileHttpRequest(PatherActivity parentActivity)
    {
        this.parentActivity = parentActivity;
    }
    @Override
    protected void onPreExecute() {
        httpDialogSomethingOptimizationFailed = httpDialog.show(parentActivity,"","",true,true,null);

        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Uri uri = (Uri)objects[0];


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String fileName = uri.toString();
        File sourceFile = new File(fileName);
        if(!sourceFile.isFile()) {
            myResult = "ERROR";
            return 0;
        }
        try {

            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(parentActivity.getString(R.string.mainurl)+"/taskSubmit");

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            conn.setRequestProperty("TOKEN",parentActivity.getLoginToken());
            conn.setRequestProperty("ANSWERID",parentActivity.getAnswerID());
            conn.setRequestProperty("TASKID",parentActivity.getTaskID());

            // conn.setRequestProperty("GPSX",GPSX);
            //   conn.setRequestProperty("GPSY",GPSY);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name =\""+ "uploaded_file"+"\";filename=\"" + sourceFile.getName() + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            myResult = convertInputStreamToString(conn.getInputStream());

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {


            ex.printStackTrace();


            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {


            e.printStackTrace();


            Log.e("Upload file ", "Exception : "
                    + e.getMessage(), e);
        }

        return null;
    }



    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        // while ((line = bufferedReader.readLine()) != null)
        line = bufferedReader.readLine();
        result += line;
        return result;

    }
}
