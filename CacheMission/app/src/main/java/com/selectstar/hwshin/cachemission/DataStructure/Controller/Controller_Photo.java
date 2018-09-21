package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.GalleryActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.Adapter.PhotoPagerAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cachemission.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class Controller_Photo extends Controller {
    PhotoPagerAdapter adapter;
    Dialog dialog;
    int serverResponseCode=0;

    public Controller_Photo(){
        controllerID= R.layout.controller_photo;
    }
    @Override
    public void resetContent(View view, String taskID) {
        adapter.stackClear();
    }


    public void addPhoto(Uri bitmap)
    {
        adapter.addPhoto(bitmap);
    }
    @Override
    public void setLayout(View view, String taskID) {
        Button photoTake = parentActivity.findViewById(R.id.phototake);
        ViewPager viewPager = parentActivity.findViewById(R.id.srcview);
        adapter = new PhotoPagerAdapter(parentActivity);
        viewPager.setAdapter(adapter);
        photoTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    System.out.println("ERROR!");
                }
                System.out.println(photoFile);
                System.out.println("----");
                System.out.println(parentActivity.getPackageName());
                photoUri = FileProvider.getUriForFile(parentActivity, parentActivity.getPackageName(), photoFile);
                System.out.println(photoUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                parentActivity.startActivityForResult(intent,1);

            }
        });
        Button gallery =parentActivity.findViewById(R.id.GPS);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(parentActivity, GalleryActivity.class);
                parentActivity.startActivityForResult(in,999);
            }
        });
        Button photoSend = parentActivity.findViewById(R.id.postphoto);
        photoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int allCount = adapter.getCount();
                if (allCount == 0) {
                    Toast.makeText(parentActivity, "사진을 올려주세요.", Toast.LENGTH_SHORT).show();
                }
                new Thread() {
                    public void run() {
                        while (!adapter.isEmpty()) {
                            Uri uri = adapter.pop();

                            Log.d("dope", uri.toString());
                            String answerID = parentActivity.getAnswerID();
                            String taskID = parentActivity.getTaskID();
                            uploadFileToServer(uri, answerID, taskID);
                        }
                        if (serverResponseCode == 200) {
                            ((TaskActivity) parentActivity).startTask();
                        }

                    }
                }.start();
                adapter.notifyDataSetChanged();

            }
        });
    }

    public int uploadFileToServer(Uri uri, String answerID, String taskID)
    {
        String fileName = uri.toString();

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);


        if (!sourceFile.isFile()) {

            return 0;
        }
        else
        {
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
                conn.setRequestProperty("ANSWERID",answerID);
                conn.setRequestProperty("TASKID",taskID);

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
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){


                }

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


            return serverResponseCode;

        } // End else block
    }
    public void deleteImage()
    {

    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        //imageFilePath = image.getAbsolutePath();
        return image;
    }

}