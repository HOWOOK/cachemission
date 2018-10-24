package com.selectstar.hwshin.cachemission.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;

import com.selectstar.hwshin.cachemission.R;

public class RegionSelectDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private RegionSelectDialogListener dialogListener;

    private ImageView cancelbtn;
    private  ImageView chungbuk;
    private  ImageView kyeongbuk;
    private  ImageView jeonbuk;
    private  ImageView chungnam;
    private  ImageView kyeongnam;
    private  ImageView jeonnam;
    private  ImageView kangwon;
    private  ImageView jeju;

    private ConstraintLayout dialogCL;

    public RegionSelectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public RegionSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public void setDialogListener(RegionSelectDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        ((Activity)context).finish();
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_regionselect);
        getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
        String region="";
        final SharedPreferences explain = context.getSharedPreferences("region", Context.MODE_PRIVATE);

        dialogCL = findViewById(R.id.dialogCL);
        cancelbtn = findViewById(R.id.cancelbtn);

        chungbuk = findViewById(R.id.chungbuk);
        kyeongbuk = findViewById(R.id.kyeongbuk);
        jeonbuk = findViewById(R.id.jeonbuk);
        chungnam = findViewById(R.id.chungnam);
        kyeongnam = findViewById(R.id.kyeongnam);
        jeonnam = findViewById(R.id.jeonnam);
        kangwon = findViewById(R.id.kangwon);
        jeju = findViewById(R.id.jeju);

        chungbuk.setOnClickListener(this);
        kyeongbuk.setOnClickListener(this);
        jeonbuk.setOnClickListener(this);
        chungnam.setOnClickListener(this);
        kyeongnam.setOnClickListener(this);
        jeonnam.setOnClickListener(this);
        kangwon.setOnClickListener(this);
        jeju.setOnClickListener(this);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(explain.getString("region","").equals("")) {
                    dismiss();
                    ((Activity) context).finish();
                }
                else {
                    dismiss();
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        String region="";
        SharedPreferences explain = context.getSharedPreferences("region", Context.MODE_PRIVATE);

        switch(v.getId()){
            case R.id.cancelbtn:
                dismiss();
                ((Activity)context).finish();

            case R.id.chungbuk:
                dialogListener.onPartChungBukClicked();
                region ="충북";
                dismiss();
                break;
            case R.id.kyeongbuk:
                dialogListener.onPartKyeongBukClicked();
                region ="경북";
                dismiss();
                break;
            case R.id.jeonbuk:
                dialogListener.onPartJeonBukClicked();
                region ="전북";
                dismiss();
                break;
            case R.id.chungnam:
                dialogListener.onPartChungNamClicked();
                region ="충남";
                dismiss();
                break;
            case R.id.kyeongnam:
                dialogListener.onPartKyeongNamClicked();
                region ="경남";
                dismiss();
                break;
            case R.id.jeonnam:
                dialogListener.onPartJeonNamClicked();
                region ="전남";
                dismiss();
                break;
            case R.id.kangwon:
                dialogListener.onPartKangwonClicked();
                region ="강원";
                dismiss();
                break;
            case R.id.jeju:
                dialogListener.onPartJejuClicked();
                region ="제주";
                dismiss();
                break;
        }
        SharedPreferences.Editor editor= explain.edit();
        editor.putString("region",region);
        editor.apply();


    }
}


