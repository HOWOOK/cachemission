package com.selectstar.hwshin.cachemission.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_regionselect);
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
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.cancelbtn:
                dismiss();
            case R.id.chungbuk:
                dialogListener.onPartChungBukClicked();
                dismiss();
                break;
            case R.id.kyeongbuk:
                dialogListener.onPartKyeongBukClicked();
                dismiss();
                break;
            case R.id.jeonbuk:
                dialogListener.onPartJeonBukClicked();
                dismiss();
                break;
            case R.id.chungnam:
                dialogListener.onPartChungNamClicked();
                dismiss();
                break;
            case R.id.kyeongnam:
                dialogListener.onPartChungNamClicked();
                dismiss();
                break;
            case R.id.jeonnam:
                dialogListener.onPartJeonNamClicked();
                dismiss();
                break;
            case R.id.kangwon:
                dialogListener.onPartKangwonClicked();
                dismiss();
                break;
            case R.id.jeju:
                dialogListener.onPartJejuClicked();
                dismiss();
                break;
        }

    }
}


