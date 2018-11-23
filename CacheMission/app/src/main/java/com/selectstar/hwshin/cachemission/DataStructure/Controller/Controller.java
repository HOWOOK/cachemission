package com.selectstar.hwshin.cachemission.DataStructure.Controller;

import android.net.Uri;
import android.view.View;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;

import org.json.JSONArray;

public abstract class Controller {
    public int controllerID;
    public Uri photoUri;
    public JSONArray BoxCropTestAnswer;
    public boolean testFlag=false;
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
