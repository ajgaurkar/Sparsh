package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.AttendanceReviewAdapterAdapter;
import com.relecotech.androidsparsh.controllers.AttendanceReviewListData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceReview extends Activity {

    ListView attendanceReviewListView;
    ArrayList<AttendanceReviewListData> attendanceReviewlist;
    TextView reviewPresentCounterTextView, reviewAbsentCounterTextView, datetextView, classTextView;
    private int presentCount = 0;
    private ProgressBar attendanceReviewProgressbar;
    private AttendanceReviewAdapterAdapter attendanceReviewStudentadapter;
    private JsonArray attendnaceArray;
    private int counterForPresent = 0;
    private int counterForAbsent = 0;

    private boolean checkconnection;
    private ConnectionDetector connectionDetector;

    String schoolClassIdForReview;
    Date dateForReview;
    String classSuffixArray[] = {"st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "th", "th"};
    MobileServiceJsonTable mobileServiceJsonTable;
    private String finalDate;
    private JsonElement responseJsonElement;
    private TextView noAttendanceLabelBodyTextView;
    private TextView noAttendanceLabelHeaderTextView;
    private RelativeLayout footerLayout;
    private JsonObject jsonObjectToReviewAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        setContentView(R.layout.attendance_review);

        //getting data through intent
        // to fetch student attendance review list
        schoolClassIdForReview = getIntent().getStringExtra("reviewClassId");
        int attendanceClass = Integer.parseInt(getIntent().getStringExtra("Class"));
        String attendanceDivision = getIntent().getStringExtra("Division");
        getIntent().getStringExtra("reviewClassId");
        System.out.println("getIntent().reviewDate : " + getIntent().getStringExtra("reviewDate"));
        DateFormat sourceDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetDateFormat = null;

        try {
            dateForReview = sourceDateFormat.parse(getIntent().getStringExtra("reviewDate"));
            targetDateFormat = new SimpleDateFormat("dd MMM yy");
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.print("exception in date conversion from intent string to date");
        }

        //initialize ui components
        reviewPresentCounterTextView = (TextView) findViewById(R.id.reviewPresentCountTextView);
        reviewAbsentCounterTextView = (TextView) findViewById(R.id.reviewAbsentCountTextView);
        datetextView = (TextView) findViewById(R.id.attendanceReviewClassDateTextView);
        classTextView = (TextView) findViewById(R.id.attendanceReviewClassTextView);
        footerLayout = (RelativeLayout) findViewById(R.id.attendanceReviewFooterLayout);
        noAttendanceLabelBodyTextView = (TextView) findViewById(R.id.noAttendanceAvailableBodyTextView);
        noAttendanceLabelHeaderTextView = (TextView) findViewById(R.id.noAttendanceAvailableHeaderTextView);
        attendanceReviewProgressbar = (ProgressBar) findViewById(R.id.attendanceReviewprogress);
        attendanceReviewListView = (ListView) findViewById(R.id.reviewAttendanceListView);


        //creating azure table object for attendance review operation
//        mobileServiceJsonTable = MainActivity.mClient.getTable("attendance");
//        System.out.println("mobileServiceJsonTable object : " + mobileServiceJsonTable);

        // Condition just for a null pointer warning
        if (targetDateFormat != null) {
            finalDate = targetDateFormat.format(dateForReview);
            datetextView.setText("On  " + targetDateFormat.format(dateForReview));
        }
        attendanceReviewlist = new ArrayList<>();
        classTextView.setText(attendanceClass + classSuffixArray[--attendanceClass] + " " + attendanceDivision);

        //check network connection
        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkconnection = connectionDetector.isConnectingToInternet();
        if (checkconnection) {
            //Rest calling done here after connection check
            //new AttendaceReviewRestcalling().execute();
            AttendanceReviewCallingApi();
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection..!.", Toast.LENGTH_LONG).show();
        }
        for (int i = 0; i < attendanceReviewlist.size(); i++) {
            AttendanceReviewListData countAttendanceReviewListData = attendanceReviewlist.get(i);
            if (countAttendanceReviewListData.getPresentStatus() == "P") {
                counterForPresent++;
            }
            if (countAttendanceReviewListData.getPresentStatus() == "A") {
                counterForAbsent++;
            }
        }
        reviewPresentCounterTextView.setText(Integer.toString(counterForPresent));
        reviewAbsentCounterTextView.setText(Integer.toString(counterForAbsent));

        attendanceReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
    }

    private void AttendanceReviewCallingApi() {
        jsonObjectToReviewAttendance = new JsonObject();
        jsonObjectToReviewAttendance.addProperty("reviewDate", finalDate);
        jsonObjectToReviewAttendance.addProperty("UserRole", "Teacher");
        jsonObjectToReviewAttendance.addProperty("schoolClassId", schoolClassIdForReview);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("attendanceReviewApi", jsonObjectToReviewAttendance);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("  Attendance Review Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Attendance Review API   response    " + response);

                //converting jsonElement to jsonArray
                attendnaceArray = response.getAsJsonArray();
                System.out.println(" AttendanceArray  " + attendnaceArray);
                AttendanceReviewParsing();

            }
        });

    }

    private void AttendanceReviewParsing() {

        try {
            if (attendnaceArray.size() == 0) {
                System.out.println("data not recived");
                noAttendanceLabelHeaderTextView.setVisibility(View.VISIBLE);
                noAttendanceLabelBodyTextView.setVisibility(View.VISIBLE);
                noAttendanceLabelBodyTextView.setText("For " + finalDate);
                footerLayout.setVisibility(View.INVISIBLE);
            } else {
                int totalStudentPresent = 0;
                int totalStudentAbsent = 0;
                for (int loop = 0; loop < attendnaceArray.size(); loop++) {

                    JsonObject jsonObjectForIteration = attendnaceArray.get(loop).getAsJsonObject();
                    String rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
                    String first_name = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String student_middleName = jsonObjectForIteration.get("middleName").toString().replace("\"", "");
                    String last_name = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String present_status = jsonObjectForIteration.get("status").toString().replace("\"", "");
                    System.out.print("student_count : " + loop + " rollNo : " + rollNo + " first_name : " + first_name + " student_middleName : " + student_middleName + " last_name : " + last_name + " present_status : " + present_status);

                    //counting total absent and present students
                    if (present_status.equals("A")) {
                        totalStudentAbsent++;
                    }
                    if (present_status.equals("P")) {
                        totalStudentPresent++;
                    }
                    AttendanceReviewListData attendanceReviewdata = new AttendanceReviewListData(first_name, student_middleName, last_name, present_status, rollNo);
                    attendanceReviewlist.add(attendanceReviewdata);
                }

                //set data to adapter
                attendanceReviewStudentadapter = new AttendanceReviewAdapterAdapter(getApplicationContext(), attendanceReviewlist);
                attendanceReviewListView.setAdapter(attendanceReviewStudentadapter);


                //set present and absebt counts to respective textviews
                footerLayout.setVisibility(View.VISIBLE);
                reviewPresentCounterTextView.setText(String.valueOf(totalStudentPresent));
                reviewAbsentCounterTextView.setText(String.valueOf(totalStudentAbsent));

            }
            //dismiss progressbar
            attendanceReviewProgressbar.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}