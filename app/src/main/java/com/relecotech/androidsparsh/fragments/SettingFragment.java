package com.relecotech.androidsparsh.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.activities.SparshAbout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by amey on 10/16/2015.
 */
public class SettingFragment extends android.support.v4.app.Fragment {

    String userRole;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    private Fragment mFragment;
    private FragmentManager fragmentManager;
    HashMap<String, String> userDetails;
    private SessionManager sessionManager;
    private TextView timerTv;
    SimpleDateFormat displayFormat, parseFormat;

    private TextView setting_AboutTextView;
    private TextView setting_FeedbackTextview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getUserDetails();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.setting, container, false);

        timerTv = (TextView) rootView.findViewById(R.id.textViewAssignmentTime);
        setting_FeedbackTextview = (TextView) rootView.findViewById(R.id.textfeedback);

        setting_AboutTextView = (TextView) rootView.findViewById(R.id.aboutTextView);

        userRole = MainActivity.userRole;
//        if (userRole.equals("Teacher")){
//
//        }

        displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        parseFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        System.out.println(" (sessionManager.getIntSharedPrefItem(SessionManager.KEY_REMINDER) " + sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER));

        if (!sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER).isEmpty()) {
            String time = sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER);
            setTimeMethod(time);
        }


        timerTv.setOnClickListener(new View.OnClickListener() {
            public String AM_PM;
            public int mMinute;
            public int mHour;

            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (hourOfDay < 12 && hourOfDay >= 0) {
                                    AM_PM = "AM";
                                } else {
                                    hourOfDay -= 12;
                                    if (hourOfDay == 0) {
                                        hourOfDay = 12;
                                    }
                                    AM_PM = "PM";
                                }

                                Date date = null;
                                try {
                                    date = parseFormat.parse(hourOfDay + ":" + minute + " " + AM_PM);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));
                                String time = displayFormat.format(date);
//                                sessionManager.setSharedPrefItem(SessionManager.KEY_REMINDER, time);
//                                System.out.println(" Integer.parseInt(time) " + Integer.parseInt(time));
                                sessionManager.setSharedPrefItem(SessionManager.KEY_REMINDER, time);
                                timerTv.setText(parseFormat.format(date));


                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        setting_FeedbackTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@relecotech.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                i.putExtra(Intent.EXTRA_TEXT, "Student Name : " + userDetails.get(SessionManager.KEY_FIRST_NAME) + " " + userDetails.get(SessionManager.KEY_MIDDLE_NAME) + " " + userDetails.get(SessionManager.KEY_LAST_NAME) + " \n " + "Mobile :" + userDetails.get(SessionManager.KEY_PHONE) + "\n \n " + "Android Version " + android.os.Build.VERSION.RELEASE + " \n" + " SDK version  " + android.os.Build.VERSION.SDK_INT + " \n " + "Brand  " + android.os.Build.BRAND);
                startActivity(Intent.createChooser(i, "Send mail..."));
            }
        });

        final Switch notificationSwitch = (Switch) rootView.findViewById(R.id.notificationSwitch);


        if (sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH).equals("true")) {
            notificationSwitch.setChecked(true);
        }
        if (sessionManager.getSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH).equals("false")) {
            notificationSwitch.setChecked(false);
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    System.out.println(" Switch " + true);
//                    notificationSwitch.setChecked(false);
                    sessionManager.setSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH, "true");
                } else {
                    System.out.println(" Switch " + false);
//                    notificationSwitch.setChecked(true);
                    sessionManager.setSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH, "false");
                }

            }
        });


        setting_AboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent=new Intent(getActivity(),SparshAbout.class);
                startActivity(aboutIntent);
            }
        });

//         Inflate the layout for this fragment
        return rootView;
    }

    private void setTimeMethod(String time) {
//        String time = sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER);
        Date timeDateFrmSession;
        String timeStringFrmSession = "";
        try {
            timeDateFrmSession = displayFormat.parse(time);
            timeStringFrmSession = parseFormat.format(timeDateFrmSession);
            System.out.println("timeDateFrmSession)   " + timeDateFrmSession);
            System.out.println("timeStringFrmSession   " + timeStringFrmSession);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timerTv.setText(timeStringFrmSession);
    }

    @Override
    public void onResume() {

        super.onResume();
        /* Below statment for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Setting");
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
}
