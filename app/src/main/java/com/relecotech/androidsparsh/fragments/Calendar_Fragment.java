package com.relecotech.androidsparsh.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marcohc.robotocalendar.RobotoCalendarView;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.CalendarDialogAdapter;
import com.relecotech.androidsparsh.adapters.CalenderPagerAdapter;
import com.relecotech.androidsparsh.controllers.CalendarDialogListData;
import com.relecotech.androidsparsh.controllers.CalendarFragmentListData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amey on 10/16/2015.
 */
public class Calendar_Fragment extends android.support.v4.app.Fragment implements RobotoCalendarView.RobotoCalendarListener {

    ListView calendarlistView;
    List<String> calendarHolidaysList;
    ArrayAdapter<String> stringArrayAdapter;
    public String userRole;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    private Fragment mFragment;
    private FragmentManager fragmentManager;

    //components for calendar view part
    CalendarView calendarView;
    private RobotoCalendarView robotoCalendarView;

    //month index for calendar page calculation
    // relative with current month (not with january)
    private int currentMonthIndex;
    // actual month index (W.R.T. january)
    public static int actualCurrentMonthIndex;

    public static Calendar currentCalendar;
    ViewPager pager;
    TabLayout tabLayout;
    Context context;
    private JsonObject calendarParamsJsonObject;
    private Calendar todaysCalendar;

    //Static bcoz these are goning to be used in caledar's individual fragments
    public static JsonElement calendar_results_json_element;
    public static JsonElement exam_results_json_element;
    public static JsonElement attenmdance_results_json_element;
    private Calendar calendarForMapFunctions;
    private List calendarEntryList;
    private List calendarEntryListForIteration;

    private Map<String, List<CalendarFragmentListData>> dateEntryMap;
    private Map<Integer, Map<String, List<CalendarFragmentListData>>> monthEntriesMap;
    static Map<String, Map<Integer, Map<String, List<CalendarFragmentListData>>>> combinedEntryMap;

    private Map<String, List<CalendarFragmentListData>> dateEntryMapForIteration;
    private Map<Integer, Map<String, List<CalendarFragmentListData>>> monthEntriesMapForIteration;
    private Date todaysDate;
    private int currentTab = 0;
    private ProgressDialog calendarProgressDialog;
    private TimeOutTimerClass timeOutTimerClass;
    private Timer calendar_Timer;
    private Handler calendar_Handler;
    long TIMEOUT_TIME = 25000;
    private SessionManager manager;
    private boolean flagRefresh = true;
    private String timeFinal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        todaysCalendar = Calendar.getInstance(Locale.getDefault());
        calendarForMapFunctions = Calendar.getInstance(Locale.getDefault());
        actualCurrentMonthIndex = calendarForMapFunctions.get(Calendar.MONTH);

        calendarProgressDialog = new ProgressDialog(getActivity());
        calendarProgressDialog.setCancelable(false);
        calendarProgressDialog.setMessage(getActivity().getString(R.string.loading));

        calendarProgressDialog.show();

        timeOutTimerClass = new TimeOutTimerClass();
        calendar_Timer = new Timer();
        calendar_Handler = new Handler();

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        calendarParamsJsonObject = new JsonObject();

        manager = new SessionManager(getActivity());
        HashMap<String, String> userDetails = manager.getUserDetails();

        calendarEntryList = new ArrayList();
        calendarEntryListForIteration = new ArrayList();
        dateEntryMap = new HashMap<>();
        monthEntriesMap = new HashMap<>();
        combinedEntryMap = new HashMap<>();

