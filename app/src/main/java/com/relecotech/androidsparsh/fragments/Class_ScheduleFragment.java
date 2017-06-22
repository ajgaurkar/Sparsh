package com.relecotech.androidsparsh.fragments;

import android.app.ProgressDialog;
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
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.ClassSchedule_PagerAdapter;
import com.relecotech.androidsparsh.controllers.StudentTimeTableListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by amey on 2/27/2016.
 */
public class Class_ScheduleFragment extends android.support.v4.app.Fragment {

    String userRole;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    private Fragment mFragment;
    private FragmentManager fragmentManager;
    private MobileServiceJsonTable class_Schedule_MobileServiceJsonTable;
    JsonElement class_Schedule_Response_JsonElement;
    HashMap<String, String> userDetails;
    SessionManager sessionManager;
    ViewPager pager;
    TabLayout tabLayout;

    private int currentTab = 0;
    private String scheduleDay;
    private String slot_start_time;
    private String slot_end_time;
    private String slot_subject;
    private String slot_type;
    private String slot_no;
    private String teacher_class;
    private String teacher_division;
    private String teacher_class_division;
    private Map<Integer, StudentTimeTableListData> slotWiseHashmapData;
    public static HashMap<String, Map<Integer, StudentTimeTableListData>> studentTimeTableHashMap;
    private StudentTimeTableListData studentTimeTableListData;
    private List testingarrayList;
    private Date class_Schedule_StartTime;
    private Date class_Schedule_EndTime;
    private String subject_teacher_first_name;
    private String subject_teacher_last_name;
    private String subject_teacher_full_name;
    private ProgressDialog class_Schedule_ProgressDialog;
    private TextView teacher_class_name_TextView;
    boolean checkconnection;
    private Handler class_schedule_Handler;
    TimeOutTimerClass timeOutTimerClass;
    Timer class_schedule_Timer;
    long TIMEOUT_TIME = 25000;
    //private Fetch_Class_Schedule_Data fetch_class_schedule_data;
    private JsonObject jsonObjectClassScheFrag;
    private boolean flagRefresh = true;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
//        class_Schedule_MobileServiceJsonTable = MainActivity.mClient.getTable("class_schedule_main");
        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getUserDetails();
        studentTimeTableHashMap = new HashMap<>();
        slotWiseHashmapData = new TreeMap<>();

        class_Schedule_ProgressDialog = new ProgressDialog(getActivity());
        class_Schedule_ProgressDialog.setCancelable(false);
        class_Schedule_ProgressDialog.setMessage(getString(R.string.loading));

        ConnectionDetector connectionDetector = new ConnectionDetector(getActivity());
        checkconnection = connectionDetector.isConnectingToInternet();
        setHasOptionsMenu(true);

        class_schedule_Handler = new Handler();
        timeOutTimerClass = new TimeOutTimerClass();
        class_schedule_Timer = new Timer();


