package com.talib.youtuberx.youtubemy.BroadcastReciever;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.talib.youtuberx.youtubemy.Common.Common;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        handleNotfication(remoteMessage.getNotification().getBody());
    }

    private void handleNotfication(String body) {
        Intent pushNotfication = new Intent(Common.STR_PUSH);
        pushNotfication.putExtra("message",body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotfication);
    }
}
