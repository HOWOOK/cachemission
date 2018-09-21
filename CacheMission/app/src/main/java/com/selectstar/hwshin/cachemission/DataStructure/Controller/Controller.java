package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.net.Uri;
import android.view.View;

import com.selectstar.hwshin.cachemission.Activity.TaskActivity;

public abstract class Controller {
    public int controllerID;
    public Uri photoUri;
    public Uri getPhotoUri(){
        return photoUri;
    }
    protected TaskActivity parentActivity;
    public void setParentActivity(TaskActivity parentActivity) {
        this.parentActivity = parentActivity;
    }
    public abstract void resetContent(View view, final String taskID);
    public abstract void setLayout(View view, String taskID);

}
