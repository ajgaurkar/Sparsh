package com.relecotech.androidsparsh.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.activities.AttendanceMark;
import com.relecotech.androidsparsh.activities.AttendanceReview;
import com.relecotech.androidsparsh.adapters.AttendanceDraftAdapterAdapter;
import com.relecotech.androidsparsh.controllers.AttendanceDraftListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ajinkya on 1/11/2017.
 */

public class AttendanceClassFragment extends Fragment {

    private TextView todayAttendanceTitleText;
    private TextView todayAttendanceStatusText;
    private Spinner classSpinner;
    private Spinner divisionSpinner;
    private Button reviewBtn;
    private Button markBtn;
    private int day, month, year;
    private Cursor schoolClassCursorData;
    private DatabaseHandler databaseHandler;
    public static String attendanceDivision, attendanceClass;
    private String attendanceSchoolClassId;
    private List<String> studentNamelist;
    private List<String> studentIdList;
    private ArrayAdapter adapterStudentName;
    private Snackbar snackbar;
    private TextView snackbarTextView;
    private View snackbarView;
    private HashMap<String, String> userDetails;
    private ListView draftListView;
    private TextView draftTitleTextView;

    List<AttendanceDraftListData> draftList;
    private Cursor draftCursor;
    private AttendanceDraftAdapterAdapter draftAdapter;
    int draftCount = 0;
    private String attendanceDateString;
    private long attendanceDate;

    int presentCount = 0;
    int j = 0;
    long postingDate;
    private boolean checkconnection;
    private ConnectionDetector connectionDetector;
    private Calendar calendar;
    SessionManager sessionManager;
//    private MobileServiceJsonTable mobileServiceJsonTable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionDetector = new ConnectionDetector(getActivity());
        checkconnection = connectionDetector.isConnectingToInternet();

        databaseHandler = new DatabaseHandler(getActivity());

        schoolClassCursorData = databaseHandler.getTeacherClassDataByCursor();
        System.out.println("schoolClassCursorData : " + schoolClassCursorData);
        System.out.println("schoolClassCursorData.getCount() : " + schoolClassCursorData.getCount());

        //getting saved data to check today's attendance is taken or not
        sessionManager = new SessionManager(getActivity());
        System.out.println("markedDate : " + sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE));

        //initializing student table object
//        mobileServiceJsonTable = MainActivity.mClient.getTable("student");

    }

    private void setDraftMetaData() {

        try {


            //setting draft metadata to draft list
            draftCursor = databaseHandler.geAttendanceDraftMetaData();
            draftList = new ArrayList<>();
            draftCount = draftCursor.getCount();

            if (draftCursor.moveToFirst()) {
                do {
                    System.out.println("draftCursor ------- " + draftCursor.getString(0) + " " + draftCursor.getString(1));

                    //getting class division from schoolclass id
                    Cursor classDivisionCursor = databaseHandler.getClassDivisionByClassId(draftCursor.getString(1));
                    classDivisionCursor.moveToFirst();
                    System.out.println("classDivisionCursor ------- :" + classDivisionCursor.getString(0) + " " + classDivisionCursor.getString(1));

                    draftList.add(new AttendanceDraftListData(draftCursor.getString(1),
                            classDivisionCursor.getString(0), classDivisionCursor.getString(1), draftCursor.getString(0)));

                } while (draftCursor.moveToNext());
            }

            draftTitleTextView.setVisibility(View.VISIBLE);
            if (draftList.size() == 0) {
                draftTitleTextView.setVisibility(View.INVISIBLE);
            }
            draftAdapter = new AttendanceDraftAdapterAdapter(getActivity(), draftList);
            draftListView.setAdapter(draftAdapter);

        } catch (Exception e) {
            System.out.println("Exception attendanceClassFragment " + e.getMessage());
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance_teacher_class_fragment, container, false);


        classSpinner = (Spinner) rootView.findViewById(R.id.attendanceClassspinner);
        divisionSpinner = (Spinner) rootView.findViewById(R.id.attendanceDivisionpinner);

        markBtn = (Button) rootView.findViewById(R.id.attendanceMarkButton);
        reviewBtn = (Button) rootView.findViewById(R.id.attendanceReviewButton);

        draftListView = (ListView) rootView.findViewById(R.id.attendanceDraftListView);
        draftTitleTextView = (TextView) rootView.findViewById(R.id.draftTitleTextView);
        draftTitleTextView.setVisibility(View.INVISIBLE);

        // text views to show current day's marked attendance status;
        todayAttendanceTitleText = (TextView) rootView.findViewById(R.id.todayAttendanceTitleTextView);
        todayAttendanceStatusText = (TextView) rootView.findViewById(R.id.todayAttendanceStatusTextView);
        todayAttendanceTitleText.setVisibility(View.INVISIBLE);
        todayAttendanceStatusText.setVisibility(View.INVISIBLE);

        setDraftMetaData();

        calendar = java.util.Calendar.getInstance();
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        month = calendar.get(java.util.Calendar.MONTH);
        year = calendar.get(java.util.Calendar.YEAR);

        // lists for class and division spinners
        List<String> classlist = new ArrayList<>();
        List<String> divlist = new ArrayList<>();

        try {

//      setting fetched data to class selection spinner
            schoolClassCursorData.moveToFirst();
            do {
                if (!classlist.contains(schoolClassCursorData.getString(2))) {
                    classlist.add(schoolClassCursorData.getString(2));
                }
            } while (schoolClassCursorData.moveToNext());


        } catch (Exception e) {
            System.out.println(" INSIDE Attendance CLass Fragment" + e.getMessage());
        }

        classlist.add("[ Class ]");
        ArrayAdapter adapterClass = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, classlist);
        adapterClass.setDropDownViewResource(R.layout.spinner_dropdown_item);
        classSpinner.setAdapter(adapterClass);
        classSpinner.setSelection(adapterClass.getCount() - 1);


        //  setting default data to division spinner
        divlist.add("[ Division ]");
        ArrayAdapter adapterDivision = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, divlist);
        adapterDivision.setDropDownViewResource(R.layout.spinner_dropdown_item);
        divisionSpinner.setAdapter(adapterDivision);
        divisionSpinner.setSelection(adapterDivision.getCount() - 1);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                attendanceClass = classSpinner.getSelectedItem().toString();

