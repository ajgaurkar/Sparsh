package com.relecotech.androidsparsh.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.controllers.StudentAttendanceListData;

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
import java.util.TreeSet;


/**
 * Created by amey on 10/30/2015.
 */
public class Attendance_Student_Fragment extends android.support.v4.app.Fragment {


    private ProgressDialog attendanceProgressDialog;
    private ConnectionDetector connectionDetector;
    private boolean checkConnection;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private Handler studentAttendance_Handler;
    private TextView monthPercent;
    private TextView yearlyAttendnace;
    private CombinedChart combinedChart;
    private JsonObject jsonObjectToFetchAttendance;
    private String attendaceDate;
    private String studentAttendaceStatus;
    private String studentAttendaceDate;
    private int month;
    private HashMap<Integer, StudentAttendanceListData> attendaceStudentHashMap;
    private HashMap<Integer, String> monthNameHashMap;
    private int totalNoOfDays;
    private int presnetStatusCount;
    private StudentAttendanceListData studentAttendancListData;
    private List<String> monthList;
    private ArrayAdapter<String> monthAdapter;
    private Spinner monthSelector;
    private ArrayList<StudentAttendanceListData> studentAttendanceArrayList;
    private float percentageOfAttendance;
    private int percentageInInterger;
    private int totaldays;
    private int presentday;
    private Set<Integer> keys;
    private DashboardStudentFragment mFragment;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getUserDetails();
        studentAttendance_Handler = new Handler();

        attendanceProgressDialog = new ProgressDialog(getActivity());
        attendanceProgressDialog.setMessage(getActivity().getString(R.string.loading));
        attendanceProgressDialog.setCancelable(false);

        attendaceStudentHashMap = new HashMap<>();
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


