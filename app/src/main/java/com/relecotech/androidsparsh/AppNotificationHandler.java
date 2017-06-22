package com.relecotech.androidsparsh;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

/**
 * Created by amey on 1/27/2016.
 */
public class AppNotificationHandler extends NotificationsHandler {

    public int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    private SessionManager sessionManager;
    String userRole;
    String teacherId;
    String studentId;
    String classId;
    // GCMBaseIntentService gcmBaseIntentService;

    String registrationTag[];
    private GCMIntentService gcmmessage;

    @Override
    public void onRegistered(final Context context, final String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);

    }


    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;

        Log.d("AppNotificationHandler ", " before send notifcation");

        String message_Payload = bundle.getString("notification_message");
        String id_Payload = bundle.getString("assignment_id");
        String submitted_by_Payload = bundle.getString("submitted_by");
        String postDate_Payload = bundle.getString("posted_date");
        String dueDate_Payload = bundle.getString("assmnt_due_date");
        String tag_Payload = bundle.getString("tag");

        System.out.println("AppNotificationHandler  ****************************");
        Log.d("bundle.id ", "" + id_Payload);
        Log.d("bundle.Message ", "" + message_Payload);
        Log.d("bundle.submitted ", "" + submitted_by_Payload);
        Log.d("bundle.dueDate ", "" + dueDate_Payload);
        Log.d("bundle.postDate ", "" + postDate_Payload);
        Log.d("bundle.tag_Payload ", "" + tag_Payload);
        Log.d("msgIntent ", " After");
        System.out.println("sendNotification Calling ****************************");

        //sendNotification(message_Payload);
        //Storing Notification data  into Sqlite database
        gcmmessage = new GCMIntentService(message_Payload);
        Intent intent = new Intent(ctx, GCMIntentService.class);
        intent.putExtra("message_Payload", message_Payload);
        intent.putExtra("submitted_by_Payload", submitted_by_Payload);
        intent.putExtra("postDate_Payload", postDate_Payload);
        intent.putExtra("dueDate_Payload", dueDate_Payload);
        intent.putExtra("id_Payload", id_Payload);
        intent.putExtra("tag_Payload", tag_Payload);

        sessionManager = new SessionManager(ctx);

        System.out.println("sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH)" + sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH));
        if (sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH).equals("true")) {


            System.out.println(" sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH) " + sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH));

            if (tag_Payload.equals("Alert")) {
                System.out.println("Alert Notification Recived");
                String alertPriority = bundle.getString("alert_priority");
                if (alertPriority.contains("Urgent")) {
                    System.out.println("Emergency Alert Recivedd----------");
                    sendEmergencyNotification(message_Payload);

                } else {
                    System.out.println("Normal Alert Recived ------------");
                    sendNormalNotification(message_Payload);
                }
            } else {
                sendNormalNotification(message_Payload);
                System.out.println("Notification Recived");
                gcmmessage.onMessage(context, intent);
            }

        } else {

            System.out.println(" sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH) " + sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH));
        }


    }

    private void sendEmergencyNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent NotifyIntent = new Intent(ctx, MainActivity.class);
        NotifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, NotifyIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Sparsh Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg);
        // mBuilder.setSound(RingtoneManager.getDefaultUri(R.raw.emergency_tone));
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        mBuilder.setSound(Uri.parse("android.resource://com.relecotech.androidsparsh/" + R.raw.emergency_tone));


        mBuilder.setContentIntent(contentIntent);
        System.out.println("NOTIFICATION_ID++  " + NOTIFICATION_ID++);
        mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());


    }

    private void sendNormalNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent NotifyIntent = new Intent(ctx, MainActivity.class);
        NotifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, NotifyIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Sparsh  Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

        mBuilder.setContentIntent(contentIntent);
        System.out.println("NOTIFICATION_ID++  " + NOTIFICATION_ID++);
        mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());


    }
}
