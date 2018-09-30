package com.selectstar.hwshin.cachemission.Adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.DataStructure.RecyclerItem;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class PhotoPagerAdapter extends RecyclerView.Adapter<PhotoPagerAdapter.ItemViewHolder> {
    ArrayList<Uri> photoList;
    AppCompatActivity parentActivity;
    public void addPhoto(Uri photoUri)
    {
        photoList.add(photoUri);
        notifyItemInserted(photoList.size()-1);
        notifyDataSetChanged();
        ((ImageView)parentActivity.findViewById(R.id.emptyview)).setVisibility(View.INVISIBLE);
    }
    public Uri getUri(int index)
    {
        return photoList.get(index);
    }
    public PhotoPagerAdapter(AppCompatActivity parentActivity) {
        photoList = new ArrayList<>();
        this.parentActivity = parentActivity;
    }
    public boolean isEmpty()
    {
        return photoList.isEmpty();
    }
    public void clearItem()
    {
        photoList = new ArrayList<>();
        notifyDataSetChanged();
        ((ImageView)parentActivity.findViewById(R.id.emptyview)).setVisibility(View.VISIBLE);
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpageritem, parent, false);
        return new ItemViewHolder(view);
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void onBindViewHolder(final @NonNull PhotoPagerAdapter.ItemViewHolder holder, int pos) {
        // holder.delete.setOnClickListener();
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();
        layoutParams.height = layoutParams.width;
        holder.itemView.requestLayout();

        final int position = pos;
        new Thread() {
            @Override
            public void run() {
               final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //Bitmap bm = BitmapFactory.decodeFile(mBasePath + File.separator + mImgs[position], options);
                //options.inSampleSize = calculateInSampleSize(options, 100, 100);
                options.inSampleSize = 8;
                options.inJustDecodeBounds = false;
                System.out.println(photoList.get(position).getPath());
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        parentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ExifInterface exif=null;
                                try{exif=new ExifInterface(photoList.get(position).toString());}
                                catch(IOException e){e.printStackTrace();}
                                int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
                                final Bitmap bm = BitmapFactory.decodeFile(photoList.get(position).toString(), options);
                                final Bitmap bmRotated = rotateBitmap(bm,orientation);
                                holder.image.setImageBitmap(bmRotated);
                            }
                        });
                    }
                }).start();
//                final Bitmap bm = BitmapFactory.decodeFile(photoList.get(position).getPath(), options);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        parentActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                holder.image.setImageBitmap(bm);
//                            }
//                        });
//                    }
//                }).start();
            }
        }.start();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                if(photoList.isEmpty())
                    ((ImageView)parentActivity.findViewById(R.id.emptyview)).setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private Button delete;
        public ItemViewHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.itemdelete);
            image = itemView.findViewById(R.id.imageitem);
        }
    }
}