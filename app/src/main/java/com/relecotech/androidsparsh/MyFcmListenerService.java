package com.relecotech.androidsparsh;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by yogesh on 22-Nov-16.
 */
public class MyFcmListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();

        System.out.println(" from from from "+ from);
        Map data = message.getData();
    }

}
