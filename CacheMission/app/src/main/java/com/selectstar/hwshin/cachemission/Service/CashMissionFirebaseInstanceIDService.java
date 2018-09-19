package com.selectstar.hwshin.cachemission.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class CashMissionFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "lbalba";

    //토큰이 새로 생성될 때마다 실행됨
    @Override
    public void onTokenRefresh() {
    // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
