//package com.relecotech.androidapp.activities;
//
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//
//import com.relecotech.sparshalpha.DatabaseHandler;
//import com.relecotech.sparshalpha.MainActivity;
//import com.relecotech.sparshalpha.R;
//import com.relecotech.sparshalpha.adapters.NotificationAdapterAdapter;
//import com.relecotech.sparshalpha.controllers.NoticeListData;
//import com.relecotech.sparshalpha.fragments.AssignmentFragment;
//import com.relecotech.sparshalpha.fragments.Parent_control;
//
//import java.util.ArrayList;
//
///**
// * Created by amey on 10/16/2015.
// */
//public class Notifications extends ActionBarActivity {
//    public static String loggedInUserForNotifications;
//    ArrayList<NoticeListData> noticeList;
//    NoticeListData noticeListData;
//    String notification_Main_Assignment_Due_Date;
//    String notifaction_Main_Post_Date;
//    String notification_Main_Tag;
//    String notifiaction_Main_Message_Body;
//    String notifcation_Main_Submitted_By;
//    String notifaction_Main_Assignm_Id;
//    ListView noticeListView;
//    String userRole;
//    DatabaseHandler notifiactio_DatabaseHandler;
//    NotificationAdapterAdapter notificationAdapterAdapter;
//    Fragment fragment = null;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.notification_action);
//        noticeList = new ArrayList<NoticeListData>();
//        noticeListView = (ListView) findViewById(R.id.notificationlistView);
//        notifiactio_DatabaseHandler = new DatabaseHandler(this);
//
//
//        try {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        } catch (NullPointerException nullPointer) {
//            nullPointer.printStackTrace();
//        }
//        userRole = MainActivity.userRole;
//        //getString(R.string.login_user_role);
//        loggedInUserForNotifications = userRole;
//        Log.d("login_user_role", userRole);
//
//        if (userRole.equals("Teacher")) {
//
//        }
//
//        if (userRole.equals("Student")) {
//
//        }
//
//        Cursor cursor = notifiactio_DatabaseHandler.getAllNotificationDataByCursor();
//        System.out.println("DatabaseHandler FINISHED");
//        cursor.moveToFirst();
//        int cursorCount = cursor.getCount();
//        for (int i = 0; i < cursorCount; i++) {
//
//            System.out.println("id" + i + " :" + cursor.getString(0));
//            System.out.println("CATEGORY" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationCategory")));
//            System.out.println("ASSIGNMENT ID" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationAssignmentId")));
//            System.out.println("NOTIFACTION TITLE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationTitle")));
//            System.out.println("NOTIFACTION MESSAGE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationMessage")));
//            System.out.println("NOTICATION SUBMITTED BY" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationSubmittedBy")));
//            System.out.println("NOTIFICATION DUE DATE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationAssmntDueDate")));
//            System.out.println("POSTDATE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationPostDate")));
//
//
//            notification_Main_Tag = cursor.getString(cursor.getColumnIndex("notificationCategory"));
//            notifiaction_Main_Message_Body = cursor.getString(cursor.getColumnIndex("notificationMessage"));
//            notifcation_Main_Submitted_By = cursor.getString(cursor.getColumnIndex("notificationSubmittedBy"));
//            notifaction_Main_Post_Date = cursor.getString(cursor.getColumnIndex("notificationPostDate"));
//            notification_Main_Assignment_Due_Date = cursor.getString(cursor.getColumnIndex("notificationAssmntDueDate"));
//            notifaction_Main_Assignm_Id = cursor.getString(cursor.getColumnIndex("notificationAssignmentId"));
//            noticeListData = new NoticeListData(notifaction_Main_Assignm_Id, notification_Main_Assignment_Due_Date, notification_Main_Tag, notifiaction_Main_Message_Body, notifaction_Main_Post_Date, notifcation_Main_Submitted_By);
//            noticeList.add(0, noticeListData);
//            cursor.moveToNext();
//            int counttt = 0;
//            System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS_" + counttt++);
//        }
//
//        notificationAdapterAdapter = new NotificationAdapterAdapter(this, noticeList);
//        noticeListView.setAdapter(notificationAdapterAdapter);
//
////
////        noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                NoticeListData noticeListData = noticeList.get(position);
////                if (noticeListData.getNotifaction_Tag().equals("Leave") || noticeListData.getNotifaction_Tag().equals("Notes")) {
////
////                } else if (noticeListData.getNotifaction_Tag().equals("Assignment")) {
////
////                }
////            }
////        });
//
//        noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                NoticeListData noticeListData = noticeList.get(position);
//                if (noticeListData.getNotifaction_Tag().equals("Leave") || noticeListData.getNotifaction_Tag().equals("Notes")) {
//                    System.out.println("In leave and Notes condtion........1 ");
//                    getSupportFragmentManager().beginTransaction().add(android.R.id.content, new Parent_control()).commit();
//                    getSupportActionBar().setTitle("Parent Zone");
//                    System.out.println("In leave and Notes condtion........2 ");
//                } else if (noticeListData.getNotifaction_Tag().equals("Assignmet")) {
//                    System.out.println("In Assignment condtion........1 ");
//                    getSupportFragmentManager().beginTransaction().add(android.R.id.content, new AssignmentFragment()).commit();
//                    System.out.println("In Assignment condtion........2 ");
//                    getSupportActionBar().setTitle("Assignment");
//                } else {
//                    System.out.println("In Assignment condtion. else------- " + noticeListData.getNotifaction_Tag());
//                }
//            }
//        });
//
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}
