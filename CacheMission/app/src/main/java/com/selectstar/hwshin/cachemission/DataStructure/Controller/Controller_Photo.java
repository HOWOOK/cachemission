package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.GalleryActivity;
import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.Adapter.PhotoPagerAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.FileHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.MyProgressDialog;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller_Photo extends Controller {
    PhotoPagerAdapter adapter;
    Dialog dialog;
    int serverResponseCode=0;
    int successCount = 0;
    int allCount = 0;
    RecyclerView recyclerView;
    public Controller_Photo(){
        controllerID= R.layout.controller_photo;
    }
    @Override
    public void resetContent(View view, String taskID) {
        adapter.clearItem();
    }


    public void addPhoto(Uri bitmap)
    {
        adapter.addPhoto(bitmap);
    }
    @Override
    public void setLayout(final View view, String taskID) {
        ImageView cameraButton = parentActivity.findViewById(R.id.camera);
        recyclerView = parentActivity.findViewById(R.id.srcview);
        adapter = new PhotoPagerAdapter(parentActivity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        cameraButton.setOnClickListener(new View.OnClickListener() {
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
                photoUri = FileProvider.getUriForFile(parentActivity, parentActivity.getPackageName(), photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                parentActivity.startActivityForResult(intent,1);

            }
        });
        ImageView galleryButton =parentActivity.findViewById(R.id.gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(parentActivity, GalleryActivity.class);
                parentActivity.startActivityForResult(in,999);
            }
        });
        Button submitButton = parentActivity.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allCount = adapter.getItemCount();
                if (allCount == 0) {
                    Toast.makeText(parentActivity, "사진을 올려주세요.", Toast.LENGTH_SHORT).show();
                }
                successCount = 0;
                for(int i=0;i<allCount;i++)
                {
                    Uri uri = adapter.getUri(i);
                    new FileHttpRequest(parentActivity) {

                        @Override
                        protected void onPostExecute(Object o) {
                            try{
                                JSONObject resultTemp = new JSONObject(myResult);
                                System.out.println(myResult);
                                if ((boolean) resultTemp.get("success")) {
                                    System.out.println(myResult);
                                    successCount += 1;
                                    String sentence = String.valueOf(successCount) + " / " + String.valueOf(allCount) + " 전송 완료!";
                                    Toast.makeText(parentActivity,sentence,Toast.LENGTH_SHORT);
                                    if(allCount == successCount) {
                                        adapter.clearItem();
                                        adapter.notifyDataSetChanged();
                                    }
                                    parentActivity.showAnimation(R.drawable.coin_animation_list,parentActivity.getUpGold());
                                    parentActivity.startTask();
                                    parentActivity.setGold(String.valueOf(resultTemp.get("gold")));
                                    parentActivity.setMaybe(String.valueOf(resultTemp.get("maybe")));


                                }else
                                {
                                    System.out.println(myResult);
                                    if (resultTemp.get("message").toString().equals("login")) {
                                        Intent in = new Intent(parentActivity, LoginActivity.class);
                                        parentActivity.startActivity(in);
                                        Toast.makeText(parentActivity, "로그인이 만료되었습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();
                                    } else if (resultTemp.get("message").toString().equals("task")) {

                                        Toast.makeText(parentActivity, "테스크가 만료되었습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();
                                    }
                                    else{
                                        Toast.makeText(parentActivity,"남은 테스크가 없습니다.",Toast.LENGTH_SHORT).show();
                                        parentActivity.finish();
                                    }

                                }

                            }
                            catch(JSONException e)
                            {
                                Toast.makeText(parentActivity,"예기치 못한 에러가 발생했습니다.(ERROR CODE: 501)", Toast.LENGTH_SHORT).show();
                                parentActivity.finish();
                            }
                            if (httpDialog!=null)
                                httpDialog.dismiss();
                            httpDialogSomethingOptimizationFailed.dismiss();
                        }
                    }.execute(uri);
                }


            }
        });
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