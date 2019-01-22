package com.talib.youtuberx.youtubemy.BroadcastReciever;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegstrationToServer(refreshedToken);
    }

    private void sendRegstrationToServer(String refreshedToken) {
        Log.d("TOKEN",refreshedToken);
    }
}
