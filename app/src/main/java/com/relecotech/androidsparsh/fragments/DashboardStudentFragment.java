package com.relecotech.androidsparsh.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.activities.Gallery;
import com.relecotech.androidsparsh.activities.Rewards;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.relecotech.androidsparsh.MainActivity.userRole;

/**
 * Created by amey on 10/16/2015.
 */
//public class DashboardStudentFragment extends android.support.v4.app.Fragment {
public class DashboardStudentFragment extends android.support.v4.app.Fragment {

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO};
    View noticeView, eventsView, attendanceView, feesView, alertsView, reportsView;
    Fragment fragment;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    private long back_pressed;
    private TextView eventPanelTextView1;
    private TextView eventPanelTextView2;
    private TextView eventPanelTextView3;
    private TextView alertPanelTextView1;
    private TextView alertPanelTextView2;
    private TextView alertPanelTextView3;
    private TextView notificationPanelTextView1;
    private TextView notificationPanelTextView2;
    private TextView notificationPanelTextView3;
    private DatabaseHandler databaseHandler;
    private int loopCount = 0;

    private MobileServiceJsonTable alertJsonDataTable;
    private MobileServiceJsonTable calendarTable;
    private MobileServiceJsonTable gettingStudentAttendanceTableData;
    private MobileServiceJsonTable feesMobileServiceJsonTable;

    private JsonElement calendarFetchResponse;
    private JsonElement alertFetchResponse;
    private JsonElement studentAttendanceTableRespone;
    private JsonElement feesFetchResponse;

    private SessionManager sessionManager;
    private HashMap<String, String> userDetailMap;
    private ArcProgress completedProgressArc;
    private TextView feePanelTextView1;
    private TextView feePanelTextView2;
    private TextView feePanelTextView3;
    ProgressDialog studentProgressDialog;

    Handler dashboard_data_Handler;
    TimeOutTimerClass dashboard_timeOutTimerClass;
    Timer dashboard_data_Timer;
    long TIMEOUT_TIME = 25000;
    private FetchData getDashboardData;

    CloudBlobContainer container;
    String directory;
    private JsonObject jsonObjectFeeFragDashParm;
    private TextView fees_NoDataAvailable_TextView;
    private TextView alert_NoDataAvailable_TextView;
    private TextView notification_NoDataAvailable_TextView;
    private TextView calendar_NoDataAvailable_TextView;
    private JsonObject jsonObjectToFetchAttendance;
    private View rewardsView, galleryView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getActivity());
        userDetailMap = sessionManager.getUserDetails();


        try {
            //Azure connection components initialization
            alertJsonDataTable = MainActivity.mClient.getTable("alert");
            calendarTable = MainActivity.mClient.getTable("calendar");
            gettingStudentAttendanceTableData = MainActivity.mClient.getTable("attendance");
            feesMobileServiceJsonTable = MainActivity.mClient.getTable("fees_status");
        } catch (Exception e) {
            System.out.println("Exception message " + e.getMessage());
        }
        studentProgressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        studentProgressDialog.setCancelable(false);
        studentProgressDialog.setIndeterminate(false);

        databaseHandler = new DatabaseHandler(getActivity());

        dashboard_data_Handler = new Handler();
        dashboard_data_Timer = new Timer();
        dashboard_timeOutTimerClass = new TimeOutTimerClass();
        setHasOptionsMenu(true);

        /*
            creating directory for storing profile pic

         */

        File dir = new File(Environment.getExternalStorageDirectory(), "/Sparsh/Profile_Pic");
        directory = dir.getPath();
        System.out.println("Directory created " + directory);

        try {
            System.out.println("directory creation block");
            if (dir.mkdir()) {
                Log.d("Directory created", "");
            } else {
                Log.d("Directory not created", "");
            }
            System.out.println("directory creation block END");
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTIION");
            e.printStackTrace();
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.refresh_dashboard) {

            if (connectionDetector.isConnectingToInternet()) {
                //Rest calling done here after connection check
                reScheduleTimer();
            } else {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
            System.out.println("refresh_dashboard call fetch dashboard data......");
        }

        return super.onOptionsItemSelected(item);
    }

    private void reScheduleTimer() {
        dashboard_data_Timer = new Timer();
        dashboard_timeOutTimerClass = new TimeOutTimerClass();
        getDashboardData = new FetchData();
        getDashboardData.execute();

        //downloadProfilePic();
        System.out.println("Download profile pic call ");

        dashboard_data_Timer.schedule(dashboard_timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("2.   reScheduleTimer");
        setNotificationData();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard_student, container, false);

//        rewardsIcon = (ImageView) rootView.findViewById(R.id.rewardsImageView);
//        galleryIcon = (ImageView) rootView.findViewById(R.id.galleryClickImageView);;

//        galleryView = (View) rootView.findViewById(R.id.viewForGalleryClick);
        completedProgressArc = (ArcProgress) rootView.findViewById(R.id.attendance_progress);
        int attendance = 89;
//        completedProgressArc.setProgress(attendance);

        // Clickable views of all xml views
        noticeView = rootView.findViewById(R.id.viewForNoticeClick);
        eventsView = rootView.findViewById(R.id.viewForEventsClick);
        attendanceView = rootView.findViewById(R.id.viewForAttendanceClick);
        alertsView = rootView.findViewById(R.id.viewForAlertClick);
        galleryView = rootView.findViewById(R.id.viewForGalleryClick);
        feesView = rootView.findViewById(R.id.viewForFeesClick);
        reportsView = rootView.findViewById(R.id.viewForReportsClick);
        rewardsView = rootView.findViewById(R.id.viewForRewardsClick);
        galleryView = rootView.findViewById(R.id.viewForGalleryClick);


        notificationPanelTextView1 = (TextView) rootView.findViewById(R.id.notificationTextFirst);
        notificationPanelTextView2 = (TextView) rootView.findViewById(R.id.notificationTextSecond);
        notificationPanelTextView3 = (TextView) rootView.findViewById(R.id.notificationTextThird);

        eventPanelTextView1 = (TextView) rootView.findViewById(R.id.eventTextFirst);
        eventPanelTextView2 = (TextView) rootView.findViewById(R.id.eventTextSecond);
        eventPanelTextView3 = (TextView) rootView.findViewById(R.id.eventTextThird);

        feePanelTextView1 = (TextView) rootView.findViewById(R.id.feesTextFirst);
        feePanelTextView2 = (TextView) rootView.findViewById(R.id.feesTextSecond);
        feePanelTextView3 = (TextView) rootView.findViewById(R.id.feesTextThird);

        alertPanelTextView1 = (TextView) rootView.findViewById(R.id.alertTextFirst);
        alertPanelTextView2 = (TextView) rootView.findViewById(R.id.alertTextSecond);
        alertPanelTextView3 = (TextView) rootView.findViewById(R.id.alertTextThird);


        notification_NoDataAvailable_TextView = (TextView) rootView.findViewById(R.id.noDataNotificationDashboardTextView);
        calendar_NoDataAvailable_TextView = (TextView) rootView.findViewById(R.id.noDataCalendarDashboardTextView);
        alert_NoDataAvailable_TextView = (TextView) rootView.findViewById(R.id.noDataAlertDashboardTextView);
        fees_NoDataAvailable_TextView = (TextView) rootView.findViewById(R.id.noDataFeesDashboardTextView);


        //setting notification data on panel from sqlite
        setNotificationData();

        System.out.println("KEY_DASHBOARD_EVENTS_JSON %%%%%%%%% : " + sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_EVENTS_JSON));

        if (sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_EVENTS_JSON) == null
                || sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_FEES_JSON) == null
                || sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_ATTENDANCE_JSON) == null
                || sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_ALERT_JSON) == null) {
            System.out.println("DATA NULL IN SP");

            if (connectionDetector.isConnectingToInternet()) {
                //Rest calling done here after connection check
                getDashboardData = new FetchData();
                getDashboardData.execute();
                //downloadProfilePic();
                System.out.println("Download profile pic call ");
                dashboard_data_Timer.schedule(dashboard_timeOutTimerClass, TIMEOUT_TIME, 1000);
                System.out.println("Timer Task Calling>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            } else {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        } else {
            System.out.println("DATA IN SP");

            setDataOnDashboardPanels(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_ALERT_JSON)),
                    new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_EVENTS_JSON)),
                    new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_ATTENDANCE_JSON)),
                    new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_FEES_JSON)));
        }


        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        rewardsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent rewardsIntent = new Intent(getActivity(), Rewards.class);
                startActivity(rewardsIntent);
            }
        });

        alertsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new AlertFragment();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent noticeViewIntent = new Intent(getActivity(), Notifications.class);
