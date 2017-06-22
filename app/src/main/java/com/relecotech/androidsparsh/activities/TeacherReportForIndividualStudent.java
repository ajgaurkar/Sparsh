package com.relecotech.androidsparsh.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.Report_student_exam_AdapterAdapter;
import com.relecotech.androidsparsh.adapters.Report_subject_AdapterAdapter;
import com.relecotech.androidsparsh.controllers.ReportExamListData;
import com.relecotech.androidsparsh.controllers.ReportListData;
import com.relecotech.androidsparsh.fragments.Reports_TeacherFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajinkya on 10/28/2015.
 */
public class TeacherReportForIndividualStudent extends ActionBarActivity {
    public Spinner select_Report_Spinner;
    public TextView select_Exam, select_Subject, reportgraph;
    ArrayAdapter<String> adapter;
    TextView exam_textView, aspect_textView, grades_textView;
    ListView show_listView;
    ArrayList<ReportListData> HindiList, EnglishList, MathList, HistoryList;
    ArrayList<ReportExamListData> PrelimsexamList, MidtermexamList, PreFinalexamlist, FinalexamList;
    ReportListData reportlistsubjectData1, reportlistsubjectData2, reportlistsubjectData3, reportlistsubjectData4, reportlistsubjectData5, reportlistsubjectData6, reportlistsubjectData7, reportlistsubjectData8, reportlistsubjectData9, reportlistsubjectData10, reportlistsubjectData11, reportlistsubjectData12, reportlistsubjectData13, reportlistsubjectData14, reportlistsubjectData15, reportlistsubjectData16;
    ReportExamListData reportlistexamData1, reportlistexamData2, reportlistexamData3, reportlistexamData4, reportlistexamData5, reportlistexamData6, reportlistexamData7, reportlistexamData8, reportlistexamData9, reportlistexamData10, reportlistexamData11, reportlistexamData12, reportlistexamData13, reportlistexamData14, reportlistexamData15;
    Report_subject_AdapterAdapter reportsubjectAdapterAdapter;
    Report_student_exam_AdapterAdapter reportexamAdapterAdapter;
    private Boolean subject_btn_status = false, exam_btn_status = true;

