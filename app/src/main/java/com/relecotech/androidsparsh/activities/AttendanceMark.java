package com.relecotech.androidsparsh.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.AttendanceMarkAdapterAdapter;
import com.relecotech.androidsparsh.controllers.AttendanceMarkListData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceMark extends AppCompatActivity {
    int presentCount = 0;
    int j = 0;
    long postingDate;

    ListView attendanceListView;
    ArrayList<AttendanceMarkListData> attendancelist;
    CheckBox checkAll;
    ProgressDialog postAttendanceprogressDialog;
    TextView presentCounterTextView, classTextView;
    AttendanceMarkAdapterAdapter markStudentadapter;
    AttendanceMarkListData selectedAttendanceMarkListData;
    private ProgressBar attendanceProgressbar;
    private boolean checkconnection;
    private ConnectionDetector connectionDetector;
    private String schoolClassIdToFetchStudentList;
    private MobileServiceJsonTable mobileServiceJsonTable;
    private String student_middleName;
    private String student_Id;
    private String student_firstName;
    private String student_lastName;
    private String student_rollNo;
    private JsonArray getJsonListResponse;
    String classSuffixArray[] = {"st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "th", "th"};
    private Calendar calendar;
    private int year;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    private JsonArray jsonArrayForNewAttendanceMark;
    private boolean dataReceivedStatus;
    private RelativeLayout markAttendanceLayout;
    private AlertDialog.Builder builder;
    private String markedDate;
    private JsonObject jsonObjectForStudentList;
    private TextView noDataTextView;
    private Button draftBtn;
    private DatabaseHandler dbHandler;
    private String attendanceDateString;
    private long attendanceDate;
    private Cursor studentNamesDraftCursor;
    private Cursor studentNamesFreshCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_mark);

        //flag to check weather data is received or not
        dataReceivedStatus = false;

        //get data from attendance teacher fragment through intent
        schoolClassIdToFetchStudentList = getIntent().getStringExtra("schoolClassId");
        String task = getIntent().getStringExtra("Task");
        String attendanceDivision = getIntent().getStringExtra("Division");
        int attendanceClass = Integer.parseInt(getIntent().getStringExtra("Class"));

        checkAll = (CheckBox) findViewById(R.id.markAllCheckBox);
        attendanceProgressbar = (ProgressBar) findViewById(R.id.attendanceMarkProgressbar);
        attendanceProgressbar.setVisibility(View.INVISIBLE);

        postAttendanceprogressDialog = new ProgressDialog(AttendanceMark.this);
        postAttendanceprogressDialog.setCancelable(false);
        postAttendanceprogressDialog.setMessage(AttendanceMark.this.getString(R.string.loading));

        classTextView = (TextView) findViewById(R.id.attendanceClassTextView);
        noDataTextView = (TextView) findViewById(R.id.noDataAttendanceMarkTextView);
        noDataTextView.setVisibility(View.INVISIBLE);

        markAttendanceLayout = (RelativeLayout) findViewById(R.id.attendanceMarkLayout);
        classTextView.setText(attendanceClass + classSuffixArray[--attendanceClass] + " " + attendanceDivision);


        dbHandler = new DatabaseHandler(this);

        calendar = Calendar.getInstance();
        postingDate = calendar.getTimeInMillis();

        attendanceDate = calendar.getTimeInMillis();

        //convert long to string date
        Date attendancedate = new Date(attendanceDate);

        DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        attendanceDateString = format.format(attendancedate);

        //creating table object for attendance mark operation
        mobileServiceJsonTable = MainActivity.mClient.getTable("student");

        //getting teacher details from shared preferences
        sessionManager = new SessionManager(getApplication());
        userDetails = sessionManager.getUserDetails();

        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkconnection = connectionDetector.isConnectingToInternet();


        presentCounterTextView = (TextView) findViewById(R.id.presentCountTextView);
        //submitAttendance = (Button) findViewById(R.id.attendanceSubmitButton);
        draftBtn = (Button) findViewById(R.id.draftButton);
        attendanceListView = (ListView) findViewById(R.id.markAttendanceListView);
        attendancelist = new ArrayList<>();

        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attendanceRefreshAdapter = new AttendanceMarkAdapterAdapter(getApplicationContext(), attendancelist);
                if (dataReceivedStatus) {
                    if (!checkAll.isChecked()) {
                        for (j = 0; j < attendanceListView.getCount(); j++) {
                            selectedAttendanceMarkListData = attendancelist.get(j);
                            selectedAttendanceMarkListData.setPresentStatus("A");
                        }
                        presentCount = 0;
                    } else {
                        presentCount = 0;
                        for (j = 0; j < attendanceListView.getCount(); j++) {
                            selectedAttendanceMarkListData = attendancelist.get(j);
                            selectedAttendanceMarkListData.setPresentStatus("P");
                            presentCount++;
                        }
                    }
                    int index = attendanceListView.getFirstVisiblePosition();
                    View v = attendanceListView.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();

                    markStudentadapter.notifyDataSetChanged();
                    presentCounterTextView.setText(Integer.toString(presentCount));
                    //attendanceListView.setAdapter(attendanceRefreshAdapter);
                    attendanceListView.setSelectionFromTop(index, top);
                }
            }

        });

        attendanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                markIndividualAttendance(i);


            }
        });


        studentNamesDraftCursor = dbHandler.getStudentNames(schoolClassIdToFetchStudentList, attendanceDateString);
        System.out.println("studentNamesDraftCursor.getCount() : " + studentNamesDraftCursor.getCount());
        if (studentNamesDraftCursor.getCount() == 0) {

            studentNamesFreshCursor = dbHandler.getStudentNames(schoolClassIdToFetchStudentList);
            System.out.println("studentNamesFreshCursor.getCount() : " + studentNamesFreshCursor.getCount());
            if (studentNamesFreshCursor.getCount() == 0) {

                if (checkconnection) {

                    //Rest calling done here after connection check
                    if (task.equals("MarkAttendance")) {
                        //fetch student list for new attendance mark
                        attendanceProgressbar.setVisibility(View.VISIBLE);
                        FetchStudentList();
                    } else {
                        //fetch student list for review attendance
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "No internet connection..!", Toast.LENGTH_LONG).show();
                }
            } else {
                //set student list from cursor
                presentCount = 0;

                studentNamesFreshCursor.moveToFirst();
                do {
                    attendancelist.add(new AttendanceMarkListData(studentNamesFreshCursor.getString(studentNamesFreshCursor.getColumnIndex("AttendanceStudentId")),
                            (studentNamesFreshCursor.getString(studentNamesFreshCursor.getColumnIndex("AttendanceStudentRollNo"))),
                            (studentNamesFreshCursor.getString(studentNamesFreshCursor.getColumnIndex("AttendanceStudentFullName"))),
                            "P"));

                    presentCount++;
                } while (studentNamesFreshCursor.moveToNext());

                presentCounterTextView.setText(Integer.toString(presentCount));
                markStudentadapter = new AttendanceMarkAdapterAdapter(getApplicationContext(), attendancelist);
                attendanceListView.setAdapter(markStudentadapter);
                dataReceivedStatus = true;
            }
        } else {
            //set student list from cursor
            presentCount = 0;

            studentNamesDraftCursor.moveToFirst();
            do {
                attendancelist.add(new AttendanceMarkListData(studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("AttendanceStudentId")),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("AttendanceStudentRollNo"))),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("AttendanceStudentFullName"))),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("AttendanceStatus")))));

                if ((studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("AttendanceStatus"))).equals("P")) {
                    System.out.println("INSIDE OFFLINE PRESENT");
                    presentCount++;
                }
            } while (studentNamesDraftCursor.moveToNext());
            System.out.println("OFFLINE presentCount : " + presentCount);
            presentCounterTextView.setText(Integer.toString(presentCount));

            markStudentadapter = new AttendanceMarkAdapterAdapter(getApplicationContext(), attendancelist);
            attendanceListView.setAdapter(markStudentadapter);
            dataReceivedStatus = true;

        }


        draftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AttendanceMark.this)
                        .setTitle("Save draft")
                        .setMessage("" + presentCount + " Student present")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Save draft", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                System.out.println(" schoolClassIdToFetchStudentList  " + schoolClassIdToFetchStudentList);
                                System.out.println(" attendanceDateString  " + attendanceDateString);
                                //deleted older draft and then insert new draft
                                dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                                //add student list to database for 1st time with draft status as 1(true)
                                dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, attendanceDateString, 1);
                                printAllData();
                                finish();

                            }
                        })
                        .create().show();

            }
        });

        attendanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                markIndividualAttendance(i);
            }
        });


    }


    private void printAllData() {
        Cursor allDataCursor = dbHandler.getAllAttendanceData();
        if (allDataCursor.moveToFirst()) {
            do {
                System.out.println("------- " + allDataCursor.getString(0)
                        + " " + allDataCursor.getString(1)
                        + " " + allDataCursor.getString(2)
                        + " " + allDataCursor.getString(3)
                        + " " + allDataCursor.getString(4)
                        + " " + allDataCursor.getString(5)
                        + " " + allDataCursor.getString(6)
                        + " " + allDataCursor.getString(7));

            } while (allDataCursor.moveToNext());
        }
    }


    private void markAttendanceToAzure() {

        Log.d("attendanceMarkjson json", "" + attendancelist);
        //start progress dialog and then do all process of new attendance
        //postAttendanceprogressDialog = ProgressDialog.show(AttendanceMark.this, AttendanceMark.this.getString(R.string.loading), "", true);
        postAttendanceprogressDialog.show();


        //convert list data to json Array
        AttendanceMarkListData attendanceMarkListDataForIteration;
        JsonObject jsonObjectForIteration;
        jsonArrayForNewAttendanceMark = new JsonArray();

        //loop to convert list data to jsonarray
        for (int i = 0; i < attendancelist.size(); i++) {
            attendanceMarkListDataForIteration = attendancelist.get(i);

            jsonObjectForIteration = new JsonObject();
            jsonObjectForIteration.addProperty("Student_id", attendanceMarkListDataForIteration.getStudentId());
            jsonObjectForIteration.addProperty("attendance_date", postingDate);
            jsonObjectForIteration.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));
            jsonObjectForIteration.addProperty("Active", 1);
            jsonObjectForIteration.addProperty("status", attendanceMarkListDataForIteration.getPresentStatus());
            jsonObjectForIteration.addProperty("SchoolClassId", schoolClassIdToFetchStudentList);
            String submittedByTeacherName = userDetails.get(SessionManager.KEY_FIRST_NAME) + " " + userDetails.get(SessionManager.KEY_LAST_NAME);
            jsonObjectForIteration.addProperty("SubmittedByTeacherName", submittedByTeacherName);

            jsonArrayForNewAttendanceMark.add(jsonObjectForIteration);
        }


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("markAttendanceApi", jsonArrayForNewAttendanceMark);

        //creating current date to save into shared pref when attendance is marked
        markedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        System.out.println("markedDate : " + markedDate);

        builder = new AlertDialog.Builder(AttendanceMark.this);
        postAttendanceprogressDialog.dismiss();
        builder.setCancelable(false);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Attendance Mark exception    " + exception);
                //something wrong has happened
                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        markAttendanceToAzure();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setTitle("Error");
                builder.show();

            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" ATTENDANCE MARK API  response    " + response);

                if (response.toString().equals("true")) {
                    // new attendance has ben marked successfully
                    sessionManager.setSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE, markedDate);


                    //deleted older draft
                    dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            printAllData();
                            finish();
                        }
                    });
                    builder.setTitle("Attendance Marked.");
                    builder.show();
                } else if (response.toString().equals("false")) {

                    //show dialog stating today's attendance already marked
                    System.out.println("attendance exist");
                    //deleted older draft
                    dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);

                    sessionManager.setSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE, markedDate);
                    builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            printAllData();
                            //printAllData();
                            finish();
                        }
                    });
                    builder.setTitle("Attendance already marked.");
                    builder.show();

                } else {
                    //something wrong has happened
                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            markAttendanceToAzure();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNeutralButton("Save draft", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //add student list to database for 1st time with draft status as 1(true)
                            dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            printAllData();
                        }
                    });
                    builder.setTitle("Error");
                    builder.show();
                }

            }
        });

    }

    private void markIndividualAttendance(int position) {

        selectedAttendanceMarkListData = attendancelist.get(position);
        System.out.println(" selectedAttendanceMarkListData " + selectedAttendanceMarkListData);

        String checkpresentStatus = selectedAttendanceMarkListData.getPresentStatus();
        System.out.println(" checkpresentStatus " + checkpresentStatus);

        if (checkpresentStatus.equals("P")) {
            selectedAttendanceMarkListData.setPresentStatus("A");
            presentCount--;
        } else {
            selectedAttendanceMarkListData.setPresentStatus("P");
            presentCount++;
        }
        int index = attendanceListView.getFirstVisiblePosition();
        View v = attendanceListView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();

        markStudentadapter.notifyDataSetChanged();
        //attendanceListView.setAdapter(attendanceRefreshAdapter);
        attendanceListView.setSelectionFromTop(index, top);
        presentCounterTextView.setText(Integer.toString(presentCount));
    }

    @Override
    public void onBackPressed() {

        if (dataReceivedStatus) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Action")
                    .setMessage("" + presentCount + " Student present")
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // submit attendance on backpress confirmation
                            markAttendanceToAzure();

                        }
                    })
                    .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //deleted older draft
                            dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            printAllData();
                            finish();

                        }
                    })
                    .setNegativeButton("Save draft", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //deleted older draft and then insert new draft
                            dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            //add student list to database for 1st time with draft status as 1(true)
                            dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            printAllData();
                            finish();

                        }
                    })
                    .create().show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Action")
                    .setMessage("" + presentCount + " Student present")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create().show();
        }
    }


    private void FetchStudentList() {

        jsonObjectForStudentList = new JsonObject();

        // Yogesh changes for invoke api
        jsonObjectForStudentList.addProperty("SchoolClassId", schoolClassIdToFetchStudentList);
        jsonObjectForStudentList.addProperty("UserRole", userDetails.get(SessionManager.KEY_USER_ROLE));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchStudentListApi", jsonObjectForStudentList);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Fetch Student List Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Fetch Student List  API   response    " + response);

                StudentListJsonParse(response);
            }
        });
    }

    private void StudentListJsonParse(JsonElement jsonElement) {

        getJsonListResponse = jsonElement.getAsJsonArray();
        try {
            if (getJsonListResponse.size() == 0) {
                System.out.println("data not received");
                noDataTextView.setVisibility(View.VISIBLE);
                noDataTextView.setGravity(Gravity.CENTER);
                attendanceProgressbar.setVisibility(View.INVISIBLE);

            } else {
                noDataTextView.setVisibility(View.INVISIBLE);
                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                    System.out.println(" INSiDE FOR LOOP");
                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                    student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    student_middleName = jsonObjectForIteration.get("middleName").toString().replace("\"", "");
                    student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    student_rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
                    System.out.println("student_Id" + student_Id);
                    System.out.println("student_count" + loop);
                    AttendanceMarkListData attendanceMarkdata = new AttendanceMarkListData(student_Id, student_rollNo,
                            student_firstName + " " + student_lastName, "P");

                    attendancelist.add(attendanceMarkdata);
                    presentCount++;
                }
                markStudentadapter = new AttendanceMarkAdapterAdapter(getApplicationContext(), attendancelist);
                attendanceListView.setAdapter(markStudentadapter);

                //add student list to database for 1st time with draft status as 0(false)
                dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, null, 0);

                attendanceProgressbar.setVisibility(View.INVISIBLE);
                presentCounterTextView.setText(Integer.toString(presentCount));
                dataReceivedStatus = true;

                sessionManager.setSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE, "customDate");

            }

        } catch (Exception e) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attendance_mark_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_send:
                //submitAlert();
                markAttendanceToAzure();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private class FetchStudentList extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                System.out.println("Doing Bqackground.......");