//                startActivity(noticeViewIntent);
                fragment = new com.relecotech.androidsparsh.fragments.Notifications();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });

        eventsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new Calendar_Fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });

        attendanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new Attendance_Student_Fragment();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });
        feesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new FeesFragment();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });
        galleryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryViewIntent = new Intent(getActivity(), Gallery.class);
                startActivity(galleryViewIntent);
                Log.d("gallery click", "" + "gallery click");
            }
        });
        reportsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new Reports_StudentFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                Log.d("reports click", "" + "reports click");
            }
        });

        return rootView;
    }


    @TargetApi(Build.VERSION_CODES.N_MR1)
    private boolean isPermissionGranted() {
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {


            return true;
        } else {

            return false;
        }
    }

    private void setNotificationData() {
        Cursor cursor = databaseHandler.getAllNotificationDataByCursor();
        System.out.println("DatabaseHandler FINISHED");
        int cursorSize = cursor.getCount();
        System.out.println("cursorSize : " + cursorSize);
        if (cursorSize == 0) {

            notification_NoDataAvailable_TextView.setVisibility(View.VISIBLE);
            notification_NoDataAvailable_TextView.setGravity(Gravity.CENTER);
        } else {
            cursor.moveToLast();
            loopCount = 0;
            notification_NoDataAvailable_TextView.setVisibility(View.INVISIBLE);
            for (int i = 0; i < cursorSize; i++) {
                System.out.println("DATE_TIME " + i + " :" + cursor.getString(2));
                System.out.println("TITLE " + i + " :" + cursor.getString(4));

                switch (loopCount) {
                    case 0:
                        System.out.println("value :" + getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelTextView1.setText(getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        break;
                    case 1:
                        System.out.println("value :" + getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelTextView2.setText(getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelTextView2.setGravity(Gravity.CENTER_VERTICAL);
                        break;
                    case 2:
                        System.out.println("value :" + getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelTextView3.setText(getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        break;
                }
                if (loopCount == 2) {
                    break;
                }
                loopCount++;
                cursor.moveToPrevious();
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        /* Below statment for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Dashboard");


        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back buttson
                    if (back_pressed + 2000 > System.currentTimeMillis()) {
                        // super.onBackPressed();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Press Once again to exit", Toast.LENGTH_SHORT).show();
                        back_pressed = System.currentTimeMillis();
                    }

                    return true;

                }

                return false;
            }
        });
    }

    private class FetchData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            studentProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Date date1 = new Date();

                // System.out.println(date1.getTime() + "alertFetchResponse start");
//
//                alertFetchResponse = alertJsonDataTable.parameter("role", "Student").parameter("studentId", userDetailMap.get(SessionManager.KEY_STUDENT_ID)).parameter("studentClass", userDetailMap.get(SessionManager.KEY_USER_CLASS)).parameter("studentDivision", userDetailMap.get(SessionManager.KEY_USER_DIVISON)).execute().get();
//                if (alertFetchResponse.equals(null)) {
//                    dashboard_timeOutTimerClass.check = false;
//                    System.out.println("json object null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                } else {
//                    System.out.println("json object not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    //set json response to shared pref
//                    sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_ALERT_JSON, alertFetchResponse.toString());
//                    dashboard_timeOutTimerClass.check = true;
//                }

                date1 = new Date();
                //System.out.println(date1.getTime() + " alertFetchResponse : " + alertFetchResponse);
                System.out.println(" userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID) " + userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID));

                calendarFetchResponse = calendarTable.where().field("School_Class_id").eq(userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID)).and().field("start_Date").ge(new Date()).orderBy("start_Date", QueryOrder.Ascending).top(3).execute().get();

                System.out.println("calendarFetchResponse   "+ calendarFetchResponse);
                date1 = new Date();
                // System.out.println(date1.getTime() + " calendarFetchResponse : " + calendarFetchResponse);
                if (calendarFetchResponse.equals(null)) {
                    dashboard_timeOutTimerClass.check = false;
                    System.out.println("json object Calendar  null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                } else {
                    System.out.println("json object Calendar not  null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    //set json response to shared pref
                    sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_EVENTS_JSON, calendarFetchResponse.toString());
                    dashboard_timeOutTimerClass.check = true;
                }

//                studentAttendanceTableRespone = gettingStudentAttendanceTableData.parameter("StudentID", userDetailMap.get(SessionManager.KEY_STUDENT_ID)).parameter("UserRole", "Student").execute().get();
//
//                if (studentAttendanceTableRespone.equals(null)) {
//                    dashboard_timeOutTimerClass.check = false;
//                    System.out.println("json object null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                } else {
//                    System.out.println("json object not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    //set json response to shared pref
//                    sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_ATTENDANCE_JSON, studentAttendanceTableRespone.toString());
//                    dashboard_timeOutTimerClass.check = true;
//                }

                date1 = new Date();


            } catch (Exception e) {
                System.out.println("null pointer exception *********************");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            fetchAlertData();
            fetchFeeData();
            fetchAttendanceData();

        }

    }

    private void fetchAlertData() {

        JsonObject jsonObjectAlert = new JsonObject();

        jsonObjectAlert.addProperty("userRole", userRole);
        jsonObjectAlert.addProperty("studentId", userDetailMap.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectAlert.addProperty("studentClass", userDetailMap.get(SessionManager.KEY_USER_CLASS));
        jsonObjectAlert.addProperty("studentDivision", userDetailMap.get(SessionManager.KEY_USER_DIVISON));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchAlertApi", jsonObjectAlert);
        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {


            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" fetchAlert exception    " + exception);
                dashboard_timeOutTimerClass.check = false;
                System.out.println("json fetchAlert null %%%%%%%%%%%%%%%%");

                if (dashboard_timeOutTimerClass.check) {
                    studentProgressDialog.dismiss();
                }
                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, studentAttendanceTableRespone, feesFetchResponse);


            }

            @Override
            public void onSuccess(JsonElement Response) {
                resultFuture.set(Response);
                System.out.println(" fetchAlert  API   response    " + Response);
                alertFetchResponse = Response;
                //set json response to shared pref
                sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_ALERT_JSON, alertFetchResponse.toString());
                dashboard_timeOutTimerClass.check = true;

                if (dashboard_timeOutTimerClass.check) {
                    studentProgressDialog.dismiss();
                }

                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, studentAttendanceTableRespone, feesFetchResponse);


            }
        });
    }

    private void fetchFeeData() {

        jsonObjectFeeFragDashParm = new JsonObject();
        jsonObjectFeeFragDashParm.addProperty("studentId", userDetailMap.get(SessionManager.KEY_STUDENT_ID));
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchFeesStatus", jsonObjectFeeFragDashParm);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                dashboard_timeOutTimerClass.check = false;
                System.out.println("json object null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                if (dashboard_timeOutTimerClass.check) {
                    studentProgressDialog.dismiss();
                }
                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, studentAttendanceTableRespone, feesFetchResponse);


            }

            @Override
            public void onSuccess(JsonElement Response) {
                resultFuture.set(Response);
                System.out.println(" FEES DASHBOARD  API   response    " + Response);
                feesFetchResponse = Response;
                System.out.println("json object  not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                //set json response to shared pref
                sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_FEES_JSON, feesFetchResponse.toString());
                dashboard_timeOutTimerClass.check = true;

                if (dashboard_timeOutTimerClass.check) {
                    studentProgressDialog.dismiss();
                }

                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, studentAttendanceTableRespone, feesFetchResponse);


            }
        });
    }

    private void fetchAttendanceData() {
        jsonObjectToFetchAttendance = new JsonObject();
        jsonObjectToFetchAttendance.addProperty("StudentID", userDetailMap.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectToFetchAttendance.addProperty("UserRole", "Student");

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("attendanceReviewApi", jsonObjectToFetchAttendance);
        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" fetchAttendanceData exception    " + exception);
                dashboard_timeOutTimerClass.check = false;
                System.out.println("json fetchAttendanceData null %%%%%%%%%%%%%%%%");

                if (dashboard_timeOutTimerClass.check) {
                    studentProgressDialog.dismiss();
                }
                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, studentAttendanceTableRespone, feesFetchResponse);


            }

            @Override
            public void onSuccess(JsonElement Response) {
                resultFuture.set(Response);
                System.out.println(" fetchAttendanceData arc  API   response    " + Response);
                studentAttendanceTableRespone = Response;
                System.out.println("json fetchAttendanceData  not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                //set json response to shared pref
                sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_ATTENDANCE_JSON, studentAttendanceTableRespone.toString());
                dashboard_timeOutTimerClass.check = true;

                if (dashboard_timeOutTimerClass.check) {
                    studentProgressDialog.dismiss();
                }

                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, studentAttendanceTableRespone, feesFetchResponse);


            }
        });
    }

    private void setDataOnDashboardPanels(JsonElement alertFetchResponse, JsonElement calendarFetchResponse, JsonElement studentAttendanceTableRespone, JsonElement feesFetchResponse) {

        loopCount = 0;

        //logic to set data on ALERT panel
        try {

            System.out.println("alertFetchResponse " + alertFetchResponse);
            System.out.println("alertFetchResponse getAsJsonArray" + alertFetchResponse.getAsJsonArray());
            JsonArray alertJsonArray = alertFetchResponse.getAsJsonArray();
            System.out.println("alertJsonArray.size() : " + alertJsonArray.size());
            if (alertJsonArray.size() == 0) {
                alert_NoDataAvailable_TextView.setVisibility(View.VISIBLE);
                alert_NoDataAvailable_TextView.setGravity(Gravity.CENTER);

            } else {
                loopCount = 0;
                alert_NoDataAvailable_TextView.setVisibility(View.INVISIBLE);
                for (int i = alertJsonArray.size() - 1; i >= 0; i--) {
                    JsonObject jsonObjectForIteration = alertJsonArray.get(i).getAsJsonObject();
                    String alertTitle = jsonObjectForIteration.get("Title").toString().replace("\"", "").replace("\\n", "\n");
                    //  System.out.println("title ; " + jsonObjectForIteration.get("title").toString().replace("\"", "").replace("\\n", "\n"));
                    switch (loopCount) {
                        case 0:
                            alertPanelTextView1.setText(getString(R.string.bullet_2_spaces) + alertTitle);
                            break;
                        case 1:
                            alertPanelTextView2.setText(getString(R.string.bullet_2_spaces) + alertTitle);
                            alertPanelTextView2.setGravity(Gravity.CENTER_VERTICAL);
                            break;
                        case 2:
                            alertPanelTextView3.setText(getString(R.string.bullet_2_spaces) + alertTitle);
                            break;
                    }
                    if (loopCount == 2) {
                        break;
                    }
                    loopCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Catch of Alert Logic");
            dashboard_timeOutTimerClass.check = false;
        }


        try {
            JsonArray  calendarJsonArray = calendarFetchResponse.getAsJsonArray();
            //logic to set data on CALENDAR panel
            System.out.println("calendarJsonArray.size() : " + calendarJsonArray.size());
            if (calendarJsonArray.size() == 0) {

                calendar_NoDataAvailable_TextView.setVisibility(View.VISIBLE);
                calendar_NoDataAvailable_TextView.setGravity(Gravity.CENTER);

            } else {
                calendar_NoDataAvailable_TextView.setVisibility(View.INVISIBLE);
                for (int i = calendarJsonArray.size() - 1; i >= 0; i--) {
                    JsonObject jsonObjectForIteration = calendarJsonArray.get(i).getAsJsonObject();
                    String eventTitle = jsonObjectForIteration.get("calendar_title").toString().replace("\"", "").replace("\\n", "\n");
                    String eventType = jsonObjectForIteration.get("type").toString().replace("\"", "").replace("\\n", "\n");
                    String eventDate = jsonObjectForIteration.get("start_Date").toString().replace("\"", "").replace("\\n", "\n");
                    // System.out.println("calendar_title ; " + jsonObjectForIteration.get("calendar_title").toString().replace("\"", "").replace("\\n", "\n"));

                    DateFormat calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    Date calDate = null;
                    try {
                        calDate = calendarDateFormat.parse(eventDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DateFormat targetDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                    eventDate = targetDateFormat.format(calDate);

                    String typeColor = "#F06103";
                    if (eventType.equals("Holiday")) {
                        typeColor = "#009688";
                    }
                    if (eventType.equals("Exam")) {
                        typeColor = "#009688";
                        eventType = "X";
                    }
                    eventType = "(" + eventType.substring(0, 1) + ")";
                    switch (i) {
                        case 0:
                            eventPanelTextView1.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " " + eventDate + " : " + eventTitle));
                            break;
                        case 1:
                            eventPanelTextView2.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " " + eventDate + " : " + eventTitle));
                            eventPanelTextView2.setGravity(Gravity.CENTER_VERTICAL);
                            break;
                        case 2:
                            eventPanelTextView3.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " " + eventDate + " : " + eventTitle));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Catch of CALENDAR Logic");
            dashboard_timeOutTimerClass.check = false;
        }

        //logic to set data on PERFORMANCE panel
        try {
            System.out.println("studentAttendanceTableRespone" + studentAttendanceTableRespone);
            JsonArray attendanceJsonArray = studentAttendanceTableRespone.getAsJsonArray();
            if (attendanceJsonArray.size() == 0) {
                Log.d("json not recived", "not recived");
            } else {

                int presentDaysCount = 0;

                for (int k = 0; k < attendanceJsonArray.size(); k++) {
                    JsonObject jsonObjectforIteration = attendanceJsonArray.get(k).getAsJsonObject();
                    String studentAttendaceStatus = jsonObjectforIteration.get("status").toString().replace("\"", "");
                    System.out.println("AttendanceStudentLog studentAttendaceStatus :" + studentAttendaceStatus);
                    if (studentAttendaceStatus.contains("P")) {
                        presentDaysCount++;
                        System.out.println("AttendanceStudentLog presentDaysCount :" + presentDaysCount);
                    }
                }
                System.out.println("AttendanceStudentLog total :" + (presentDaysCount * 100) / attendanceJsonArray.size());
                completedProgressArc.setProgress((presentDaysCount * 100) / attendanceJsonArray.size());

            }
        } catch (Exception e) {
            e.printStackTrace();
            dashboard_timeOutTimerClass.check = false;

        }
        try {
            //logic to set data on FEES panel
            JsonArray feesJsonArray = feesFetchResponse.getAsJsonArray();
            System.out.print("feesFetchResponse +++++++ " + feesFetchResponse);
            if (feesJsonArray.size() == 0) {
                fees_NoDataAvailable_TextView.setVisibility(View.VISIBLE);
                fees_NoDataAvailable_TextView.setGravity(Gravity.CENTER);
            } else {
                int overDueCount = 0;
                fees_NoDataAvailable_TextView.setVisibility(View.INVISIBLE);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    Date nextPaymentDate = null;
                    int nextPayAmount = 0;
                    int nextPayCount = 0;
                    for (int loopI = 0; loopI < feesJsonArray.size(); loopI++) {
                        JsonObject jsonObjectForIteration = feesJsonArray.get(loopI).getAsJsonObject();

                        System.out.println("fee_status ; " + jsonObjectForIteration.get("fee_status").toString().replace("\"", ""));
                        System.out.println("amount ; " + jsonObjectForIteration.get("amount").toString().replace("\"", ""));
                        System.out.println("paid_date ; " + jsonObjectForIteration.get("paid_date").toString().replace("\"", ""));
                        System.out.println("due_date ; " + jsonObjectForIteration.get("due_date").toString().replace("\"", ""));

                        if (jsonObjectForIteration.get("paid_date").isJsonNull()) {
                            System.out.println("---------------------");

                            String feeDueDateString = jsonObjectForIteration.get("due_date").toString().replace("\"", "");
                            Date feeDueDate = dateFormat.parse(feeDueDateString);
                            System.out.println("feeDueDate.compareTo(new Date() ; " + feeDueDate.compareTo(new Date()));

                            switch (feeDueDate.compareTo(new Date())) {
                                case -1:
                                    System.out.println(" case -1:");
                                    overDueCount++;
                                    nextPayCount++;
                                    break;
                                case 1:
                                    System.out.println(" case 1:");

                                    if (nextPaymentDate == null) {
                                        nextPaymentDate = feeDueDate;
                                        nextPayAmount += Integer.parseInt(jsonObjectForIteration.get("amount").toString().replace("\"", ""));

                                    } else {
                                        if (feeDueDate.compareTo(nextPaymentDate) < 0) {
                                            nextPaymentDate = feeDueDate;
                                            nextPayAmount = Integer.parseInt(jsonObjectForIteration.get("amount").toString().replace("\"", ""));
                                        }
                                    }
                                    break;
                                case 0:
                                    System.out.println("SET TODAY AS PAYMENT DATE");
                                    nextPayAmount += Integer.parseInt(jsonObjectForIteration.get("amount").toString().replace("\"", ""));
                                    break;
                            }
                        }

                    }
                    System.out.println("overDueCount : " + overDueCount);
//                     System.out.println("nextPaymentDate : " + nextPaymentDate.toString());
                    System.out.println("nextPayAmount : " + nextPayAmount);

                    dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());


                    if (nextPayAmount == 0) {

                        if (overDueCount > 0) {

                            feePanelTextView2.setText(nextPayCount + " Pending Fees");
                        } else {

                            feePanelTextView2.setText("No Pending Fees");
                        }

                    } else {
                        feePanelTextView1.setText("Next pay ₹" + nextPayAmount);
                        feePanelTextView2.setText("On " + dateFormat.format(nextPaymentDate));
                    }

                    if (overDueCount > 0) {
                        feePanelTextView3.setText(overDueCount + " Fee overdue");
                        feePanelTextView3.setTextColor(Color.parseColor("#FFC63D3D"));

                    } else {
                        feePanelTextView3.setText(" No overdue");
                        feePanelTextView3.setTextColor(Color.parseColor("#6c6c6c"));
                    }
                } catch (Exception e) {
                    System.out.println("Cathc of Fees Logic");
                    dashboard_timeOutTimerClass.check = false;
                }
            }
        } catch (Exception e) {
            System.out.println("Cathc of Fees Logic");
            dashboard_timeOutTimerClass.check = false;
        }
    }

    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            dashboard_data_Handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("check 22222222222222222222222222 " + check);
                    try{
                        if (!check) {
                            // fetechAlertDataTask.cancel(true);
                            dashboard_data_Timer.cancel();
                            getDashboardData.cancel(true);
                            studentProgressDialog.dismiss();
                            System.out.println("if condtion check is true");

                            Snackbar.make(alertsView, R.string.check_network, Snackbar.LENGTH_SHORT).show();

                        } else {
                        /*
                              this line of code is used when everything goes normal .
                              and cancel the timer.
                         */
                            System.out.println("Dashboard timer Cancel Before CALLING");
                            dashboard_data_Timer.cancel();
                            Date lastUpdated_Date = new Date();
                            //  Snackbar.make(alertsView, "Last Rest Calling" + lastUpdated_Date.getTime(), Snackbar.LENGTH_LONG).show();
                            System.out.println("Dashboard timer Cancel After CALLING");
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                }
            });
        }
    }
}



