package com.relecotech.androidsparsh.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;

/**
 * Created by Amey on 02-03-2017.
 */
public class AlarmReceiver extends BroadcastReceiver {
    Context ctx;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static int NOTIFICATION_ID = 1;
    public static int cout_how_much_time_alarm_recived = 0;
    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        sendNotifiaction();
    }

    public void sendNotifiaction() {
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent NotifyIntent = new Intent(ctx, MainActivity.class);
        NotifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, NotifyIntent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Sparsh  Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("You have assignment due tomorrow"))
                        .setAutoCancel(true)
                        .setContentText("You have assignment due tomorrow");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setSound(uri);
        mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());
        System.out.println("Count ==============" + cout_how_much_time_alarm_recived++);
    }
}
