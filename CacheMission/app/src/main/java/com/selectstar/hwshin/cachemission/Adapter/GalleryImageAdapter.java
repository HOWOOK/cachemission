package com.selectstar.hwshin.cachemission.Adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.GalleryActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.RecyclerItem;
import com.selectstar.hwshin.cachemission.R;

import java.io.File;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ItemViewHolder> {
    ArrayList<RecyclerItem> mItems;
    GalleryActivity mActivity;
    String basePath;
    String[] mImgs;
    boolean[] checkList;
    ChoiceImageAdapter choiceImageAdapter;
    public void removeItem(Integer integer)
    {

        checkList[integer] = false;

    }
    public GalleryImageAdapter(String basePath, GalleryActivity mActivity, ChoiceImageAdapter choiceImageAdapter) {
        this.mActivity = mActivity;
        this.basePath = basePath;
        this.choiceImageAdapter = choiceImageAdapter;
        //this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File file = new File(basePath); // 지정 경로의 directory를 File 변수로 받아
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.d(TAG, "failed to create directory");
            }
        }
        mItems = new ArrayList<>();
        mImgs = file.list(); // file.list() method를 통해 directory 내 file 명들을 String[] 에 저// 장
        checkList = new boolean[mImgs.length];
        for (int i=0;i<mImgs.length;i++) {
            RecyclerItem item = new RecyclerItem(mImgs[i]);
            mItems.add(item);
            checkList[i] = false;
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_image, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight() / 4;
        view.setLayoutParams(lp);
        final RadioButton button = view.findViewById(R.id.chooseoption);
        button.setClickable(false);

        return new ItemViewHolder(view);
    }

    public void toggleButton(View v,int position)
    {
        RadioButton button = v.findViewById(R.id.chooseoption);
        if(button.isChecked()) {
            choiceImageAdapter.removePhoto(new Integer(position));
        }
        else {
            if(mActivity.availNumber <= choiceImageAdapter.checkedPhoto.size())
            {
                Toast.makeText(mActivity,"더 이상 제출할 수 없습니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            button.setChecked(true);
            checkList[position] = true;
            choiceImageAdapter.addPhoto(new Integer(position));
        }
    }
    @Override
    public void onBindViewHolder(final @NonNull GalleryImageAdapter.ItemViewHolder holder, int pos) {
        final int position = pos;
        holder.image.setImageDrawable(ContextCompat.getDrawable(mActivity,R.drawable.grayback));
        holder.myCheck.setChecked(checkList[pos]);
        holder.myCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        toggleButton(v,position);

            }
        });
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton(v,position);
            }
        });
        new Thread() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //Bitmap bm = BitmapFactory.decodeFile(mBasePath + File.separator + mImgs[position], options);
                //options.inSampleSize = calculateInSampleSize(options, 100, 100);
                options.inSampleSize = 8;
                options.inJustDecodeBounds = false;
                final Bitmap bm = BitmapFactory.decodeFile(basePath + File.separator + mImgs[position], options);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.image.setImageBitmap(bm);
                            }
                        });
                    }
                }).start();
            }
        }.start();


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private RadioButton myCheck;
        private View myView;
        public void setFalse()
        {
            myCheck.setChecked(false);
        }
        public ItemViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            myCheck = itemView.findViewById(R.id.chooseoption);
            image = itemView.findViewById(R.id.imagecontent);
        }
    }
}