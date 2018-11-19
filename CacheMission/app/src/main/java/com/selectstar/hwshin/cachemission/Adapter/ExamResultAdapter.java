package com.selectstar.hwshin.cachemission.Adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

import com.google.android.gms.common.util.ArrayUtils;
import com.selectstar.hwshin.cachemission.Activity.GalleryActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.RecyclerItem;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static android.support.constraint.Constraints.TAG;

public class ExamResultAdapter extends RecyclerView.Adapter<ExamResultAdapter.ItemViewHolder> {
    ArrayList<RecyclerItem> mItems;


    private LayoutInflater inflater;
    private ArrayList<JSONObject> mTaskList;
    private JSONObject userInfo;
    public void setUserInfo(JSONObject  userInfo) {
        this.userInfo = userInfo;

    }
    public void removeItem(Integer integer)
    {


    }
    public ExamResultAdapter(Context mContext,ArrayList<JSONObject> taskList) {


    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_result_item, parent, false);
//        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
//        lp.height = parent.getMeasuredWidth() / 3;
//        view.setLayoutParams(lp);
//        final RadioButton button = view.findViewById(R.id.chooseoption);
//        button.setClickable(false);

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
    public void onBindViewHolder(final @NonNull ExamResultAdapter.ItemViewHolder holder, int pos) {


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