//                //JsonElement jsonElement = mobileServiceJsonTable.parameter("SchoolClassId", schoolClassIdToFetchStudentList).execute().get();
//                System.out.println("SessionManager.KEY_USER_ROLE-----------------------" + userDetails.get(SessionManager.KEY_USER_ROLE));
//                System.out.println("schoolClassIdToFetchStudentList---" + schoolClassIdToFetchStudentList);
//
//                JsonElement jsonElement = mobileServiceJsonTable.parameter("SchoolClassId", schoolClassIdToFetchStudentList).parameter("UserRole", userDetails.get(SessionManager.KEY_USER_ROLE)).execute().get();
//                System.out.println("jsonElement" + jsonElement);
//                getJsonListResponse = jsonElement.getAsJsonArray();
//                System.out.println("getJsonListResponse.size()" + getJsonListResponse.size());
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            try {
//                if (getJsonListResponse.size() == 0) {
//                    System.out.println("data not recived");
//                }
//                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {
//
//                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
//                    student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
//                    student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
//                    student_middleName = jsonObjectForIteration.get("middleName").toString().replace("\"", "");
//                    student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
//                    student_rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
//                    System.out.println("student_Id" + student_Id);
//                    System.out.println("student_count" + loop);
//                    AttendanceMarkListData attendanceMarkdata = new AttendanceMarkListData(student_Id, student_rollNo,
//                            student_firstName + " " + student_lastName, "P");
//
//                    attendancelist.add(attendanceMarkdata);
//                    presentCount++;
//                }
//                markStudentadapter = new AttendanceMarkAdapterAdapter(getApplicationContext(), attendancelist);
//                attendanceListView.setAdapter(markStudentadapter);
//                attendanceProgressbar.setVisibility(View.INVISIBLE);
//                presentCounterTextView.setText(Integer.toString(presentCount));
//                dataReceivedStatus = true;
//
//                sessionManager.setSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE, "customDate");
//
//            } catch (Exception e) {
//
//            }
//
//        }
//    }
}
