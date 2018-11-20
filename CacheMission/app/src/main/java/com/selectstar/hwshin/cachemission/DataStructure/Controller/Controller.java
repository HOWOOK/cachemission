package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.net.Uri;
import android.view.View;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;

public abstract class Controller {
    public int controllerID;
    public Uri photoUri;
    public String BoxCropTestAnswer="";
    public Uri getPhotoUri(){
        return photoUri;
    }
    protected PatherActivity parentActivity;
    public void setParentActivity(PatherActivity parentActivity) {
        this.parentActivity = parentActivity;
    }
    public abstract void resetContent(View view, final String taskID);
    public abstract void setLayout(View view, String taskID);

}
