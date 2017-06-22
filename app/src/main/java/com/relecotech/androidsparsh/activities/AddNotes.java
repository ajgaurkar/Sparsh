package com.relecotech.androidsparsh.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.AddNotesTeacherAdpaterAdpater;
import com.relecotech.androidsparsh.azurecontroller.Parent_zone;
import com.relecotech.androidsparsh.controllers.AddNotesTeacherListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class AddNotes extends AppCompatActivity {
    private static TextView timePickertextview;
    Spinner tagSpinner, teacherSpinner;
    List<String> spinnertaglist;
    ArrayAdapter<String> tagArrayAdapter;
    AddNotesTeacherAdpaterAdpater teacherArrayAdapter;
    private Button notesSubmitButton;
    private EditText descrpitionEditText, subjectEditText;
    private MobileServiceTable<Parent_zone> mParentZone;
    private Parent_zone notes_parentZoneItem;
    private Date meetingdDate;
    Calendar calendar;
    SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private MobileServiceJsonTable mobileServiceJsonTable;
    private String teacher_ID;
    private String teacher_firstName;
    private String teacher_lastName;
    private String teacher_fullname;
    private ProgressDialog addNotesProgressDialog;
    private Parent_zone notes_Response_parent_zone;
    private Boolean restCaliingFlag = true;
    private ArrayList<AddNotesTeacherListData> spinnerTeacherList;
    private String selectedTeacher_ID;
    private AddNotesTeacherListData spinnerTeacherListData;
    private List<String> checkingDuplicateTeacherID;
    private JsonObject jsonObjectParentParameters;
    private long postingDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        calendar = Calendar.getInstance();
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();


        postingDate = calendar.getTimeInMillis();

        mobileServiceJsonTable = MainActivity.mClient.getTable("parent_zone");

        addNotesProgressDialog = new ProgressDialog(AddNotes.this);
        addNotesProgressDialog.setMessage(AddNotes.this.getString(R.string.loading));
        addNotesProgressDialog.setCancelable(false);

        timePickertextview = (TextView) findViewById(R.id.timepicker);
        notesSubmitButton = (Button) findViewById(R.id.notesSubmitButton);
        descrpitionEditText = (EditText) findViewById(R.id.descriptioneditText);


        tagSpinner = (Spinner) findViewById(R.id.tagspinner);
        teacherSpinner = (Spinner) findViewById(R.id.tospinner);
        timePickertextview.setVisibility(View.INVISIBLE);

        spinnertaglist = new ArrayList<>();
        spinnerTeacherList = new ArrayList<>();
        checkingDuplicateTeacherID = new ArrayList<>();

        spinnertaglist.add("Meeting");
        spinnertaglist.add("Feedback");
        spinnertaglist.add("General Query");
        spinnertaglist.add("[ Category ]");

        tagArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnertaglist);
        tagSpinner.setAdapter(tagArrayAdapter);
        tagArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        tagSpinner.setSelection(tagArrayAdapter.getCount() - 1);

//
//        spinnerTeacherList.clear();
//        spinnerTeacherList.add(0, new AddNotesTeacherListData("[ Select Teacher ]", "0"));
//        teacherArrayAdapter = new AddNotesTeacherAdpaterAdpater(getApplicationContext(), spinnerTeacherList);
//        teacherSpinner.setAdapter(teacherArrayAdapter);
//        teacherSpinner.setSelection(teacherSpinner.getCount() - 1);

