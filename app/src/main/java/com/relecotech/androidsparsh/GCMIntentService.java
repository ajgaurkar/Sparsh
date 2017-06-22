package com.relecotech.androidsparsh;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.relecotech.androidsparsh.controllers.NoticeListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by amey on 3/9/2016.
 */


public class GCMIntentService extends GCMBaseIntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public GCMIntentService(String name) {
        super(name);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {

        System.out.println("GCMIntentService Before   ****************************");

        String notification_Assignment_Id_Payload = intent.getStringExtra("assignment_id");
        String notifaction_Assignment_DueDate_Payload = intent.getStringExtra("dueDate_Payload");
        String notifaction_Tag_Payload = intent.getStringExtra("tag_Payload");
        String notifaciton_Message_Payload = intent.getStringExtra("message_Payload");
        String notifaction_PostDate_Payload = intent.getStringExtra("postDate_Payload");
        String notifaciton_Submitted_By_Payload = intent.getStringExtra("submitted_by_Payload");


        try {
            if (!notifaction_PostDate_Payload.equals(null)) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date datepostby = null;
                try {
                    datepostby = simpleDateFormat.parse(notifaction_PostDate_Payload);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                notifaction_PostDate_Payload = targetDateFormat.format(datepostby);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@ Date time" + notifaction_PostDate_Payload);
            }

        } catch (Exception e) {
            System.out.println("Execption on GCM INTENT SERVICE NOTIFICATION PAYLOAD IS GIVING NULLL VALUE");
            e.printStackTrace();
        }


        System.out.println("notification_ID  ############################## 1. " + notification_Assignment_Id_Payload);
        System.out.println("notifaciton_Message_Payload ################### 2." + notifaciton_Message_Payload);
        System.out.println("notifaciton_Submitted_By_Payload ############## 3." + notifaciton_Submitted_By_Payload);
        System.out.println("notifaction_PostDate_Payload ################## 4.  " + notifaction_PostDate_Payload);
        System.out.println("notifaction_DueDate_Payload ################### 5. " + notifaction_Assignment_DueDate_Payload);
        System.out.println("notifaction_DueDate_Payload ################### 6. " + notifaction_Tag_Payload);

        NoticeListData noticeListData = new NoticeListData(notification_Assignment_Id_Payload, notifaction_Assignment_DueDate_Payload, notifaction_Tag_Payload, notifaciton_Message_Payload, notifaction_PostDate_Payload, notifaciton_Submitted_By_Payload);

        System.out.println("DatabaseHandler STARTED");
        DatabaseHandler dataBaseHelper = new DatabaseHandler(context);
        System.out.println("DatabaseHandler FINISHED");
        dataBaseHelper.addNotificationToDatabase(noticeListData);
        System.out.println("add data FINISHED");

        System.out.println("GCMIntentService after   ****************************");


    }

    @Override
    protected void onError(Context context, String s) {

    }

    @Override
    protected void onRegistered(Context context, String s) {

    }

    @Override
    protected void onUnregistered(Context context, String s) {

    }


}
