package com.relecotech.androidsparsh.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.microsoft.windowsazure.notifications.NotificationsHandler;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.activities.AlarmReceiver;
import com.relecotech.androidsparsh.activities.AssignmentPost;
import com.relecotech.androidsparsh.adapters.AssignmentPagerAdapter;
import com.relecotech.androidsparsh.controllers.AssListData;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by amey  on 10/16/2015.
 */
public class AssignmentFragment extends android.support.v4.app.Fragment {

    public static String loggedInUserForAssignmentListAdapter;
    ArrayList<AssListData> arrayAssListData;
    ListView listViewAssignment;
    String Class = "";
    String max_credit;
    String division = "";
    String Subject;
    String due_Date = "";
    String issue_Date = "";
    String ass_status = "";
    String ass_credits_earned = "";
    String ass_grade_earned = "";
    String ass_note = "";
    String ass_submitted_by_name = "";
    String ass_score_type = "";
    String description;
    String ass_attachment_count;
    String ass_id;
    String submitted_by_teacher;
    private ProgressDialog assignmentProgressDialog;
    String monthNames[] = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    FloatingActionButton addAssignmentBtn;
    ConnectionDetector connectionDetector;
    boolean checkconnection;
    String userRole;
    private String getStartDateValue, getEndDateValue;
    String teacherRegId;
    String studentId;
    private View stick;
    private Fragment mFragment;
    private FragmentManager fragmentManager;
    public SessionManager sessionManager;
    private JsonObject jsonObjectToFetchAssignment;
    private String issueDate, dueDate;
    private String studentSchoolClassId;
    private Handler assignment_Handler;
    public ViewPager pager;
    private int currentTab = 0;
    TimeOutTimerClass timeOutTimerClass;
    private static Timer assignment_Timer;
    long TIMEOUT_TIME = 15000;
    private DatabaseHandler databaseHandler;
    public Map<String, List<AssListData>> assignmentList_Map;
    private List<AssListData> assignmentList;
    public static TabLayout assignment_tabLayout;
    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> fragmentsNameList;
    private Cursor getSubjectListCursor;
    public static List<AssListData> assignmentListValue;
    AssignmentPagerAdapter adapter;
    private FragmentManager manager;
    private Map<String, Long> assignmentDueDateNotification_Map;
    Bundle bundle;
    Intent intent;
    private static int flag = 0;
    public static TextView startDateForFileterAssignmentTextView, endDateForFileterAssignmentTextView;
    HashMap<String, String> userDetails;
    private Button searchForFileterAssignment;
    private RelativeLayout assignmentFilterLayout;
    private Spinner selectedSubjectSpinner;
    private ArrayList<String> subjectList;
    private ArrayAdapter<String> subjectAdapter;
    private String selectedSubjectString;
    private SimpleDateFormat getParcebaleDateFormat;
    private SimpleDateFormat getTargetDateFormat;
    private String executionStatus = "Normal";
    private RelativeLayout assignmentMainLayout;
    public static Date date;
    private Calendar cal;
    private Calendar calen;
    private Date date7daysBefore;
    private Date todaysDate;
    private SimpleDateFormat getParcebleDateFormat;
    private String date7dayBefore;
    private String todayDate;
    private Date endDate;
    private Date startDate;
    private Date getParcebleStartDate, getParcebleEndDate;
    private List<PendingIntent> pendingIntentList;
    private AlarmManager alarmManagerForSetNotificaion;
    public static int PENDING_INTENT_REQUEST_CODE = 0;
    private PendingIntent pendingIntentForSetNotification;
    private Long timeInMillisecondForAlarm;
    private TextView noDataAvailableTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        connectionDetector = new ConnectionDetector(getActivity());
        checkconnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());

        ((MainActivity) getActivity()).setActionBarTitle("Assignment");

        userDetails = sessionManager.getUserDetails();
        assignmentList = new ArrayList<>();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        loggedInUserForAssignmentListAdapter = userRole;
        jsonObjectToFetchAssignment = new JsonObject();

        pendingIntentList = new ArrayList<>();

        assignment_Handler = new Handler();
        timeOutTimerClass = new TimeOutTimerClass();
        assignment_Timer = new Timer();
        intent = new Intent(getActivity(), NotificationsHandler.class);

        assignmentProgressDialog = new ProgressDialog(getActivity());
        assignmentProgressDialog.setCancelable(false);
        assignmentProgressDialog.setMessage(getActivity().getString(R.string.loading));


        assignmentList_Map = new HashMap<>();
        assignmentDueDateNotification_Map = new HashMap<>();
        assignmentListValue = new ArrayList<>();
        arrayAssListData = new ArrayList<>();

        getParcebaleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        getTargetDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        jsonObjectData();
    }

    private void jsonObjectData() {

        try {
            Date getParcebleStartDate = getParcebleDateFormat.parse(startDateForFileterAssignmentTextView.getText().toString());
            Date getParcebleEndDate = getParcebleDateFormat.parse(endDateForFileterAssignmentTextView.getText().toString());
            getStartDateValue = getTargetDateFormat.format(getParcebleStartDate);
            getEndDateValue = getTargetDateFormat.format(getParcebleEndDate);

        } catch (Exception e) {
            System.out.println(" INSIDE DATE Exception");
        }

        try {


            if (executionStatus == "Filter") {

                System.out.println("INSIDE FILTER");

                if (userRole.equals("Teacher")) {
                    teacherRegId = userDetails.get(SessionManager.KEY_TEACHER_REG_ID);
                    jsonObjectToFetchAssignment.addProperty("role", userRole);
                    jsonObjectToFetchAssignment.addProperty("regId", teacherRegId);
                    jsonObjectToFetchAssignment.addProperty("startDate", getStartDateValue);
                    jsonObjectToFetchAssignment.addProperty("endDate", getEndDateValue);
                    jsonObjectToFetchAssignment.addProperty("subject", selectedSubjectString);
                    jsonObjectToFetchAssignment.addProperty("status", "Filter");
                }
                if (userRole.equals("Student")) {
                    studentId = userDetails.get(SessionManager.KEY_STUDENT_ID);
                    studentSchoolClassId = userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID);
                    jsonObjectToFetchAssignment.addProperty("role", userRole);
                    jsonObjectToFetchAssignment.addProperty("studentId", studentId);
                    jsonObjectToFetchAssignment.addProperty("studentSchoolClassId", studentSchoolClassId);
                    jsonObjectToFetchAssignment.addProperty("startDate", getStartDateValue);
                    jsonObjectToFetchAssignment.addProperty("endDate", getEndDateValue);
                    jsonObjectToFetchAssignment.addProperty("subject", selectedSubjectString);
                    jsonObjectToFetchAssignment.addProperty("status", "Filter");
                }
            } else {
                System.out.println("Else of INSIDE FILTER");

                if (userRole.equals("Teacher")) {
                    teacherRegId = userDetails.get(SessionManager.KEY_TEACHER_REG_ID);
                    jsonObjectToFetchAssignment.addProperty("role", userRole);
                    jsonObjectToFetchAssignment.addProperty("regId", teacherRegId);
                    jsonObjectToFetchAssignment.addProperty("status", "Normal");
                }
                if (userRole.equals("Student")) {
                    studentId = userDetails.get(SessionManager.KEY_STUDENT_ID);
                    studentSchoolClassId = userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID);
                    jsonObjectToFetchAssignment.addProperty("role", userRole);
                    jsonObjectToFetchAssignment.addProperty("studentId", studentId);
                    jsonObjectToFetchAssignment.addProperty("studentSchoolClassId", studentSchoolClassId);
                    jsonObjectToFetchAssignment.addProperty("status", "Normal");
                }
            }
        } catch (Exception e) {
            System.out.println(" Inside Assignment Fragment Catch");
            System.out.println(e.getMessage());
        }

        System.out.println("jsonObjectToFetchAssignment " + jsonObjectToFetchAssignment);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.assignment, container, false);

        addAssignmentBtn = (FloatingActionButton) rootView.findViewById(R.id.addAssignmentButton);

        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.assignmentNoDataAvailable_textView);

        assignmentMainLayout = (RelativeLayout) rootView.findViewById(R.id.assignment_layout);

        listViewAssignment = (ListView) rootView.findViewById(R.id.assignmentlistView);

        startDateForFileterAssignmentTextView = (TextView) rootView.findViewById(R.id.startTextView);
        endDateForFileterAssignmentTextView = (TextView) rootView.findViewById(R.id.endTextView);
        selectedSubjectSpinner = (Spinner) rootView.findViewById(R.id.selectSubjectSpinner);

        assignmentFilterLayout = (RelativeLayout) rootView.findViewById(R.id.assignment_filter_layout);

        searchForFileterAssignment = (Button) rootView.findViewById(R.id.btnSubmit);
        pager = (ViewPager) rootView.findViewById(R.id.assignment_view_pager);
        addAssignmentBtn.setVisibility(View.INVISIBLE);
        assignment_tabLayout = (TabLayout) rootView.findViewById(R.id.assignment_tab_layout);
        assignmentFilterLayout.setVisibility(View.INVISIBLE);

        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();
        subjectList = new ArrayList<>();

        getSubjectListCursor = databaseHandler.getTeacherClassDataByCursor();
        System.out.println(" getSubjectListCursor.getCount()" + getSubjectListCursor.getCount());


        cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -7);
        date7daysBefore = cal.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        date7dayBefore = formatter.format(date7daysBefore);
        System.out.println("daysBeforeDate " + date7dayBefore);

        startDateForFileterAssignmentTextView.setText(date7dayBefore);

        calen = Calendar.getInstance();
        calen.setTime(new Date());
        calen.add(Calendar.DAY_OF_YEAR, 0);
        todaysDate = calen.getTime();

        todayDate = formatter.format(todaysDate);
        System.out.println("todayDate " + todayDate);
        endDateForFileterAssignmentTextView.setText(todayDate);

        startDateForFileterAssignmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getActivity().getFragmentManager(), "DatePicker");
                System.out.println("date7daysBefore  " + date7daysBefore);
                try {
                    date = getParcebaleDateFormat.parse(startDateForFileterAssignmentTextView.getText().toString());
                    startDate = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                flag = 1;
            }
        });

        endDateForFileterAssignmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getActivity().getFragmentManager(), "DatePicker");
                System.out.println("todaysDate  " + todaysDate);
                try {
                    date = getParcebaleDateFormat.parse(endDateForFileterAssignmentTextView.getText().toString());
                    endDate = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        stick = rootView.findViewById(R.id.assignmentStickView);
        stick.setVisibility(View.INVISIBLE);


        System.out.println(" mainManager.getSharedPrefItem(SessionManager.KEY_ASSIGNMENT_JSON) " + sessionManager.getSharedPrefItem(SessionManager.KEY_ASSIGNMENT_JSON));

        try {
            //Set Data in Subject spinner
            getSubjectListCursor.moveToFirst();
            for (int getsubjectloop = 0; getsubjectloop < getSubjectListCursor.getCount(); getsubjectloop++) {
                if (!subjectList.contains(getSubjectListCursor.getString(getSubjectListCursor.getColumnIndex("subject")))) {
                    subjectList.add(getSubjectListCursor.getString(getSubjectListCursor.getColumnIndex("subject")));
                }
                getSubjectListCursor.moveToNext();
            }
            subjectList.add("[ Select Subject ]");

            subjectAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, subjectList);
            subjectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            selectedSubjectSpinner.setAdapter(subjectAdapter);
            selectedSubjectSpinner.setSelection(subjectAdapter.getCount() - 1);
        } catch (Exception e) {
            System.out.println(" Exception Assignment " + e.getMessage());
        }


        /*
        Start fetching Data
         */
        //onExecutionStart();

        /*
           Initialize FragmentManager for calling date picker
         */
        final android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();

        selectedSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubjectString = selectedSubjectSpinner.getSelectedItem().toString();
                System.out.println("tempSubjectString-----------" + selectedSubjectString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchForFileterAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    executionStatus = "Filter";
                    getParcebleStartDate = getParcebaleDateFormat.parse(startDateForFileterAssignmentTextView.getText().toString());
                    getParcebleEndDate = getParcebaleDateFormat.parse(endDateForFileterAssignmentTextView.getText().toString());


                    getStartDateValue = getTargetDateFormat.format(getParcebleStartDate);
                    getEndDateValue = getTargetDateFormat.format(getParcebleEndDate);

                    System.out.println("getStartDateValue---------" + getStartDateValue);
                    System.out.println("getEndDateValue---------" + getEndDateValue);

                    if (connectionDetector.isConnectingToInternet()) {
                        if (!selectedSubjectSpinner.getSelectedItem().toString().equals("[ Select Subject ]")) {
                            if (getParcebleStartDate.equals(getParcebleEndDate) || getParcebleStartDate.before(getParcebleEndDate)) {
                                assignment_Timer.cancel();
                                onExecutionStart();
                            } else {
                                Snackbar.make(rootView, "Select Proper Date", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(rootView, "Select Subject", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(rootView, "No Internet Connection...!", Snackbar.LENGTH_SHORT).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });


        addAssignmentBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkconnection) {
                            addAssignmentBtn.hide();
                            Intent assPostIntent = new Intent(getActivity(), AssignmentPost.class);
                            startActivity(assPostIntent);
                        } else {
                            Toast.makeText(getActivity(), "No Internet Connection...!", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        //logic to show/hide FAb on list scroll
        listViewAssignment.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    Log.i("SCROLLING DOWN", "TRUE");
                    if (userRole.equals("Teacher")) {
                        addAssignmentBtn.hide();
                    }
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    Log.i("SCROLLING UP", "TRUE");
                    if (userRole.equals("Teacher")) {
                        addAssignmentBtn.show();
                    }
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });


        if (sessionManager.getSharedPrefItem(SessionManager.KEY_ASSIGNMENT_JSON) == null) {

            if (connectionDetector.isConnectingToInternet()) {
                assignment_Timer.cancel();
                onExecutionStart();

            } else {
                Toast.makeText(getActivity(),R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        } else {

            parseJSONAndPopulate(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_ASSIGNMENT_JSON)));

        }
        return rootView;
    }


    public void onExecutionStart() {
        assignment_Timer = new Timer("assignmentTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        assignmentFilterLayout.setVisibility(View.INVISIBLE);
        assignmentProgressDialog.show();

        if (assignmentList.size() == 0) {
            System.out.println("assignmentList is ZERO " + assignmentList.size());
        } else {
            assignmentList.clear();
        }

        fetchAssignment();
        assignment_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("onExecutionStart---");
    }


    private void reScheduleTimer() {

        assignment_Timer = new Timer("assignmentTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        assignmentList.clear();
        assignmentFilterLayout.setVisibility(View.INVISIBLE);
        if (assignmentList.size() == 0) {
            System.out.println("assignmentList is ZERO " + assignmentList.size());
        } else {
            assignmentList.clear();
        }
        fetchAssignment();
        assignmentProgressDialog.show();
        assignment_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
    }

    public void fetchAssignment() {
        jsonObjectData();

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchAssignmentApi", jsonObjectToFetchAssignment);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);

            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                try {
                    if (!executionStatus.equals("Filter")) {
                        sessionManager.setSharedPrefItem(SessionManager.KEY_ASSIGNMENT_JSON, String.valueOf(response));
                    }
                    parseJSONAndPopulate(response);
                } catch (Exception e) {
                    System.out.println("Post Execute Catch Error");
                    timeOutTimerClass.check = false;
                }

            }
        });

    }


    private void parseJSONAndPopulate(JsonElement jsonObject) {


        if (jsonObject == null) {
            timeOutTimerClass.check = false;
            System.out.println("json object null ");
            assignmentFilterLayout.setVisibility(View.VISIBLE);
        } else {
            timeOutTimerClass.check = true;
            System.out.println("json object not null ");

        }
        JsonArray assignmentListResponse = jsonObject.getAsJsonArray();
        fragmentsList.clear();
        fragmentsNameList.clear();
        System.out.println("assignmentList_Map size BEFORE CLEAR : " + assignmentList_Map.size());
        assignmentList_Map.clear();
        System.out.println("assignmentList_Map size AFTER CLEAR : " + assignmentList_Map.size());
        try {
            if ((assignmentListResponse.size()) == 0) {
                Log.d("if", "NO ASSMT RECEIVED");

                if (userRole.equals("Teacher")) {

                    noDataAvailableTextView.setVisibility(View.VISIBLE);
                    noDataAvailableTextView.setText(R.string.noDataAvailable);
//                    noDataAvailableTextView.setText("@@@@@@@@@@@@@@@@@@@@@@@@@");

                } else if (userRole.equals("Student")) {
                    // noDataAvailableTextView.setText("No data available");
                    /*
                    No Need to set 'No Data Available' text because Tab already have No Data Available text.
                     */
                }

            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);
                Log.d("Else ", " ASSMT RECEIVED");
                for (int i = 0; i <= assignmentListResponse.size() - 1; i++) {

                    JsonObject jsonObjectForIteration = assignmentListResponse.get(i).getAsJsonObject();
                    Log.d("jsonObjectForIteration0", "" + jsonObjectForIteration);

                    ass_id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    issue_Date = jsonObjectForIteration.get("assignment_postdate").toString().replace("\"", "");
                    due_Date = jsonObjectForIteration.get("assignment_dauedate").toString().replace("\"", "");
                    max_credit = jsonObjectForIteration.get("assignment_credit").toString().replace("\"", "");
                    Subject = jsonObjectForIteration.get("assignment_subject").toString().replace("\"", "");
                    submitted_by_teacher = jsonObjectForIteration.get("assignment_submitted_by").toString().replace("\"", "");
                    description = jsonObjectForIteration.get("assignment_description").toString();
                    ass_score_type = jsonObjectForIteration.get("score_type").toString().replace("\"", "");
                    ass_attachment_count = jsonObjectForIteration.get("attachement_count").toString().replace("\"", "");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    Date dateissue = simpleDateFormat.parse(issue_Date);
                    Date datedue = simpleDateFormat.parse(due_Date);

                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy", Locale.getDefault());

                    issueDate = targetDateFormat.format(dateissue);
                    dueDate = targetDateFormat.format(datedue);

                    description = description.substring(1, description.length() - 1);

                    if (userRole.equals("Teacher")) {
                        Class = jsonObjectForIteration.get("assignment_class").toString().replace("\"", "");
                        division = jsonObjectForIteration.get("assignment_div").toString().replace("\"", "");
                        String classDivision = Class + " " + division;

                        if (!fragmentsNameList.contains(classDivision)) {
                            fragmentsNameList.add(classDivision);
                        } else {
                            System.out.println("class Division already present..............");
                        }

                        if (assignmentList_Map.containsKey(classDivision)) {
                            assignmentList = assignmentList_Map.get(classDivision);
                            assignmentList.add(0, new AssListData(R.drawable.star100, ass_id, max_credit, Subject, issueDate, dueDate, Class, division, ass_submitted_by_name, description, ass_status, ass_credits_earned, ass_grade_earned, ass_note, ass_score_type, ass_attachment_count));
                            assignmentList_Map.put(classDivision, assignmentList);
                        } else {
                            assignmentList = new ArrayList<>();
                            assignmentList.add(0, new AssListData(R.drawable.star100, ass_id, max_credit, Subject, issueDate, dueDate, Class, division, ass_submitted_by_name, description, ass_status, ass_credits_earned, ass_grade_earned, ass_note, ass_score_type, ass_attachment_count));
                            assignmentList_Map.put(classDivision, assignmentList);
                        }

                    }
                    if (userRole.equals("Student")) {
                        ass_status = jsonObjectForIteration.get("assignment_status_status").toString().replace("\"", "");

                        //these 3 aren't added to asslist adapter
                        // add it for detailed info about approved assignment student
                        ass_credits_earned = jsonObjectForIteration.get("assignment_status_credit").toString().replace("\"", "");
                        ass_grade_earned = jsonObjectForIteration.get("assignment_status_grades").toString().replace("\"", "");
                        ass_note = jsonObjectForIteration.get("assignment_status_notes").toString();
                        ass_submitted_by_name = jsonObjectForIteration.get("firstName").toString().replace("\"", "") + " " + jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                        ass_note = ass_note.substring(1, ass_note.length() - 1);

                        if (ass_status.equals("Pending") || ass_status.equals("Re-Submit")) {
                            if (assignmentList_Map.containsKey("Pending")) {
                                System.out.println("If Condition============================== " + max_credit);
                                assignmentList = assignmentList_Map.get("Pending");
                                assignmentList.add(0, new AssListData(R.drawable.star100, ass_id, max_credit, Subject, issueDate, dueDate, Class, division, ass_submitted_by_name, description, ass_status, ass_credits_earned, ass_grade_earned, ass_note, ass_score_type, ass_attachment_count));
                                assignmentList_Map.put("Pending", assignmentList);
                                long dueDateMillSendForNotification = GetMillisecondMethod(datedue);
//                            assignmentDueDateNotification_Map.put(ass_id, dueDateMillseondForNotification);
                                if (dueDateMillSendForNotification >= System.currentTimeMillis()) {
                                    assignmentDueDateNotification_Map.put(ass_id, dueDateMillSendForNotification);
                                } else {
                                    System.out.println("Due date milisecond is less tham curren millisecond........");
                                    System.out.println("dueDateMillSendForNotification------------" + dueDateMillSendForNotification);
                                }

                            } else {
                                System.out.println("Else Condition============================= " + max_credit);
                                assignmentList = new ArrayList<>();
                                assignmentList.add(0, new AssListData(R.drawable.star100, ass_id, max_credit, Subject, issueDate, dueDate, Class, division, ass_submitted_by_name, description, ass_status, ass_credits_earned, ass_grade_earned, ass_note, ass_score_type, ass_attachment_count));
                                assignmentList_Map.put("Pending", assignmentList);
                                long dueDateMillSendForNotification = GetMillisecondMethod(datedue);
//                            assignmentDueDateNotification_Map.put(ass_id, dueDateMillseondForNotification);
                                if (dueDateMillSendForNotification >= System.currentTimeMillis()) {
                                    assignmentDueDateNotification_Map.put(ass_id, dueDateMillSendForNotification);
                                } else {
                                    System.out.println("Due date milisecond is less tham curren millisecond........");
                                    System.out.println("dueDateMillSendForNotification------------" + dueDateMillSendForNotification);
                                }
                            }

                        } else if (ass_status.equals("Approved")) {

                            if (assignmentList_Map.containsKey("Approved")) {
                                System.out.println("IF Condition=============================  " + max_credit);
                                assignmentList = assignmentList_Map.get("Approved");
                                assignmentList.add(0, new AssListData(R.drawable.star100, ass_id, max_credit, Subject, issueDate, dueDate, Class, division, ass_submitted_by_name, description, ass_status, ass_credits_earned, ass_grade_earned, ass_note, ass_score_type, ass_attachment_count));
                                assignmentList_Map.put("Approved", assignmentList);
                            } else {
                                System.out.println("Else Condition=============================  " + max_credit);
                                assignmentList = new ArrayList<>();
                                assignmentList.add(0, new AssListData(R.drawable.star100, ass_id, max_credit, Subject, issueDate, dueDate, Class, division, ass_submitted_by_name, description, ass_status, ass_credits_earned, ass_grade_earned, ass_note, ass_score_type, ass_attachment_count));
                                assignmentList_Map.put("Approved", assignmentList);

                            }

                        }

                    }

                }
            }

            if (userRole.equals("Teacher")) {
                addAssignmentBtn.setVisibility(View.VISIBLE);

                for (int i = 0; i < fragmentsNameList.size(); i++) {
                    Bundle fragmentBundle = new Bundle();
                    fragmentBundle.putSerializable("assmntList", (Serializable) assignmentList_Map.get(fragmentsNameList.get(i)));
                    Assignment_Tabs_Fragment assignment_tabs_fragment = new Assignment_Tabs_Fragment();
                    assignment_tabs_fragment.setArguments(fragmentBundle);
                    fragmentsList.add(assignment_tabs_fragment);
                    System.out.println("fragmentsList Size-----------" + fragmentsList.size());
                }
            }
            if (userRole.equals("Student")) {

                addAssignmentBtn.setVisibility(View.INVISIBLE);

                fragmentsNameList.add("Pending");
                fragmentsNameList.add("Approved");

                for (int i = 0; i < fragmentsNameList.size(); i++) {
                    Bundle fragmentBundle = new Bundle();
                    fragmentBundle.putSerializable("assmntList", (Serializable) assignmentList_Map.get(fragmentsNameList.get(i)));
                    Assignment_Tabs_Fragment assignment_tabs_fragment = new Assignment_Tabs_Fragment();
                    assignment_tabs_fragment.setArguments(fragmentBundle);
                    fragmentsList.add(assignment_tabs_fragment);
                    System.out.println("fragmentsList Size-----------" + fragmentsList.size());
                }

            }
            assignmentMainLayout.setVisibility(View.VISIBLE);
            setTabs();
            assignmentFilterLayout.setVisibility(View.GONE);
            assignmentProgressDialog.dismiss();

            setNotificationForAssignmentDueDate();

        } catch (Exception e) {
            Log.d("exption in ASSMT fetch", "exption in ASSMT fetch");
            e.printStackTrace();

        }
    }

    /*
        Set Assignment Tabs
     */
    private void setTabs() {

        manager = getFragmentManager();
        adapter = new AssignmentPagerAdapter(manager, fragmentsList, fragmentsNameList);
        pager.setAdapter(adapter);
        assignment_tabLayout.setupWithViewPager(pager);
        System.out.println("set tabs called");

    }


    private void setNotificationForAssignmentDueDate() {

        ClearAllAlarm(pendingIntentList);

        alarmManagerForSetNotificaion = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intentForNotification = new Intent(getActivity(), AlarmReceiver.class);

        for (Map.Entry<String, Long> entry : assignmentDueDateNotification_Map.entrySet()) {

            System.out.println("PENDING_INTENT_REQUEST_CODE-----------" + PENDING_INTENT_REQUEST_CODE);

            pendingIntentForSetNotification = PendingIntent.getBroadcast(getActivity(), PENDING_INTENT_REQUEST_CODE, intentForNotification, 0);
            PENDING_INTENT_REQUEST_CODE++;
            pendingIntentList.add(pendingIntentForSetNotification);
            timeInMillisecondForAlarm = entry.getValue();
            System.out.println("Time in millliscond-----------" + timeInMillisecondForAlarm);
            alarmManagerForSetNotificaion.set(AlarmManager.RTC_WAKEUP, timeInMillisecondForAlarm, pendingIntentForSetNotification);

        }

        System.out.println("pendingIntentList-------------" + pendingIntentList);
    }


    private void ClearAllAlarm(List<PendingIntent> pendingIntentList) {
        System.out.println("Clear All Alarm method called.");
        System.out.println("+ this.pendingIntentList" + this.pendingIntentList);
        System.out.println("+ this.pendingIntentList  Size " + this.pendingIntentList.size());
        for (int loop = 0; loop < this.pendingIntentList.size(); loop++) {
            System.out.println("Clear All Alarm method called.");
            System.out.println("pending intent ----------" + pendingIntentList.get(loop));
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntentList.get(loop));
        }
    }


    public long GetMillisecondMethod(Date date_time) {

        Calendar calculateDueDateForNotification = Calendar.getInstance();
        calculateDueDateForNotification.setTime(date_time);
        calculateDueDateForNotification.add(Calendar.DATE, -1);

        System.out.println(" sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER)" + sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER));
        String time = sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER);
        String[] hhmm = time.split(":");
        int hh = Integer.parseInt(hhmm[0]);
        int min = Integer.parseInt(hhmm[1]);

        int yy = calculateDueDateForNotification.get(Calendar.YEAR);
        int mm = calculateDueDateForNotification.get(Calendar.MONTH);
        int dd = calculateDueDateForNotification.get(Calendar.DAY_OF_MONTH);
        calculateDueDateForNotification.set(yy, mm, dd, hh, min, 00);
        Date calculatedDueDateForAssignmentAlert = calculateDueDateForNotification.getTime();

        System.out.println("date_time- After Calculation------------" + calculatedDueDateForAssignmentAlert);
        long dueDateMillescond = calculatedDueDateForAssignmentAlert.getTime();
        System.out.println("dueDateMillisecond---GetMillisecondMethod----" + dueDateMillescond);

        return dueDateMillescond;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assignment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_filter:
                System.out.println("Action Filter--");
                assignmentFilterLayout.setVisibility(View.VISIBLE);
                assignmentMainLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.action_refresh:
                if (checkconnection) {
                    reScheduleTimer();
                    System.out.println("Action Refresh--");
                }

                break;
            default:
                return super.onOptionsItemSelected(item);

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        super.onResume();

        System.out.println("tabLayout name-----------" + assignment_tabLayout.getSelectedTabPosition());

        if (userRole.equals("Teacher")) {
            addAssignmentBtn.show();
        }
        /* Below statment for changing Action Bar Title */
        //((MainActivity) getActivity()).setActionBarTitle("Assignment");

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back button
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

    /*
     Timer Class Handle timer
     */

    public class TimeOutTimerClass extends TimerTask {
        int count = 0;
        Boolean check = false;

        @Override
        public void run() {
            assignment_Handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("check Variable Assignment" + check);
                    if (!check) {
                        //fetchAlertDataTask.cancel(true);
                        assignment_Timer.cancel();
                        assignmentProgressDialog.dismiss();
                        System.out.println("thread cancel call" + (count++));
                        //System.out.println("fetchAlertDataTask  Status " + fetchAlertDataTask.getStatus());

                        try {

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            dialogBuilder.setMessage(R.string.check_network);
                            dialogBuilder.setCancelable(false);
                            dialogBuilder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println("Dialog Retry Button call");

                                    assignmentProgressDialog.show();

                                    stick.setVisibility(View.INVISIBLE);

                                    reScheduleTimer();

                                }
                            });
                            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                      /*
                                            method is used to remove all cancelled tasks from this timer's task queue.
                                       */
                                    System.out.println("Dismiss called.......");
                                    assignment_Timer.purge();
                                    assignment_Timer.cancel();

                                    System.out.println(" UserRole " + userRole);

                                    if (userRole.equals("Teacher")) {

                                        Fragment fragment = new DashboardTeacherFragment();
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.commit();

                                    } else {

                                        Fragment fragment = new DashboardStudentFragment();
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.commit();
                                    }

                                }
                            });
                            dialogBuilder.create().show();
                        } catch (Exception e) {
                            System.out.println("Exception Handle for Alert Dialog");
                        }

                    } else {
                        assignment_Timer.cancel();
                        System.out.println("Timer Cancelled Assignment");
                    }
                }
            });
        }
    }


    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private String datee;
        private int yy, mm, dd;
        private Date dateTemp;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();

            if (AssignmentFragment.flag == 1) {
                dateTemp = date;
            } else {
                dateTemp = date;
            }

            System.out.println("dateTemp" + dateTemp);

            calendar.setTime(dateTemp);
            yy = calendar.get(Calendar.YEAR);
            mm = calendar.get(Calendar.MONTH);
            dd = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, yy, mm, dd) {
                @Override
                public void onClick(DialogInterface dialog, int doneBtn) {
                    if (doneBtn == BUTTON_POSITIVE) {

                        int year = getDatePicker().getYear();
                        int month = getDatePicker().getMonth();
                        int day = getDatePicker().getDayOfMonth();

                        calendar.set(year, month, day);

                        // datee = day + "-" + month + "-" + year;

                        //SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(calendar.getTime());
                        System.out.println("formattedDate---------" + formattedDate);

                        if (AssignmentFragment.flag == 1) {
                            System.out.println("Flag --- 1");
                            startDateForFileterAssignmentTextView.setText(formattedDate);
                            AssignmentFragment.flag = 0;

                        } else {
                            System.out.println("Flag --- 0");
                            endDateForFileterAssignmentTextView.setText(formattedDate);
                        }

                    }
                    super.onClick(dialog, doneBtn);
                }
            };
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        }

    }
}