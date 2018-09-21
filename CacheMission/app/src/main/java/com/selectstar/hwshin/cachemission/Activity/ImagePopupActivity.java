package com.selectstar.hwshin.cachemission.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.R;

public class ImagePopupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepopup);
        ImageView iv=findViewById(R.id.imagepopup);
        String uristring=getIntent().getStringExtra("image");
        Uri uri=Uri.parse(uristring);
        iv.setImageURI(uri);

    }
}
