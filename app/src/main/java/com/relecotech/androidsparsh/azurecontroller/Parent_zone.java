package com.relecotech.androidsparsh.azurecontroller;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by amey on 2/8/2016.
 */
public class Parent_zone {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("category")
    private String category;
    @SerializedName("postdate")
    private Date postdate;
    @SerializedName("startdate")
    private Date startdate;
    @SerializedName("enddate")
    private Date enddate;
    @SerializedName("schedule")
    private Date meetingschedule;
    @com.google.gson.annotations.SerializedName("status")
    private String status;
    @com.google.gson.annotations.SerializedName("reply")
    private String reply;
    @com.google.gson.annotations.SerializedName("description_cause")
    private String description_cause;
    @com.google.gson.annotations.SerializedName("no_of_days")
    private int no_of_days;
    @com.google.gson.annotations.SerializedName("active")
    private Boolean active;
    @com.google.gson.annotations.SerializedName("Student_id")
    private String Student_id;
    @com.google.gson.annotations.SerializedName("Teacher_id")
    private String Teacher_id;
    @com.google.gson.annotations.SerializedName("School_Class_id")
    private String School_class_id;

    public Parent_zone() {
    }

    public Parent_zone(String id, String category, Date postdate, Date startdate, Date enddate, Date meetingschedule, String status, String reply, String description_cause, int no_of_days, Boolean active, String student_id, String teacher_id, String school_class_id) {
        this.id = id;
        this.category = category;
        this.postdate = postdate;
        this.startdate = startdate;
        this.enddate = enddate;
        this.meetingschedule = meetingschedule;
        this.status = status;
        this.reply = reply;
        this.description_cause = description_cause;
        this.no_of_days = no_of_days;
        this.active = active;
        Student_id = student_id;
        Teacher_id = teacher_id;
        School_class_id = school_class_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getPostdate() {
        return postdate;
    }

    public void setPostdate(Date postdate) {
        this.postdate = postdate;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Date getMeetingschedule() {
        return meetingschedule;
    }

    public void setMeetingschedule(Date meetingschedule) {
        this.meetingschedule = meetingschedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getDescription_cause() {
        return description_cause;
    }

    public void setDescription_cause(String description_cause) {
        this.description_cause = description_cause;
    }

    public int getNo_of_days() {
        return no_of_days;
    }

    public void setNo_of_days(int no_of_days) {
        this.no_of_days = no_of_days;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getStudent_id() {
        return Student_id;
    }

    public void setStudent_id(String student_id) {
        Student_id = student_id;
    }

    public String getTeacher_id() {
        return Teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        Teacher_id = teacher_id;
    }

    public String getSchool_class_id() {
        return School_class_id;
    }

    public void setSchool_class_id(String school_class_id) {
        School_class_id = school_class_id;
    }
}
