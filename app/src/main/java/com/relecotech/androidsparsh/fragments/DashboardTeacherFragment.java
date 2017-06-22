package com.relecotech.androidsparsh.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.activities.AttendanceMark;
import com.relecotech.androidsparsh.activities.Gallery;
import com.relecotech.androidsparsh.activities.Rewards;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.relecotech.androidsparsh.MainActivity.userRole;
import static com.relecotech.androidsparsh.fragments.DashboardStudentFragment.PERMISSIONS;
import static com.relecotech.androidsparsh.fragments.DashboardStudentFragment.PERMISSION_ALL;

/**
 * Created by amey on 10/16/2015.
 */
public class DashboardTeacherFragment extends android.support.v4.app.Fragment {

    TextView startAttendanceBtn;
    TextView rememberClassTextView;
    View noticeView, eventsView, alertsView, parentZoneView, rewardsView, galleryView;
    Fragment fragment;
    String user;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    private long back_pressed;
    private HashMap<String, String> userDetailMap;
    private SessionManager sessionManager;

    private MobileServiceJsonTable alertJsonDataTable;
    private MobileServiceJsonTable parentsZoneTable;
    private MobileServiceJsonTable calendarTable;

    private JsonElement alertFetchResponse;
    private JsonElement calendarFetchResponse;
    private JsonElement parentsZoneFetchResponse;

    private TextView parentZonePanelText1;
    private TextView parentZonePanelText2;
    private TextView parentZonePanelText3;
    private ProgressBar parentZoneProgressBar;
    private TextView alertPanelText1;
    private TextView alertPanelText2;
    private TextView alertPanelText3;

    private TextView eventsPanelText1;
    private TextView eventsPanelText2;
    private TextView eventsPanelText3;

    private TextView notificationPanelText1;
    private TextView notificationPanelText2;
    private TextView notificationPanelText3;

    private DatabaseHandler databaseHandler;
    private int loopCount = 0;
    ProgressDialog teacherProgressDialog;