        if (checkConnection) {
            attendanceProgressDialog.show();
            onExecutionStart();
        } else {
            // Toast.makeText(getActivity(), "No internet connection..!.", Toast.LENGTH_LONG).show();
        }


    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance_individual_review_student, container, false);

        monthPercent = (TextView) rootView.findViewById(R.id.monthCountIndividualStudentReview);
        yearlyAttendnace = (TextView) rootView.findViewById(R.id.yearlyattendancetextView);
        combinedChart = (CombinedChart) rootView.findViewById(R.id.individualStudentAttendanceCombinedChart);
        monthSelector = (Spinner) rootView.findViewById(R.id.individualStudentReviewMonthselectorSpiner);

        Calendar calendar = Calendar.getInstance();
        int yearAttendance = calendar.get(Calendar.YEAR);
        yearlyAttendnace.setText("Yearly Attendance - " + yearAttendance);

        monthList = new ArrayList<>();
        studentAttendanceArrayList = new ArrayList<>();

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


        return rootView;
    }


    private void onExecutionStart() {
        FetchStudentAttendance();
//        studentAttendanceTimer.schedule(attendanceTimeOutTimerClass, TIMEOUT_TIME, 1000);

    }

    private void FetchStudentAttendance() {

        jsonObjectToFetchAttendance = new JsonObject();
        jsonObjectToFetchAttendance.addProperty("StudentID", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectToFetchAttendance.addProperty("UserRole", "Student");

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("attendanceReviewApi", jsonObjectToFetchAttendance);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Attendance_Student_Fragment exception    " + exception);
                attendanceProgressDialog.dismiss();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Attendance_Student_Fragment    response    " + response);

                //converting jsonElement to jsonArray
                JsonArray attendanceArray = response.getAsJsonArray();
                // System.out.println(" AttendanceArray  " + attendanceArray);
                AttendanceParsing(attendanceArray);

            }
        });

    }

    private void AttendanceParsing(JsonArray attendanceArray) {

        try {
            if (attendanceArray.size() == 0) {
                Log.d("json not received", "not received");
                attendanceProgressDialog.dismiss();
                //attendanceTimeOutTimerClass.check = false;
            } else {
                //attendanceTimeOutTimerClass.check = true;
                for (int k = 0; k < attendanceArray.size(); k++) {
                    JsonObject jsonObjectforIteration = attendanceArray.get(k).getAsJsonObject();
                    studentAttendaceStatus = jsonObjectforIteration.get("status").toString().replace("\"", "");
                    attendaceDate = jsonObjectforIteration.get("attendance_date").toString().replace("\"", "");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                    Date attendancedate = null;
                    try {
                        attendancedate = simpleDateFormat.parse(attendaceDate);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());

                        studentAttendaceDate = targetDateFormat.format(attendancedate);
                        System.out.println(k + "  studentAttendaceDate &  " + studentAttendaceDate + " studentAttendaceStatus  " + studentAttendaceStatus);
                        month = (attendancedate.getMonth() + 1);
                        System.out.println(" month " + month);

                        if (!attendaceStudentHashMap.containsKey(month)) {
                            System.out.println("In IF Condition...........");
                            presnetStatusCount = 0;
                            totalNoOfDays = 0;
                            if (studentAttendaceStatus.contains("P")) {
                                presnetStatusCount++;
                            }
                            studentAttendancListData = new StudentAttendanceListData(month, presnetStatusCount, totalNoOfDays + 1);
                            attendaceStudentHashMap.put(month, studentAttendancListData);

                        } else {
                            System.out.println("In ELSE Condition...........");
                            StudentAttendanceListData value = attendaceStudentHashMap.get(month);
                            totalNoOfDays = value.getTotalDays() + 1;
                            if (studentAttendaceStatus.contains("P")) {
                                presnetStatusCount = value.getPresentDays() + 1;
                            }
                            studentAttendancListData = new StudentAttendanceListData(month, presnetStatusCount, totalNoOfDays);
                            attendaceStudentHashMap.put(month, studentAttendancListData);

                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
//                HashSet<Integer> keys = new HashSet<Integer>(attendaceStudentHashMap.keySet());

//.


                keys = new TreeSet<Integer>(attendaceStudentHashMap.keySet());
                for (Integer key : keys) {
                    System.out.println("Key of hash map onPostExecute  " + key);
                    monthList.add(0, monthNameHashMap.get(key));
                }
                monthAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, monthList);
                monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                monthSelector.setAdapter(monthAdapter);
//                System.out.println("monthAdapter.getCount()" + monthAdapter.getCount());
//                monthSelector.setSelection(monthAdapter.getCount()-1);

                         /* this method used to call Logic of Graph.*/
                DrawGraph();
                attendanceProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void DrawGraph() {

        CombinedData data = new CombinedData(getXAxisValues());

        data.setData(barData());
        data.setData(lineData());

        combinedChart.setData(data);
        combinedChart.setDrawGridBackground(false);
        combinedChart.getXAxis().setDrawGridLines(false);

        combinedChart.setNoDataTextDescription("Data unavailable");
        combinedChart.setDescription(null);
        combinedChart.animateXY(1000, 1000);
        combinedChart.setScaleEnabled(false);
        combinedChart.setHighlightPerDragEnabled(false);
        combinedChart.setHighlightPerTapEnabled(false);

        combinedChart.getAxisRight().setDrawLabels(false);
        combinedChart.getAxisRight().setEnabled(false);
        combinedChart.getAxisLeft().setEnabled(false);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    // creating list of x-axis values
    private ArrayList<String> getXAxisValues() {

        ArrayList<String> labels = new ArrayList<>();

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

    // this method is used to create data for Bar graph
    public BarData barData() {

        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        int barDataPos = 1;

        //keys = attendaceStudentHashMap.keySet();
        keys = new TreeSet<Integer>(attendaceStudentHashMap.keySet());
        System.out.print(" keys " + keys);
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
            System.out.println("BarData of  totaldays " + totaldays);
            System.out.println("BarData of  presentday " + presentday);
            float newFloat = (float) totaldays;
            barEntryArrayList.add(new BarEntry(newFloat, barDataPos));
            barDataPos++;

        }

        BarDataSet barDataSet = null;


        barDataSet = new BarDataSet(barEntryArrayList, "Total Days");
        barDataSet.setColor(Color.parseColor("#104E78"));
        barDataSet.setValueFormatter(new FloatToIntFormatter());
        barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet.setValueTextSize(16f);

        barDataSet.setValueTextColor(Color.BLACK);


        BarData barData = new BarData(getXAxisValues(), barDataSet);
        return barData;

    }


    // this method is used to create data for line graph
    public LineData lineData() {
        ArrayList<Entry> line = new ArrayList<>();
        int lineDataPos = 1;

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

//        keys = new TreeSet<Integer>(attendaceStudentHashMap.keySet());
//        for (Integer key : keys) {
//
//            totaldays = attendaceStudentHashMap.get(key).getTotalDays();
//            presentday = attendaceStudentHashMap.get(key).getPresentDays();
//            float newFloat = (float) presentday;
//            System.out.println("LineData PresentDays float value " + newFloat);
//            line.add(new Entry(newFloat, lineDataPos));
//            lineDataPos++;
//        }
        LineDataSet lineDataSet = new LineDataSet(line, "Days Present");
        lineDataSet.setColor(Color.parseColor("#FF4081"));
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCircleRadius(9.0f);
//        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCubicIntensity(0.1f);
        lineDataSet.setValueFormatter(new FloatToIntFormatter());
        lineDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        lineDataSet.setValueTextSize(16f);

//        if (totaldays != presentday) {
//
//            lineDataSet.setValueTextColor(Color.WHITE);
//
//        } else {

        lineDataSet.setValueTextColor(Color.WHITE);
        //}

        LineData lineData = new LineData(getXAxisValues(), lineDataSet);
        return lineData;

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

    public float getPercentageOfAttendance(int i) {

        //   This method to calculate percentage of Attendance
        int totalpresentdays = attendaceStudentHashMap.get(i).getPresentDays();
        int totaldays = attendaceStudentHashMap.get(i).getTotalDays();
        float percentage = (float) ((totalpresentdays) * 100 / totaldays);
        return percentage;
    }


    @Override
    public void onResume() {

        super.onResume();
        /* Below statment for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Attendance");
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back button
                    mFragment = new DashboardStudentFragment();
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    return true;

                }

                return false;
            }
        });
    }
}