//        mParentZone = MainActivity.mClient.getTable(Parent_zone.class);
        notes_parentZoneItem = new Parent_zone();

        jsonObjectParentParameters = new JsonObject();

        jsonObjectParentParameters.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
        jsonObjectParentParameters.addProperty("TAG", "Add_notes");
        jsonObjectParentParameters.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectParentParameters.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectParentParameters.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));


        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tagselector = (String) tagSpinner.getItemAtPosition(position);
                switch (tagselector) {
                    case "Select Tag":
                        break;
                    case "Meeting":
                        showTimePickerTextView();
                        break;
                    case "Feedback":
                        hideTimePickerTextView();
                    case "General Query":
                        hideTimePickerTextView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        timePickertextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        notesSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tagSpinner.getSelectedItem().toString().equals("[ Category ]")) {
                    System.out.println(teacherSpinner.getSelectedItem().toString());

                    AddNotesTeacherListData adNotesTeacherList = (AddNotesTeacherListData) teacherSpinner.getSelectedItem();
                    String selectTeacher = adNotesTeacherList.getMessage();
                    System.out.println( " select Teacher  " + selectTeacher);
                    if (!selectTeacher.equals("[ Select Teacher ]")) {

                        if (descrpitionEditText.length() > 0) {

                            String s = timePickertextview.getText().toString();

                            //  finish();
                            if (s.equals("Select Time")) {

                                Snackbar.make(v, "Select Time", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            } else {

                                //Snackbar.make(v, "@@@@@@@@@@@@@@@", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }


                            addNotes();

                            Log.d("Submit Button Pressed", "addnotes()call");

                        } else {
//                            Toast.makeText(getApplicationContext(), "Add some descrpition ", Toast.LENGTH_LONG).show();
                            Snackbar.make(v, "Add some description", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(v, "Please Select Teacher", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(v, "Please Select Category", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });


        //getting teacher data for tagTeaqher Spinner

        if (restCaliingFlag.equals(true)) {
            Log.d("restCaliingFlag", "if..restCaliingFlag");
            // new GettingTeacherAsyntask().execute();
            teacherSpinnerData();
        } else {
            Log.d("restCaliingFlag", "else..restCaliingFlag");
        }

    }

    private void hideTimePickerTextView() {

        // get the center for the clipping circle
        int cx = timePickertextview.getWidth() / 2;
        int cy = timePickertextview.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(timePickertextview, cx, cy, initialRadius, 0);


            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    timePickertextview.setVisibility(View.INVISIBLE);
                }
            });


            // start the animation
            anim.start();
        } else {
            timePickertextview.setVisibility(View.INVISIBLE);
        }
    }

    private void showTimePickerTextView() {
        //method to show transition to reveal

        // get the center for the clipping circle
        int cx = timePickertextview.getWidth() / 2;
        int cy = timePickertextview.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(timePickertextview, cx, cy, 0, finalRadius);
        }

        // make the view visible and start the animation
        timePickertextview.setVisibility(View.VISIBLE);
        if (anim != null) {
            anim.start();
        }
    }

    public void addNotes() {

        addNotesProgressDialog.show();
        jsonObjectParentParameters = new JsonObject();

        if (tagSpinner.getSelectedItem().toString().equals("Meeting")) {

            String getMeetingShedule = timePickertextview.getText().toString();
            System.out.println("getMeetingShedule " + getMeetingShedule);
            java.text.DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa");
            try {
                meetingdDate = dateFormat.parse(getMeetingShedule);
                System.out.println(" meeting Date " + meetingdDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            jsonObjectParentParameters.addProperty("meetingShedule", meetingdDate.getTime());
        }

        jsonObjectParentParameters.addProperty("category", tagSpinner.getSelectedItem().toString());
        jsonObjectParentParameters.addProperty("Description_cause", descrpitionEditText.getText().toString());
        jsonObjectParentParameters.addProperty("postdate", postingDate);
        jsonObjectParentParameters.addProperty("Student_id", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectParentParameters.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectParentParameters.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));
        jsonObjectParentParameters.addProperty("Status", "Pending");
        jsonObjectParentParameters.addProperty("Reply", "");

        AddLeaveApiCalling();
    }

    private void AddLeaveApiCalling() {
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("insertParentZoneApi", jsonObjectParentParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Add Notes API exception    " + exception);
                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        addNotesProgressDialog.cancel();
                        new AlertDialog.Builder(AddNotes.this)
                                .setTitle(R.string.check_network)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addNotes();
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
                System.out.println(" Add Notes API   response    " + response);

                if (response.toString().equals("true")) {

                    addNotesProgressDialog.dismiss();
                    AlertDialog.Builder notesAlertDialog = new AlertDialog.Builder(AddNotes.this);
                    notesAlertDialog.setMessage("Notes Successfully Submitted");
                    notesAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    notesAlertDialog.setNegativeButton("Add More Notes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddNotes addNotes = new AddNotes();
                            addNotes.finish();
                            // start same activity..
                            Intent addAddnotesintent = new Intent(getApplicationContext(), AddNotes.class);
                            startActivity(addAddnotesintent);
                            restCaliingFlag = false;
                            finish();
                        }
                    });
                    notesAlertDialog.create().show();

                } else {
                    System.out.println("leave_parentZoneItem is null");

                    Runnable progressRunnable = new Runnable() {

                        @Override
                        public void run() {
                            addNotesProgressDialog.cancel();
                            new AlertDialog.Builder(AddNotes.this)
                                    .setTitle(R.string.check_network)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addNotes();
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

    private void teacherSpinnerData() {

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchParentZoneApi", jsonObjectParentParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                spinnerTeacherList.clear();
                spinnerTeacherList.add(0, new AddNotesTeacherListData("[ Select Teacher ]", "0"));
                teacherArrayAdapter = new AddNotesTeacherAdpaterAdpater(getApplicationContext(), spinnerTeacherList);
                teacherSpinner.setAdapter(teacherArrayAdapter);
                teacherSpinner.setSelection(teacherSpinner.getCount() - 1);
                teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinnerTeacherListData = spinnerTeacherList.get(position);
                        selectedTeacher_ID = spinnerTeacherListData.getId();
                        Log.d("list id", "list id" + spinnerTeacherListData.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" PARENT ZONE  ADD API   response    " + response);

                JsonArray getJsonListResponse = response.getAsJsonArray();
                try {
                    if (getJsonListResponse.size() == 0) {
                        Log.d("json not recived", "not recived");
                    }
                    for (int g = 0; g < getJsonListResponse.size(); g++) {
                        JsonObject jsonObjectforIterarion = getJsonListResponse.get(g).getAsJsonObject();
                        teacher_ID = jsonObjectforIterarion.get("id").toString().replace("\"", "");
                        teacher_firstName = jsonObjectforIterarion.get("firstName").toString().replace("\"", "");
                        teacher_lastName = jsonObjectforIterarion.get("lastName").toString().replace("\"", "");
                        teacher_fullname = teacher_firstName + " " + teacher_lastName;

                        if (!checkingDuplicateTeacherID.contains(teacher_ID)) {

                            spinnerTeacherListData = new AddNotesTeacherListData(teacher_fullname, teacher_ID);
                            spinnerTeacherList.add(spinnerTeacherListData);
                            checkingDuplicateTeacherID.add(teacher_ID);

                        }

                    }
                    Log.d("size of soinner list", "" + spinnerTeacherList.size());


                    spinnerTeacherList.add(0, new AddNotesTeacherListData("[ Select Teacher ]", "0"));
                    teacherArrayAdapter = new AddNotesTeacherAdpaterAdpater(getApplicationContext(), spinnerTeacherList);
                    teacherSpinner.setAdapter(teacherArrayAdapter);
                    teacherSpinner.setSelection(teacherSpinner.getCount() - 1);
                    teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerTeacherListData = spinnerTeacherList.get(position);
                            selectedTeacher_ID = spinnerTeacherListData.getId();
                            Log.d("list id", "list id" + spinnerTeacherListData.getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (Exception e) {

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


    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String error) {
        Throwable ex = exception;
        createAndShowDialog(ex.getMessage(), error);
    }

    private void createAndShowDialog(final String message, final String title) {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        //builder.setMessage("Sorry Somthing went Wrong");
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.create().show();
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private static String date;
        String AM_PM;
        private int yy, mm, dd;
        private int mHour, mMinute;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            yy = calendar.get(java.util.Calendar.YEAR);
            mm = calendar.get(java.util.Calendar.MONTH);
            dd = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR);
            mMinute = calendar.get(Calendar.MINUTE);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd) {
                @Override
                public void onClick(DialogInterface dialog, int doneBtn) {
                    if (doneBtn == BUTTON_POSITIVE) {
                        int year = getDatePicker().getYear();
                        int month = getDatePicker().getMonth() + 1;
                        int day = getDatePicker().getDayOfMonth();
                        SelectDateFragment.date = day + "-" + month + "-" + year;
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12 && hourOfDay >= 0) {
                                    AM_PM = "AM";
                                } else {
                                    hourOfDay -= 12;
                                    if (hourOfDay == 0) {
                                        hourOfDay = 12;
                                    }
                                    AM_PM = "PM";
                                }
                                AddNotes.timePickertextview.setText("" + date + " " + hourOfDay + ":" + minute + " " + AM_PM);
                            }
                        },
                                mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                    super.onClick(dialog, doneBtn);
                }

                @Override
                public void onDateChanged(DatePicker view, int Dyear, int DmonthOfYear, int DdayOfMonth) {
                    if (Dyear < yy)
                        view.updateDate(yy, mm, dd);

                    if (DmonthOfYear < mm && Dyear == yy)
                        view.updateDate(yy, mm, dd);

                    if (DdayOfMonth < dd && Dyear == yy && DmonthOfYear == mm)
                        view.updateDate(yy, mm, dd);

                }
            };
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        }

    }
}