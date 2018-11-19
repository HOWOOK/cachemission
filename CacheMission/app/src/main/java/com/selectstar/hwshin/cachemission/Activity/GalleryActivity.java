package com.selectstar.hwshin.cachemission.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.Adapter.ChoiceImageAdapter;
import com.selectstar.hwshin.cachemission.Adapter.GalleryImageAdapter;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class GalleryActivity extends AppCompatActivity {

    public String basePath = null;
    //public GridView mGridView;
    public RecyclerView mView;
    public RecyclerView mChoiceView;
    public GalleryImageAdapter mAdapter;
    public ChoiceImageAdapter mChoiceAdapter;
    public int availNumber;
    final int P_RECORD_AUDIO=77;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("GalleryActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        Intent intent = getIntent();
        availNumber = intent.getIntExtra("avail",0);
        setContentView(R.layout.activity_gallery);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM),"Camera");
        basePath = mediaStorageDir.getPath();
        mView = findViewById(R.id.gallerygridview);
        mChoiceAdapter = new ChoiceImageAdapter(mView);

        mAdapter = new GalleryImageAdapter(basePath,this,mChoiceAdapter);
        mChoiceAdapter.setGalleryAdapter(mAdapter);
        mView.setLayoutManager(new GridLayoutManager(this,3));
        mView.setAdapter(mAdapter);
        mChoiceView = findViewById(R.id.choiceimage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mChoiceView.setLayoutManager(layoutManager);
        mChoiceView.setAdapter(mChoiceAdapter);
        TextView tv = findViewById(R.id.picselect);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> photos = mChoiceAdapter.getChoicePhoto();
                if(photos.size() > 0)
                {

                    Intent resultIntent = new Intent();
                    resultIntent.putStringArrayListExtra("result",photos);
                    setResult(RESULT_OK,resultIntent);
                    finish();
                }
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);


    }
    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


}
