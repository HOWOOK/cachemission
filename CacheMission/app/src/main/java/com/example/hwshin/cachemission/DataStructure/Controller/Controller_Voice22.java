package com.example.hwshin.cachemission.DataStructure.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hwshin.cachemission.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Controller_Voice22 extends Controller {
    boolean isRecording=false;
    boolean isPlaying = false;
    boolean isPausing = false;
    MediaRecorder recorder=new MediaRecorder();
    MediaPlayer mPlayer = new MediaPlayer();
    String oldpath="init";
    String path;
    int serverResponseCode = 0;
    public Controller_Voice22() {
        controllerID = R.layout.controller_voice;
    }


    public void startRec(){

        try {

            File file= Environment.getExternalStorageDirectory();
//갤럭시 S4기준으로 /storage/emulated/0/의 경로를 갖고 시작한다.
            if(oldpath!="init"){
                Uri i= Uri.parse(oldpath);
                File f=new File(i.getPath());
                f.delete();
            }
            path=file.getAbsolutePath()+"/"+"recoder_"+System.currentTimeMillis()+".wav";
            oldpath=path;
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setAudioSamplingRate(44100);
            //recorder.setAudioEncodingBitRate(1);
//첫번째로 어떤 것으로 녹음할것인가를 설정한다. 마이크로 녹음을 할것이기에 MIC로 설정한다.
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//이것은 파일타입을 설정한다. 녹음파일의경우 3gp로해야 용량도 작고 효율적인 녹음기를 개발할 수있다.
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//이것은 코덱을 설정하는 것이라고 생각하면된다.
            recorder.setOutputFile(path);
//저장될 파일을 저장한뒤
            recorder.prepare();
            recorder.start();
//시작하면된다.

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopRec(){
        try{
            Log.d("STOPREC","STOPPED");
            recorder.stop();
        }catch(RuntimeException e){
            e.printStackTrace();
        }
//멈추는 것이다.

    }
    @Override
    public void setLayout(final String id, View view, Context c, Intent in, String buttons) {



        final Context c_ = c;
        ConstraintLayout templayout = (ConstraintLayout) view;
        Button post=templayout.findViewById(R.id.post);
        final ImageView recb =templayout.findViewById(R.id.recb);
        final ImageView mBtPlay = templayout.findViewById(R.id.stream);



        recb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording==false){
                    startRec();
                    //recorder.start();
                    isRecording=true;
                    recb.setImageDrawable(ContextCompat.getDrawable(c_, R.drawable.recordstopbtn));
                    //recb.setText("녹음그만");
                }
                else{
                    stopRec();
                    isRecording=false;
                    recb.setImageDrawable(ContextCompat.getDrawable(c_, R.drawable.recordstartbtn));
                    //recb.setText("다시녹음하기");
                    }

            }
        });

        mBtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying == false) {
                    try {
                        mPlayer.setDataSource(path);
                        mPlayer.prepare();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.start();

                    isPlaying = true;
                    mBtPlay.setImageDrawable(ContextCompat.getDrawable(c_, R.drawable.recordpausebtn));
                    //mBtPlay.setText("듣기중지");
                }
                else {
                    mPlayer.reset();

                    isPlaying = false;
                    mBtPlay.setImageDrawable(ContextCompat.getDrawable(c_, R.drawable.recordplaybtn));
                    //mBtPlay.setText("들어보기");
                }
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(){
                    public void run() {
                        uploadFile(path,id);
                        Log.d("serverres",String.valueOf(serverResponseCode));
                        if(serverResponseCode==200){

                            parentActivity.startActivity(parentIntent);
                            parentActivity.finish();
                        }
                        else{
                            Toast.makeText(parentActivity,"남은 테스크가 없습니다.",Toast.LENGTH_SHORT).show();
                            parentActivity.finish();
                        }
                    }
                }.start();



            }
        });


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.reset();
                isPlaying = false;
                mBtPlay.setImageDrawable(ContextCompat.getDrawable(c_, R.drawable.recordplaybtn));
                //mBtPlay.setText("들어보기");
            }
        });



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

        if (!sourceFile.isFile()) {



            //Log.e("uploadFile", "Source File not exist :" +uploadFilePath + "" + uploadFileName);


            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://18.222.204.84/voiceSubmit");

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
                conn.setRequestProperty("BASEID",String.valueOf(mtaskview.gettaskID()));
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
