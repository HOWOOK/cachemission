package com.example.hwshin.cachemission.DataStructure.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hwshin.cachemission.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Controller_Voice extends Controller {

    public Controller_Voice() {
        controllerID = R.layout.controller_voice;
    }
    private final int mBufferSize = 1024;
    private final int mBytesPerElement = 2;

// 설정할 수 있는 sampleRate, AudioFormat, channelConfig 값들을 정의

    private final int[] mSampleRates = new int[] {44100, 22050, 11025, 8000};
    private final short[] mAudioFormats = new short[] {AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    private final short[] mChannelConfigs = new short[] {AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO};

// 위의 값들 중 실제 녹음 및 재생 시 선택된 설정값들을 저장

    private int mSampleRate;
    private short mAudioFormat;
    private short mChannelConfig;

    private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;
    private Button mRecordBtn, mPlayBtn;
    private boolean mIsRecording = false;           // 녹음 중인지에 대한 상태값
    private String mPath = "";                      // 녹음한 파일을 저장할 경로

    private boolean isPlaying = false;
    MediaPlayer mPlayer = new MediaPlayer();
    private String oldpath="init";
    int serverResponseCode = 0;

    private boolean isrecordstarted=false;

    @Override
    public void setLayout(final String id, View view, Context c, Intent in, String buttons) {
        ConstraintLayout temp=(ConstraintLayout)view;
        mRecordBtn = (Button)temp.findViewById(R.id.recb);
        mPlayBtn = (Button)temp.findViewById(R.id.stream);
        mRecordBtn.setOnClickListener(btnClick);
        mPlayBtn.setOnClickListener(btnClick);
        Button post=temp.findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isrecordstarted == false) {
                    Toast.makeText(parentActivity,"먼저 음성을 녹음해 주세요",Toast.LENGTH_SHORT).show();

                } else {
                    File f1 = new File(mPath + ".pcm"); // The location of your PCM file
                    File f2 = new File(mPath + ".wav"); // The location where you want your WAV file
                    try {
                        rawToWave(f1, f2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    new Thread() {
                        public void run() {
                            uploadFile(mPath + ".wav", id);
                            Log.d("serverres", String.valueOf(serverResponseCode));
                            Log.d("serverres", mPath + ".wav");
                            Uri i = Uri.parse(mPath + ".pcm");
                            File f = new File(i.getPath());
                            //f.delete();
                            Uri i2 = Uri.parse(mPath + ".wav");
                            File f2 = new File(i2.getPath());
                            //f2.delete();
                            if (serverResponseCode == 200) {

                                parentActivity.startActivity(parentIntent);
                                parentActivity.finish();
                            } else {
                                //Toast.makeText(parentActivity,"남은 테스크가 없습니다.",Toast.LENGTH_SHORT).show();
                                parentActivity.finish();
                            }
                        }
                    }.start();


                }
            }
        });
    }


// 녹음을 수행할 Thread를 생성하여 녹음을 수행하는 함수

    private void startRecording() {

        mRecorder = findAudioRecord();
        mRecorder.startRecording();
        mIsRecording = true;
        mRecordingThread = new Thread(new Runnable() {



            @Override

            public void run() {
                writeAudioDataToFile();
            }

        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }



// 녹음을 하기 위한 sampleRate, audioFormat, channelConfig 값들을 설정


    private AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short format : mAudioFormats) {
                for (short channel : mChannelConfigs) {
                    try {

                        int bufferSize = AudioRecord.getMinBufferSize(rate, channel, format);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {

                            mSampleRate = rate;
                            mAudioFormat = format;
                            mChannelConfig = channel;

                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, mChannelConfig, mAudioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {

                                return recorder;    // 적당한 설정값들로 생성된 Recorder 반환

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;                     // 적당한 설정값들을 찾지 못한 경우 Recorder를 찾지 못하고 null 반환

    }



// 실제 녹음한 data를 file에 쓰는 함수

    private void writeAudioDataToFile() {

        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

        mPath = sd + "/voice2_"+System.currentTimeMillis();
        if(oldpath!="init"){
            Uri i= Uri.parse(oldpath);
            File f=new File(i.getPath());
            f.delete();
        }
        oldpath=mPath+".pcm";

        short sData[] = new short[mBufferSize];
        FileOutputStream fos = null;



        try {

            fos = new FileOutputStream(mPath+".pcm");

            while (mIsRecording) {

                mRecorder.read(sData, 0, mBufferSize);
                byte bData[] = short2byte(sData);
                fos.write(bData, 0, mBufferSize * mBytesPerElement);

            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



// short array형태의 data를 byte array형태로 변환하여 반환하는 함수
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {

            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;

        }
        return bytes;

    }



// 녹음을 중지하는 함수

    private void stopRecording() {

        if (mRecorder != null) {
            mIsRecording = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordingThread = null;
        }
    }



// 녹음할 때 설정했던 값과 동일한 설정값들로 해당 파일을 재생하는 함수


    private void playWaveFile() {

        int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, mSampleRate, mChannelConfig, mAudioFormat, minBufferSize, AudioTrack.MODE_STREAM);
        int count = 0;
        byte[] data = new byte[mBufferSize];

        try {

            FileInputStream fis = new FileInputStream(mPath+".pcm");
            DataInputStream dis = new DataInputStream(fis);
            audioTrack.play();

            while ((count = dis.read(data, 0, mBufferSize)) > -1) {

                audioTrack.write(data, 0, count);

            }

            audioTrack.stop();
            audioTrack.release();
            dis.close();
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private View.OnClickListener btnClick = new View.OnClickListener() {

        @Override

        public void onClick(View v) {
            isrecordstarted=true;
            switch (v.getId()) {

                // 녹음 버튼일 경우 녹음 중이지 않을 때는 녹음 시작, 녹음 중일 때는 녹음 중지로 이미지 변경
                case R.id.recb:

                    if (mIsRecording == false) {
                        startRecording();
                        mIsRecording = true;
                        mRecordBtn.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.recordstop));
                    } else {
                        stopRecording();
                        mIsRecording = false;
                        mRecordBtn.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.recordstart));

                    }
                    break;

                case R.id.stream:
                    // 녹음 파일이 없는 상태에서 재생 버튼 클릭 시, 우선 녹음부터 하도록 Toast 표시
                    if ((mPath+".pcm").length() == 0 || mIsRecording) {
                        Toast.makeText(parentActivity, "Please record, first.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 녹음된 파일이 있는 경우 해당 파일 재생
                    playWaveFile();
                    break;
            }
        }

    };

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 2); // number of channels
            writeInt(output, 44100); // sample rate
            writeInt(output, mSampleRate *2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }
    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
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
                URL url = new URL("http://18.222.204.84/taskSubmit");

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
                conn.setRequestProperty("ANSWERID",String.valueOf(mtaskview.gettaskID()));
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
