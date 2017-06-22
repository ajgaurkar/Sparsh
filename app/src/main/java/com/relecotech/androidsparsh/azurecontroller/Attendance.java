package com.relecotech.androidsparsh.azurecontroller;

import java.util.Date;

/**
 * Created by ajinkya on 3/21/2016.
 */
public class Attendance {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("attendance_date")
    private Date attendanceDate;
    @com.google.gson.annotations.SerializedName("status")
    private String status;
    @com.google.gson.annotations.SerializedName("active")
    private String active;
    @com.google.gson.annotations.SerializedName("Teacher_id")
    private String Teacher_id;
    @com.google.gson.annotations.SerializedName("Student_id")
    private String Student_id;
    @com.google.gson.annotations.SerializedName("School_Class_id")
    private String School_Class_id;

    public Attendance() {
    }

    public Attendance(String id, Date attendanceDate, String status, String active, String teacher_id, String student_id, String school_Class_id) {
        this.id = id;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.active = active;
        Teacher_id = teacher_id;
        Student_id = student_id;
        School_Class_id = school_Class_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTeacher_id() {
        return Teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        Teacher_id = teacher_id;
    }

    public String getStudent_id() {
        return Student_id;
    }

    public void setStudent_id(String student_id) {
        Student_id = student_id;
    }

    public String getSchool_Class_id() {
        return School_Class_id;
    }

    public void setSchool_Class_id(String school_Class_id) {
        School_Class_id = school_Class_id;
    }
}