        dateEntryMapForIteration = new HashMap<>();
        monthEntriesMapForIteration = new HashMap<>();

        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        try {

            if (userRole.equals("Teacher")) {
                calendarParamsJsonObject.addProperty("ClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                calendarParamsJsonObject.addProperty("Role", "Teacher");
            }
            if (userRole.equals("Student")) {
                calendarParamsJsonObject.addProperty("ClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                calendarParamsJsonObject.addProperty("StudentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
                calendarParamsJsonObject.addProperty("Role", "Student");
            }
        } catch (Exception e) {
            System.out.println(" Inside Calender_Fragment Catch");
            System.out.println(e.getMessage());
        }

        setHasOptionsMenu(true);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.calendar_student, container, false);

        robotoCalendarView = (RobotoCalendarView) rootView.findViewById(R.id.robotoCalendarView);

        // Set listener, in this case, the same activity
        robotoCalendarView.setRobotoCalendarListener(this);
        // Initialize the RobotoCalendarPicker with the current index and date
        currentMonthIndex = 0;
        currentCalendar = Calendar.getInstance(Locale.getDefault());
        todaysDate = currentCalendar.getTime();
        // Mark current da0y
        robotoCalendarView.markDayAsSelectedDay(todaysDate);

        pager = (ViewPager) rootView.findViewById(R.id.calendar_view_pager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Events"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("Holidays"), 1);
        tabLayout.addTab(tabLayout.newTab().setText("Exams"), 2);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if (userRole.equals("Student")) {
            tabLayout.addTab(tabLayout.newTab().setText("Attendance"), 3);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

        if (manager.getSharedPrefItem(SessionManager.KEY_CALENDAR_JSON) == null) {
            System.out.println("CALENDAR OFFLINE LOGS : INSIDE DATA NULL");
            if (checkConnection) {
                //Rest calling done here after connection check
                onStartExecution();
            } else {
                setTabs();
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                calendarProgressDialog.dismiss();

            }
        } else {
            System.out.println("CALENDAR OFFLINE LOGS : DATA PRESENT SPLIT CALLED");
            splitData(new JsonParser().parse(manager.getSharedPrefItem(SessionManager.KEY_CALENDAR_JSON)));

        }

        return rootView;
    }

    //method to get calendar data from azure
    private void fetchCalendarData() {
        System.out.println("calling fetchCalendarData......");
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchCalendarDataApi", calendarParamsJsonObject);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Calender  api exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("Calendar API   response    " + response);
                if (response == null) {
                    System.out.println("json object null $$$$$$$$$$$$$$$$$$$$$$$");
                    timeOutTimerClass.check = false;
                    manager.setSharedPrefItem(SessionManager.KEY_CALENDAR_JSON, null);
                } else {
                    timeOutTimerClass.check = true;
                    manager.setSharedPrefItem(SessionManager.KEY_CALENDAR_JSON, response.toString());
                }
                splitData(response);
            }
        });
    }

    private void splitData(JsonElement responseJsonElement) {

        //step 1
        System.out.println("responseJsonElement " + responseJsonElement);

        JsonObject responseToJsonObject = responseJsonElement.getAsJsonObject();
        System.out.println("responseToJsonObject " + responseToJsonObject);

        //step 2
        JsonElement dataFromJsonObjectToJsonElement = responseToJsonObject.get("message");

        //step 3
        JsonObject dataFromJsonElementToJsonObject = dataFromJsonObjectToJsonElement.getAsJsonObject();

        //step 4
        calendar_results_json_element = dataFromJsonElementToJsonObject.get("calendar_Results_delimiter");
        exam_results_json_element = dataFromJsonElementToJsonObject.get("exam_Results_delimiter");
        attenmdance_results_json_element = dataFromJsonElementToJsonObject.get("attendance_Results_delimiter");

        System.out.println("attenmdance_results_json_element :" + attenmdance_results_json_element);


        setDataToMap(calendar_results_json_element, "Event");
        setDataToMap(calendar_results_json_element, "Holiday");
        setDataToMap(exam_results_json_element, "Exam");

        if (userRole.equals("Student")) {
            setDataToMap(attenmdance_results_json_element, "Attendance");
        }

        setTabs();
        flagRefresh = true;
        calendarProgressDialog.dismiss();
        setIndicatorsOnCalendar("Event");
        setIndicatorsOnCalendar("Holiday");
        setIndicatorsOnCalendar("Exam");
        if (userRole.equals("Student")) {
            setIndicatorsOnCalendar("Attendance");
        }
//                robotoCalendarView.markDayAsCurrentDay(currentCalendar.getTime());

    }

    private void setIndicatorsOnCalendar(String eventType) {

        Iterator dateEntryMapIterator = null;

        dateEntryMapForIteration = new HashMap<>();
        monthEntriesMapForIteration = new HashMap<>();
        monthEntriesMapForIteration = combinedEntryMap.get(eventType);

        try {

            System.out.println("monthEntriesMapForIteration BEFORE----- : " + monthEntriesMapForIteration);
            System.out.println("actualCurrentMonthIndex BEFORE----- : " + actualCurrentMonthIndex);
            dateEntryMapForIteration = monthEntriesMapForIteration.get(actualCurrentMonthIndex);
            System.out.println("monthEntriesMapForIteration AFTER----- : " + monthEntriesMapForIteration);

            System.out.println("dateEntryMap value : " + dateEntryMapForIteration);

            dateEntryMapIterator = dateEntryMapForIteration.entrySet().iterator();

            DateFormat format = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
            Date calDateSetter = null;
            System.out.println("dateEntryMapIterator value : " + dateEntryMapIterator);
            while (dateEntryMapIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) dateEntryMapIterator.next();

                calendarEntryListForIteration = new ArrayList();
                calendarEntryListForIteration = (ArrayList) pair.getValue();

                for (int i = 0; i < calendarEntryListForIteration.size(); i++) {
                    CalendarFragmentListData listLoopObj = (CalendarFragmentListData) calendarEntryListForIteration.get(i);
                    try {
                        calDateSetter = format.parse(listLoopObj.getCalendarDate());
                        switch (eventType) {
                            case "Event":
                                robotoCalendarView.markFirstUnderlineWithStyle(R.color.darkBlue, calDateSetter);
                                break;
                            case "Holiday":
                                robotoCalendarView.markFirstUnderlineWithStyle(R.color.darkBlue, calDateSetter);
                                break;
                            case "Exam":
                                robotoCalendarView.markDayAsCurrentDay(calDateSetter);
                                break;
                            case "Attendance":
                                System.out.println("RED MARK-------------------------------------------------------------------------");
                                robotoCalendarView.markSecondUnderlineWithStyle(R.color.darkRed, calDateSetter);
                                break;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println("listLoopObj.getCalendarDate():  " + listLoopObj.getCalendarDate());
                }
            }
        } catch (Exception nullException) {
            nullException.printStackTrace();
        }
    }

    private void setDataToMap(JsonElement jsonForMapEntry, String calendatType) {

        //getting data from calendar main fragment.
        JsonArray calendarEnrtiesJsonArray = jsonForMapEntry.getAsJsonArray();

        if ((calendarEnrtiesJsonArray.size()) == 0) {
            Log.d("NO data RECEIVED", "NO data RECEIVED");

        } else {
            SimpleDateFormat calendarDateFormat = null;

            calendarHolidaysList = new ArrayList<>();
            monthEntriesMap = new HashMap<>();

            switch (calendatType) {

                case "Holiday":
                    for (int i = 0; i <= calendarEnrtiesJsonArray.size() - 1; i++) {
                        JsonObject jsonObjectForIteration = calendarEnrtiesJsonArray.get(i).getAsJsonObject();

                        String calendarType = jsonObjectForIteration.get("type").toString().replace("\"", "");
                        if (calendarType.equals("Holiday")) {
                            String calendarTitle = jsonObjectForIteration.get("calendar_title").toString();
                            String calendarDescription = jsonObjectForIteration.get("calendar_description").toString();
                            String calendarStartDate = jsonObjectForIteration.get("start_Date").toString().replace("\"", "");
                            String calendarEndDate = jsonObjectForIteration.get("end_date").toString().replace("\"", "");

                            calendarTitle = calendarTitle.substring(1, calendarTitle.length() - 1);
                            calendarDescription = calendarDescription.substring(1, calendarDescription.length() - 1);

                            calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                            Date startDate = null;
                            Date endDate = null;
                            try {
                                startDate = calendarDateFormat.parse(calendarStartDate);
                                endDate = calendarDateFormat.parse(calendarEndDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            calendarDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
//                            robotoCalendarView.markFirstUnderlineWithStyle(R.color.darkBlue, startDate);

                            String calStartDate = calendarDateFormat.format(startDate);
                            String calEndDate = calendarDateFormat.format(endDate);

                            System.out.println("Holidays------------- ");

                            calendarForMapFunctions.setTime(startDate);
                            System.out.println("calendarForMapFunctions.get(Calendar.MONTH) :" + calendarForMapFunctions.get(Calendar.MONTH));
                            System.out.println("monthEntriesMap.containsKey :" + monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH)));

                            if (monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH))) {

                                dateEntryMap = new HashMap<>();
                                dateEntryMap = monthEntriesMap.get(calendarForMapFunctions.get(Calendar.MONTH));
//                                System.out.println("dateEntryMap main get :" + dateEntryMap);

                                if (dateEntryMap.containsKey(calStartDate)) {
                                    System.out.println("IF IF ");

                                    calendarEntryList = new ArrayList();
                                    calendarEntryList = dateEntryMap.get(calStartDate);

                                    calendarEntryList.add(new CalendarFragmentListData(calendarTitle, calendarDescription, calStartDate, "", calendarType));

                                    dateEntryMap.put(calStartDate, calendarEntryList);

                                } else {
                                    System.out.println("IF ELSE ");

                                    calendarEntryList = new ArrayList();

                                    calendarEntryList.add(new CalendarFragmentListData(calendarTitle, calendarDescription, calStartDate, "", calendarType));
                                    dateEntryMap.put(calStartDate, calendarEntryList);
                                }

                            } else {
                                System.out.println("ELSE ");

                                calendarEntryList = new ArrayList();
                                dateEntryMap = new HashMap<>();
                                calendarEntryList.add(new CalendarFragmentListData(calendarTitle, calendarDescription, calStartDate, "", calendarType));
//                                System.out.println("dateEntryMap :" + dateEntryMap);
                                dateEntryMap.put(calStartDate, calendarEntryList);

                                monthEntriesMap.put(calendarForMapFunctions.get(Calendar.MONTH), dateEntryMap);

//                                System.out.println("dateEntryMap after add:" + dateEntryMap);
//                                System.out.println("monthEntriesMap after :" + monthEntriesMap);
                            }
                        }
                    }
                    break;

                case "Event":
                    for (int i = 0; i <= calendarEnrtiesJsonArray.size() - 1; i++) {
                        JsonObject jsonObjectForIteration = calendarEnrtiesJsonArray.get(i).getAsJsonObject();

                        String calendarType = jsonObjectForIteration.get("type").toString().replace("\"", "");
                        if (calendarType.equals("Event")) {
                            String calendarTitle = jsonObjectForIteration.get("calendar_title").toString();
                            String calendarDescription = jsonObjectForIteration.get("calendar_description").toString();
                            String calendarStartDate = jsonObjectForIteration.get("start_Date").toString().replace("\"", "");
                            String calendarEndDate = jsonObjectForIteration.get("end_date").toString().replace("\"", "");

                            calendarTitle = calendarTitle.substring(1, calendarTitle.length() - 1);
                            calendarDescription = calendarDescription.substring(1, calendarDescription.length() - 1);

                            calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                            Date startDate = null;
                            Date endDate = null;
                            try {
                                startDate = calendarDateFormat.parse(calendarStartDate);
                                endDate = calendarDateFormat.parse(calendarEndDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            calendarDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

                            robotoCalendarView.markFirstUnderlineWithStyle(R.color.darkBlue, startDate);

                            System.out.println("startDate----------" + startDate.getTime());

                            long getTime = startDate.getTime();

                            //---------------------code for get time---------------------
                            Date date = new Date(getTime);
                            DateFormat formatter = new SimpleDateFormat("HH:mm a", Locale.getDefault());
                            String calTime = formatter.format(date);
                            System.out.println("get time---------------" + calTime);

                            //--------------------------code for get time ---------------------------------------

                            System.out.println("after getting time ---------------" + calTime);

                            String calStartDate = calendarDateFormat.format(startDate);
                            String calEndDate = calendarDateFormat.format(endDate);


                            calendarForMapFunctions.setTime(startDate);
                            System.out.println("calendarForMapFunctions.get(Calendar.MONTH) :" + calendarForMapFunctions.get(Calendar.MONTH));
                            System.out.println("monthEntriesMap.containsKey :" + monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH)));

                            if (monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH))) {

                                dateEntryMap = new HashMap<>();
                                dateEntryMap = monthEntriesMap.get(calendarForMapFunctions.get(Calendar.MONTH));
                                System.out.println("dateEntryMap main get :" + dateEntryMap);

                                if (dateEntryMap.containsKey(calStartDate)) {
                                    System.out.println("IF IF ");

                                    calendarEntryList = new ArrayList();
//                                    System.out.println("dateEntryMap get:" + dateEntryMap);
                                    calendarEntryList = dateEntryMap.get(calStartDate);
//                                    System.out.println("calendarEntryList : " + calendarEntryList);
                                    calendarEntryList.add(new CalendarFragmentListData(calendarTitle, calendarDescription, calStartDate, calTime, calendarType));

                                    dateEntryMap.put(calStartDate, calendarEntryList);
//                                    System.out.println("dateEntryMap aftre add:" + dateEntryMap);
//                                    System.out.println("monthEntriesMap after :" + monthEntriesMap);

                                } else {
                                    System.out.println("IF ELSE ");

                                    calendarEntryList = new ArrayList();

                                    calendarEntryList.add(new CalendarFragmentListData(calendarTitle, calendarDescription, calStartDate, calTime, calendarType));
                                    dateEntryMap.put(calStartDate, calendarEntryList);

//                                    System.out.println("dateEntryMap after add:" + dateEntryMap);
//                                    System.out.println("monthEntriesMap after :" + monthEntriesMap);
                                }

                            } else {
                                System.out.println("ELSE ");

                                calendarEntryList = new ArrayList();
                                dateEntryMap = new HashMap<>();
                                calendarEntryList.add(new CalendarFragmentListData(calendarTitle, calendarDescription, calStartDate, calTime, calendarType));
//                                System.out.println("dateEntryMap :" + dateEntryMap);
                                dateEntryMap.put(calStartDate, calendarEntryList);

                                monthEntriesMap.put(calendarForMapFunctions.get(Calendar.MONTH), dateEntryMap);
//                                System.out.println("dateEntryMap after add:" + dateEntryMap);
//                                System.out.println("monthEntriesMap after :" + monthEntriesMap);
                            }
                        }
                    }
                    break;

                case "Exam":
                    for (int i = 0; i <= calendarEnrtiesJsonArray.size() - 1; i++) {
                        JsonObject jsonObjectForIteration = calendarEnrtiesJsonArray.get(i).getAsJsonObject();

                        String calendarExamType = jsonObjectForIteration.get("examtype").toString().replace("\"", "");
                        String calendarExamYear = jsonObjectForIteration.get("acadmic_year").toString().replace("\"", "");
                        String calendarExamDate = jsonObjectForIteration.get("examdate").toString().replace("\"", "");
                        String calendarExamSubject = jsonObjectForIteration.get("subject").toString().replace("\"", "");

                        calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        Date examDate = null;
                        try {
                            examDate = calendarDateFormat.parse(calendarExamDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        calendarDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
//                        robotoCalendarView.markDayAsCurrentDay(examDate);

                        String calExamDate = calendarDateFormat.format(examDate);

                        System.out.println("Exam---------- ");

                        calendarForMapFunctions.setTime(examDate);

//                        System.out.println("calendarForMapFunctions.get(Calendar.MONTH) :" + calendarForMapFunctions.get(Calendar.MONTH));
//                        System.out.println("monthEntriesMap.containsKey :" + monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH)));

                        if (monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH))) {
                            System.out.println("************************************************************************************");

                            dateEntryMap = new HashMap<>();
                            dateEntryMap = monthEntriesMap.get(calendarForMapFunctions.get(Calendar.MONTH));
                            System.out.println("dateEntryMap main get :" + dateEntryMap);

                            if (dateEntryMap.containsKey(calExamDate)) {
                                System.out.println("IF IF ");

                                calendarEntryList = new ArrayList();
//                                System.out.println("dateEntryMap get:" + dateEntryMap);
                                calendarEntryList = dateEntryMap.get(calExamDate);
//                                System.out.println("calendarEntryList : " + calendarEntryList);
                                calendarEntryList.add(new CalendarFragmentListData(calendarExamSubject + " (" + calendarExamType + ") ", null, calExamDate, "10:30 AM", "Exam"));

//                                System.out.println("dateEntryMap before:" + dateEntryMap);
                                dateEntryMap.put(calExamDate, calendarEntryList);
//                                System.out.println("dateEntryMap after:" + dateEntryMap);

//                                System.out.println("monthEntriesMap after :" + monthEntriesMap);

                            } else {
                                System.out.println("IF ELSE ");

                                calendarEntryList = new ArrayList();

                                calendarEntryList.add(new CalendarFragmentListData(calendarExamSubject + " (" + calendarExamType + ") ", null, calExamDate, "10:30 AM", "Exam"));
                                dateEntryMap.put(calExamDate, calendarEntryList);
//                                System.out.println("dateEntryMap after add:" + dateEntryMap);
//                                System.out.println("monthEntriesMap after :" + monthEntriesMap);

                            }

                        } else {
                            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                            System.out.println("ELSE ");

                            calendarEntryList = new ArrayList();
                            dateEntryMap = new HashMap<>();
                            calendarEntryList.add(new CalendarFragmentListData(calendarExamSubject + " (" + calendarExamType + ") ", null, calExamDate, "10:30 AM", "Exam"));

//                            System.out.println("dateEntryMap :" + dateEntryMap);
                            dateEntryMap.put(calExamDate, calendarEntryList);
//                            System.out.println("dateEntryMap after add:" + dateEntryMap);

//                            System.out.println("monthEntriesMap before :" + monthEntriesMap);
                            monthEntriesMap.put(calendarForMapFunctions.get(Calendar.MONTH), dateEntryMap);
//                            System.out.println("monthEntriesMap after :" + monthEntriesMap);

                        }
                    }
                    break;

                case "Attendance":

                    for (int i = 0; i <= calendarEnrtiesJsonArray.size() - 1; i++) {
                        JsonObject jsonObjectForIteration = calendarEnrtiesJsonArray.get(i).getAsJsonObject();
                        Log.d("jsonObjectForIteration0", "" + jsonObjectForIteration);

                        String calendarAttendanceDate = jsonObjectForIteration.get("attendance_date").toString().replace("\"", "");

                        calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        Date absentDate = null;
                        try {
                            absentDate = calendarDateFormat.parse(calendarAttendanceDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        calendarDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

                        String calAbsentDate = calendarDateFormat.format(absentDate);

//                        System.out.println("Attendance------- ");

                        calendarForMapFunctions.setTime(absentDate);

                        System.out.println("calendarForMapFunctions.get(Calendar.MONTH) :" + calendarForMapFunctions.get(Calendar.MONTH));
                        System.out.println("monthEntriesMap.containsKey :" + monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH)));

                        if (monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH))) {
//                            System.out.println("************************************************************************************");
//                            System.out.println("IF");

                            dateEntryMap = new HashMap<>();
                            dateEntryMap = monthEntriesMap.get(calendarForMapFunctions.get(Calendar.MONTH));
//                            System.out.println("dateEntryMap main get :" + dateEntryMap);

                            calendarEntryList = new ArrayList();
//                            System.out.println("dateEntryMap get:" + dateEntryMap);

                            calendarEntryList.add(new CalendarFragmentListData("Absent", null, calAbsentDate, null, "Attendance"));
                            dateEntryMap.put(calAbsentDate, calendarEntryList);

//                            System.out.println("dateEntryMap after add:" + dateEntryMap);
//                            System.out.println("monthEntriesMap after :" + monthEntriesMap);

                        } else {
                            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                            System.out.println("ELSE ");

                            calendarEntryList = new ArrayList();
                            dateEntryMap = new HashMap<>();
                            calendarEntryList.add(new CalendarFragmentListData("Absent", null, calAbsentDate, null, "Attendance"));

//                            System.out.println("dateEntryMap :" + dateEntryMap);
                            dateEntryMap.put(calAbsentDate, calendarEntryList);
//                            System.out.println("dateEntryMap after add:" + dateEntryMap);

//                            System.out.println("monthEntriesMap before :" + monthEntriesMap);
                            monthEntriesMap.put(calendarForMapFunctions.get(Calendar.MONTH), dateEntryMap);
//                            System.out.println("monthEntriesMap after :" + monthEntriesMap);

                        }
                    }
                    break;
            }

            combinedEntryMap.put(calendatType, monthEntriesMap);