        jsonObjectClassScheFrag = new JsonObject();

    }


    private void onExecutionStart() {
//        fetch_class_schedule_data = new Fetch_Class_Schedule_Data();
//        fetch_class_schedule_data.execute();
        fetch_class_schedule_data();
        class_schedule_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("1.   onExecutionStart");
    }


    private void reScheduleTimer() {
        class_schedule_Timer = new Timer("class_schedule_Timer", true);
        timeOutTimerClass = new TimeOutTimerClass();
//        fetch_class_schedule_data = new Fetch_Class_Schedule_Data();
        fetch_class_schedule_data();
        class_schedule_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        class_Schedule_ProgressDialog.show();
        System.out.println("2.   reScheduleTimer");
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_schedule, container, false);

        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        Log.d("login_user_role", userRole);

        pager = (ViewPager) rootView.findViewById(R.id.class_schedule_view_pager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.class_schedule_tab_layout);
        teacher_class_name_TextView = (TextView) rootView.findViewById(R.id.teacher_class_name_textView);

        tabLayout.addTab(tabLayout.newTab().setText("MON"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("TUE"), 1);
        tabLayout.addTab(tabLayout.newTab().setText("WED"), 2);
        tabLayout.addTab(tabLayout.newTab().setText("THU"), 3);
        tabLayout.addTab(tabLayout.newTab().setText("FRI"), 4);
        tabLayout.addTab(tabLayout.newTab().setText("SAT"), 5);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);


        if (sessionManager.getSharedPrefItem(SessionManager.KEY_CLASS_SCHEDULE_JSON) == null) {


            if (userRole.equals("Teacher")) {

                if (checkConnection) {
                    //Rest calling done here after connection check
                    //   new Fetch_Class_Schedule_Data().execute();
                    onExecutionStart();
                    System.out.println("Fetch Class Schedule Called @@@@@@@@@@@@@@@@@@@ ");
                    class_Schedule_ProgressDialog.show();
                    teacher_class_name_TextView.setText("Class");

                } else {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();

                }


            }
            if (userRole.equals("Student")) {
                if (checkConnection) {
                    //Rest calling done here after connection check
//                    new Fetch_Class_Schedule_Data().execute();
                    onExecutionStart();
                    System.out.println("Fetch Class Schedule Called @@@@@@@@@@@@@@@@@@@ ");
                    class_Schedule_ProgressDialog.show();
                    teacher_class_name_TextView.setText("Teacher");


                } else {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();

                }

            }
        } else {
            Class_Schedule_JSON_Parsing(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_CLASS_SCHEDULE_JSON)));

        }
        // Inflate the layout for this fragment
        return rootView;
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

    public void setTab() {
        FragmentManager manager = getFragmentManager();
        ClassSchedule_PagerAdapter adapter = new ClassSchedule_PagerAdapter(manager, tabLayout.getTabCount());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTabsFromPagerAdapter(adapter);


        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date current_date = new Date();
        String dayOfTheWeek = sdf.format(current_date);
        switch (dayOfTheWeek) {
            case "Monday":
                currentTab = 0;
                break;
            case "Tuesday":
                currentTab = 1;
                break;
            case "Wednesday":
                currentTab = 2;
                break;
            case "Thursday":
                currentTab = 3;
                break;
            case "Friday":
                currentTab = 4;
                break;
            case "Saturday":
                currentTab = 5;
                break;
            default:
                break;

        }
        System.out.println("currentTab : " + currentTab);
        pager.setCurrentItem(currentTab);
    }

    @Override
    public void onResume() {

        super.onResume();
        /* Below statment for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Class Schedule");
        getView().setFocusableInTouchMode(true);
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


    private void fetch_class_schedule_data() {

        jsonObjectClassScheFrag.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectClassScheFrag.addProperty("Teacher_Regs_Id", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));
        jsonObjectClassScheFrag.addProperty("UserRole", userDetails.get(SessionManager.KEY_USER_ROLE));
        jsonObjectClassScheFrag.addProperty("Student_School_Class_Id", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("classScheduleMainApi", jsonObjectClassScheFrag);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement Response) {
                resultFuture.set(Response);
                System.out.println(" class_Schedule_Response_JsonElement  API   response    " + Response);
                //set json response to shared pref

                class_Schedule_Response_JsonElement = Response;
                if (userRole.equals("Teacher")) {
                    try {
                        sessionManager.setSharedPrefItem(SessionManager.KEY_CLASS_SCHEDULE_JSON, class_Schedule_Response_JsonElement.toString());

                    } catch (Exception e) {
                        System.out.println("null pointer exception *********************");

                    }
                } else if (userRole.equals("Student")) {
                    try {
                        sessionManager.setSharedPrefItem(SessionManager.KEY_CLASS_SCHEDULE_JSON, class_Schedule_Response_JsonElement.toString());

                    } catch (Exception e) {
                        System.out.println("null pointer exception *********************");

                    }

                }
                Class_Schedule_JSON_Parsing(class_Schedule_Response_JsonElement);

            }
        });
    }


    public void Class_Schedule_JSON_Parsing(JsonElement class_Schedule_Response_JsonElement) {
        try {
            if (class_Schedule_Response_JsonElement.equals(null)) {
                System.out.println("class_Schedule_Response_JsonElement Null  @@@@@@ @@@@@@@@ @@@@@@ @@@" + class_Schedule_Response_JsonElement);
                timeOutTimerClass.check = false;
            } else {
                System.out.println("class_Schedule_Response_JsonElement not Null  @@@@@@ @@@@@@@@ @@@@@@ @@@" + class_Schedule_Response_JsonElement);
                timeOutTimerClass.check = true;
            }

            JsonArray classSchedulejsonArray = class_Schedule_Response_JsonElement.getAsJsonArray();
            if (classSchedulejsonArray.size() != 0) {

                String previous_end_time = classSchedulejsonArray.get(0).getAsJsonObject().get("slot_start_time").toString().replace("\"", "");
                System.out.println("previous_end_time " + previous_end_time);

                for (int loop_C = 0; loop_C < classSchedulejsonArray.size(); loop_C++) {
                    JsonObject jsonObjectforIteration = classSchedulejsonArray.get(loop_C).getAsJsonObject();
                    scheduleDay = jsonObjectforIteration.get("scheduleDay").toString().replace("\"", "");
                    slot_start_time = jsonObjectforIteration.get("slot_start_time").toString().replace("\"", "");
                    slot_end_time = jsonObjectforIteration.get("slot_end_time").toString().replace("\"", "");
                    slot_type = jsonObjectforIteration.get("slot_type").toString().replace("\"", "");
                    slot_no = jsonObjectforIteration.get("slot_no").toString().replace("\"", "");
                    slot_subject = jsonObjectforIteration.get("slot_subject").toString().replace("\"", "");


                    if (userRole.equals("Student")) {
                        subject_teacher_first_name = jsonObjectforIteration.get("firstName").toString().replace("\"", "");
                        subject_teacher_last_name = jsonObjectforIteration.get("lastName").toString().replace("\"", "");
                        subject_teacher_full_name = subject_teacher_first_name + " " + subject_teacher_last_name;
                        teacher_class_division = null;
                    }
                    if (userRole.equals("Teacher")) {
                        teacher_class = jsonObjectforIteration.get("class").toString().replace("\"", "");
                        teacher_division = jsonObjectforIteration.get("division").toString().replace("\"", "");
                        teacher_class_division = teacher_class + "" + teacher_division;
                    }


                    SimpleDateFormat recivedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    SimpleDateFormat targetedDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    recivedDateFormat.setTimeZone(TimeZone.getDefault());
                    try {
                        class_Schedule_StartTime = recivedDateFormat.parse(slot_start_time);
                        class_Schedule_EndTime = recivedDateFormat.parse(slot_end_time);
                        slot_start_time = targetedDateFormat.format(class_Schedule_StartTime);
                        slot_end_time = targetedDateFormat.format(class_Schedule_EndTime);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (studentTimeTableHashMap.containsKey(scheduleDay)) {
                        System.out.println("@@@@@@@@@@@@@@@@@@@ if ");
                        slotWiseHashmapData = new TreeMap<>();
                        slotWiseHashmapData = studentTimeTableHashMap.get(scheduleDay);
                        slotWiseHashmapData.put(Integer.valueOf(slot_no), new StudentTimeTableListData(class_Schedule_StartTime, class_Schedule_EndTime, slot_subject, subject_teacher_full_name, slot_type, teacher_class_division, false));
                        studentTimeTableHashMap.put(scheduleDay, slotWiseHashmapData);
                    } else {
                        System.out.println("@@@@@@@@@@@@@@@@@@@ else ");
                        slotWiseHashmapData = new TreeMap<>();
                        slotWiseHashmapData.put(Integer.valueOf(slot_no), new StudentTimeTableListData(class_Schedule_StartTime, class_Schedule_EndTime, slot_subject, subject_teacher_full_name, slot_type, teacher_class_division, false));
                        studentTimeTableHashMap.put(scheduleDay, slotWiseHashmapData);

                        // System.out.println("studentTimeTableHashMap.entrySet() *** else" + studentTimeTableHashMap.entrySet());
                    }


                    System.out.println("scheduleDay -----------------" + scheduleDay);
                    System.out.println("slot_start_time -------------" + slot_start_time);
                    System.out.println("slot_end_time ---------------" + slot_end_time);
                    System.out.println("slot_subject ----------------" + slot_subject);
                    System.out.println("slot_type -------------------" + slot_type);
                    System.out.println("slot_no -------------------" + slot_no);
                    System.out.println("subject_teacher_full_name -------------------" + subject_teacher_full_name);
                    System.out.println("teacher_class_division -------------------" + teacher_class_division);


                }
            } else {
                System.out.println("Class Schedule Json Array not recived");
            }


            setTab();
            flagRefresh = true;

            class_Schedule_ProgressDialog.dismiss();

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Post Execute Catch Error");
            timeOutTimerClass.check = false;
        }
    }


    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            class_schedule_Handler.post(new Runnable() {
                @Override
                public void run() {
                    class_schedule_Handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!check) {
                                class_schedule_Timer.cancel();
                                class_Schedule_ProgressDialog.dismiss();
                                System.out.println("Timer Canceled");
                                try {
                                    new android.app.AlertDialog.Builder(getActivity()).setCancelable(false)
                                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    reScheduleTimer();

                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                            /*   method is used to remove all cancelled tasks from this timer's task queue.*/
                                                    class_schedule_Timer.purge();

//                                                    Fragment fragment = new DashboardStudentFragment();
//                                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                                                    ft.replace(R.id.content_frame, fragment);
//                                                    ft.commit();
                                                }
                                            }).setMessage(R.string.check_network).create().show();
                                } catch (Exception e) {
                                         /*
                                          it may produce null pointer exception on creation of alert dialog object
                                         */
                                    System.out.println("Exception Handle for Alert Dialog");
                                }
                            } else {
                              /*  this line of code is used when everything goes normal .
                                  and cancel the timer.  */
                                class_schedule_Timer.cancel();

                            }

                        }
                    });
                }
            });

        }
    }


}
