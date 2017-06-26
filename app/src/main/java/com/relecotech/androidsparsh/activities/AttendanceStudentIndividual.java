package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.controllers.StudentAttendanceListData;
import com.relecotech.androidsparsh.fragments.DashboardStudentFragment;
import com.relecotech.androidsparsh.fragments.DashboardTeacherFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceStudentIndividual extends Activity {

    private ProgressDialog attendanceProgressDialog;
    String userRole;
    private Spinner monthSelector;
    private TextView monthPercent;
    private List<String> monthList;
    private ArrayAdapter<String> monthAdapter;
    private String selectedStudentId;
    private MobileServiceJsonTable studentAttendanceTable;

    private CombinedChart combinedChart;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private MobileServiceJsonTable StudentAttendanceTable;
    private HashMap<Integer, StudentAttendanceListData> attendaceStudentHashMap;
    private HashMap<Integer, String> monthNameHashMap;
    private float percentageOfAttendance;
    private int percentageInInterger;
    private int presnetStatusCount;
    private int totalNoOfDays;
    private StudentAttendanceListData studentAttendancListData;
    private Set<Integer> keys;
    private JsonObject jsonObjectToFetchAttendance;
    private JsonArray attendnaceArray;
    private TextView yearlyAttendnace;
    private int totaldays;
    private int presentday;
    private Integer positionVal;
    private Handler attendance_Handler;
    private static Timer attendance_Timer;
    private TimeOutTimerClass attendanceTimeOutTimerClass;
    private long TIMEOUT_TIME = 15000;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        setContentView(R.layout.attendance_individual_review_student);

        selectedStudentId = getIntent().getStringExtra("studentId");
        String selectedStudentName = getIntent().getStringExtra("studentName");

        System.out.println("selectedStudentId : " + selectedStudentId);
        System.out.println("selectedStudentName : " + selectedStudentName);

        userRole = MainActivity.userRole;
        Log.d("login_user_role", userRole);

        attendance_Handler = new Handler();
        attendanceTimeOutTimerClass = new TimeOutTimerClass();
        attendance_Timer = new Timer();

        attendanceProgressDialog = new ProgressDialog(this);
        attendanceProgressDialog.setMessage(this.getString(R.string.loading));
        attendanceProgressDialog.setCancelable(false);

        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        StudentAttendanceTable = MainActivity.mClient.getTable("Attendance");
        attendaceStudentHashMap = new HashMap<>();

        monthList = new ArrayList<>();

        monthNameHashMap = new HashMap<>();
        monthNameHashMap.put(1, "January");
        monthNameHashMap.put(2, "February");
        monthNameHashMap.put(3, "March");
        monthNameHashMap.put(4, "April");
        monthNameHashMap.put(5, "May");
        monthNameHashMap.put(6, "June");
        monthNameHashMap.put(7, "July");
        monthNameHashMap.put(8, "August");
        monthNameHashMap.put(9, "September");
        monthNameHashMap.put(10, "October");
        monthNameHashMap.put(11, "November");
        monthNameHashMap.put(12, "December");


        monthPercent = (TextView) findViewById(R.id.monthCountIndividualStudentReview);
        yearlyAttendnace = (TextView) findViewById(R.id.yearlyattendancetextView);
        monthSelector = (Spinner) findViewById(R.id.individualStudentReviewMonthselectorSpiner);
        combinedChart = (CombinedChart) findViewById(R.id.individualStudentAttendanceCombinedChart);
        System.out.println("Calendar.getInstance().YEAR---------" + Calendar.getInstance().YEAR);
        Calendar calendar = Calendar.getInstance();
        int getcurrentYear = calendar.get(Calendar.YEAR);
        System.out.println("Current year--" + getcurrentYear);
        yearlyAttendnace.setText("Yearly attendance " + getcurrentYear);
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        boolean checkconnection = connectionDetector.isConnectingToInternet();
        if (checkconnection) {
            //Rest calling done here after connection check
            //new GettingStudentAttendance().execute();
            onExecutionStart();
            System.out.println(" Outside the GettingStudentAttendance");
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection..!", Toast.LENGTH_LONG).show();
        }


        System.out.println(" Outside the  async");
        monthSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) monthSelector.getItemAtPosition(position);
                Log.d("selected Item Position", "" + s);
                switch (s) {
                    case "January":
                        percentageOfAttendance = getPercentageOfAttendance(1);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "February":

                        percentageOfAttendance = getPercentageOfAttendance(2);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "March":

                        percentageOfAttendance = getPercentageOfAttendance(3);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "April":

                        percentageOfAttendance = getPercentageOfAttendance(4);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "May":

                        percentageOfAttendance = getPercentageOfAttendance(5);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "June":

                        percentageOfAttendance = getPercentageOfAttendance(6);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "July":

                        percentageOfAttendance = getPercentageOfAttendance(7);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "August":

                        percentageOfAttendance = getPercentageOfAttendance(8);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "September":

                        percentageOfAttendance = getPercentageOfAttendance(9);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "October":

                        percentageOfAttendance = getPercentageOfAttendance(10);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "November":

                        percentageOfAttendance = getPercentageOfAttendance(11);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;
                    case "December":

                        percentageOfAttendance = getPercentageOfAttendance(12);
                        percentageInInterger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInterger) + "%");
                        break;


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onExecutionStart() {
        attendance_Timer = new Timer("attendanceTimer", true);
        attendanceTimeOutTimerClass = new TimeOutTimerClass();
        attendanceProgressDialog.show();
        FetchStudentAttendance();
        attendance_Timer.schedule(attendanceTimeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("onExecutionStart---");
    }

    public void reScheduleTimer() {
        attendance_Timer = new Timer("attendanceTimer", true);
        attendanceTimeOutTimerClass = new TimeOutTimerClass();
        attendanceProgressDialog.show();
        FetchStudentAttendance();
        attendance_Timer.schedule(attendanceTimeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("onExecutionStart---");
    }

    private void FetchStudentAttendance() {
        jsonObjectToFetchAttendance = new JsonObject();
        jsonObjectToFetchAttendance.addProperty("StudentID", selectedStudentId);
        jsonObjectToFetchAttendance.addProperty("UserRole", "Student");

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("attendanceReviewApi", jsonObjectToFetchAttendance);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Attendance Individual exception    " + exception);
                attendanceTimeOutTimerClass.check = false;
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Attendance Individual    response    " + response);

                //converting jsonElement to jsonArray
                attendnaceArray = response.getAsJsonArray();
                System.out.println(" AttendanceArray  " + attendnaceArray);
                AttendanceParsing(attendnaceArray);

            }
        });

    }

    private void AttendanceParsing(JsonArray attendanceArray) {


        try {
            if (attendanceArray.size() == 0) {
                attendanceTimeOutTimerClass.check = false;
                Log.d("json not received", "not received");
            } else {
                attendanceTimeOutTimerClass.check = true;
                for (int k = 0; k < attendanceArray.size(); k++) {
                    JsonObject jsonObjectforIteration = attendanceArray.get(k).getAsJsonObject();
                    String studentAttendanceStatus = jsonObjectforIteration.get("status").toString().replace("\"", "");
                    String AttendaceDate = jsonObjectforIteration.get("attendance_date").toString().replace("\"", "");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                    Date attendancedate;

                    try {
                        attendancedate = simpleDateFormat.parse(AttendaceDate);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());

                        String studentAttendanceDate = targetDateFormat.format(attendancedate);
                        System.out.println(k + "  studentAttendanceDate &  " + studentAttendanceDate + " studentAttendanceStatus  " + studentAttendanceStatus);
                        int month = (attendancedate.getMonth() + 1);

                        if (!attendaceStudentHashMap.containsKey(month)) {
                            System.out.println("In IF Condition...........");
                            presnetStatusCount = 0;
                            totalNoOfDays = 0;
                            if (studentAttendanceStatus.contains("P")) {
                                presnetStatusCount++;
                            }
                            studentAttendancListData = new StudentAttendanceListData(month, presnetStatusCount, totalNoOfDays + 1);
                            attendaceStudentHashMap.put(month, studentAttendancListData);
                        } else {
                            System.out.println("In ELSE Condition...........");
                            StudentAttendanceListData value = attendaceStudentHashMap.get(month);
                            System.out.println(k + " month " + value.getMonth());
                            System.out.println(k + " PresentDay " + value.getPresentDays());
                            System.out.println(k + " TotalDays " + value.getTotalDays());
                            totalNoOfDays = value.getTotalDays() + 1;
                            if (studentAttendanceStatus.contains("P")) {
                                presnetStatusCount = value.getPresentDays() + 1;
                            }
                            System.out.println("After Update Total Count " + presnetStatusCount);
                            System.out.println("After Update Present Days " + totalNoOfDays);

                            studentAttendancListData = new StudentAttendanceListData(month, presnetStatusCount, totalNoOfDays);
                            attendaceStudentHashMap.put(month, studentAttendancListData);
                            System.out.println("Total Count " + presnetStatusCount);
                            System.out.println("Present Days " + totalNoOfDays);

                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                        /*  below logic used for to add data in Month Spinner */

                //keys = attendaceStudentHashMap.keySet();
                keys = new TreeSet<Integer>(attendaceStudentHashMap.keySet());
                for (Integer key : keys) {
                    System.out.println("Key of hash map onPostExecute  " + key);
                    monthList.add(0, monthNameHashMap.get(key));
                }
                monthAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, monthList);
                monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                monthSelector.setAdapter(monthAdapter);
//                System.out.println("monthAdapter.getCount()" + monthAdapter.getCount());
//                monthSelector.setSelection(monthAdapter.getCount() - 1);

                //method to call Logic for plotting graph.
                DrawGraph();
                attendanceProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public class GettingStudentAttendance extends AsyncTask<Void, Void, Void> {
//
//        private JsonElement individualStudentAttendanceRespone;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            try {
//
//                /*role is teacher here, but data is being fetched of student,
//                 hence role as "Student" is passed to the following request*/
//
//                individualStudentAttendanceRespone = StudentAttendanceTable.parameter("StudentID", selectedStudentId).parameter("UserRole", "Student").execute().get();
//                JsonArray attendanceJsonArray = individualStudentAttendanceRespone.getAsJsonArray();
//                System.out.println("attendanceJsonArray  " + attendanceJsonArray);
//                if (attendanceJsonArray.size() == 0) {
//                    Log.d("json not received", "not received");
//                } else {
//                    for (int k = 0; k < attendanceJsonArray.size(); k++) {
//                        JsonObject jsonObjectforIteration = attendanceJsonArray.get(k).getAsJsonObject();
//                        String studentAttendaceStatus = jsonObjectforIteration.get("status").toString().replace("\"", "");
//                        String AttendaceDate = jsonObjectforIteration.get("attendance_date").toString().replace("\"", "");
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
//                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
//                        Date attendancedate;
//
//                        try {
//                            attendancedate = simpleDateFormat.parse(AttendaceDate);
//                            targetDateFormat.setTimeZone(TimeZone.getDefault());
//
//                            String studentAttendaceDate = targetDateFormat.format(attendancedate);
//                            System.out.println(k + "  studentAttendaceDate &  " + studentAttendaceDate + " studentAttendaceStatus  " + studentAttendaceStatus);
//                            int month = (attendancedate.getMonth() + 1);
//
//                            if (!attendaceStudentHashMap.containsKey(month)) {
//                                System.out.println("In IF Condition...........");
//                                presnetStatusCount = 0;
//                                totalNoOfDays = 0;
//                                if (studentAttendaceStatus.contains("P")) {
//                                    presnetStatusCount++;
//                                }
//                                studentAttendancListData = new StudentAttendanceListData(month, presnetStatusCount, totalNoOfDays + 1);
//                                attendaceStudentHashMap.put(month, studentAttendancListData);
//                            } else {
//                                System.out.println("In ELSE Condition...........");
//                                StudentAttendanceListData value = attendaceStudentHashMap.get(month);
//                                System.out.println(k + " month " + value.getMonth());
//                                System.out.println(k + " PresentDay " + value.getPresentDays());
//                                System.out.println(k + " TotolDays " + value.getTotalDays());
//                                totalNoOfDays = value.getTotalDays() + 1;
//                                if (studentAttendaceStatus.contains("P")) {
//                                    presnetStatusCount = value.getPresentDays() + 1;
//                                }
//                                System.out.println("After Update Total Count " + presnetStatusCount);
//                                System.out.println("After Update Present Days " + totalNoOfDays);
//
//                                studentAttendancListData = new StudentAttendanceListData(month, presnetStatusCount, totalNoOfDays);
//                                attendaceStudentHashMap.put(month, studentAttendancListData);
//                                System.out.println("Total Count " + presnetStatusCount);
//                                System.out.println("Present Days " + totalNoOfDays);
//                            }
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//             /*
//              below logic used for to add data in Month Spinner
//             */
//            keys = attendaceStudentHashMap.keySet();
//            for (Integer key : keys) {
//                System.out.println("Key of hash map onPostExecute  " + key);
//                monthList.add(monthNameHashMap.get(key));
//            }
//            monthAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, monthList);
//            monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
//            monthSelector.setAdapter(monthAdapter);
//
//            //method to call Logic for plotting graph.
//            DrawGraph();
//
//        }
//    }

    public void DrawGraph() {
        CombinedData data = new CombinedData(getXAxisValues());

        System.out.println(" INSIDE 11111 POST EXECUTE");


        data.setData(barData());
        data.setData(lineData());

        System.out.println(" barData().getXValCount()   " + barData().getXValCount());
        System.out.println(" lineData().getYValCount()  " + lineData().getYValCount());
        System.out.println(" INSIDE 222222 POST EXECUTE");

        combinedChart.setData(data);
        combinedChart.setDrawGridBackground(false);
        combinedChart.getXAxis().setDrawGridLines(false);

        combinedChart.setNoDataTextDescription("Data unavailable");
        combinedChart.setDescription(null);
        combinedChart.animateXY(1000, 1000);
        combinedChart.setScaleEnabled(false);
        combinedChart.setHighlightPerDragEnabled(false);
        combinedChart.setHighlightPerTapEnabled(false);

        System.out.println(" INSIDE 3 3 3 3 3 POST EXECUTE");

        combinedChart.getAxisRight().setDrawLabels(false);
        combinedChart.getAxisRight().setEnabled(false);
        combinedChart.getAxisLeft().setEnabled(false);
        combinedChart.getAxisLeft().setEnabled(false);

        System.out.println(" INSIDE 44444 POST EXECUTE");

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        System.out.println(" INSIDE 555555 POST EXECUTE");

    }


    // this method is used to create data for line graph
    public LineData lineData() {
        ArrayList<Entry> line = new ArrayList<>();
        int lineDataPos = 0;

//        keys = attendaceStudentHashMap.keySet();
        keys = new TreeSet<Integer>(attendaceStudentHashMap.keySet());

        System.out.println(" KEY values" + keys);

        for (Integer key : keys) {

            if (key == 0) {
                lineDataPos = key + 1;
                System.out.println("barDataPos  == 0" + lineDataPos);
            } else {

                lineDataPos = key - 1;
                System.out.println("barDataPos  /= 0" + lineDataPos);
            }

            presentday = attendaceStudentHashMap.get(key).getPresentDays();
            float newFloat = (float) presentday;
            System.out.println("LineData PresentDays float value " + newFloat);
            line.add(new Entry(newFloat, lineDataPos));
            System.out.println("lineDataPos  before" + lineDataPos);
            lineDataPos++;
            System.out.println("lineDataPos after " + lineDataPos);
        }

        LineDataSet lineDataSet = new LineDataSet(line, "Days Present");

        lineDataSet.setColor(Color.parseColor("#FF4081"));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleRadius(9.0f);
        lineDataSet.setCubicIntensity(0.1f);
        lineDataSet.setValueFormatter(new FloatToIntFormatter());
        lineDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setValueTextSize(16f);


        LineData lineData = new LineData(getXAxisValues(), lineDataSet);

        return lineData;

    }

    // this method is used to create data for Bar graph
    public BarData barData() {

        ArrayList<BarEntry> group1 = new ArrayList<>();
        int barDataPos = 0;

//        keys = attendaceStudentHashMap.keySet();

        keys = new TreeSet<Integer>(attendaceStudentHashMap.keySet());
        System.out.println(" KEY values" + keys);

        for (Integer key : keys) {

            if (key == 0) {
                barDataPos = key + 1;
                System.out.println("barDataPos  == 0" + barDataPos);
            } else {

                barDataPos = key - 1;
                System.out.println("barDataPos  /= 0" + barDataPos);
            }

            totaldays = attendaceStudentHashMap.get(key).getTotalDays();
            presentday = attendaceStudentHashMap.get(key).getPresentDays();
            float newFloat = (float) totaldays;
            System.out.println("BarData of  totaldays float value  " + newFloat);
            System.out.println("BarData of  totaldays float value  " + newFloat);
            group1.add(new BarEntry(newFloat, barDataPos));
            System.out.println("lineDataPos after " + barDataPos);
            barDataPos++;
            System.out.println("lineDataPos after " + barDataPos);

        }

        BarDataSet barDataSet = new BarDataSet(group1, "Total Days");
        barDataSet.setColor(Color.parseColor("#104E78"));
        barDataSet.setValueFormatter(new FloatToIntFormatter());
        barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(getXAxisValues(), barDataSet);
        return barData;

    }

    // creating list of x-axis values
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> labels = new ArrayList<>();

//        keys = attendaceStudentHashMap.keySet();
//        for (Integer key : keys) {
//            System.out.println("Key of hash map  " + key);
//            labels.add(monthNameHashMap.get(key));
//        }

        labels.add("Jan");
        labels.add("Feb");
        labels.add("Mar");
        labels.add("Apr");
        labels.add("May");
        labels.add("Jun");
        labels.add("Jul");
        labels.add("Aug");
        labels.add("Sep");
        labels.add("Oct");
        labels.add("Nov");
        labels.add("Dec");

        return labels;
    }

    public float getPercentageOfAttendance(int i) {
        /*
        method to calculate percentage of Attendance
         */
        int totalpresentdays = attendaceStudentHashMap.get(i).getPresentDays();
        int totaldays = attendaceStudentHashMap.get(i).getTotalDays();
        float percentage = (float) ((totalpresentdays) * 100 / totaldays);
        return percentage;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class FloatToIntFormatter implements ValueFormatter {

        /*class to return int instead of float
        bcoz we have to show int value on graph for day count*/
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            // write your logic here
            return "" + ((int) value);

        }
    }

    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            attendance_Handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("check Variable AttendanceStudentIndividual " + check);
                    if (!check) {

                        attendance_Timer.cancel();
                        attendanceProgressDialog.dismiss();
                        try {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AttendanceStudentIndividual.this);
                            dialogBuilder.setMessage(R.string.check_network);
                            dialogBuilder.setCancelable(false);
                            dialogBuilder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println("Dialog Retry Button call");

                                    attendanceProgressDialog.show();
//                                        stick.setVisibility(View.INVISIBLE);
                                    reScheduleTimer();

                                }
                            });
                            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println("Dismiss called.......");
                                    attendance_Timer.purge();
                                    attendance_Timer.cancel();
                                    onBackPressed();
                                }
                            });
                            dialogBuilder.create().show();

                        } catch (Exception e) {
                            System.out.println("Exception Handle for Alert Dialog");
                        }

                    } else {
                        attendance_Timer.cancel();
                        System.out.println("Timer Cancelled AttendanceStudentIndividual");
                    }
                }
            });
        }
    }
}