//      setting fetched data to division selection spinner
                List<String> divlist = new ArrayList<>();
                try {
                    schoolClassCursorData.moveToFirst();
                    do {
                        if (schoolClassCursorData.getString(2).equals(attendanceClass)) {
                            if (!divlist.contains(schoolClassCursorData.getString(3))) {
                                divlist.add(schoolClassCursorData.getString(3));
                                System.out.println("divlist : " + divlist);
                            }
                        }
                    } while (schoolClassCursorData.moveToNext());

                } catch (Exception e) {
                    System.out.println("Exception in class spinner of attendanceClassFrag");
                }
                divlist.add("[ Division ]");
                ArrayAdapter adapterDivision = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, divlist);
                adapterDivision.setDropDownViewResource(R.layout.spinner_dropdown_item);
                divisionSpinner.setAdapter(adapterDivision);
                divisionSpinner.setSelection(adapterDivision.getCount() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                attendanceDivision = divisionSpinner.getSelectedItem().toString();
                try {
                    schoolClassCursorData.moveToFirst();
                    do {
                        if (schoolClassCursorData.getString(2).equals(attendanceClass) && schoolClassCursorData.getString(3).equals(attendanceDivision)) {
                            attendanceSchoolClassId = schoolClassCursorData.getString(1);
                            System.out.println(" attendanceSchoolClassId : " + attendanceSchoolClassId);
                        }
                    } while (schoolClassCursorData.moveToNext());
                } catch (Exception e) {
                    System.out.println("Exception in class spinner of attendanceClassFrag");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkAttendanceStatus();

        markBtn.setOnClickListener(new View.OnClickListener() {
                                       public AlertDialog.Builder builder;

                                       @Override
                                       public void onClick(View view) {

                                           attendanceDivision = divisionSpinner.getSelectedItem().toString();
                                           attendanceClass = classSpinner.getSelectedItem().toString();

                                           // getting teacher primary class id
                                           // to check if selected class ih her/his primary class or not
                                           userDetails = sessionManager.getUserDetails();

                                           if (attendanceClass != "[ Class ]") {
                                               if (attendanceDivision != "[ Division ]") {
                                                   if (attendanceSchoolClassId.equals(userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID))) {
                                                       if (!checkAttendanceStatus()) {

                                                           // Calling new attendance activity
                                                           // along with passing necessary data to fetch student list from azure

                                                           Intent markAttendanceIntent = new Intent(getActivity(), AttendanceMark.class);
                                                           markAttendanceIntent.putExtra("schoolClassId", attendanceSchoolClassId);
                                                           markAttendanceIntent.putExtra("Task", "MarkAttendance");
                                                           markAttendanceIntent.putExtra("Class", attendanceClass);
                                                           markAttendanceIntent.putExtra("Division", attendanceDivision);
                                                           startActivity(markAttendanceIntent);

                                                       } else {
                                                           builder = new AlertDialog.Builder(getActivity());
                                                           builder.setCancelable(false);
                                                           builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {
                                                                   dialog.dismiss();
                                                               }
                                                           });
                                                           builder.setTitle("Attendance marked");
                                                           builder.show();

                                                       }
                                                   } else {
                                                       showSnack("Select your own class", view);
                                                   }
                                                   System.out.println(" mark ");
                                               } else {
                                                   showSnack("Select Division", view);
                                               }
                                           } else {
                                               showSnack("Select Class", view);
                                           }


                                       }
                                   }

        );

        draftListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                             {
                                                 @Override
                                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                     AttendanceDraftListData selectedDraftItem = draftList.get(position);

                                                     // Calling new attendance activity
                                                     // along with passing necessary data to fetch student list from azure
                                                     Intent markAttendanceIntent = new Intent(getActivity(), AttendanceMark.class);
                                                     markAttendanceIntent.putExtra("schoolClassId", selectedDraftItem.getSchoolClassId());
                                                     markAttendanceIntent.putExtra("Task", "MarkAttendance");
                                                     markAttendanceIntent.putExtra("Class", selectedDraftItem.getAttendanceClass());
                                                     markAttendanceIntent.putExtra("Division", selectedDraftItem.getAttendanceDivision());
                                                     markAttendanceIntent.putExtra("Date", selectedDraftItem.getAttendanceDate());
                                                     startActivity(markAttendanceIntent);
                                                 }
                                             }

        );

        reviewBtn.setOnClickListener(new View.OnClickListener()

                                     {
                                         @Override
                                         public void onClick(View view) {
                                             attendanceDivision = divisionSpinner.getSelectedItem().toString();
                                             attendanceClass = classSpinner.getSelectedItem().toString();

                                             if (attendanceClass != "[ Class ]") {
                                                 if (attendanceDivision != "[ Division ]") {
                                                     System.out.println(" review ");

                                                     DialogFragment newFragment = new SelectDateFragment();

                                                     newFragment.show(getActivity().getFragmentManager(), "DatePicker");


                                                 } else {
                                                     showSnack("Select Division", view);
                                                 }
                                             } else {
                                                 showSnack("Select Class", view);
                                             }
                                         }
                                     }

        );

        return rootView;

    }

    private void showSnack(String message, View view) {
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbarView = snackbar.getView();
        snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private boolean checkAttendanceStatus() {

        //getting current date and comparing with saved date
        String todaysDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        System.out.println("today's Date : " + todaysDate);

        //condition to check weather tab is class tab and attendance is marked or not
        if (todaysDate.equals(sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE))) {
            System.out.println("Attendance completed ");
            todayAttendanceTitleText.setVisibility(View.VISIBLE);
            todayAttendanceTitleText.setText("Attendance for " + sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE));
            todayAttendanceStatusText.setVisibility(View.VISIBLE);
            return true;
        } else {
            System.out.println("Attendance left OR in student tab");
            todayAttendanceTitleText.setVisibility(View.INVISIBLE);
            todayAttendanceStatusText.setVisibility(View.INVISIBLE);
            return false;
        }

    }


    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            final int yy = calendar.get(java.util.Calendar.YEAR);
            final int mm = calendar.get(java.util.Calendar.MONTH);
            final int dd = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, yy, mm, dd) {
                @Override
                public void onDateChanged(DatePicker view, int year, int month, int day) {
                    super.onDateChanged(view, year, month, day);
                    if (year > yy) {
                        view.updateDate(yy, mm, dd);

                    }
                    if (month > mm && year == yy) {
                        view.updateDate(yy, mm, dd);
                    }
                    if (day > dd && month == mm && year == yy) {
                        view.updateDate(yy, mm, dd);
                    }
                }

                @Override
                public void onClick(DialogInterface dialog, int doneBtn) {
                    if (doneBtn == BUTTON_POSITIVE) {

                        int year = getDatePicker().getYear();
                        int month = getDatePicker().getMonth();
                        int day = getDatePicker().getDayOfMonth();

                        SessionManager sessionManager;
                        sessionManager = new SessionManager(getActivity());
                        HashMap<String, String> map = sessionManager.getUserDetails();

                        Intent reviewAttendanceIntent = new Intent(getActivity(), AttendanceReview.class);
                        reviewAttendanceIntent.putExtra("reviewDate", day + "-" + (month + 1) + "-" + year);
                        reviewAttendanceIntent.putExtra("reviewClassId", map.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                        reviewAttendanceIntent.putExtra("Class", attendanceClass);
                        reviewAttendanceIntent.putExtra("Division", attendanceDivision);
                        startActivity(reviewAttendanceIntent);
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
