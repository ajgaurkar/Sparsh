package com.relecotech.androidsparsh.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ListView;
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
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.activities.AlertDetail;
import com.relecotech.androidsparsh.activities.AlertPost;
import com.relecotech.androidsparsh.adapters.AlertAdapterAdapter;
import com.relecotech.androidsparsh.controllers.AlertListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amey on 10/16/2015.
 */
public class AlertFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static String loggedInUserForAlertListAdapter;
    FloatingActionButton alertAddBtn;
    ListView listViewAlerts;
    ArrayList<AlertListData> alertList;
    View alertTimelineStick;
    String user;
    FragmentManager fragmentManager;
    private Fragment mFragment;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    String userRole;
    private AlertAdapterAdapter alertAdapter;
    Handler alert_Handler;
    TimeOutTimerClass timeOutTimerClass;
    Timer alert_Timer;
    long TIMEOUT_TIME = 25000;
    private ProgressDialog progressDialog;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    JsonObject jsonObjectAlertParameters;
    AlertListData alertData = null;
    private TextView noDataAvailableTextView;
    private boolean flagRefresh = true;

    //Commit changes from amey in master


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity())
                .setActionBarTitle("Alerts");
        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        setHasOptionsMenu(true);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getActivity().getString(R.string.loading));

        alert_Handler = new Handler();
        alert_Timer = new Timer();
        timeOutTimerClass = new TimeOutTimerClass();

    }
    /* This method is used for Start AsynTask with Timer in OnCreate */

    private void onExecutionStart() {
        CallingAlert();
        progressDialog.show();
        alert_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("1.   onExecutionStart");
    }

    /*
           This method is used for
           1.Start AsynTask With Timer on Retry Button of alertDialog When ConnectionTime Out.
           2.OnSwipeRefresh Method
     */
    private void reScheduleTimer() {
        alert_Timer = new Timer("alertTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        CallingAlert();
        alertList.clear();
        progressDialog.show();
        alert_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("2.   reScheduleTimer");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.alert, container, false);

        alertAddBtn = (FloatingActionButton) rootView.findViewById(R.id.addAlertButton);
        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.noDataAvailableTextView);

        alertAddBtn.setVisibility(View.INVISIBLE);

        alertTimelineStick = rootView.findViewById(R.id.timelineStickView);
        alertTimelineStick.setVisibility(View.INVISIBLE);

        user = userDetails.get(SessionManager.KEY_USER_ROLE);
        loggedInUserForAlertListAdapter = user;

        listViewAlerts = (ListView) rootView.findViewById(R.id.alertlistview);
        alertList = new ArrayList<AlertListData>();

        // Condition to show/hide new alert add button according to user role
        if (user.equals("Teacher")) {
            alertAddBtn.setVisibility(View.VISIBLE);
        }
        if (user.equals("Student")) {
            alertAddBtn.setVisibility(View.INVISIBLE);
        }

        if (sessionManager.getSharedPrefItem(SessionManager.KEY_ALERT_JSON) == null) {

            if (connectionDetector.isConnectingToInternet()) {
                onExecutionStart();

            } else {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        } else {

            parseJSONAndPopulate(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_ALERT_JSON)));

        }


        // Button listener for New alert page
        alertAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertAddBtn.hide();
                Intent alertAddIntent = new Intent(getActivity(), AlertPost.class);
                startActivity(alertAddIntent);
            }
        });


        // Click on list and get navigation btn on alert dialog
        listViewAlerts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                AlertListData selectedAlertListData = alertList.get(position);
                Intent alertDetailIntent = new Intent(getActivity(), AlertDetail.class);
                alertDetailIntent.putExtra("AlertTitle", selectedAlertListData.getTitle());
                alertDetailIntent.putExtra("AlertDescrition", selectedAlertListData.getBody());
                alertDetailIntent.putExtra("AlertSubmittedBy", selectedAlertListData.getSubmitted_By_to());
                alertDetailIntent.putExtra("AlertTag", selectedAlertListData.getTag());
                alertDetailIntent.putExtra("AlertPostDate", selectedAlertListData.getIssueDate());
                alertDetailIntent.putExtra("AlertId", selectedAlertListData.getAlertId());
                alertDetailIntent.putExtra("AlertAttachmentCount", selectedAlertListData.getAttachmentCount());
                startActivity(alertDetailIntent);

            }
        });

        //logic to show/hide FAb on list scroll
        listViewAlerts.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        alertAddBtn.hide();
                    }
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    Log.i("SCROLLING UP", "TRUE");
                    if (userRole.equals("Teacher")) {
                        alertAddBtn.show();
                    }
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });

        return rootView;

    }


    @Override
    public void onResume() {

        super.onResume();
        if (user.equals("Teacher")) {
            alertAddBtn.show();
        }
        /* Below statment for changing Action Bar Title */
        //((MainActivity) getActivity()).setActionBarTitle("Alert");

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back button
                    if (user.equals("Teacher")) {
                        mFragment = new DashboardTeacherFragment();
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    }
                    if (user.equals("Student")) {
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
        System.out.println("Refresh started");

        //clearing the list before it is updated
        alertList.clear();
        alertAdapter.notifyDataSetChanged();
        alertTimelineStick.setVisibility(View.INVISIBLE);
        reScheduleTimer();

        System.out.println("Refresh ended");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void CallingAlert() {

        jsonObjectAlertParameters = new JsonObject();

        if (userRole.equals("Teacher")) {
            jsonObjectAlertParameters.addProperty("userRole", userRole);
            System.out.println(" userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID) " + userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));
            jsonObjectAlertParameters.addProperty("teacherRecordId", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));
        }
        if (userRole.equals("Student")) {

            jsonObjectAlertParameters.addProperty("userRole", userRole);
            jsonObjectAlertParameters.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
            jsonObjectAlertParameters.addProperty("studentClass", userDetails.get(SessionManager.KEY_USER_CLASS));
            jsonObjectAlertParameters.addProperty("studentDivision", userDetails.get(SessionManager.KEY_USER_DIVISON));
        }

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchAlertApi", jsonObjectAlertParameters);

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
                    sessionManager.setSharedPrefItem(SessionManager.KEY_ALERT_JSON, response.toString());
                    parseJSONAndPopulate(response);

                } catch (Exception e) {
                    System.out.println("Post Execute Catch Error");
                    timeOutTimerClass.check = false;
                }
            }
        });
    }


    private void parseJSONAndPopulate(JsonElement alertFetchResponse) {

        try {

            if (alertFetchResponse == null) {
                System.out.println("alertFetchResponse Null  @@@@@@ @@@@@@@@ @@@@@@ @@@" + alertFetchResponse);
                timeOutTimerClass.check = false;
            } else {
                System.out.println("alertFetchResponse  NOT Null  @@@@@@ @@@@@@@@ @@@@@@ @@@" + alertFetchResponse);
                timeOutTimerClass.check = true;
            }
            JsonArray alertJsonArray = alertFetchResponse.getAsJsonArray();
            System.out.println("alertJsonArray.size() : " + alertJsonArray.size());
        /*
                            Alert Json Parsing Start.
         */
            if (alertJsonArray.size() != 0) {
                System.out.println("Alert Json Array Size ZERO");
                noDataAvailableTextView.setVisibility(View.INVISIBLE);


                for (int i = 0; i < alertJsonArray.size(); i++) {
                    System.out.println("inf loop--------------------------");
                    JsonObject jsonObjectForIteration = alertJsonArray.get(i).getAsJsonObject();

                    System.out.println("Title ; " + jsonObjectForIteration.get("Title").toString().replace("\"", ""));
                    System.out.println("category ; " + jsonObjectForIteration.get("Category").toString().replace("\"", ""));
                    System.out.println("class ; " + jsonObjectForIteration.get("Alert_class").toString().replace("\"", ""));
                    System.out.println("divison ; " + jsonObjectForIteration.get("alert_divison").toString().replace("\"", ""));
                    System.out.println("Teacher_id ; " + jsonObjectForIteration.get("Teacher_id").toString().replace("\"", ""));
                    System.out.println("Postdate ; " + jsonObjectForIteration.get("Postdate").toString().replace("\"", ""));
                    String alertDescription = jsonObjectForIteration.get("Alert_description").toString().replace("\\n", "\n");
                    System.out.println("alert_description ; " + alertDescription.substring(1, alertDescription.length() - 1));
                    String postDate = jsonObjectForIteration.get("Postdate").toString().replace("\"", "");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    try {
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                        Date datepost = dateFormat.parse(postDate);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());
                        postDate = targetDateFormat.format(datepost);

                        Log.d("Post Date ", "111   " + postDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //differentiation, because different data is to shown on alert list according to user role
                    if (userRole.equals("Teacher")) {

                        try {

                            alertData = new AlertListData(postDate,
                                    jsonObjectForIteration.get("Title").toString().replace("\"", ""),
                                    alertDescription.substring(1, alertDescription.length() - 1), "",
                                    jsonObjectForIteration.get("Category").toString().replace("\"", ""),
                                    jsonObjectForIteration.get("Alert_class").toString().replace("\"", ""),
                                    jsonObjectForIteration.get("alert_divison").toString().replace("\"", ""),
                                    jsonObjectForIteration.get("firstName").toString().replace("\"", "") + " " + jsonObjectForIteration.get("lastName").toString().replace("\"", ""),
                                    jsonObjectForIteration.get("Student_id").toString().replace("\"", ""),
                                    jsonObjectForIteration.get("id").toString().replace("\"", ""),
                                    jsonObjectForIteration.get("Attachment_count").toString().replace("\"", ""), jsonObjectForIteration.get("Alert_priority").toString().replace("\"", ""));


                        } catch (Exception e) {
                            System.out.println("alertData-----" + alertData);
                            System.out.println("Error in Alert Data---" + e.getMessage());
                        }


                    }
                    if (userRole.equals("Student")) {
                        System.out.println("firstName ; " + jsonObjectForIteration.get("firstName").toString().replace("\"", ""));
                        System.out.println("lastName ; " + jsonObjectForIteration.get("lastName").toString().replace("\"", ""));

//                    alertData = new AlertListData(postDate,
//                            jsonObjectForIteration.get("Title").toString().replace("\"", ""),
//                            alertDescription.substring(1, alertDescription.length() - 1),
//                            jsonObjectForIteration.get("Category").toString().replace("\"", ""),
//                            "", "", "", "", jsonObjectForIteration.get("firstName").toString().replace("\"", "") + " " + jsonObjectForIteration.get("lastName").toString().replace("\"", ""),
//                            jsonObjectForIteration.get("id").toString().replace("\"", ""),
//                            jsonObjectForIteration.get("Attachment_count").toString().replace("\"", ""),
//                            jsonObjectForIteration.get("Alert_priority").toString().replace("\"", "")
//                    );

                        alertData = new AlertListData(postDate,
                                jsonObjectForIteration.get("Title").toString().replace("\"", ""),
                                alertDescription.substring(1, alertDescription.length() - 1),
                                jsonObjectForIteration.get("firstName").toString().replace("\"", "") + " " + jsonObjectForIteration.get("lastName").toString().replace("\"", ""),
                                jsonObjectForIteration.get("Category").toString().replace("\"", ""),
                                "", "", "", "", jsonObjectForIteration.get("id").toString().replace("\"", ""), jsonObjectForIteration.get("Attachment_count").toString().replace("\"", ""), jsonObjectForIteration.get("Alert_priority").toString().replace("\"", "")
                        );
                    }

                    alertList.add(0, alertData);
                }
            } else {
                noDataAvailableTextView.setVisibility(View.VISIBLE);
                System.out.println("Alert Json  is null");
            }


         /* Alert Json Parsing End.
            And Prepare List to show user3
         */
            flagRefresh = true;
            alertAdapter = new AlertAdapterAdapter(getActivity(), alertList);
            listViewAlerts.setAdapter(alertAdapter);
            progressDialog.dismiss();
            alertTimelineStick.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            System.out.println("Catch AlertFragment " + e.getMessage());
        }

    }

    public class TimeOutTimerClass extends TimerTask {
        int count = 0;
        Boolean check = false;

        @Override
        public void run() {
            alert_Handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Alert Timer Class check Variable  " + check);
                    if (!check) {
                        //fetchAlertDataTask.cancel(true);
                        alert_Timer.cancel();
                        progressDialog.dismiss();

                        System.out.println("thread cancel call" + (count++));
                        //System.out.println("fetchAlertDataTask  Status " + fetchAlertDataTask.getStatus());

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
                                            alert_Timer.purge();
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
                                    }).setMessage(R.string.check_network).create().show();

                        } catch (Exception e) {
                            /* it may produce null pointer exception on creation of alert dialog object
                             */
                            System.out.println("Exception Handle for Alert Dialog");
                        }
                    } else {
                        /*   this line of code is used when everything goes normal.
                             and cancel the timer.*/
                        alert_Timer.cancel();
                    }
                }
            });
        }
    }

}