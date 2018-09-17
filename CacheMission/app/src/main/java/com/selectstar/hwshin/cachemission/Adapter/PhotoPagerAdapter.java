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
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.selectstar.hwshin.cachemission.R;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static android.content.Context.MODE_PRIVATE;

public class PhotoPagerAdapter extends PagerAdapter {
    private Stack<Uri> photoStack;
    private LinkedList<Uri> photoList;
    private LayoutInflater inflater;
    private Context context;

    public PhotoPagerAdapter(Context context) {
        this.context = context;
        photoList.pollLast();
        photoStack = new Stack<>();
    }
    public boolean isEmpty() {
        return photoStack.isEmpty();
    }
    public Uri pop() {
        return photoStack.pop();
    }
    public void stackClear() {
        photoStack.clear();
        notifyDataSetChanged();
    }

    public void addPhoto(Uri uri) {
        photoStack.push(uri);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return photoStack.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ConstraintLayout) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.viewpageritem, container, false);
        ImageView imageView = (ImageView)(v.findViewById(R.id.imageitem));
        imageView.setImageURI(photoStack.get(position));
        container.addView(v);
        return v;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }

}
