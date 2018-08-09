package com.example.hwshin.cachemission.DataStructure;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

public abstract class Controller implements Serializable {
    public int controllerID;
    public abstract String getAnswer();
    public abstract void setLayout(View view, Context c);
}
