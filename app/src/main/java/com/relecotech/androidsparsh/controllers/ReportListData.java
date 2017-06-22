package com.relecotech.androidsparsh.controllers;

/**
 * Created by ajinkya on 10/20/2015.
 */
public class ReportListData {
    int nullId = 0;
    private String subject;
    private String marks;
    private String grades;
    //private String outofmarks;
    private boolean isheader;

    public ReportListData(String subject, String marks, String grades, boolean isheader) {
        // this.outofmarks = outofmarks;
        this.grades = grades;
        this.marks = marks;
        this.subject = subject;
        this.isheader = isheader;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    // public String getOutofmarks() {
    // return outofmarks;
    // }

    //public void setOutofmarks(String outofmarks) {
    // this.outofmarks = outofmarks;
    // }


    public boolean isheader() {
        return isheader;
    }

    public void setIsheader(boolean isheader) {
        this.isheader = isheader;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }
}
