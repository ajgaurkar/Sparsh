package com.relecotech.androidsparsh.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.relecotech.androidsparsh.activities.AttendanceStudentIndividual;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ajinkya on 1/11/2017.
 */

public class AttendanceStudentFragment extends Fragment {
    private Button studentViewBtn;
    private Spinner classSpinner;
    private Spinner divisionSpinner;
    private Spinner studentNameSpinner;
    private Calendar calendar;
    private int day;
    private int month;
    private int year;
    private ConnectionDetector connectionDetector;
    private DatabaseHandler databaseHandler;
    private boolean checkconnection;
    private Cursor schoolClassCursorData;
    private SessionManager sessionManager;
    private MobileServiceJsonTable mobileServiceJsonTable;
    private String attendanceClass;
    private String attendanceDivision;
    private List<String> studentNamelist;
    private List<String> studentIdList;
    private ArrayAdapter<String> adapterStudentName;
    private String attendanceSchoolClassId;
    private Snackbar snackbar;
    private View snackbarView;
    private TextView snackbarTextView;
    private String attendanceIndividualStudent;
    private ProgressDialog attendanceProgressDialog;
    private JsonObject jsonObjectForStudentList;
    private JsonArray getJsonListResponse;


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
//not needed        mobileServiceJsonTable = MainActivity.mClient.getTable("student");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance_teacher_student_fragment, container, false);

        studentViewBtn = (Button) rootView.findViewById(R.id.attendanceStudentViewButton);


        classSpinner = (Spinner) rootView.findViewById(R.id.attendanceClassspinner);
        divisionSpinner = (Spinner) rootView.findViewById(R.id.attendanceDivisionpinner);
        studentNameSpinner = (Spinner) rootView.findViewById(R.id.attendanceStudentNamespinner);


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
            System.out.println("AttendanceStudentFragment Exception " + e.getMessage());
        }

        classlist.add("[ Class ]");
        ArrayAdapter adapterClass = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, classlist);
        adapterClass.setDropDownViewResource(R.layout.spinner_dropdown_item);
        classSpinner.setAdapter(adapterClass);
        classSpinner.setSelection(adapterClass.getCount() - 1);

