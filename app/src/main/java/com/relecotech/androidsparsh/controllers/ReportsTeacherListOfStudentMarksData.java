package com.relecotech.androidsparsh.controllers;

/**
 * Created by ajinkya on 10/29/2015.
 */
public class ReportsTeacherListOfStudentMarksData {

    String rollNo;
    String studentName;
    String grades;
    // int outOff;
    //int score;
    int nullId;

    public ReportsTeacherListOfStudentMarksData(String rollNo, String studentName, String grades) {
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.grades = grades;
        // this.score = score;
        //this.outOff = outOff;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

//    public int getScore() {
//        return score;
//    }

//    public void setScore(int score) {
//        this.score = score;
//    }
//
//    public int getOutOff() {
//        return outOff;
//    }
//
//    public void setOutOff(int outOff) {
//        this.outOff = outOff;
//    }


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

}
