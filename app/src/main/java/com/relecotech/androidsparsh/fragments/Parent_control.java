package com.relecotech.androidsparsh.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.relecotech.androidsparsh.activities.AddLeave;
import com.relecotech.androidsparsh.activities.AddNotes;
import com.relecotech.androidsparsh.adapters.LeavesAdapterAdapter;
import com.relecotech.androidsparsh.adapters.NotesAdapter;
import com.relecotech.androidsparsh.adapters.ParentControlPagerAdapter;
import com.relecotech.androidsparsh.controllers.LeavesListData;
import com.relecotech.androidsparsh.controllers.NotesListData;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amey on 10/16/2015.
 */
public class Parent_control extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static String loggedInUserForParentControl;
    public static LeavesListData selectedLeavesListData;
    public static NotesListData selectedNoteslistData;
    Button passcodeEnterBtn;
    List leavesArrayList;
    List notesArrayList;
    DrawerLayout drawerLayout;
    RelativeLayout passcodeLayout;
    String userRole;
    private ListView list;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    HashMap<String, String> userDetails;
    SessionManager sessionManager;
    private MobileServiceJsonTable gettingParentsZoneTableData;

    private Fragment mFragment;
    private FragmentManager fragmentManager;
    private String postDate;
    private String category;
    private String parentZoneStatus;
    private String leaveCause;
    private String startDate;
    private String endDate;
    private String leaveDayCount;
    private int noOfDays;
    private String studentFullname;
    private String studentFirstname;
    private String studentLastname;
    private String studentRollno;
    NotesAdapter notesAdapter;
    LeavesAdapterAdapter leavesAdapter;
    private String teracherid;
    private String teacherFristName;
    private String teacherLastName;
    private String teacherFullName;
    private String studentLastName;
    private String studentFristName;
    private String studentFullName;
    private String studentRollNo;
    private String meetingSchedule;
    private String parntZoneId;
    private String parntZoneReply;
    private String studentId;
    private ProgressDialog parentControlProgressDialog;
    private int flag = 0;
    private Handler parent_control_Handler;
    //private GettingParentZoneDataAsyntask getParentContolData;
    long TIMEOUT_TIME = 25000;
    ViewPager pager;
    TabLayout tabLayout;
    private int currentTab = 0;
    private TimeOutTimerClass timeOutTimerClass;
    private Timer parent_contol_Timer;
    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> fragmentsNameList;

    public static Map<String, List<NotesListData>> notes_map;
    public static Map<String, List<LeavesListData>> leave_map;
    private FloatingActionButton addButton;
    private JsonObject jsonObjectParentParameters;
    private boolean flagRefresh = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity())
                .setActionBarTitle("Parent Zone");

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();

        parentControlProgressDialog = new ProgressDialog(getActivity());
        parentControlProgressDialog.setCancelable(false);
        parentControlProgressDialog.setMessage(getString(R.string.loading));

        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getUserDetails();
        //gettingParentsZoneTableData = MainActivity.mClient.getTable("parent_zone");
        studentFirstname = userDetails.get(SessionManager.KEY_FIRST_NAME);
        studentLastname = userDetails.get(SessionManager.KEY_LAST_NAME);
        studentFullname = studentFirstname + " " + studentLastname;
        studentRollno = userDetails.get(SessionManager.KEY_ROLL_NO);
        teracherid = userDetails.get(SessionManager.KEY_TEACHER_REG_ID);
        Log.d("teracherid", "Oncreate teracherid" + teracherid);
        notes_map = new HashMap<>();
        leave_map = new HashMap<>();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        parent_control_Handler = new Handler();
        timeOutTimerClass = new TimeOutTimerClass();
        parent_contol_Timer = new Timer();
        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();

    }

    private void onExecutionStart() {
        CallingParent();
        parentControlProgressDialog.show();
        parent_contol_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
    }

    public void reScheduleTimer() {
        fragmentsNameList.clear();
        fragmentsList.clear();
        parentControlProgressDialog.show();
        parent_contol_Timer = new Timer();
        timeOutTimerClass = new TimeOutTimerClass();
        parent_contol_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        leavesArrayList.clear();
        notesArrayList.clear();
        CallingParent();
        System.out.println("2.   reScheduleTimer");
    }

    private void setTabs() {
        FragmentManager manager = getFragmentManager();
        System.out.println("fragmentsNameList   size -------------" + fragmentsNameList.size());
        System.out.println("fragmentsList Size --------------------" + fragmentsList.size());
        ParentControlPagerAdapter adapter = new ParentControlPagerAdapter(manager, fragmentsList, fragmentsNameList);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

        // mTabLayout.setupWithViewPager(mPager1);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTabsFromPagerAdapter(adapter);
        System.out.println("currentTab : " + currentTab);
        pager.setCurrentItem(currentTab);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {

        View rootView = inflater.inflate(R.layout.parental_control, container, false);
        pager = (ViewPager) rootView.findViewById(R.id.calendar_view_pager);
        passcodeLayout = (RelativeLayout) rootView.findViewById(R.id.passcodelayout);
        final EditText passcodeInputEditText = (EditText) rootView.findViewById(R.id.parentPasscodeEditText);


        passcodeEnterBtn = (Button) rootView.findViewById(R.id.passcodeEnterButton);
        passcodeEnterBtn.setVisibility(View.INVISIBLE);
        addButton = (FloatingActionButton) rootView.findViewById(R.id.parentPageAddFab);
        tabLayout = (TabLayout) rootView.findViewById(R.id.parent_control_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Leave"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("Notes"), 1);

        leavesArrayList = new ArrayList<>();
        notesArrayList = new ArrayList<>();

        loggedInUserForParentControl = userRole;

        if (userRole.equals("Teacher")) {
            passcodeLayout.setVisibility(View.INVISIBLE);
            addButton.setVisibility(View.INVISIBLE);

        }
        if (userRole.equals("Student")) {
            passcodeLayout.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            passcodeInputEditText.requestFocus();

        }

        if (userRole.equals("Teacher")) {
            setHasOptionsMenu(true);

            if (sessionManager.getSharedPrefItem(SessionManager.KEY_PARENTZONE_JSON) == null)

                if (connectionDetector.isConnectingToInternet()) {
                    onExecutionStart();
                } else {
                    Toast.makeText(getActivity(), "No internet connection..!", Toast.LENGTH_LONG).show();
                }
            else {
                parseJSONAndPopulate(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_PARENTZONE_JSON)));
                createAndSetFragments();

            }
        }

//        passcodeInputEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//            }
//            @Override
//            public void onTextChanged(final CharSequence s, int start, int before,int count) {
//
//            }
//            @Override
//            public void afterTextChanged(final Editable s) {
//                //avoid triggering event when text is too short
//                if (s.length() == 4) {
//                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    mgr.hideSoftInputFromWindow(passcodeInputEditText.getWindowToken(), 0);
//                }
//            }
//        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Get current tab 1---------------" + tabLayout.getSelectedTabPosition());
                if (tabLayout.getSelectedTabPosition() == 0) {
                    System.out.println("tabLayout name-----------" + tabLayout.getTabAt(0).getText());
                    Intent addLeaveIntent = new Intent(getActivity(), AddLeave.class);
                    startActivity(addLeaveIntent);

                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    System.out.println("tabLayout name-----------" + tabLayout.getTabAt(1).getText());
                    Intent notesIntent = new Intent(getActivity(), AddNotes.class);
                    startActivity(notesIntent);
                }


            }
        });


        passcodeEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("userPin", "userPin" + userDetails.get(SessionManager.KEY_USER_PIN));
                if (passcodeInputEditText.getText().toString().equals(userDetails.get(SessionManager.KEY_USER_PIN))) {
                    passcodeLayout.setVisibility(View.INVISIBLE);

//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(passcodeInputEditText.getWindowToken(), 0);

                    setHasOptionsMenu(true);
                    /*
                        calling of getting parent Zone Data
                     */
                    System.out.println("KEY_PARENTZONE_JSON : " + sessionManager.getSharedPrefItem(SessionManager.KEY_PARENTZONE_JSON));

                    if (sessionManager.getSharedPrefItem(SessionManager.KEY_PARENTZONE_JSON) == null)

                        if (connectionDetector.isConnectingToInternet()) {
                            onExecutionStart();
                        } else {
                            Toast.makeText(getActivity(), "No internet connection..!", Toast.LENGTH_LONG).show();
                        }
                    else {
                        parseJSONAndPopulate(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_PARENTZONE_JSON)));
                        createAndSetFragments();

                    }

                } else {
                    passcodeInputEditText.setError("Invalid code");
                }
            }
        });

        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                Log.d("pass code lenght", "" + String.valueOf(s.length()));
                if (s.length() == 4) {

                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(passcodeInputEditText.getWindowToken(), 0);
                    passcodeEnterBtn.setVisibility(View.VISIBLE);

                } else {

                    passcodeEnterBtn.setVisibility(View.INVISIBLE);
                    System.out.println("Pin no less then 4");

                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
        passcodeInputEditText.addTextChangedListener(mTextEditorWatcher);


        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            Toast.makeText(getActivity(), "No Internet Connection..!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        super.onResume();
        if (userRole.equals("Student")) {
            //   addBtn.show();
        }

    /* Below statment for changing Action Bar Title */
        //((MainActivity) getActivity()).setActionBarTitle("Parent Zone");
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

    @Override
    public void onRefresh() {

    }


    private void CallingParent() {


        jsonObjectParentParameters = new JsonObject();

        jsonObjectParentParameters.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
        jsonObjectParentParameters.addProperty("TAG", "Parent_Control_Frag");
        jsonObjectParentParameters.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectParentParameters.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectParentParameters.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));

        System.out.println(" teacherId " + userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchParentZoneApi", jsonObjectParentParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Parent control exception    " + exception);
                timeOutTimerClass.check = false;
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" PARENT ZONE  API   response    " + response);
                try {
                    sessionManager.setSharedPrefItem(SessionManager.KEY_PARENTZONE_JSON, response.toString());
                    parseJSONAndPopulate(response);

                    if (timeOutTimerClass.check) {
                        flagRefresh = true;
                        parentControlProgressDialog.dismiss();
                    }

                    createAndSetFragments();

                } catch (Exception e) {
                    System.out.println("Post Execute Catch Error");
                    timeOutTimerClass.check = false;
                }
            }
        });
    }


    private void createAndSetFragments() {
        setLeaveFragment();
        setNotesFragment();
        //ge♦t current activated tab no
        currentTab = pager.getCurrentItem();
        setTabs();
        //set last opened tab
        pager.setCurrentItem(currentTab);
    }

    private void parseJSONAndPopulate(JsonElement parentsZoneTableJsonResponse) {
        System.out.println("parseJSONAndPopulate CALLED");

        if (parentsZoneTableJsonResponse == null) {
            timeOutTimerClass.check = false;
        } else {
            timeOutTimerClass.check = true;
        }


        JsonArray parentZonejsonArray = parentsZoneTableJsonResponse.getAsJsonArray();
        if (parentZonejsonArray.size() == 0) {
            Log.d("json not recived", "not recived");
        }
        for (int k = 0; k < parentZonejsonArray.size(); k++) {

            JsonObject jsonObjectforIteration = parentZonejsonArray.get(k).getAsJsonObject();

            postDate = jsonObjectforIteration.get("postdate").toString().replace("\"", "");
            category = jsonObjectforIteration.get("category").toString().replace("\"", "");
            parentZoneStatus = jsonObjectforIteration.get("status").toString().replace("\"", "");
            leaveCause = jsonObjectforIteration.get("description_cause").toString().replace("\"", "");
            System.out.println("IIIIIIIIII    " + leaveCause);
            endDate = jsonObjectforIteration.get("enddate").toString().replace("\"", "");
            startDate = jsonObjectforIteration.get("startdate").toString().replace("\"", "");
            meetingSchedule = jsonObjectforIteration.get("schedule").toString().replace("\"", "");
            leaveDayCount = jsonObjectforIteration.get("no_of_days").toString().replace("\"", "");

                    /*kind of redundant first and last names.
                     * json keys are same for different values.
                     * May be this is needed/not needed in future*/
            teacherFristName = jsonObjectforIteration.get("firstName").toString().replace("\"", "");
            teacherLastName = jsonObjectforIteration.get("lastName").toString().replace("\"", "");
            studentFristName = jsonObjectforIteration.get("firstName").toString().replace("\"", "");
            studentLastName = jsonObjectforIteration.get("lastName").toString().replace("\"", "");

            //append student and teacher names
            studentFullName = studentFristName + " " + studentLastName;
            teacherFullName = teacherFristName + " " + teacherLastName;

            parntZoneId = jsonObjectforIteration.get("id").toString().replace("\"", "");
            parntZoneReply = jsonObjectforIteration.get("reply").toString();
            studentId = jsonObjectforIteration.get("Student_id").toString();


            if (category.equals("Leave")) {
                noOfDays = Integer.parseInt(leaveDayCount);
            }
            parntZoneReply = parntZoneReply.substring(1, parntZoneReply.length() - 1);
            studentId = studentId.substring(1, studentId.length() - 1);


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                Date datepost = dateFormat.parse(postDate);
                targetDateFormat.setTimeZone(TimeZone.getDefault());
                postDate = targetDateFormat.format(datepost);
                if (category.contains("Meeting")) {
                    Log.d("in meeting if", "1111111");
                    Date dateofmeeting = dateFormat.parse(meetingSchedule);
                    meetingSchedule = targetDateFormat.format(dateofmeeting);
                } else {
                    Log.d("in meeting else", "222222");
                    meetingSchedule = null;
                }

                Log.d("Post Date ", "111" + postDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (category.equals("Leave")) {
                try {
                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy", Locale.getDefault());
                    Date datestart = dateFormat.parse(startDate);
                    Date dateend = dateFormat.parse(endDate);
                    endDate = targetDateFormat.format(dateend);
                    startDate = targetDateFormat.format(datestart);
                    System.out.println("JSON Count...." + k);
                    System.out.println("JSON endDate " + endDate);
                    System.out.println("JSON startDate " + startDate);

                    if (userRole.equals("Student")) {

                        leavesArrayList.add(0, new LeavesListData(parntZoneId, studentId, postDate, leaveCause, parentZoneStatus, startDate, endDate, studentFullname, parntZoneReply, studentRollno, noOfDays));
                    }
                    if (userRole.equals("Teacher")) {
                        Log.d("Teacher ", "role");

                        studentRollNo = jsonObjectforIteration.get("rollNo").toString().replace("\"", "");
                        leavesArrayList.add(0, new LeavesListData(parntZoneId, studentId, postDate, leaveCause, parentZoneStatus, startDate, endDate, studentFullName, parntZoneReply, studentRollNo, noOfDays));

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("Data Put in map 1");
                leave_map.put("Leave", leavesArrayList);
                System.out.println("Data Put in map ");
            }


            if (!category.equals("Leave")) {
                if (userRole.equals("Teacher")) {
                    Log.d("Teacher ", "role");
                    Log.d("Teacher ", "role" + studentFullName);

                    studentRollNo = jsonObjectforIteration.get("rollNo").toString().replace("\"", "");
                    notesArrayList.add(0, new NotesListData(parntZoneId, studentId, leaveCause, category, postDate, meetingSchedule, parentZoneStatus, parntZoneReply, teacherFullName, studentFullName, studentRollNo));

                }
                if (userRole.equals("Student")) {
                    Log.d("student ", "role");
                    notesArrayList.add(0, new NotesListData(parntZoneId, studentId, leaveCause, category, postDate, meetingSchedule, parentZoneStatus, parntZoneReply, teacherFullName, studentFullName, studentRollno));

                }
                System.out.println("Data Put in map 1");
                notes_map.put("Notes", notesArrayList);
                System.out.println("Data Put in map 2");
            }
        }

    }

    private void setNotesFragment() {
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putSerializable("NotesList", (Serializable) notes_map.get("Notes"));
        Parent_control_Notes_Fragment parentControlNotesFragment = new Parent_control_Notes_Fragment();
        parentControlNotesFragment.setArguments(fragmentBundle);
        fragmentsNameList.add("Notes");
        fragmentsList.add(parentControlNotesFragment);
    }

    private void setLeaveFragment() {

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putSerializable("LeaveList", (Serializable) leave_map.get("Leave"));
        Parent_control_Leave_Fragment parentControlLeaveFragment = new Parent_control_Leave_Fragment();
        parentControlLeaveFragment.setArguments(fragmentBundle);
        fragmentsNameList.add("Leave");
        fragmentsList.add(parentControlLeaveFragment);

    }

    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            parent_control_Handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!check) {
                        parent_contol_Timer.cancel();
                        System.out.print("before dialog cancel");
                        parentControlProgressDialog.cancel();

                        //getParentContolData.cancel(true);
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
                                              /*
                                                method is used to remove all cancelled tasks from this timer's task queue.
                                             */
                                            parent_contol_Timer.purge();
                                            if (userRole.equals("Student")) {
                                                Fragment fragment = new DashboardStudentFragment();
                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                ft.replace(R.id.content_frame, fragment);
                                                ft.commit();
                                            } else {
                                                Fragment fragment = new DashboardTeacherFragment();
                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                ft.replace(R.id.content_frame, fragment);
                                                ft.commit();
                                            }


                                        }
                                    }).setMessage(R.string.check_network).create().show();

                        } catch (Exception e) {

                             /*    it may produce null pointer exception on creation of alert dialog object */
                            System.out.println("Exception Handle for Alert Dialog");
                        }
                    } else {

                     /*  this line of code is used when everything goes normal .
                         and cancel the timer.  */

                        parent_contol_Timer.cancel();
                    }
                }
            });
        }
    }


}
