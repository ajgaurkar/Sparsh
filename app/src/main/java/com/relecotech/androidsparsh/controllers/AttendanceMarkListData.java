package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceMarkListData {
    String studentId;
    String rollNo;
    String fullName;
    String presentStatus;

    public AttendanceMarkListData(String studentId, String rollNo, String fullName, String presentStatus) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.fullName = fullName;
        this.presentStatus = presentStatus;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(String presentStatus) {
        this.presentStatus = presentStatus;
    }
}
