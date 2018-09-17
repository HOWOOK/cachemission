package com.selectstar.hwshin.cachemission.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.selectstar.hwshin.cachemission.R;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class CircularPagerAdapter extends PagerAdapter {

    private ViewPager mPager = null;

    private int[] mPagerIDsArray;

    private int mCount;

    private Context mContext;

    private String mUri;



public  CircularPagerAdapter(){


}

    public CircularPagerAdapter(final ViewPager pager, int[] pageArray, String uri) {

        super();

mUri=uri;

        mPager = pager;

// 순환하는 형태의 ViewPager를 구현하기 위해 View들의 양 끝에 임시 View 추가

        int actualNoOfIDs = pageArray.length;

        mCount = actualNoOfIDs ;

        mPagerIDsArray = new int[mCount];



        for (int i = 0; i < actualNoOfIDs; i++) {

            mPagerIDsArray[i ] = pageArray[i];

        }

      //  mPagerIDsArray[0] = pageArray[actualNoOfIDs - 1];

       // mPagerIDsArray[mCount - 1] = pageArray[0];



// Page를 순환하여 Navigate할 수 있도록 구현 (일종의 눈속임)

        mPager.setOnPageChangeListener(new OnPageChangeListener() {
/*
            @Override

            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_IDLE) {              // 좌우 Flicking이 끝나는 시점

                    int pageCount = getCount();

                    if (mPager.getCurrentItem() == 0) {               // 왼쪽끝 임시 View로 이동한 경우

// 실제 View들 중 마지막 View로 Animation없이 이동

                        mPager.setCurrentItem(pageCount - 2, false);

                    } else if (mPager.getCurrentItem() == mCount - 1) { // 오른쪽끝 임시 View로 이동한 경우

// 실제 View들 중 첫번째 View로 Animation없이 이동

                        mPager.setCurrentItem(1, false);

                    }

                }

            }
            */



            @Override

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }



            @Override

            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

    }



    public int getCount() {

        return mCount;

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


    public Object instantiateItem(View pager, int position) {                // position 별로 pager에 view들을 등록

        mContext = pager.getContext();

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View view = inflater.inflate(R.layout.pagerimage1, null);                // ViewPager에 inflate 시킬 layout 연결


if(position==2) {
    SharedPreferences imagefilepath = mContext.getSharedPreferences("imagefilepath", MODE_PRIVATE);
    String stringtoken;
    stringtoken = imagefilepath.getString("imagefilepath", null);
    if (stringtoken == null) {
        stringtoken = "";
    }
    Bitmap bitmap = BitmapFactory.decodeFile(stringtoken);
    ExifInterface exif = null;

    try {
        exif = new ExifInterface(stringtoken);
    } catch (IOException e) {
        e.printStackTrace();
    }

    int exifOrientation;
    int exifDegree;

    if (exif != null) {
        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        exifDegree = exifOrientationToDegrees(exifOrientation);
    } else {
        exifDegree = 0;
    }


   // byte[] decodedByteArray = Base64.decode(stringtoken, Base64.NO_WRAP);
  //  Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
//Uri uri=Uri.parse(stringtoken);
    ImageView iv = view.findViewById(R.id.pagerimage);
    if(bitmap!=null){
    iv.setImageBitmap(rotate(bitmap, exifDegree));}

}


        ((ViewPager) pager).addView(view, 0);

        return view;

    }



    @Override

    public void destroyItem(View view, int position, Object obj) {

        ((ViewPager) view).removeView((View) obj);

    }



    @Override

    public boolean isViewFromObject(View view, Object obj) {

        return view == (View) obj;

    }



    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}

    @Override public Parcelable saveState() { return null; }

    @Override public void startUpdate(View arg0) {}

    @Override public void finishUpdate(View arg0) {}

}
