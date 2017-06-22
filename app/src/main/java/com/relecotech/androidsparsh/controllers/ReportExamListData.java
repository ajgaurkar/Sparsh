package com.relecotech.androidsparsh.controllers;

/**
 * Created by ajinkya on 10/20/2015.
 */
public class ReportExamListData {
    int nullId = 0;
    private String exam;
    private String marks;
    private String grades;
    private boolean isheader;

    public ReportExamListData(String exam, String marks, String grades, boolean isheader) {
        this.exam = exam;
        this.marks = marks;
        this.grades = grades;
        this.isheader = isheader;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getGrades() {
        return grades;
    }

    public void setGrades(String grades) {
        this.grades = grades;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }

    public boolean isheader() {
        return isheader;
    }

    public void setIsheader(boolean isheader) {
        this.isheader = isheader;
    }
}
