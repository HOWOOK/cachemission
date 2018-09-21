package com.selectstar.hwshin.cachemission.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Adapter.CustomImageAdapter;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    public String basePath = null;
    public GridView mGridView;
    public CustomImageAdapter mCustomImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
/*
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
            }
        }
*/
        basePath = mediaStorageDir.getPath();

        mGridView = (GridView)findViewById(R.id.gallerygridview); // .xml의 GridView와 연결
        mCustomImageAdapter = new CustomImageAdapter(this, basePath,mGridView); // 앞에서 정의한 Custom Image Adapter와 연결
        Log.d("dhodho",basePath);
        mGridView.setAdapter(mCustomImageAdapter); // GridView가 Custom Image Adapter에서 받은 값을 뿌릴 수 있도록 연결

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), mCustomImageAdapter.getItemPath(position), Toast.LENGTH_LONG).show();
            }
        });
        ImageView im=findViewById(R.id.picselect);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result=new Intent();
                ArrayList<String> res=new ArrayList<>();
                for(int i=0; i<mCustomImageAdapter.getSelected().size();i++){
                    res.add( mCustomImageAdapter.getItemPath(mCustomImageAdapter.getSelected().get(i)));

                }


                result.putStringArrayListExtra("pic",res);
                setResult(RESULT_OK,result);
                finish();


            }
        });
    }

}
