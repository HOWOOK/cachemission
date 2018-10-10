package com.selectstar.hwshin.cachemission.DataStructure;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;

import com.selectstar.hwshin.cachemission.R;

public class PartSelectDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private PartSelectDialogListener dialogListener;

    private ImageView cancelbtn;
    private ImageView partPole;
    private ImageView partTree;
    private ImageView partTransformer;
    private ConstraintLayout dialogCL;

    public PartSelectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public PartSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public void setDialogListener(PartSelectDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_partselect);
        dialogCL = findViewById(R.id.dialogCL);
        cancelbtn = findViewById(R.id.cancelbtn);

        partPole = findViewById(R.id.part_pole);
        partTree = findViewById(R.id.part_tree);
        partTransformer = findViewById(R.id.part_transformer);

        partPole.setOnClickListener(this);
        partTree.setOnClickListener(this);
        partTransformer.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.cancelbtn:
                dismiss();
            case R.id.part_pole:
                dialogListener.onPartPoleClicked();
                dismiss();
                break;
            case R.id.part_tree:
                dialogListener.onPartTreeClicked();
                dismiss();
                break;
            case R.id.part_transformer:
                dialogListener.onPartTransformerClicked();
                dismiss();
                break;
        }

    }
}


