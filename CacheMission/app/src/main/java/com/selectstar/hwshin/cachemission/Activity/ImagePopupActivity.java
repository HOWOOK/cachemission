package com.selectstar.hwshin.cachemission.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selectstar.hwshin.cachemission.R;

public class ImagePopupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Tracker t = ((GlobalApplication)getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
        t.setScreenName("ImagePopupActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        setContentView(R.layout.activity_imagepopup);
        ImageView iv=findViewById(R.id.imagepopup);
        String uristring=getIntent().getStringExtra("image");
        Uri uri=Uri.parse(uristring);
        iv.setImageURI(uri);

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