    Handler dashboard_data_Handler;
    TimeOutTimerClass dashboard_timeOutTimerClass;
    Timer dashboard_data_Timer;
    long TIMEOUT_TIME = 25000;
    private FetchData getDashboardData;
    private View attendancePanelBodyView;
    private View attendancePanelHeaderView;
    private TextView attendancePanelHeaderTextView;
    private TextView parentZoneNoDataAvailableText;
    private TextView alertNoDataAvailableText;
    private TextView notificationNoDataAvailableText;
    private TextView calendarNoDataAvailableText;
    HashMap<String, String> userDetails;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getActivity());
        userDetailMap = sessionManager.getUserDetails();

        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        try {

            //Azure connection components initalization
            alertJsonDataTable = MainActivity.mClient.getTable("alert");
            parentsZoneTable = MainActivity.mClient.getTable("parent_zone");
            calendarTable = MainActivity.mClient.getTable("calendar");
        } catch (Exception e) {
            System.out.println(" Inside DashboardTeacherFragment Catch");
            System.out.println(e.getMessage());
        }

        databaseHandler = new DatabaseHandler(getActivity());

        teacherProgressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        teacherProgressDialog.setCancelable(false);
        teacherProgressDialog.setIndeterminate(false);

        dashboard_data_Handler = new Handler();
        dashboard_data_Timer = new Timer();
        dashboard_timeOutTimerClass = new TimeOutTimerClass();
        setHasOptionsMenu(true);
    }


    private void reScheduleTimer() {
        dashboard_data_Timer = new Timer();
        dashboard_timeOutTimerClass = new TimeOutTimerClass();
        getDashboardData = new FetchData();
        getDashboardData.execute();
        dashboard_data_Timer.schedule(dashboard_timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("2.   reScheduleTimer");
        setNotificationData();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_teacher, container, false);


        // Clickable views of all xml views
        noticeView = rootView.findViewById(R.id.viewForNoticeClick);
        eventsView = rootView.findViewById(R.id.viewForEventsClick);
        rewardsView = rootView.findViewById(R.id.viewForRewardsClick);
        galleryView = rootView.findViewById(R.id.viewForGalleryClick);
        alertsView = rootView.findViewById(R.id.viewForAlertClick);
        parentZoneView = rootView.findViewById(R.id.viewForParentZoneClick);

        //all compo of alert panel.
        rememberClassTextView = (TextView) rootView.findViewById(R.id.attendanceRememberClassTextView);
        startAttendanceBtn = (TextView) rootView.findViewById(R.id.attendanceMarkTextView);
        attendancePanelHeaderView = (View) rootView.findViewById(R.id.viewFeesColor);
        attendancePanelBodyView = (View) rootView.findViewById(R.id.viewAttendanceBg);
        attendancePanelHeaderTextView = (TextView) rootView.findViewById(R.id.textViewFeesitle);

        System.out.println("attendancePanelHeaderView-----------------"+attendancePanelHeaderView);
        System.out.println("attendancePanelBodyView-----------------"+attendancePanelBodyView);
        System.out.println("attendancePanelHeaderTextView-----------------"+attendancePanelHeaderTextView);
        System.out.println("rememberClassTextView-----------------"+rememberClassTextView);
        System.out.println("startAttendanceBtn-----------------"+startAttendanceBtn);




        notificationPanelText1 = (TextView) rootView.findViewById(R.id.notificationTextFirst);
        notificationPanelText2 = (TextView) rootView.findViewById(R.id.notificationTextSecond);
        notificationPanelText3 = (TextView) rootView.findViewById(R.id.notificationTextThird);

        eventsPanelText1 = (TextView) rootView.findViewById(R.id.eventTextFirst);
        eventsPanelText2 = (TextView) rootView.findViewById(R.id.eventTextSecond);
        eventsPanelText3 = (TextView) rootView.findViewById(R.id.eventTextThird);

        alertPanelText1 = (TextView) rootView.findViewById(R.id.alertTextFirst);
        alertPanelText2 = (TextView) rootView.findViewById(R.id.alertTextSecond);
        alertPanelText3 = (TextView) rootView.findViewById(R.id.alertTextThird);

        parentZonePanelText1 = (TextView) rootView.findViewById(R.id.parent_zoneTextFirst);
        parentZonePanelText2 = (TextView) rootView.findViewById(R.id.parent_zoneTextSecond);
        parentZonePanelText3 = (TextView) rootView.findViewById(R.id.parent_zoneTextThird);


//        rewardsIcon = (ImageView) rootView.findViewById(R.id.rewardsImageView);
//        galleryIcon = (ImageView) rootView.findViewById(R.id.galleryClickImageView);



//        attendancePanelHeaderView = (View) rootView.findViewById(R.id.viewAttendanceColor);
//        attendancePanelBodyView = (View) rootView.findViewById(R.id.viewAttendanceBg);
//        attendancePanelHeaderTextView = (TextView) rootView.findViewById(R.id.textViewAttendanceTitle);
//

//        parentZoneRotateLoading = (RotateLoading) rootView.findViewById(R.id.dashParentZoneRotateLoading);
//        alertRotateLoading = (RotateLoading) rootView.findViewById(R.id.dashAlertRotateLoading);
//        eventRotateLoading = (RotateLoading) rootView.findViewById(R.id.dashEventRotateLoading);


        //No data available textViews
        notificationNoDataAvailableText = (TextView) rootView.findViewById(R.id.noDataNotificationDashboardTextView);
        calendarNoDataAvailableText = (TextView) rootView.findViewById(R.id.noDataCalendarDashboardTextView);
        alertNoDataAvailableText = (TextView) rootView.findViewById(R.id.noDataAlertDashboardTextView);
        parentZoneNoDataAvailableText = (TextView) rootView.findViewById(R.id.noDataParentZoneDashboardTextView);


        setNotificationData();

        if (sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_EVENTS_JSON) == null
                || sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_PARENTZONE_JSON) == null
                || sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_ALERT_JSON) == null) {

            System.out.println("Data In SP ------------ IF");
            if (connectionDetector.isConnectingToInternet()) {

                //Rest calling done here after connection check
                //new FetchData().execute();

                getDashboardData = new FetchData();
                getDashboardData.execute();
                dashboard_data_Timer.schedule(dashboard_timeOutTimerClass, TIMEOUT_TIME, 1000);
                System.out.println("Timer Task Calling>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            } else {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        } else {
            System.out.println("DATA IN SP");

            System.out.println("setDataOnDashboardPanels Method Called  -------------------  DATA IN SP ");

            setDataOnDashboardPanels(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_ALERT_JSON)),
                    new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_EVENTS_JSON)),
                    new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_DASHBOARD_PARENTZONE_JSON)));
        }

        //setting primary class to (remember class) text view. * If teacher is primary teacher
        try {

            System.out.println("userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID) " + userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID));
            if (userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID).equalsIgnoreCase("null")) {
                System.out.println("NON PRIMARY teacher");
                attendancePanelHeaderView.setVisibility(View.GONE);
                attendancePanelBodyView.setVisibility(View.GONE);
                attendancePanelHeaderTextView.setVisibility(View.GONE);
                startAttendanceBtn.setVisibility(View.GONE);
                rememberClassTextView.setVisibility(View.GONE);
                alertNoDataAvailableText.setGravity(Gravity.CENTER_HORIZONTAL);

            } else {
                System.out.println("PRIMARY teacher");
                rememberClassTextView.setText(String.format("%s%s%s %s", "Class : ", userDetailMap.get(SessionManager.KEY_USER_CLASS), getClassSuffix(userDetailMap.get(SessionManager.KEY_USER_CLASS)), userDetailMap.get(SessionManager.KEY_USER_DIVISON)));
            }
        } catch (Exception e) {

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
        parentZoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new Parent_control();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new com.relecotech.androidsparsh.fragments.Notifications();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });
        eventsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                fragment = new Calendar_Teacher_Fragment();
                fragment = new Calendar_Fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });
        startAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //first check if attendance is marked or not
                // if not then mark else show status
                boolean statusAttendance = checkAttendanceStatus();
                System.out.println("statusAttendance  : " + statusAttendance);
                System.out.println("checkAttendanceStatus() : " + checkAttendanceStatus());
                if (!checkAttendanceStatus()) {

                    System.out.println("inside if block : ");
                    System.out.println("KEY_USER_CLASS : " + userDetailMap.get(SessionManager.KEY_USER_CLASS));
                    System.out.println("KEY_USER_DIVISON : " + userDetailMap.get(SessionManager.KEY_USER_DIVISON));

                    Intent markAttendanceIntent = new Intent(getActivity(), AttendanceMark.class);
                    markAttendanceIntent.putExtra("schoolClassId", userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                    markAttendanceIntent.putExtra("Task", "MarkAttendance");
                    markAttendanceIntent.putExtra("Class", userDetailMap.get(SessionManager.KEY_USER_CLASS));
                    markAttendanceIntent.putExtra("Division", userDetailMap.get(SessionManager.KEY_USER_DIVISON));
                    startActivity(markAttendanceIntent);
                } else {
                    System.out.println("inside else block : ");

                    Toast.makeText(getActivity(), "Attendance already marked", Toast.LENGTH_SHORT).show();
                }
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

    private void setDataOnDashboardPanels(JsonElement alertFetchResponse, JsonElement calendarFetchResponse, JsonElement parentsZoneTableJsonResponse) {

        loopCount = 0;
        //logic to set data on PARENT ZONE panel

        System.out.println("alertFetchResponse-----------" + alertFetchResponse);
        System.out.println("calendarFetchResponse-----------" + calendarFetchResponse);
        System.out.println("parentsZoneTableJsonResponse-----------" + parentsZoneTableJsonResponse);

        try {
            JsonArray parentZonejsonArray = parentsZoneTableJsonResponse.getAsJsonArray();
            System.out.println("parentsZoneTableJsonResponse array size" + parentZonejsonArray.size());
            if (parentZonejsonArray.size() == 0) {
                System.out.println("---------json not recived------" + parentZonejsonArray);


                parentZoneNoDataAvailableText.setVisibility(View.VISIBLE);
                parentZoneNoDataAvailableText.setGravity(Gravity.CENTER);

            } else {
                loopCount = 0;
                //loop is inverse : json parsing starts from oldest to newest, we ned newest
                parentZoneNoDataAvailableText.setVisibility(View.INVISIBLE);
                System.out.println("Parentzone INVISIBLE");
                for (int k = parentZonejsonArray.size() - 1; k >= 0; k--) {

                    JsonObject jsonObjectforIteration = parentZonejsonArray.get(k).getAsJsonObject();

                    String category = jsonObjectforIteration.get("category").toString().replace("\"", "").replace("\\n", "\n");
                    String cause = jsonObjectforIteration.get("description_cause").toString().replace("\"", "").replace("\\n", "\n");

                    String categoryColor = "#F06103";
                    if (category.equals("Leave")) {
                        categoryColor = "#009688";
                    }
                    switch (loopCount) {
                        case 0:
                            parentZonePanelText1.setText(Html.fromHtml("<i><font color=\"" + categoryColor + "\">" + category + "</font></i>" + " : " + cause));
                            break;
                        case 1:
                            parentZonePanelText2.setText(Html.fromHtml("<i><font color=\"" + categoryColor + "\">" + category + "</font></i>" + " : " + cause));
                            parentZonePanelText2.setGravity(Gravity.CENTER_VERTICAL);
                            break;
                        case 2:
                            parentZonePanelText3.setText(Html.fromHtml("<i><font color=\"" + categoryColor + "\">" + category + "</font></i>" + " : " + cause));
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
            System.out.println("Catch of parentZoneDashboard Logic");
            dashboard_timeOutTimerClass.check = false;
        }

        //logic to set data on ALERT panel
        try {
            JsonArray alertJsonArray = alertFetchResponse.getAsJsonArray();
            System.out.println("alertJsonArray.size() : " + alertJsonArray.size());
            if (alertJsonArray.size() == 0) {

                alertNoDataAvailableText.setVisibility(View.VISIBLE);
                alertNoDataAvailableText.setGravity(Gravity.CENTER);

            } else {
                loopCount = 0;
                alertNoDataAvailableText.setVisibility(View.INVISIBLE);
                for (int i = alertJsonArray.size() - 1; i >= 0; i--) {
                    JsonObject jsonObjectForIteration = alertJsonArray.get(i).getAsJsonObject();
                    String alertTitle = jsonObjectForIteration.get("Title").toString().replace("\"", "").replace("\\n", "\n");
                    System.out.println("title ; " + jsonObjectForIteration.get("Title").toString().replace("\"", "").replace("\\n", "\n"));
                    switch (loopCount) {
                        case 0:
                            alertPanelText1.setText(getString(R.string.bullet_2_spaces) + alertTitle);
                            break;
                        case 1:
                            alertPanelText2.setText(getString(R.string.bullet_2_spaces) + alertTitle);
                            alertPanelText2.setGravity(Gravity.CENTER_VERTICAL);
                            break;
                        case 2:
                            alertPanelText3.setText(getString(R.string.bullet_2_spaces) + alertTitle);
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
            System.out.println("Catch of Logic Alert Dashboard ");
            dashboard_timeOutTimerClass.check = false;
        }

        //logic to set data on CALENDAR panel
        try {
            JsonArray calendarJsonArray = calendarFetchResponse.getAsJsonArray();
            System.out.println("alertJsonArray.size() : " + calendarJsonArray.size());
            if (calendarJsonArray.size() == 0) {
                calendarNoDataAvailableText.setVisibility(View.VISIBLE);
                calendarNoDataAvailableText.setGravity(Gravity.CENTER);

            } else {
                calendarNoDataAvailableText.setVisibility(View.INVISIBLE);
                for (int i = calendarJsonArray.size() - 1; i >= 0; i--) {
                    JsonObject jsonObjectForIteration = calendarJsonArray.get(i).getAsJsonObject();
                    String eventTitle = jsonObjectForIteration.get("calendar_title").toString().replace("\"", "").replace("\\n", "\n");
                    String eventType = jsonObjectForIteration.get("type").toString().replace("\"", "").replace("\\n", "\n");
                    System.out.println("calendar_title ; " + jsonObjectForIteration.get("calendar_title").toString().replace("\"", "").replace("\\n", "\n"));

                    String eventDate = jsonObjectForIteration.get("start_Date").toString().replace("\"", "").replace("\\n", "\n");
                    System.out.println("calendar_title ; " + jsonObjectForIteration.get("calendar_title").toString().replace("\"", "").replace("\\n", "\n"));

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
                            eventsPanelText1.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " " + eventDate + " : " + eventTitle));
                            break;
                        case 1:
                            eventsPanelText2.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " " + eventDate + " : " + eventTitle));
                            eventsPanelText2.setGravity(Gravity.CENTER_VERTICAL);
                            break;
                        case 2:
                            eventsPanelText3.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " " + eventDate + " : " + eventTitle));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Catch of Logic Calendar");
            dashboard_timeOutTimerClass.check = false;
        }
    }

    private void setNotificationData() {

        Cursor cursor = databaseHandler.getAllNotificationDataByCursor();
        System.out.println("DatabaseHandler FINISHED");
        int cursorSize = cursor.getCount();
        System.out.println("cursorSize : " + cursorSize);
        if (cursorSize == 0) {

//            notificationPanelText2.setText(Html.fromHtml("<i><font color=\"#0d3e60\"> No Notifications </font></i>"));
//            notificationPanelText2.setGravity(Gravity.CENTER);

            notificationNoDataAvailableText.setVisibility(View.VISIBLE);
            notificationNoDataAvailableText.setText(Html.fromHtml("<i><font color=\"#0d3e60\"> No Notifications </font></i>"));
            notificationNoDataAvailableText.setGravity(Gravity.CENTER);

        } else {
            cursor.moveToLast();
            loopCount = 0;
            notificationNoDataAvailableText.setVisibility(View.INVISIBLE);
            for (int i = 0; i < cursorSize; i++) {
                System.out.println("DATE_TIME " + i + " :" + cursor.getString(2));
                System.out.println("TITLE " + i + " :" + cursor.getString(4));

                switch (loopCount) {
                    case 0:
//                    notificationPanelText1.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " : " + eventTitle));
                        System.out.println("value :" + getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelText1.setText(getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        break;
                    case 1:
//                    notificationPanelText2.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " : " + eventTitle));
                        System.out.println("value :" + getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelText2.setText(getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelText2.setGravity(Gravity.CENTER_VERTICAL);
                        break;
                    case 2:
//                    notificationPanelText3.setText(Html.fromHtml("<i><font color=\"" + typeColor + "\">" + eventType + "</font></i>" + " : " + eventTitle));
                        System.out.println("value :" + getString(R.string.bullet_2_spaces) + cursor.getString(4));
                        notificationPanelText3.setText(getString(R.string.bullet_2_spaces) + cursor.getString(4));
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

    //method to get suffix to class(number)
    private String getClassSuffix(String clazz) {
        switch (clazz) {

            case "1":
                clazz = "st";
                break;
            case "2":
                clazz = "nd";
                break;
            case "3":
                clazz = "rd";
                break;
            default:
                clazz = "th";
        }
        return clazz;
    }

    private boolean checkAttendanceStatus() {
        //getting current date and comparing with saved date
        String todaysDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        System.out.println(" todaysDate " + todaysDate);
        System.out.println(" sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE) " + sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE));
        return todaysDate.equals(sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE));

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
        private JsonElement alertJsonElement;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            teacherProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Date date1 = new Date();
                System.out.println(date1.getTime() + "alertFetchResponse start");

//                alertFetchResponse = alertJsonDataTable.parameter("role", "Teacher").parameter("teacherRecordId", userDetailMap.get(SessionManager.KEY_TEACHER_RECORD_ID)).execute().get();
//                date1 = new Date();
//                System.out.println(date1.getTime() + " alertFetchResponse : " + alertFetchResponse);
//                if (alertFetchResponse.equals(null)) {
//                    dashboard_timeOutTimerClass.check = false;
//                    System.out.println("json object null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                } else {
//                    System.out.println("json object not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    //set json response to shared pref
//                    sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_ALERT_JSON, alertFetchResponse.toString());
//                    dashboard_timeOutTimerClass.check = true;
//                }

                calendarFetchResponse = calendarTable.where().field("School_Class_id").eq(userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID)).and().field("start_Date").ge(new Date()).orderBy("start_Date", QueryOrder.Ascending).top(3).execute().get();
                date1 = new Date();
                System.out.println(date1.getTime() + " calendarFetchResponse : " + calendarFetchResponse);
                if (calendarFetchResponse.equals(null)) {
                    dashboard_timeOutTimerClass.check = false;
                    System.out.println("json object null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                } else {
                    System.out.println("json object not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    //set json response to shared pref
                    sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_EVENTS_JSON, calendarFetchResponse.toString());
                    dashboard_timeOutTimerClass.check = true;
                }
//                parentsZoneTableJsonResponse = parentsZoneTable.parameter("TAG", "Parent_Control_Frag")
//                        .parameter("SchoolClassId", userDetailMap.get(SessionManager.KEY_SCHOOL_CLASS_ID))
//                        .parameter("StudentId", userDetailMap.get(SessionManager.KEY_STUDENT_ID))
//                        .parameter("TeacherId", userDetailMap.get(SessionManager.KEY_TEACHER_RECORD_ID))
//                        .parameter("UserRole", "Teacher").execute().get();
//
//                date1 = new Date();
//                System.out.println(date1.getTime() + " parentsZoneTableJsonResponse " + parentsZoneTableJsonResponse);
//                if (parentsZoneTableJsonResponse.equals(null)) {
//                    dashboard_timeOutTimerClass.check = false;
//                    System.out.println("json object null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                } else {
//                    System.out.println("json object not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    //set json response to shared pref
//                    sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_PARENTZONE_JSON, parentsZoneTableJsonResponse.toString());
//                    dashboard_timeOutTimerClass.check = true;
//                }


            } catch (Exception e) {
                System.out.println("null pointer exception *********************");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//
//            if (dashboard_timeOutTimerClass.check) {
//                teacherProgressDialog.dismiss();
//            }
//            System.out.println("setDataOnDashboardPanels Method Called  ------------------- when Data Fetch...");
//            setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, parentsZoneTableJsonResponse);
//            JsonElement alertJsonElement = fetchAlertData();
            fetchAlertData();
            fetchParentZoneData();
        }


    }


    private JsonElement fetchAlertData() {

        JsonObject jsonObjectAlert = new JsonObject();

        jsonObjectAlert.addProperty("userRole", userRole);
        jsonObjectAlert.addProperty("teacherRecordId", userDetailMap.get(SessionManager.KEY_TEACHER_RECORD_ID));

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
                    teacherProgressDialog.dismiss();
                }

                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, parentsZoneFetchResponse);

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
                    teacherProgressDialog.dismiss();
                }

                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, parentsZoneFetchResponse);
            }
        });
        return alertFetchResponse;
    }

    private void fetchParentZoneData() {

        JsonObject jsonObjectParentZone = new JsonObject();

        jsonObjectParentZone.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
        jsonObjectParentZone.addProperty("TAG", "Parent_Control_Frag");
        jsonObjectParentZone.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectParentZone.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectParentZone.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchParentZoneApi", jsonObjectParentZone);


        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Parent Zone  DASHBOARD exception    " + exception);
                dashboard_timeOutTimerClass.check = false;
                System.out.println("json object null %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                if (dashboard_timeOutTimerClass.check) {
                    teacherProgressDialog.dismiss();
                }

                System.out.println("Parent exception  alertFetchResponse " + alertFetchResponse);
                System.out.println("Parent exception  calendarFetchResponse " + calendarFetchResponse);
                System.out.println("Parent exception  parentsZoneFetchResponse " + parentsZoneFetchResponse);
                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, parentsZoneFetchResponse);


            }

            @Override
            public void onSuccess(JsonElement Response) {
                resultFuture.set(Response);
                System.out.println(" Parent Zone  DASHBOARD  API   response    " + Response);
                parentsZoneFetchResponse = Response;
                System.out.println("json object  not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                //set json response to shared pref
                sessionManager.setSharedPrefItem(SessionManager.KEY_DASHBOARD_PARENTZONE_JSON, parentsZoneFetchResponse.toString());
                dashboard_timeOutTimerClass.check = true;

                if (dashboard_timeOutTimerClass.check) {
                    teacherProgressDialog.dismiss();
                }

                System.out.println("Parent   alertFetchResponse " + alertFetchResponse);
                System.out.println("Parent   calendarFetchResponse " + calendarFetchResponse);
                System.out.println("Parent   parentsZoneFetchResponse " + parentsZoneFetchResponse);
                //mtd to set data on dashboard panels
                setDataOnDashboardPanels(alertFetchResponse, calendarFetchResponse, parentsZoneFetchResponse);


            }
        });
    }


    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;


        @Override
        public void run() {
            dashboard_data_Handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("check @@@@@@@@@@@@@@@ " + check);
                    if (!check) {
                        // fetechAlertDataTask.cancel(true);
                        dashboard_data_Timer.cancel();
                        getDashboardData.cancel(true);
                        teacherProgressDialog.dismiss();
                        System.out.println("if condtion check is true");

                        Snackbar.make(alertsView, R.string.check_network, Snackbar.LENGTH_SHORT).show();

                    } else {
                        /*
                              this line of code is used when everything goes normal .
                              and cancel the timer.
                         */
                        System.out.println("Dashboard timer Cancel Before CALLING");
                        dashboard_data_Timer.cancel();


                    }
                }
            });
        }
    }

}
