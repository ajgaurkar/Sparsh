package com.relecotech.androidsparsh.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.Reports_Teacher_Student_Marks_adapter;
import com.relecotech.androidsparsh.controllers.ReportsTeacherListOfStudentMarksData;
import com.relecotech.androidsparsh.fragments.Reports_TeacherFragment;

import java.util.ArrayList;

/**
 * Created by ajinkya on 10/28/2015.
 */
public class TeacherReportListViewOfStudent extends ActionBarActivity {

    String selectedDivision = Reports_TeacherFragment.selectedDivision;
    String selectedClass = Reports_TeacherFragment.selectedClass;
    String selectdStudent = Reports_TeacherFragment.selectedStudent;
    String selectedExam = Reports_TeacherFragment.selectedExam;
    String selectedSubject = Reports_TeacherFragment.selectedSubject;

    //teacherListLayout components

    ListView studentMarkListView; //teacher_view_reports_list_view

    ArrayList<ReportsTeacherListOfStudentMarksData> studentMarkList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ReportsTeacherListOfStudentMarksData reportsTeacherListOfStudentMarksData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_reports_view_student_list);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(selectedClass + " " + selectedDivision + "  " + selectedExam + "  " + selectedSubject);
        } catch (Exception actionBarException) {

        }

        studentMarkListView = (ListView) findViewById(R.id.teacher_view_reports_list_view);

        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("01", "Ravichnadra Ashwin", "A+");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("02", "Nikhil Gardas", "A++");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("03", "Parinta Hadke", "B+");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("04", "Vijay Yadav", "B+");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("05", "Neha Patil", "B++");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("06", "Mukta Barve", "C");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("07", "Puja Muke", "A");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("08", "Aliya Khan", "B++");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("09", "Joseph David", "A+");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("10", "Andrew Waller", "C");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("11", "Paul Jhonson", "A");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("12", "Marry Farnadez", "A+");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("13", "Harpal Chaddha", "B");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        reportsTeacherListOfStudentMarksData = new ReportsTeacherListOfStudentMarksData("14", "Rajan Patil", "B");
        studentMarkList.add(reportsTeacherListOfStudentMarksData);
        Reports_Teacher_Student_Marks_adapter adapter = new Reports_Teacher_Student_Marks_adapter(getApplicationContext(), studentMarkList);
        studentMarkListView.setAdapter(adapter);


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
}
