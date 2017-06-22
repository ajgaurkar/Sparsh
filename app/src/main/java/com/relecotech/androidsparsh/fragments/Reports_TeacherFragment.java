package com.relecotech.androidsparsh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.activities.TeacherReportForIndividualStudent;
import com.relecotech.androidsparsh.activities.TeacherReportListViewOfStudent;

import java.util.ArrayList;

/**
 * Created by ajinkya on 10/28/2015.
 */
public class Reports_TeacherFragment extends android.support.v4.app.Fragment {


    //teacherInputlayout components

    public static String selectedDivision;
    public static String selectedClass;
    public static String selectedStudent;
    public static String selectedExam;
    public static String selectedSubject;
    Spinner teacherSelectClassSpinner;
    Spinner teacherSelectDivisionSpinner;
    Spinner teacherSelectStudentSpinner;
    Spinner teacherSelectExamTypeSpinner;
    Spinner teacherSelectSubjectSpinner;
    RadioGroup teacherRadioSeletionGroup;
    RadioButton teacherSubjectRadioBtn;
    RadioButton teacherStudentRadioBtn;
    Button teacherReportViewBtn;
    ImageView divisionArrow;
    ImageView classArrow;
    ImageView studentArrow;
    ImageView examArrow;
    ArrayList<String> inputLayoutClasslist = new ArrayList<>();
    ArrayList<String> inputLayoutDivisionlist = new ArrayList<>();
    ArrayList<String> inputLayoutStudentlist = new ArrayList<>();
    ArrayList<String> inputLayoutExamlist = new ArrayList<>();
    ArrayList<String> inputLayoutSubjectlist = new ArrayList<>();

    String radioValue = "Student";

    ArrayAdapter<String> adapter;
    String user;
    private Intent intent;
    private ConnectionDetector connectionDetector;
    private boolean checkconnection;

    private Fragment mFragment;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionDetector = new ConnectionDetector(getActivity());
        checkconnection = connectionDetector.isConnectingToInternet();
        if (checkconnection) {

        } else {

            Toast.makeText(getActivity(), "No internet connection..!.", Toast.LENGTH_LONG).show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reports_teacher_view, container, false);


//teacherInputlayout components
        teacherSelectClassSpinner = (Spinner) rootView.findViewById(R.id.teacher_reports_class_spinner);
        teacherSelectDivisionSpinner = (Spinner) rootView.findViewById(R.id.teacher_reports_division_spinner);
        teacherSelectStudentSpinner = (Spinner) rootView.findViewById(R.id.teacher_reports_student_selector_spinner);
        teacherSelectExamTypeSpinner = (Spinner) rootView.findViewById(R.id.teacher_reports_exam_selector_spinner);
        teacherSelectSubjectSpinner = (Spinner) rootView.findViewById(R.id.teacher_reports_subject_selector_spinner);


        divisionArrow = (ImageView) rootView.findViewById(R.id.div_arrow_down_float);
        classArrow = (ImageView) rootView.findViewById(R.id.class_arrow_down_float);
        studentArrow = (ImageView) rootView.findViewById(R.id.student_arrow_down_float);
        examArrow = (ImageView) rootView.findViewById(R.id.exam_arrow_down_float);
        examArrow.setVisibility(View.INVISIBLE);

        teacherSelectStudentSpinner.setVisibility(View.VISIBLE);
        teacherSelectExamTypeSpinner.setVisibility(View.INVISIBLE);
        teacherSelectSubjectSpinner.setVisibility(View.INVISIBLE);

        teacherRadioSeletionGroup = (RadioGroup) rootView.findViewById(R.id.teacherreportsRadioGroup);
        teacherSubjectRadioBtn = (RadioButton) rootView.findViewById(R.id.subjectRadioButton);
        teacherStudentRadioBtn = (RadioButton) rootView.findViewById(R.id.studentRadioButton);
        teacherReportViewBtn = (Button) rootView.findViewById(R.id.teacher_reports_view_button);


