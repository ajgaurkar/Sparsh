package com.relecotech.androidsparsh.controllers;

import java.util.Date;

/**
 * Created by amey on 6/13/2016.
 */
public class StudentTimeTableListData {
    private java.util.Date slot_StartTime;
    private java.util.Date slot_EndTime;
    private String slot_Subject;
    private String subject_teacher_name;
    private String slot_type;
    private String teacher_class_division;
    private Boolean expandable_list_status;

    public StudentTimeTableListData(Date slot_StartTime, Date slot_EndTime, String slot_Subject, String subject_teacher_name, String slot_type, String teacher_class_division, Boolean expandable_list_status) {
        this.slot_StartTime = slot_StartTime;
        this.slot_EndTime = slot_EndTime;
        this.slot_Subject = slot_Subject;
        this.subject_teacher_name = subject_teacher_name;
        this.slot_type = slot_type;
        this.teacher_class_division = teacher_class_division;
        this.expandable_list_status = expandable_list_status;
    }

    public Date getSlot_StartTime() {
        return slot_StartTime;
    }

    public void setSlot_StartTime(Date slot_StartTime) {
        this.slot_StartTime = slot_StartTime;
    }

    public Date getSlot_EndTime() {
        return slot_EndTime;
    }

    public void setSlot_EndTime(Date slot_EndTime) {
        this.slot_EndTime = slot_EndTime;
    }

    public String getSlot_Subject() {
        return slot_Subject;
    }

    public void setSlot_Subject(String slot_Subject) {
        this.slot_Subject = slot_Subject;
    }

    public String getSubject_teacher_name() {
        return subject_teacher_name;
    }

    public void setSubject_teacher_name(String subject_teacher_name) {
        this.subject_teacher_name = subject_teacher_name;
    }

    public String getSlot_type() {
        return slot_type;
    }

    public void setSlot_type(String slot_type) {
        this.slot_type = slot_type;
    }

    public String getTeacher_class_division() {
        return teacher_class_division;
    }

    public void setTeacher_class_division(String teacher_class_division) {
        this.teacher_class_division = teacher_class_division;
    }

    public Boolean getExpandable_list_status() {
        return expandable_list_status;
    }

    public void setExpandable_list_status(Boolean expandable_list_status) {
        this.expandable_list_status = expandable_list_status;
    }
}

