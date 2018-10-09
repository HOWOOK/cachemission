package com.selectstar.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
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
import java.net.URI;
import java.net.URISyntaxException;
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
        InputStream inputStream;
        try {
            String token="";
            if(objects.length == 3)
            {
                token = objects[2].toString();
            }

            System.out.println(fileName);
            if(fileName.substring(0,7).equals("content"))
            {
                inputStream = parentActivity.getContentResolver().openInputStream(uri);
            }
            else
            {
                inputStream =  new FileInputStream(fileName);
            }
            // open a URL connection to the Servlet
            URL url = new URL(parentActivity.getString(R.string.mainurl)+"/fileSubmit");

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
            conn.setRequestProperty("Token", "jwt " + token);
            //conn.setRequestProperty("Authorization","jwt " + parentActivity.getLoginToken());
            conn.setRequestProperty("ANSWERID",parentActivity.getAnswerID());
            conn.setRequestProperty("TASKID",parentActivity.getTaskID());

            // conn.setRequestProperty("GPSX",GPSX);
            //   conn.setRequestProperty("GPSY",GPSY);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name =\""+ "uploaded_file"+"\";filename=\"" + fileName + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = inputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = inputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);

                bytesAvailable = inputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = inputStream.read(buffer, 0, bufferSize);

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
            inputStream.close();
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

    private String getFileUrifromContentUri(Uri _uri)
    {
        String filePath = null;
        if (_uri != null && "content".equals(_uri.getScheme())) {
            Cursor cursor = parentActivity.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = _uri.getPath();
        }
        return filePath;
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