        inputLayoutClasslist.add("1st");
        inputLayoutClasslist.add("2nd");
        inputLayoutClasslist.add("3rd");
        inputLayoutClasslist.add("4th");
        inputLayoutClasslist.add("[ Class ]");
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, inputLayoutClasslist);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        teacherSelectClassSpinner.setAdapter(adapter);
        teacherSelectClassSpinner.setSelection(adapter.getCount() - 1);

        inputLayoutDivisionlist.add("A");
        inputLayoutDivisionlist.add("B");
        inputLayoutDivisionlist.add("C");
        inputLayoutDivisionlist.add("[ Division ]");
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, inputLayoutDivisionlist);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        teacherSelectDivisionSpinner.setAdapter(adapter);
        teacherSelectDivisionSpinner.setSelection(adapter.getCount() - 1);

        inputLayoutStudentlist.add("Kapil Mohitkar");
        inputLayoutStudentlist.add("Ravindra Jadeja");
        inputLayoutStudentlist.add("Nikhil Gardas");
        inputLayoutStudentlist.add("Parinta Hadke");
        inputLayoutStudentlist.add("Vijay Yadav");
        inputLayoutStudentlist.add("Neha Patil");
        inputLayoutStudentlist.add("Mukta Barve");
        inputLayoutStudentlist.add("Aliya Khan");
        inputLayoutStudentlist.add("Joseph David");
        inputLayoutStudentlist.add("Andrew Waller");
        inputLayoutStudentlist.add("Paul Jhonson");
        inputLayoutStudentlist.add("Marry Farnadez");
        inputLayoutStudentlist.add("Harpal Chaddha");
        inputLayoutStudentlist.add("Rajan Patil");
        inputLayoutStudentlist.add("Jasmeet Kaur");
        inputLayoutStudentlist.add("Manish Gidwani");
        inputLayoutStudentlist.add("[ Select Student ]");
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, inputLayoutStudentlist);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        teacherSelectStudentSpinner.setAdapter(adapter);
        teacherSelectStudentSpinner.setSelection(adapter.getCount() - 1);

        inputLayoutExamlist.add("Prelims");
        inputLayoutExamlist.add("Mid-Term");
        inputLayoutExamlist.add("Pre-Final");
        inputLayoutExamlist.add("Final");
        inputLayoutExamlist.add("[ Exam ]");
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, inputLayoutExamlist);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        teacherSelectExamTypeSpinner.setAdapter(adapter);
        teacherSelectExamTypeSpinner.setSelection(adapter.getCount() - 1);

        inputLayoutSubjectlist.add("English");
        inputLayoutSubjectlist.add("Hindi");
        inputLayoutSubjectlist.add("Mathematics");
        inputLayoutSubjectlist.add("History");
        inputLayoutSubjectlist.add("Science");
        inputLayoutSubjectlist.add("Geography");
        inputLayoutSubjectlist.add("[ Subject ]");
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, inputLayoutSubjectlist);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        teacherSelectSubjectSpinner.setAdapter(adapter);
        teacherSelectSubjectSpinner.setSelection(adapter.getCount() - 1);

        teacherRadioSeletionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.subjectRadioButton:
                        Log.d("checked Subject ", "Subject" + checkedId);
                        radioValue = "Subject";
                        teacherSelectStudentSpinner.setVisibility(View.INVISIBLE);
                        teacherSelectExamTypeSpinner.setVisibility(View.VISIBLE);
                        teacherSelectSubjectSpinner.setVisibility(View.VISIBLE);

                        examArrow.setVisibility(View.VISIBLE);

                        break;

                    case R.id.studentRadioButton:
                        Log.d("checked Student ", "Student" + checkedId);
                        radioValue = "Student";
                        teacherSelectStudentSpinner.setVisibility(View.VISIBLE);
                        teacherSelectExamTypeSpinner.setVisibility(View.INVISIBLE);
                        teacherSelectSubjectSpinner.setVisibility(View.INVISIBLE);

                        examArrow.setVisibility(View.INVISIBLE);

                        break;

                }
            }
        });

        teacherReportViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDivision = teacherSelectDivisionSpinner.getSelectedItem().toString();
                selectedClass = teacherSelectClassSpinner.getSelectedItem().toString();
                selectedStudent = teacherSelectStudentSpinner.getSelectedItem().toString();
                selectedExam = teacherSelectExamTypeSpinner.getSelectedItem().toString();
                selectedSubject = teacherSelectSubjectSpinner.getSelectedItem().toString();

                if (!selectedClass.equals("[ Class ]")) {
                    if (!selectedDivision.equals("[ Division ]")) {
                        if (teacherRadioSeletionGroup.getCheckedRadioButtonId() == R.id.studentRadioButton) {
                            if (!selectedStudent.equals("[ Select Student ]")) {

                                intent = new Intent(getActivity(), TeacherReportForIndividualStudent.class);
                                startActivity(intent);

                                Snackbar.make(v, "Goto student reports_student_view page", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(v, "Select Student", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        if (teacherRadioSeletionGroup.getCheckedRadioButtonId() == R.id.subjectRadioButton) {
                            if (!selectedExam.equals("[ Exam ]")) {
                                if (!selectedSubject.equals("[ Subject ]")) {


                                    intent = new Intent(getActivity(), TeacherReportListViewOfStudent.class);
                                    startActivity(intent);

                                    Snackbar.make(v, "Goto student Subject wise reports_student_view", Snackbar.LENGTH_SHORT).show();

                                } else {
                                    Snackbar.make(v, "Select Subject", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(v, "Select Exam", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Snackbar.make(v, "Select Division", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "Select Class", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
        /* Below statment for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Reports");
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back buttson
                    mFragment = new DashboardTeacherFragment();
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();
                    return true;

                }

                return false;
            }
        });
    }
}