    private String selectdStudent = Reports_TeacherFragment.selectedStudent;
    private String selectedDivision = Reports_TeacherFragment.selectedDivision;
    private String selectedClass = Reports_TeacherFragment.selectedClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_student_report_for_teacher);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(selectedClass + " " + selectedDivision + "  " + selectdStudent);
        } catch (Exception actionBarException) {

        }

        select_Exam = (TextView) findViewById(R.id.select_exam_button);
        select_Subject = (TextView) findViewById(R.id.select_subject_button);
        select_Report_Spinner = (Spinner) findViewById(R.id.reportselect_spinner);
        show_listView = (ListView) findViewById(R.id.showlistView);


        exam_textView = (TextView) findViewById(R.id.reportHeadingTextView1);
        aspect_textView = (TextView) findViewById(R.id.reportHeadingTextView2);
        grades_textView = (TextView) findViewById(R.id.reportHeadingTextView3);

        HindiList = new ArrayList<>();
        EnglishList = new ArrayList<>();
        MathList = new ArrayList<>();
        HistoryList = new ArrayList<>();

        PrelimsexamList = new ArrayList<>();
        MidtermexamList = new ArrayList<>();
        PreFinalexamlist = new ArrayList<>();
        FinalexamList = new ArrayList<>();

        List<String> select_Report_Exam_List = new ArrayList<>();
        select_Report_Exam_List.add("Prelims ");
        select_Report_Exam_List.add("Mid-Term");
        select_Report_Exam_List.add("PreFinal");
        select_Report_Exam_List.add("Final");

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, select_Report_Exam_List);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        select_Report_Spinner.setAdapter(adapter);

        setPrelims();

        select_Exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (exam_btn_status == false) {

                    exam_btn_status = true;
                    subject_btn_status = false;

                    select_Exam.setBackgroundResource(R.drawable.reports_textview_color_bg_left);
                    select_Subject.setBackgroundResource(R.drawable.reports_textview_color_bg_left_clicked);
                    select_Subject.setTextColor(getResources().getColorStateList(R.color.colorPrimaryDark));
                    select_Exam.setTextColor(Color.WHITE);

                    List<String> select_Report_Exam_List = new ArrayList<>();
                    select_Report_Exam_List.add("Prelims ");
                    select_Report_Exam_List.add("Mid-Term");
                    select_Report_Exam_List.add("PreFinal");
                    select_Report_Exam_List.add("Final");
                    adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, select_Report_Exam_List);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    select_Report_Spinner.setAdapter(adapter);
                    adapter.getCount();

                    exam_textView.setText("Exam");
                    aspect_textView.setText("Aspects");
                    grades_textView.setText("Grades");


                    setPrelims();

                }
            }
        });
        select_Subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (subject_btn_status == false) {

                    subject_btn_status = true;
                    exam_btn_status = false;

                    select_Subject.setBackgroundResource(R.drawable.reports_textview_color_bg_right);
                    select_Exam.setBackgroundResource(R.drawable.reports_textview_color_bg_right_clicked);
                    select_Subject.setTextColor(Color.WHITE);
                    select_Exam.setTextColor(getResources().getColorStateList(R.color.colorPrimaryDark));

                    List<String> select_Report_Subject_List = new ArrayList<>();
                    select_Report_Subject_List.add("English");
                    select_Report_Subject_List.add("Mathematics");
                    select_Report_Subject_List.add("History");
                    select_Report_Subject_List.add("Hindi");

                    adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, select_Report_Subject_List);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    select_Report_Spinner.setAdapter(adapter);
                    adapter.getCount();

                    exam_textView.setText("Subject");
                    aspect_textView.setText("Aspects");
                    grades_textView.setText("Grades");

                }
            }

        });


        select_Report_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d("selected Item Position", "" + select_Report_Spinner.getItemAtPosition(position));
                String s = (String) select_Report_Spinner.getItemAtPosition(position);
                // Log.d("selected Item Position", "" + s);
                switch (s) {
                    case "Hindi":
                        setHindi();
                        break;
                    case "Mathmatics":
                        setMath();
                        break;
                    case "History":
                        setHistory();
                        break;
                    case "English":
                        setEnglish();
                        break;
                    case "Prelims":
                        setPrelims();
                        break;
                    case "Final":
                        setFinal();
                        break;
                    case "Mid-Term":
                        setMid_Term();
                        break;
                    case "PreFinal":
                        setPreFinal();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void setHindi() {
        reportlistsubjectData1 = new ReportListData("Prelims", "", "", true);
        reportlistsubjectData2 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData3 = new ReportListData("", "Writing Skill", "A", false);
        reportlistsubjectData4 = new ReportListData("", "Speaking Skill", "B+", false);
        reportlistsubjectData5 = new ReportListData("Midterms", "", "", true);
        reportlistsubjectData6 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData7 = new ReportListData("", "Writing Skill", "A", false);
        reportlistsubjectData8 = new ReportListData("", "Speaking Skill", "B+", false);
        reportlistsubjectData9 = new ReportListData("PreFinal", "", "", true);
        reportlistsubjectData10 = new ReportListData("", "Reading Skill", "B+", false);
        reportlistsubjectData11 = new ReportListData("", "Writing Skill", "C", false);
        reportlistsubjectData12 = new ReportListData("", "Speaking Skill", "A", false);
        reportlistsubjectData13 = new ReportListData("Final", "", "", true);
        reportlistsubjectData14 = new ReportListData("", "Reading Skill", "A", false);
        reportlistsubjectData15 = new ReportListData("", "Writing Skill", "A+", false);
        reportlistsubjectData16 = new ReportListData("", "Speaking Skill", "B+", false);
        HindiList.add(reportlistsubjectData1);
        HindiList.add(reportlistsubjectData2);
        HindiList.add(reportlistsubjectData3);
        HindiList.add(reportlistsubjectData4);
        HindiList.add(reportlistsubjectData5);
        HindiList.add(reportlistsubjectData6);
        HindiList.add(reportlistsubjectData7);
        HindiList.add(reportlistsubjectData8);
        HindiList.add(reportlistsubjectData9);
        HindiList.add(reportlistsubjectData10);
        HindiList.add(reportlistsubjectData11);
        HindiList.add(reportlistsubjectData12);
        HindiList.add(reportlistsubjectData13);
        HindiList.add(reportlistsubjectData14);
        HindiList.add(reportlistsubjectData15);
        HindiList.add(reportlistsubjectData16);
        reportsubjectAdapterAdapter = new Report_subject_AdapterAdapter(getApplicationContext(), HindiList);
        show_listView.setAdapter(reportsubjectAdapterAdapter);
        //  reportsubjectAdapterAdapter.updateResults(HindiList);
    }

    public void setEnglish() {
        reportlistsubjectData1 = new ReportListData("Prelims", "", "", true);
        reportlistsubjectData2 = new ReportListData("", "Reading Skill", "B+", false);
        reportlistsubjectData3 = new ReportListData("", "Writing Skill", "A+", false);
        reportlistsubjectData4 = new ReportListData("", "Speaking Skill", "B+", false);
        reportlistsubjectData5 = new ReportListData("Midterms", "", "", true);
        reportlistsubjectData6 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData7 = new ReportListData("", "Writing Skill", "c+", false);
        reportlistsubjectData8 = new ReportListData("", "Speaking Skill", "B", false);
        reportlistsubjectData9 = new ReportListData("PreFinal", "", "", true);
        reportlistsubjectData10 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData11 = new ReportListData("", "Writing Skill", "C", false);
        reportlistsubjectData12 = new ReportListData("", "Speaking Skill", "A", false);
        reportlistsubjectData13 = new ReportListData("Final", "", "", true);
        reportlistsubjectData14 = new ReportListData("", "Reading Skill", "B", false);
        reportlistsubjectData15 = new ReportListData("", "Writing Skill", "B+", false);
        reportlistsubjectData16 = new ReportListData("", "Speaking Skill", "B+", false);
        EnglishList.add(reportlistsubjectData1);
        EnglishList.add(reportlistsubjectData2);
        EnglishList.add(reportlistsubjectData3);
        EnglishList.add(reportlistsubjectData4);
        EnglishList.add(reportlistsubjectData5);
        EnglishList.add(reportlistsubjectData6);
        EnglishList.add(reportlistsubjectData7);
        EnglishList.add(reportlistsubjectData8);
        EnglishList.add(reportlistsubjectData9);
        EnglishList.add(reportlistsubjectData10);
        EnglishList.add(reportlistsubjectData11);
        EnglishList.add(reportlistsubjectData12);
        EnglishList.add(reportlistsubjectData13);
        EnglishList.add(reportlistsubjectData14);
        EnglishList.add(reportlistsubjectData15);
        EnglishList.add(reportlistsubjectData16);

        reportsubjectAdapterAdapter = new Report_subject_AdapterAdapter(getApplicationContext(), EnglishList);
        show_listView.setAdapter(reportsubjectAdapterAdapter);
        //**********clear the list data*********//
        HindiList.clear();
        MathList.clear();
        HistoryList.clear();

        PrelimsexamList.clear();
        PreFinalexamlist.clear();
        FinalexamList.clear();
        MidtermexamList.clear();
        //**********clear the list data*********//


    }

    public void setMath() {
        reportlistsubjectData1 = new ReportListData("Prelims", "", "", true);
        reportlistsubjectData2 = new ReportListData("", "Concept", "C+", false);
        reportlistsubjectData3 = new ReportListData("", "Activity", "A", false);
        reportlistsubjectData4 = new ReportListData("", "Mental Work", "B+", false);
        reportlistsubjectData5 = new ReportListData("Midterms", "", "", true);
        reportlistsubjectData6 = new ReportListData("", "Concept", "B+", false);
        reportlistsubjectData7 = new ReportListData("", "Activity", "A", false);
        reportlistsubjectData8 = new ReportListData("", "Mental Work", "A+", false);
        reportlistsubjectData9 = new ReportListData("PreFinal", "", "", true);
        reportlistsubjectData10 = new ReportListData("", "Concept", "A+", false);
        reportlistsubjectData11 = new ReportListData("", "Activity", "B", false);
        reportlistsubjectData12 = new ReportListData("", "Mental Work", "B+", false);
        reportlistsubjectData13 = new ReportListData("Final", "", "", true);
        reportlistsubjectData14 = new ReportListData("", "Concept", "A+", false);
        reportlistsubjectData15 = new ReportListData("", "Activity", "B", false);
        reportlistsubjectData16 = new ReportListData("", "Mental Work", "A+", false);
        MathList.add(reportlistsubjectData1);
        MathList.add(reportlistsubjectData2);
        MathList.add(reportlistsubjectData3);
        MathList.add(reportlistsubjectData4);
        MathList.add(reportlistsubjectData5);
        MathList.add(reportlistsubjectData6);
        MathList.add(reportlistsubjectData7);
        MathList.add(reportlistsubjectData8);
        MathList.add(reportlistsubjectData9);
        MathList.add(reportlistsubjectData10);
        MathList.add(reportlistsubjectData11);
        MathList.add(reportlistsubjectData12);
        MathList.add(reportlistsubjectData13);
        MathList.add(reportlistsubjectData14);
        MathList.add(reportlistsubjectData15);
        MathList.add(reportlistsubjectData16);
        reportsubjectAdapterAdapter = new Report_subject_AdapterAdapter(getApplicationContext(), MathList);
        show_listView.setAdapter(reportsubjectAdapterAdapter);
        //**********clear the list data*********//
        HindiList.clear();
        EnglishList.clear();
        HistoryList.clear();

        PrelimsexamList.clear();
        PreFinalexamlist.clear();
        FinalexamList.clear();
        MidtermexamList.clear();
        //**********clear the list data*********//

    }

    public void setHistory() {
        reportlistsubjectData1 = new ReportListData("Prelims", "", "", true);
        reportlistsubjectData2 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData3 = new ReportListData("", "Writing Skill", "A+", false);
        reportlistsubjectData4 = new ReportListData("", "Speaking Skill", "A+", false);
        reportlistsubjectData5 = new ReportListData("Midterms", "", "", true);
        reportlistsubjectData6 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData7 = new ReportListData("", "Writing Skill", "A+", false);
        reportlistsubjectData8 = new ReportListData("", "Speaking Skill", "A+", false);
        reportlistsubjectData9 = new ReportListData("PreFinal", "", "", true);
        reportlistsubjectData10 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData11 = new ReportListData("", "Writing Skill", "A+", false);
        reportlistsubjectData12 = new ReportListData("", "Speaking Skill", "A+", false);
        reportlistsubjectData13 = new ReportListData("Final", "", "", true);
        reportlistsubjectData14 = new ReportListData("", "Reading Skill", "A+", false);
        reportlistsubjectData15 = new ReportListData("", "Writing Skill", "A+", false);
        reportlistsubjectData16 = new ReportListData("", "Speaking Skill", "A+", false);
        HistoryList.add(reportlistsubjectData1);
        HistoryList.add(reportlistsubjectData2);
        HistoryList.add(reportlistsubjectData3);
        HistoryList.add(reportlistsubjectData4);
        HistoryList.add(reportlistsubjectData5);
        HistoryList.add(reportlistsubjectData6);
        HistoryList.add(reportlistsubjectData7);
        HistoryList.add(reportlistsubjectData8);
        HistoryList.add(reportlistsubjectData9);
        HistoryList.add(reportlistsubjectData10);
        HistoryList.add(reportlistsubjectData11);
        HistoryList.add(reportlistsubjectData12);
        HistoryList.add(reportlistsubjectData13);
        HistoryList.add(reportlistsubjectData14);
        HistoryList.add(reportlistsubjectData15);
        HistoryList.add(reportlistsubjectData16);
        reportsubjectAdapterAdapter = new Report_subject_AdapterAdapter(getApplicationContext(), HistoryList);
        show_listView.setAdapter(reportsubjectAdapterAdapter);
        //**********clear the list data*********//
        HindiList.clear();
        MathList.clear();
        EnglishList.clear();

        PrelimsexamList.clear();
        PreFinalexamlist.clear();
        FinalexamList.clear();
        MidtermexamList.clear();
        //**********clear the list data*********//
    }

    public void setPrelims() {

        reportlistexamData1 = new ReportExamListData("Hindi", "", "", true);
        reportlistexamData2 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData3 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData4 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData5 = new ReportExamListData("English", "", "", true);
        reportlistexamData6 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData7 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData8 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData9 = new ReportExamListData("Mathmatics", "", "", true);
        reportlistexamData10 = new ReportExamListData("", "Concept", "A+", false);
        reportlistexamData11 = new ReportExamListData("", "Tables", "A+", false);
        reportlistexamData12 = new ReportExamListData("", "Mental Ability", "A+", false);
        reportlistexamData13 = new ReportExamListData("Computer", "", "", true);
        reportlistexamData14 = new ReportExamListData("", "Skill", "A+", false);
        reportlistexamData15 = new ReportExamListData("", "Aptitude", "A+", false);


        PrelimsexamList.add(reportlistexamData1);
        PrelimsexamList.add(reportlistexamData2);
        PrelimsexamList.add(reportlistexamData3);
        PrelimsexamList.add(reportlistexamData5);
        PrelimsexamList.add(reportlistexamData6);
        PrelimsexamList.add(reportlistexamData7);
        PrelimsexamList.add(reportlistexamData8);
        PrelimsexamList.add(reportlistexamData9);
        PrelimsexamList.add(reportlistexamData10);
        PrelimsexamList.add(reportlistexamData11);
        PrelimsexamList.add(reportlistexamData12);
        PrelimsexamList.add(reportlistexamData13);
        PrelimsexamList.add(reportlistexamData14);
        PrelimsexamList.add(reportlistexamData15);

        reportexamAdapterAdapter = new Report_student_exam_AdapterAdapter(getApplicationContext(), PrelimsexamList);
        show_listView.setAdapter(reportexamAdapterAdapter);
        //**********clear the list data*********//
        PreFinalexamlist.clear();
        FinalexamList.clear();
        MidtermexamList.clear();

        HindiList.clear();
        EnglishList.clear();
        HistoryList.clear();
        MathList.clear();
        //**********clear the list data*********//
    }

    public void setFinal() {

        reportlistexamData1 = new ReportExamListData("Hindi", "", "", true);
        reportlistexamData2 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData3 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData4 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData5 = new ReportExamListData("English", "", "", true);
        reportlistexamData6 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData7 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData8 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData9 = new ReportExamListData("Maathmatics", "", "", true);
        reportlistexamData10 = new ReportExamListData("", "Concept", "A+", false);
        reportlistexamData11 = new ReportExamListData("", "Tables", "A+", false);
        reportlistexamData12 = new ReportExamListData("", "Mental Ability", "A+", false);
        reportlistexamData13 = new ReportExamListData("Computer", "", "", true);
        reportlistexamData14 = new ReportExamListData("", "Skill", "A+", false);
        reportlistexamData15 = new ReportExamListData("", "Aptitude", "A+", false);

        FinalexamList.add(reportlistexamData1);
        FinalexamList.add(reportlistexamData2);
        FinalexamList.add(reportlistexamData3);
        FinalexamList.add(reportlistexamData4);
        FinalexamList.add(reportlistexamData5);
        FinalexamList.add(reportlistexamData6);
        FinalexamList.add(reportlistexamData7);
        FinalexamList.add(reportlistexamData8);
        FinalexamList.add(reportlistexamData9);
        FinalexamList.add(reportlistexamData10);
        FinalexamList.add(reportlistexamData11);
        FinalexamList.add(reportlistexamData12);
        FinalexamList.add(reportlistexamData13);
        FinalexamList.add(reportlistexamData14);
        FinalexamList.add(reportlistexamData15);

        reportexamAdapterAdapter = new Report_student_exam_AdapterAdapter(getApplicationContext(), FinalexamList);
        show_listView.setAdapter(reportexamAdapterAdapter);

        //**********clear the list data*********//
        PreFinalexamlist.clear();
        PrelimsexamList.clear();
        MidtermexamList.clear();

        HindiList.clear();
        EnglishList.clear();
        HistoryList.clear();
        MathList.clear();

        //**********clear the list data*********//
    }


    public void setMid_Term() {

        reportlistexamData1 = new ReportExamListData("Hindi", "", "", true);
        reportlistexamData2 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData3 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData4 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData5 = new ReportExamListData("English", "", "", true);
        reportlistexamData6 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData7 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData8 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData9 = new ReportExamListData("Maathmatics", "", "", true);
        reportlistexamData10 = new ReportExamListData("", "Concept", "A+", false);
        reportlistexamData11 = new ReportExamListData("", "Tables", "A+", false);
        reportlistexamData12 = new ReportExamListData("", "Mental Ability", "A+", false);
        reportlistexamData13 = new ReportExamListData("Computer", "", "", true);
        reportlistexamData14 = new ReportExamListData("", "Skill", "A+", false);
        reportlistexamData15 = new ReportExamListData("", "Aptitude", "A+", false);

        MidtermexamList.add(reportlistexamData1);
        MidtermexamList.add(reportlistexamData2);
        MidtermexamList.add(reportlistexamData3);
        MidtermexamList.add(reportlistexamData4);
        MidtermexamList.add(reportlistexamData5);
        MidtermexamList.add(reportlistexamData6);
        MidtermexamList.add(reportlistexamData7);
        MidtermexamList.add(reportlistexamData8);
        MidtermexamList.add(reportlistexamData9);
        MidtermexamList.add(reportlistexamData10);
        MidtermexamList.add(reportlistexamData11);
        MidtermexamList.add(reportlistexamData12);
        MidtermexamList.add(reportlistexamData13);
        MidtermexamList.add(reportlistexamData14);
        MidtermexamList.add(reportlistexamData15);

        reportexamAdapterAdapter = new Report_student_exam_AdapterAdapter(getApplicationContext(), MidtermexamList);
        show_listView.setAdapter(reportexamAdapterAdapter);
        //**********clear the list data*********//
        PreFinalexamlist.clear();
        FinalexamList.clear();
        PrelimsexamList.clear();

        HindiList.clear();
        EnglishList.clear();
        HistoryList.clear();
        MathList.clear();
        //**********clear the list data*********//
    }

    public void setPreFinal() {

        reportlistexamData1 = new ReportExamListData("Hindi", "", "", true);
        reportlistexamData2 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData3 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData4 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData5 = new ReportExamListData("English", "", "", true);
        reportlistexamData6 = new ReportExamListData("", "Reading", "A+", false);
        reportlistexamData7 = new ReportExamListData("", "Writing", "A+", false);
        reportlistexamData8 = new ReportExamListData("", "Speaking", "A+", false);
        reportlistexamData9 = new ReportExamListData("Maathmatics", "", "", true);
        reportlistexamData10 = new ReportExamListData("", "Concept", "A+", false);
        reportlistexamData11 = new ReportExamListData("", "Tables", "A+", false);
        reportlistexamData12 = new ReportExamListData("", "Mental Ability", "A+", false);
        reportlistexamData13 = new ReportExamListData("Computer", "", "", true);
        reportlistexamData14 = new ReportExamListData("", "Skill", "A+", false);
        reportlistexamData15 = new ReportExamListData("", "Aptitude", "A+", false);

        PreFinalexamlist.add(reportlistexamData1);
        PreFinalexamlist.add(reportlistexamData2);
        PreFinalexamlist.add(reportlistexamData3);
        PreFinalexamlist.add(reportlistexamData4);
        PreFinalexamlist.add(reportlistexamData5);
        PreFinalexamlist.add(reportlistexamData6);
        PreFinalexamlist.add(reportlistexamData7);
        PreFinalexamlist.add(reportlistexamData8);
        PreFinalexamlist.add(reportlistexamData9);
        PreFinalexamlist.add(reportlistexamData10);
        PreFinalexamlist.add(reportlistexamData11);
        PreFinalexamlist.add(reportlistexamData12);
        PreFinalexamlist.add(reportlistexamData13);
        PreFinalexamlist.add(reportlistexamData14);
        PreFinalexamlist.add(reportlistexamData15);

        reportexamAdapterAdapter = new Report_student_exam_AdapterAdapter(getApplicationContext(), PreFinalexamlist);
        show_listView.setAdapter(reportexamAdapterAdapter);

        //**********clear the list data*********//
        PrelimsexamList.clear();
        FinalexamList.clear();
        MidtermexamList.clear();

        HindiList.clear();
        EnglishList.clear();
        HistoryList.clear();
        MathList.clear();
        //**********clear the list data*********//
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
