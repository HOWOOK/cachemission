package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Adapter.PhotoPagerAdapter;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller_Photo extends Controller {
    PhotoPagerAdapter adapter;
    Dialog dialog;
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
                Uri photoUri = FileProvider.getUriForFile(parentActivity, parentActivity.getPackageName(), photoFile);
                System.out.println(photoUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                parentActivity.startActivityForResult(intent,1);

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
                while(!adapter.isEmpty()) {
                    Uri uri = adapter.pop();
                    String answerID = parentActivity.getAnswerID();
                    String taskID = parentActivity.getTaskID();
                    uploadFileToServer(uri,answerID,taskID);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    public void uploadFileToServer(Uri uri, String answerID, String taskID)
    {

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
