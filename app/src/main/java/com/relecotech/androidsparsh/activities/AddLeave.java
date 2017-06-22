package com.relecotech.androidsparsh.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.azurecontroller.Parent_zone;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by amey on 10/16/2015.
 */
public class AddLeave extends AppCompatActivity {

    static int currentDay, currentMonths, currentYear;
    static int flag = 0;
    static int fromDay = 0, toDay = 0;
    private static TextView leaveFromTextView, leaveToTextView;
    private static int datecompare, diffdate;
    private static Date leavefromdate, leavetodate;
    private EditText dayCountEditText;
    EditText causeEditText;
    Button leavesSubmitBtn;
    String currentdate;
    String leaveCause;
    int leavesDayCount;
    private java.util.Calendar calendar;
    private Parent_zone leave_parentZoneItem;
    private MobileServiceTable<Parent_zone> mParentZone;
    private SessionManager getStudentId;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    private static String getTextTodateTextView;
    private static String getTextFromdateTextView;
    private static Date dateObjectFor_TOdate;
    private static Date dateObjectFor_Fromdate;
    private String Category_TAG = "Leave";
    private Parent_zone leave_Response_parent_zone;
    private AlertDialog.Builder alertDialog;
    private ProgressDialog progressDialog;
    private int edittextcount;
    private int dayCountOfLeave;
    private JsonObject jsonObjectAddLeaveParameters;
    private long postingDate;
    private String getFromDate;
    private String getToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addleave);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leaveFromTextView = (TextView) findViewById(R.id.leavesFromTextView);
        leaveToTextView = (TextView) findViewById(R.id.leavesToTextView);
        dayCountEditText = (EditText) findViewById(R.id.leavesCount);
        dayCountEditText.setText("1");
        causeEditText = (EditText) findViewById(R.id.leavesCauseEditText);
        leavesSubmitBtn = (Button) findViewById(R.id.leavesSubmitButton);
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        alertDialog = new AlertDialog.Builder(getApplicationContext());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(AddLeave.this.getString(R.string.loading));
        progressDialog.setCancelable(false);

        jsonObjectAddLeaveParameters = new JsonObject();


        calendar = java.util.Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonths = calendar.get(Calendar.MONTH) + 1;
        currentYear = calendar.get(Calendar.YEAR);

        postingDate = calendar.getTimeInMillis();

        currentdate = currentDay + "/" + currentMonths + "/" + currentYear;
        leaveFromTextView.setText("" + currentdate);
        leaveToTextView.setText("" + currentdate);
        leavefromdate = new Date(currentYear, currentMonths, currentDay);
        leavetodate = new Date(currentYear, currentMonths, currentDay);

        leaveFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
                flag = 1;

            }
        });
        leaveToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");


            }
        });

        leavesSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFromDate = leaveFromTextView.getText().toString();
                getToDate = leaveToTextView.getText().toString();
                long getExactDaysOfCount = DaysCount(getFromDate, getToDate);
                System.out.println("getEaxctDaysOfCount " + getExactDaysOfCount);
                dayCountOfLeave = Integer.parseInt(dayCountEditText.getText().toString());

                if (getExactDaysOfCount > 0) {
                    if (dayCountOfLeave <= getExactDaysOfCount || dayCountEditText.getText().toString().equals("1")) {
                        if (causeEditText.getText().toString().length() > 0) {

                            System.out.println("Yes clear criteria of add leave.....");
                            addLeave();


                        } else {
                            Snackbar.make(v, "Add cause for the leave", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(v, "Please Enter Correct Day Count", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(v, "Leave To Date should be greater than Leave From date", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addLeave() {

        progressDialog.show();

        getTextFromdateTextView = leaveFromTextView.getText().toString();
        getTextTodateTextView = leaveToTextView.getText().toString();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            dateObjectFor_Fromdate = format.parse(getTextFromdateTextView);
            dateObjectFor_TOdate = format.parse(getTextTodateTextView);
            Log.d("dateto", "dateto  " + dateObjectFor_TOdate);
            Log.d("datefrom", "datefrom  " + dateObjectFor_Fromdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        jsonObjectAddLeaveParameters.addProperty("No_of_days", dayCountOfLeave);
        leaveCause = causeEditText.getText().toString();
        jsonObjectAddLeaveParameters.addProperty("Description_cause", leaveCause);
        jsonObjectAddLeaveParameters.addProperty("category", Category_TAG);
        jsonObjectAddLeaveParameters.addProperty("Startdate", dateObjectFor_Fromdate.getTime());
        jsonObjectAddLeaveParameters.addProperty("Enddate", dateObjectFor_TOdate.getTime());
        jsonObjectAddLeaveParameters.addProperty("Student_id", userDetails.get(SessionManager.KEY_STUDENT_ID));

        System.out.println("schoolClassId" + userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectAddLeaveParameters.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));

        System.out.println("Teacher_id" + userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));

        jsonObjectAddLeaveParameters.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));
        jsonObjectAddLeaveParameters.addProperty("postdate", postingDate);
        jsonObjectAddLeaveParameters.addProperty("Status", "Pending");
        jsonObjectAddLeaveParameters.addProperty("Reply", "");
        jsonObjectAddLeaveParameters.addProperty("meetingShedule", "");


        Log.d("Posting date ", "Leave" + new Date(calendar.getTimeInMillis()));

        //   Insert leave calling
        ExecuteAddingLeave();

    }


    private void ExecuteAddingLeave() {


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("insertParentZoneApi", jsonObjectAddLeaveParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Add Leave API exception    " + exception);
                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        progressDialog.cancel();
                        new AlertDialog.Builder(AddLeave.this)
                                .setTitle(R.string.check_network)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addLeave();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).show();
                    }
                };

                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 10000);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("  Add Leave API   response    " + response);

                if (response.toString().equals("true")) {

                    progressDialog.dismiss();
                    final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(AddLeave.this);
                    alertDialog.setMessage("Leave Submitted ");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    alertDialog.create().show();

                } else {
                    Log.d("leave_parentZoneItem", "is null");
                    Runnable progressRunnable = new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.cancel();
                            new AlertDialog.Builder(AddLeave.this)
                                    .setTitle(R.string.check_network)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addLeave();
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).show();
                        }
                    };

                    Handler pdCanceller = new Handler();
                    pdCanceller.postDelayed(progressRunnable, 10000);

                }
            }
        });
    }

    public static void dayscount() {

        getTextTodateTextView = leaveToTextView.getText().toString();
        getTextFromdateTextView = leaveFromTextView.getText().toString();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            dateObjectFor_TOdate = format.parse(getTextTodateTextView);
            dateObjectFor_Fromdate = format.parse(getTextFromdateTextView);

            System.out.println("dateObjectFor_TOdate-dayscount--------" + dateObjectFor_TOdate);
            System.out.println("dateObjectFor_Fromdate---dayscount-----" + dateObjectFor_Fromdate);


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public long DaysCount(String fromdate, String todate) {


        Date toDate = null;
        Date fromDate = null;
        long getDaysCount = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            toDate = dateFormat.parse(todate);
            fromDate = dateFormat.parse(fromdate);
            System.out.println("todate------" + todate);
            System.out.println("fromdate------" + fromdate);
            /*
              get Date  difference milliseconds
              */
            //  long diffBetweenFromDateAndToDate = fromDate.getTime() - toDate.getTime();
            long diffBetweenFromDateAndToDate = toDate.getTime() - fromDate.getTime();
            getDaysCount = diffBetweenFromDateAndToDate / (24 * 60 * 60 * 1000);
            System.out.println("getDaysCout " + getDaysCount);

        } catch (Exception e) {
            System.out.println("Days cout Exception----" + e.getMessage());
        }

        return ++getDaysCount;
    }


    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private static String date;
        private int yy, mm, dd;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            yy = calendar.get(java.util.Calendar.YEAR);
            mm = calendar.get(java.util.Calendar.MONTH);
            dd = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, yy, mm, dd) {
                @Override
                public void onClick(DialogInterface dialog, int doneBtn) {
                    if (doneBtn == BUTTON_POSITIVE) {

                        int year = getDatePicker().getYear();
                        int month = getDatePicker().getMonth() + 1;
                        int day = getDatePicker().getDayOfMonth();

                        SelectDateFragment.date = day + "/" + month + "/" + year;

                        if (AddLeave.flag == 1) {
                            AddLeave.leaveFromTextView.setText(SelectDateFragment.date);
                            AddLeave.flag = 0;
                            leavefromdate = new Date(year, month, day);
                            AddLeave.fromDay = day;

                        } else {
                            AddLeave.leaveToTextView.setText(SelectDateFragment.date);
                            AddLeave.toDay = day;
                            leavetodate = new Date(year, month, day);
                            AddLeave.dayscount();
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