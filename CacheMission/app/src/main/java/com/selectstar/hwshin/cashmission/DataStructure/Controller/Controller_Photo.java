package com.selectstar.hwshin.cashmission.DataStructure.Controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.selectstar.hwshin.cashmission.Adapter.CircularPagerAdapter;
import com.selectstar.hwshin.cashmission.DataStructure.ControllerBundle.ControllerBundle_GPS;
import com.selectstar.hwshin.cashmission.R;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class Controller_Photo extends Controller {

    String imageFilePath;
    Uri photoUri;
int serverResponseCode;

    private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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
        imageFilePath = image.getAbsolutePath();
        SharedPreferences imagefilepath = parentActivity.getSharedPreferences("imagefilepath", MODE_PRIVATE);
        SharedPreferences.Editor editor = imagefilepath.edit();
        editor.putString("imagefilepath", imageFilePath);
        editor.commit();

        return image;
    }
    public Controller_Photo() {
        controllerID = R.layout.controller_photo;
    }
    @Override
    public void setLayout(final String id, View view, Context c, String tasktype, Intent in, String buttons) {
        SharedPreferences token = parentActivity.getSharedPreferences("token", MODE_PRIVATE);


        final String logintoken = token.getString("logintoken",null);

        ConstraintLayout templayout = (ConstraintLayout) view;
        Button phototake=templayout.findViewById(R.id.phototake);
        Button photosend=templayout.findViewById(R.id.postphoto);
        Log.d("dddggg",String.valueOf(mtaskview.gettaskID()));
        phototake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takepictureintent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takepictureintent.resolveActivity(parentActivity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }

                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(parentActivity, "com.selectstar.hwshin.cashmission", photoFile);
                        SharedPreferences photouri = parentActivity.getSharedPreferences("photouri", MODE_PRIVATE);
                        SharedPreferences.Editor editor = photouri.edit();
                        editor.putString("photouri", photoUri.toString());
                        editor.commit();
                        takepictureintent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        parentActivity.startActivityForResult(takepictureintent, 1);
                    }
                }

            }
        });
        photosend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread() {
                    public void run() {
                        SharedPreferences imagefilepath = parentActivity.getSharedPreferences("imagefilepath", MODE_PRIVATE);
                        String stringtoken;
                        stringtoken = imagefilepath.getString("imagefilepath", null);
                        if (stringtoken == null) {
                            stringtoken = "";
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile(stringtoken);
                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(stringtoken);


                            int exifOrientation;
                            int exifDegree;

                            if (exif != null) {
                                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                exifDegree = exifOrientationToDegrees(exifOrientation);
                            } else {
                                exifDegree = 0;
                            }
                            bitmap = rotate(bitmap, exifDegree);
                            String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

                            String mPath = sd + "/image_"+System.currentTimeMillis();
                            SaveBitmapToFileCache(bitmap,mPath+".jpg");

                            uploadFile(mPath+".jpg",id);

/*
                            String attachmentName = "bitmap";
                            String attachmentFileName = "bitmap.bmp";
                            String crlf = "\r\n";
                            String twoHyphens = "--";
                            String boundary = "*****";

                            HttpURLConnection httpUrlConnection = null;
                            URL url = new URL(parentActivity.getString(R.string.mainurl) + "/taskSubmit");
                            httpUrlConnection = (HttpURLConnection) url.openConnection();
                            httpUrlConnection.setUseCaches(false);
                            httpUrlConnection.setDoOutput(true);

                            httpUrlConnection.setRequestMethod("POST");
                            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                            httpUrlConnection.setRequestProperty(
                                    "Content-Type", "multipart/form-data;boundary=" + boundary);
                            httpUrlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                            httpUrlConnection.setRequestProperty("uploaded_file", photoUri.toString());
                            httpUrlConnection.setRequestProperty("TOKEN",logintoken);
                            httpUrlConnection.setRequestProperty("ANSWERID",String.valueOf(mtaskview.gettaskID()));
                            httpUrlConnection.setRequestProperty("TASKID",id);
                            DataOutputStream request = new DataOutputStream(
                                    httpUrlConnection.getOutputStream());

                            request.writeBytes(twoHyphens + boundary + crlf);
                            request.writeBytes("Content-Disposition: form-data; name=\"" +
                                    attachmentName + "\";filename=\"" +
                                    attachmentFileName + "\"" + crlf);
                            request.writeBytes(crlf);

                            //I want to send only 8 bit black & white bitmaps
                            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
                            for (int i = 0; i < bitmap.getWidth(); ++i) {
                                for (int j = 0; j < bitmap.getHeight(); ++j) {
                                    //we're interested only in the MSB of the first byte,
                                    //since the other 3 bytes are identical for B&W images
                                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                                }
                            }

                            request.write(pixels);

                            request.writeBytes(crlf);
                            request.writeBytes(twoHyphens + boundary +
                                    twoHyphens + crlf);

                            request.flush();
                            request.close();

                            InputStream responseStream = new
                                    BufferedInputStream(httpUrlConnection.getInputStream());
                            serverResponseCode = httpUrlConnection.getResponseCode();
                            BufferedReader responseStreamReader =
                                    new BufferedReader(new InputStreamReader(responseStream));

                            String line = "";
                            StringBuilder stringBuilder = new StringBuilder();

                            while ((line = responseStreamReader.readLine()) != null) {
                                stringBuilder.append(line).append("\n");
                            }
                            responseStreamReader.close();

                            String response = stringBuilder.toString();

                            responseStream.close();
                            httpUrlConnection.disconnect();
*/

                        } catch (
                                IOException e)

                        {
                            e.printStackTrace();
                        }

                        if (serverResponseCode == 200)

                        {

                            parentIntent.putExtra("from", 1);
                            parentActivity.startActivity(parentIntent);
                            parentActivity.finish();
                        } else

                        {
                            //Toast.makeText(parentActivity, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
                            parentActivity.finish();
                        }

                    }
                }.start();

            }
        });

    }
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public int uploadFile(String sourceFileUri, String id) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        SharedPreferences token = parentActivity.getSharedPreferences("token",Context.MODE_PRIVATE);
        final String logintoken = token.getString("logintoken",null);
        SharedPreferences iddd = parentActivity.getSharedPreferences("iddd", MODE_PRIVATE);
        final  String idd=iddd.getString("iddd",null);
        Log.d("bbbbbb",idd);
        if (!sourceFile.isFile()) {



            //Log.e("uploadFile", "Source File not exist :" +uploadFilePath + "" + uploadFileName);


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
                conn.setRequestProperty("TOKEN",logintoken);
                conn.setRequestProperty("ANSWERID",idd);
                conn.setRequestProperty("TASKID",id);

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

                    usingactivity.runOnUiThread(new Runnable() {
                        public void run() {

                            // String msg = "File Upload Completed.\n\n See uploaded file here : \n\n" +" http://www.androidexample.com/media/uploads/" +uploadFileName;

                            // messageText.setText(msg);
                            //Toast.makeText(RecActivity.this, "File Upload Complete.",
                            //    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {


                ex.printStackTrace();

                usingactivity.runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("MalformedURLException Exception : check script url.");

                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {


                e.printStackTrace();

                usingactivity.runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");

                    }
                });
                Log.e("Upload file ", "Exception : "
                        + e.getMessage(), e);
            }


            return serverResponseCode;

        } // End else block
    }
}
