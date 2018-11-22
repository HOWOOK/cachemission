package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.GalleryActivity;
import com.selectstar.hwshin.cachemission.Adapter.PhotoPagerAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.FileHttpRequest;
import com.selectstar.hwshin.cachemission.DataStructure.ServerMessageParser;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Controller_Photo extends Controller {
    PhotoPagerAdapter adapter;
    Dialog dialog;
    int serverResponseCode=0;
    int successCount = 0;
    int allCount = 0;
    RecyclerView recyclerView;
    final int P_RECORD_AUDIO=77;
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
                if(parentActivity.getAvailableCount() <= adapter.getItemCount())
                {
                    Toast.makeText(parentActivity,"더 이상 사진을 추가할 수 없습니다!",Toast.LENGTH_SHORT).show();
                    return;
                }
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
                if ((ActivityCompat.checkSelfPermission(parentActivity, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(parentActivity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(parentActivity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(parentActivity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {


                    ActivityCompat.requestPermissions(parentActivity, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                            P_RECORD_AUDIO);

                } else {

                    if (parentActivity.getAvailableCount() <= adapter.getItemCount()) {
                        Toast.makeText(parentActivity, "더 이상 사진을 추가할 수 없습니다!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent in = new Intent(parentActivity, GalleryActivity.class);

                    in.putExtra("avail", parentActivity.getAvailableCount() - adapter.getItemCount());
                    parentActivity.startActivityForResult(in, 999);
                }
            }
        });
        Button submitButton = parentActivity.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allCount = adapter.getItemCount();
                if (allCount == 0) {
                    Toast.makeText(parentActivity, "사진을 올려주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(allCount > parentActivity.getAvailableCount())
                {
                    int x = allCount - parentActivity.getAvailableCount();
                    Toast.makeText(parentActivity, "최대 10장까지만 가능합니다. " + String.valueOf(x) + "개를 빼주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                successCount = 0;
                for(int i=0;i<allCount;i++)
                {
                    Uri uri = adapter.getUri(i);
                    Log.d("lflflflflf",String.valueOf(getEXIFWidth(uri)));
                    Log.d("lflflflflffff",String.valueOf(getEXIFLength(uri)));
                    Log.d("asdadsas",String.valueOf(getBitmapWidth(uri)));
//                    if(getEXIFWidth(uri)<1920||getEXIFLength(uri)<1080){
//                        getDialog("사진 해상도 낮음",String.valueOf(i+1)+"번째 사진의 해상도가 1920*1080 미만입니다. 해상도가 1920*1080 이상인 사진만 업로드할 수 있습니다.");
//                        adapter.dropPhoto(i+1);
//                        break;
//                    }
                    if(!adapter.resolution.get(i)){
                        getDialog("사진 해상도 낮음",String.valueOf(i+1)+"번째 사진의 해상도가 1920*1080 미만입니다. 해상도가 1920*1080 이상인 사진만 업로드할 수 있습니다.");
                        adapter.dropPhoto(i+1);
                        break;
                    }

                    System.out.println(uri.toString());
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
                                    Toast.makeText(parentActivity,sentence,Toast.LENGTH_SHORT).show();
                                    if(allCount == successCount) {
                                        adapter.clearItem();
                                        adapter.notifyDataSetChanged();
                                        parentActivity.startTask();
                                        parentActivity.setQuestList(String.valueOf(resultTemp.get("questList")));
                                    }
                                    parentActivity.goldSetting(String.valueOf(resultTemp.get("gold")));
                                    parentActivity.maybeSetting(String.valueOf(resultTemp.get("maybe")));

                                }else{
                                    new ServerMessageParser().taskSubmitFailParse(parentActivity,resultTemp);

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
                    }.execute(uri,parentActivity.getLoginToken());
                }


            }
        });
    }
    private void getDialog(String title, String value)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(value);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
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
    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {

            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getPathFromUri(Uri uri){

        Cursor cursor = parentActivity.getContentResolver().query(uri, null, null, null, null );

        cursor.moveToNext();

        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );

        cursor.close();



        return path;

    }


    private int getEXIFWidth(Uri uri){
            int result=0;
        String path =getRealPathFromURI(parentActivity,uri);
        InputStream inputStream=null;
        ExifInterface exif = null;
        int orientation=0;
        try {

            if(uri.toString().substring(0,7).equals("content"))
            {
                inputStream = parentActivity.getContentResolver().openInputStream(uri);
            }
            else
            {
                inputStream =  new FileInputStream(uri.toString());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif = new ExifInterface(inputStream);
                result=Integer.parseInt(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return result;



    }
    private int getEXIFLength(Uri uri){
        int result=0;
        String path =getRealPathFromURI(parentActivity,uri);
        InputStream inputStream=null;
        ExifInterface exif = null;
        int orientation=0;
        try {

            if(uri.toString().substring(0,7).equals("content"))
            {
                inputStream = parentActivity.getContentResolver().openInputStream(uri);
            }
            else
            {
                inputStream =  new FileInputStream(uri.toString());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif = new ExifInterface(inputStream);
                result=Integer.parseInt(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));

            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return result;



    }
    private int getBitmapWidth(Uri uri){
        try {
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds =true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;


//            String path = getRealPathFromURI(parentActivity,uri);
//            String filename = path.substring(path.lastIndexOf("/")+1);
//            String fileWOExtension;
//            if (filename.indexOf(".") > 0) {
//                fileWOExtension = filename.substring(0, filename.lastIndexOf("."));
//            } else {
//                fileWOExtension =  filename;
//            }
            String path =getPathFromUri(uri); // "/mnt/sdcard/FileName.mp3"

            Bitmap b = BitmapFactory.decodeFile(path,options);
            Log.d("jdjdjdjdjdj",path);
            Bitmap bm = MediaStore.Images.Media.getBitmap(parentActivity.getContentResolver(), uri);
            return bm.getHeight();

        }catch (Exception e){
            return 0;

        }

    }

}