//      setting default data to division spinner
        divlist.add("[ Division ]");
        ArrayAdapter adapterDivision = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, divlist);
        adapterDivision.setDropDownViewResource(R.layout.spinner_dropdown_item);
        divisionSpinner.setAdapter(adapterDivision);
        divisionSpinner.setSelection(adapterDivision.getCount() - 1);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                attendanceClass = classSpinner.getSelectedItem().toString();

                List<String> divlist = new ArrayList<>();
                try {

                    //      setting fetched data to division selection spinner
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
                    System.out.println("Exception in class spinner of attendancestudfrag");
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

                    if ((!classSpinner.getSelectedItem().equals("[ Class ]")) && (!divisionSpinner.getSelectedItem().equals("[ Division ]"))) {

                        System.out.println(" Student Tab ");
                        //new FetchingStudent().execute();
                        FetchStudentList();
                    }
                } catch (Exception e) {
                    System.out.println("Exception in div spinner of attendancestudfrag");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        studentViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("studentIdList : " + studentIdList);
                System.out.println("studentNamelist : " + studentNamelist);

                attendanceDivision = divisionSpinner.getSelectedItem().toString();
                attendanceClass = classSpinner.getSelectedItem().toString();
                attendanceIndividualStudent = studentNameSpinner.getSelectedItem().toString();
                String selectedStudentId = studentIdList.get(studentNameSpinner.getSelectedItemPosition());
                System.out.println(" selectedStudentId " + selectedStudentId);
                System.out.println(" studentNameSpinner.getSelectedItem().toString() " + studentNameSpinner.getSelectedItem().toString());

                if (attendanceClass != "[ Class ]") {
                    if (attendanceDivision != "[ Division ]") {
                        if (attendanceIndividualStudent != "[ Select Student ]") {

                            Intent studIndividualAttendanceIntent = new Intent(getActivity(), AttendanceStudentIndividual.class);
                            studIndividualAttendanceIntent.putExtra("studentId", selectedStudentId);
                            studIndividualAttendanceIntent.putExtra("studentName", studentNameSpinner.getSelectedItem().toString());
                            startActivity(studIndividualAttendanceIntent);
                            Log.d("individual", "individual");

                        } else {
                            showSnack("Select Student", view);
                        }
                    } else {
                        showSnack("Select Division", view);
                    }
                } else {
                    showSnack("Select Class", view);
                }
            }
        });

        studentNamelist = new ArrayList<>();
        studentIdList = new ArrayList<>();
        studentNamelist.add("[ Select Student ]");
        studentIdList.add("DummyId");
        adapterStudentName = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, studentNamelist);
        adapterStudentName.setDropDownViewResource(R.layout.spinner_dropdown_item);
        studentNameSpinner.setAdapter(adapterStudentName);
        studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);
        return rootView;

    }

    private void showSnack(String message, View view) {
        snackbar = Snackbar.make(view, "Select your own class", Snackbar.LENGTH_SHORT);
        snackbarView = snackbar.getView();
        snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(Color.WHITE);
        snackbar.show();
    }


    private void FetchStudentList() {

        jsonObjectForStudentList = new JsonObject();

        // Yogesh changes for invoke api
        jsonObjectForStudentList.addProperty("SchoolClassId", attendanceSchoolClassId);
//        jsonObjectForStudentList.addProperty("UserRole", userDetails.get(SessionManager.KEY_USER_ROLE));

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
                System.out.println("json not received");
                studentNamelist.clear();
                studentNamelist.add("[ Select Student ]");
                studentIdList.clear();
                studentIdList.add("DummyId");

            } else {

                studentNamelist.clear();
                studentIdList.clear();
                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                    String student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String student_fullName = student_firstName + " " + student_lastName;
                    System.out.println("student_Id" + student_Id);
                    System.out.println("student_count" + loop);
                    System.out.println("student_fullName" + student_fullName);

                            /*two lists, one to maintain name and other for id.
                            we could have instead used custom adapter with list<object>
                            but that results in addition of list data class and writting custom adapter
                            so to avoid that 2 lists are used for trial. this logic may sound bad but easy to write and deal with*/
                    studentNamelist.add(student_fullName);
                    studentIdList.add(student_Id);

                }
                studentNamelist.add("[ Select Student ]");
                studentIdList.add("DummyId");

                adapterStudentName.notifyDataSetChanged();
                studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);
            }

            System.out.println("studentSpinnerList  " + studentNamelist.size());
            System.out.println("studentIdList  " + studentIdList.size());
        } catch (Exception e) {

        }
    }

//    public class FetchingStudent extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                System.out.println("Doing Background.......");
//                System.out.println("attendanceSchoolClassId.......####################  " + attendanceSchoolClassId);
//                JsonElement jsonElement = mobileServiceJsonTable.parameter("SchoolClassId", attendanceSchoolClassId).execute().get();
//                System.out.println("jsonElement" + jsonElement);
//                JsonArray getJsonListResponse = jsonElement.getAsJsonArray();
//                System.out.println("getJsonListResponse.size()" + getJsonListResponse.size());
//                try {
//                    if (getJsonListResponse.size() == 0) {
//                        System.out.println("json not received");
//                        studentNamelist.clear();
//                        studentNamelist.add("[ Select Student ]");
//                        studentIdList.clear();
//                        studentIdList.add("DummyId");
//
//                    } else {
//
//                        studentNamelist.clear();
//                        studentIdList.clear();
//                        for (int loop = 0; loop < getJsonListResponse.size(); loop++) {
//
//                            JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
//                            String student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
//                            String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
//                            String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
//                            String student_fullName = student_firstName + " " + student_lastName;
//                            System.out.println("student_Id" + student_Id);
//                            System.out.println("student_count" + loop);
//                            System.out.println("student_fullName" + student_fullName);
//
//                            /*two lists, one to maintain name and other for id.
//                            we could have instead used custom adapter with list<object>
//                            but that results in addition of list data class and writting custom adapter
//                            so to avoid that 2 lists are used for trial. this logic may sound bad but easy to write and deal with*/
//                            studentNamelist.add(student_fullName);
//                            studentIdList.add(student_Id);
//
//                        }
//                    }
//
//                    System.out.println("studentSpinnerList  " + studentNamelist.size());
//                    System.out.println("studentIdList  " + studentIdList.size());
//                } catch (Exception e) {
//
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//            studentNamelist.add("[ Select Student ]");
//            studentIdList.add("DummyId");
//
//            adapterStudentName.notifyDataSetChanged();
//            studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);
//
//        }
//    }
}
