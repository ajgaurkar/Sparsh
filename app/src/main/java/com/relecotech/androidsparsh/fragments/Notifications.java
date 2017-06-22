package com.relecotech.androidsparsh.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.NotificationAdapterAdapter;
import com.relecotech.androidsparsh.controllers.NoticeListData;

import java.util.ArrayList;

/**
 * Created by ajinkya on 12/20/2016.
 */
public class Notifications extends Fragment {

    public static String loggedInUserForNotifications;
    ArrayList<NoticeListData> noticeList;
    NoticeListData noticeListData;
    String notification_Main_Assignment_Due_Date;
    String notifaction_Main_Post_Date;
    String notification_Main_Tag;
    String notifiaction_Main_Message_Body;
    String notifcation_Main_Submitted_By;
    String notifaction_Main_Assignm_Id;
    ListView noticeListView;
    String userRole;
    DatabaseHandler notifiactio_DatabaseHandler;
    NotificationAdapterAdapter notificationAdapterAdapter;
    Fragment fragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_action, container, false);
        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Notifications");
        noticeList = new ArrayList<>();
        noticeListView = (ListView) rootView.findViewById(R.id.notificationlistView);
        notifiactio_DatabaseHandler = new DatabaseHandler(getActivity());


//        try {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        } catch (NullPointerException nullPointer) {
//            nullPointer.printStackTrace();
//        }
        userRole = MainActivity.userRole;
        //getString(R.string.login_user_role);
        loggedInUserForNotifications = userRole;
        Log.d("login_user_role", userRole);

        if (userRole.equals("Teacher")) {

        }

        if (userRole.equals("Student")) {

        }

        Cursor cursor = notifiactio_DatabaseHandler.getAllNotificationDataByCursor();
        System.out.println("DatabaseHandler FINISHED");
        cursor.moveToFirst();
        int cursorCount = cursor.getCount();
        for (int i = 0; i < cursorCount; i++) {

            System.out.println("id" + i + " :" + cursor.getString(0));
            System.out.println("CATEGORY" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationCategory")));
            System.out.println("ASSIGNMENT ID" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationAssignmentId")));
            System.out.println("NOTIFACTION TITLE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationTitle")));
            System.out.println("NOTIFACTION MESSAGE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationMessage")));
            System.out.println("NOTICATION SUBMITTED BY" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationSubmittedBy")));
            System.out.println("NOTIFICATION DUE DATE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationAssmntDueDate")));
            System.out.println("POSTDATE" + i + " :" + cursor.getString(cursor.getColumnIndex("notificationPostDate")));


            notification_Main_Tag = cursor.getString(cursor.getColumnIndex("notificationCategory"));
            notifiaction_Main_Message_Body = cursor.getString(cursor.getColumnIndex("notificationMessage"));
            notifcation_Main_Submitted_By = cursor.getString(cursor.getColumnIndex("notificationSubmittedBy"));
            notifaction_Main_Post_Date = cursor.getString(cursor.getColumnIndex("notificationPostDate"));
            notification_Main_Assignment_Due_Date = cursor.getString(cursor.getColumnIndex("notificationAssmntDueDate"));
            notifaction_Main_Assignm_Id = cursor.getString(cursor.getColumnIndex("notificationAssignmentId"));
            noticeListData = new NoticeListData(notifaction_Main_Assignm_Id, notification_Main_Assignment_Due_Date, notification_Main_Tag, notifiaction_Main_Message_Body, notifaction_Main_Post_Date, notifcation_Main_Submitted_By);
            noticeList.add(0, noticeListData);
            cursor.moveToNext();
            int counttt = 0;
            System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS_" + counttt++);
        }

        notificationAdapterAdapter = new NotificationAdapterAdapter(getActivity(), noticeList);
        noticeListView.setAdapter(notificationAdapterAdapter);


        noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeListData noticeListData = noticeList.get(position);
                if (noticeListData.getNotifaction_Tag().equals("Leave") || noticeListData.getNotifaction_Tag().equals("Notes")) {
                    System.out.println("In leave and Notes condtion........1 ");
                   // getFragmentManager().beginTransaction().add(R.id.content_frame, new Parent_control()).commit();
                    getFragmentManager().beginTransaction().replace(R.id.content_frame,  new Parent_control()).addToBackStack(null).commit();
                    //((MainActivity) getActivity()).getSupportActionBar().setTitle("Parent Zone");
                    System.out.println("In leave and Notes condtion........2 ");
                } else if (noticeListData.getNotifaction_Tag().equals("Assignment")) {
                    System.out.println("In Assignment condtion........1 ");
                    //getFragmentManager().beginTransaction().add(R.id.content_frame, new AssignmentFragment()).commit();
                    //fragmentManager = getFragmentManager();
                    getFragmentManager().beginTransaction().replace(R.id.content_frame,  new AssignmentFragment()).addToBackStack(null).commit();
                    System.out.println("In Assignment condtion........2 ");
                   //((MainActivity) getActivity()).getSupportActionBar().setTitle("Assignment");
                } else {
                    System.out.println("In Assignment condtion. else------- " + noticeListData.getNotifaction_Tag());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back buttson
                    if (userRole.equals("Teacher")) {
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, new DashboardTeacherFragment()).addToBackStack(null).commit();

                    }
                    if (userRole.equals("Student")) {
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, new DashboardStudentFragment()).addToBackStack(null).commit();

                    }
                    return true;
                }
                return false;
            }
        });
    }

}
