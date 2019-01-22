package com.talib.youtuberx.youtubemy.BroadcastReciever;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.talib.youtuberx.youtubemy.R;
import com.talib.youtuberx.youtubemy.SignActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, SignActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_logo).
                setContentTitle("Youtuber Tap").setColor(ContextCompat.getColor(context,R.color.fbutton_color_alizarin)).setContentText("Amınyüm Bayram Nurludamı youtube`a başladı?").setSound(alarmSound)
                .setAutoCancel(true).setWhen(when).setContentIntent(pendingIntent).setVibrate(new long[]{1000,1000});
        notificationManager.notify(0,builder.build());
    }
}