//            System.out.println("monthEntriesMap.size() : " + monthEntriesMap.size());
//            System.out.println("combinedEntryMap.size() : " + combinedEntryMap.size());
//            System.out.println("combinedEntryMap. : " + combinedEntryMap);

        }

    }

    private void setTabs() {
        // FragmentManager fragManager = getFragmentManager();
        FragmentManager manager = getFragmentManager();
        CalenderPagerAdapter adapter = new CalenderPagerAdapter(manager, tabLayout.getTabCount());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

        // mTabLayout.setupWithViewPager(mPager1);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTabsFromPagerAdapter(adapter);
        System.out.println("currentTab : " + currentTab);
        pager.setCurrentItem(currentTab);

    }


    private void onStartExecution() {

        fetchCalendarData();
        calendar_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("1. onExecutionStart");
    }

    private void reScheduleTimer() {
        calendarProgressDialog.show();
        calendar_Timer = new Timer("calendarTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        fetchCalendarData();
        calendar_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("2.   reScheduleTimer");

    }

    @Override
    public void onResume() {

        super.onResume();

        /* Below statment for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Calendar");
        try {
            getView().setFocusableInTouchMode(true);
        } catch (Exception nullpointer) {
            nullpointer.printStackTrace();
        }
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back buttson
                    if (userRole.equals("Teacher")) {
                        mFragment = new DashboardTeacherFragment();
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    }
                    if (userRole.equals("Student")) {
                        mFragment = new DashboardStudentFragment();
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    }
                    return true;

                }

                return false;
            }
        });
    }

    @Override
    public void onRightButtonClick() {
        currentMonthIndex++;

        actualCurrentMonthIndex++;

        if (actualCurrentMonthIndex == 12) {
            actualCurrentMonthIndex = 0;
        }
        updateCalendar();

    }

    @Override
    public void onLeftButtonClick() {
        currentMonthIndex--;

        actualCurrentMonthIndex--;

        if (actualCurrentMonthIndex == -1) {
            actualCurrentMonthIndex = 11;
        }
        updateCalendar();

    }

    private void updateCalendar() {

        currentCalendar = Calendar.getInstance(Locale.getDefault());
        currentCalendar.add(Calendar.MONTH, currentMonthIndex);
        robotoCalendarView.initializeCalendar(currentCalendar);
//        System.out.println("setIndicatorsOnCalendar(actualCurrentMonthIndex) : " + actualCurrentMonthIndex);

        //setting date indicatore on calendar
        setIndicatorsOnCalendar("Event");
        setIndicatorsOnCalendar("Holiday");
        setIndicatorsOnCalendar("Exam");
        if (userRole.equals("Student")) {
            setIndicatorsOnCalendar("Attendance");
        }

        //condition to check whether current month = to selected month
        //to show today's date as circled
        // w/o this condtn circle stayed in place even if month was changed

        if (actualCurrentMonthIndex == Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH)) {

            robotoCalendarView.markDayAsSelectedDay(todaysDate);
        }

        //get current visible tab in order to rewfrwsh data and display current tab on refresh of swipe tabs
        currentTab = pager.getCurrentItem();
        setTabs();

    }

    @Override
    public void onDateSelected(Date date) {

        ArrayList<CalendarDialogListData> dialogList = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

        LayoutInflater flater = getActivity().getLayoutInflater();
        View view = flater.inflate(R.layout.calendar_custom_dialoge, null);
        AlertDialog.Builder calendarDialog = new AlertDialog.Builder(getActivity());
        calendarDialog.setView(view);

        ListView dialogListView = (ListView) view.findViewById(R.id.calendarDialogListView);
        View dialogDivider = (View) view.findViewById(R.id.calendarDialogDivider);

        calendarDialog.setTitle(format.format(date));

        List<CalendarFragmentListData> listForIteration = null;

        Iterator combinedMapIterator = null;
        combinedMapIterator = combinedEntryMap.entrySet().iterator();

        monthEntriesMapForIteration = new HashMap<>();

        while (combinedMapIterator.hasNext()) {
            Map.Entry entryPair = (Map.Entry) combinedMapIterator.next();

            monthEntriesMapForIteration = (Map<Integer, Map<String, List<CalendarFragmentListData>>>) entryPair.getValue();
            System.out.println(" monthEntriesMapForIteration " + monthEntriesMapForIteration);
            try {

                System.out.println(" actualCurrentMonthIndex " + actualCurrentMonthIndex);
                System.out.println(" date " + date);

                listForIteration = monthEntriesMapForIteration.get(actualCurrentMonthIndex).get(format.format(date));
                System.out.println("listForIteration : " + listForIteration);
            } catch (NullPointerException nullPointer) {
                nullPointer.printStackTrace();
            }

            if (!(listForIteration == null)) {
                for (CalendarFragmentListData loopObject : listForIteration) {
                    System.out.println("onDateSelected : " + loopObject.getCalendarTitle() + loopObject.getCalendarBody());
                    dialogList.add(new CalendarDialogListData(loopObject.getCalendarTag(), loopObject.getCalendarTitle()));
                }
            }
        }

        if (dialogList.size() == 0) {
            calendarDialog.setMessage("No entries");
            dialogDivider.setVisibility(View.INVISIBLE);
        } else {

            System.out.println(" dialog list " + dialogList.size());
            CalendarDialogAdapter dialogAdapter = new CalendarDialogAdapter(getActivity(), dialogList);
            dialogListView.setAdapter(dialogAdapter);
        }

        calendarDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        calendarDialog.setCancelable(false);
        calendarDialog.create().show();
//
//        calendarEntryListForIteration = new ArrayList();
//
//        //iterating over values only
//        for (CalendarFragmentListData value :combinedEntryMap.get("Attendance").get(actualCurrentMonthIndex).get(format.format(date))) {
//            System.out.println("Value = " + value);
//        }

//        while (dateEntryMapIterator.hasNext()) {
//            Map.Entry pair = (Map.Entry) dateEntryMapIterator.next();
//            calendarEntryListForIteration = new ArrayList();
//            calendarEntryListForIteration = (ArrayList) pair.getValue();


//        dateEntryMapForIteration = combinedEntryMap.get("Event").get(3);
//        System.out.println("dateEntryMapForIteration : "+dateEntryMapForIteration);
//        System.out.println("dateEntryMapForIteration.get(format.format(date)) : "+dateEntryMapForIteration.get(format.format(date)));

//        System.out.println("combinedEntryMap.get(format.format(date) : " + combinedEntryMap.get("Event"));
//
//        dateEntryMapForIteration = new HashMap<>();
//        monthEntriesMapForIteration = new HashMap<>();
////        monthEntriesMapForIteration = combinedEntryMap.get(eventType);
//
//        monthEntriesMapForIteration = combinedEntryMap.get("Event");
//        dateEntryMapForIteration = monthEntriesMapForIteration.get(3);

    }

    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            calendar_Handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!check) {
                        calendar_Timer.cancel();
                        calendarProgressDialog.cancel();
                        try {
                            new AlertDialog.Builder(getActivity()).setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            reScheduleTimer();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (userRole.equals("Teacher")) {
//                                                Fragment fragment = new DashboardTeacherFragment();
//                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                                                ft.replace(R.id.content_frame, fragment);
//                                                ft.commit();
//                                            } else if (userRole.equals("Student")) {
//                                                Fragment fragment = new DashboardStudentFragment();
//                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                                                ft.replace(R.id.content_frame, fragment);
//                                                ft.commit();
//                                            }
                                        }
                                    }).setMessage(R.string.check_network).create().show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        calendar_Timer.cancel();

                    }

                }
            });
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
        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        if (checkConnection) {
            if (id == R.id.refresh_dashboard) {

                if (flagRefresh == true) {

                    System.out.println(" INSIDE IF onOptionsItemSelected");
                    reScheduleTimer();
                    System.out.println("if  flagRefresh " + flagRefresh);
                    flagRefresh = false;

                } else {
                    System.out.println(" INSIDE Else onOptionsItemSelected");
                    System.out.println(" else flagRefresh " + flagRefresh);
                }
            }
        } else {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    