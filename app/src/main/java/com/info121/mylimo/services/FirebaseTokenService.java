package com.info121.mylimo.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by KZHTUN on 7/28/2017.
 */

public class FirebaseTokenService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseTokenService";

    String FCM_TOKEN = "";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        FCM_TOKEN = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "FCN Token" +  FCM_TOKEN);

    }
}
