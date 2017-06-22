package com.relecotech.androidsparsh.azurecontroller;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Created by ajinkya on 12/28/2015.
 */
public class Assignment {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("assignment_subject")
    private String assignment_subject;
    @SerializedName("assignment_postdate")
    private Date assignment_postdate;
    @SerializedName("assignment_dauedate")
    private Date assignment_dauedate;
    @com.google.gson.annotations.SerializedName("assignment_description")
    private String assignment_description;
    @com.google.gson.annotations.SerializedName("assignmnet_grades")
    private String assignmnet_grades;
    @com.google.gson.annotations.SerializedName("assignment_class")
    private String assignment_class;
    @com.google.gson.annotations.SerializedName("assignment_submitted_by")
    private String assignment_submitted_by;
    @com.google.gson.annotations.SerializedName("assignment_div")
    private String assignment_div;
    @com.google.gson.annotations.SerializedName("active")
    private Boolean active;
    @com.google.gson.annotations.SerializedName("score_type")
    private String score_type;
    @com.google.gson.annotations.SerializedName("assignment_credit")
    private Integer assignment_credit;
    @com.google.gson.annotations.SerializedName("attachement_count")
    private Integer attachment_count;
    @com.google.gson.annotations.SerializedName("attachementIdentifier")
    private String attachement_identifier;
    @com.google.gson.annotations.SerializedName("School_Class_id")
    private String School_class_id;

    public Assignment() {

    }

    public Assignment(String id, String assignment_subject, Date assignment_postdate, Date assignment_dauedate, String assignment_description, String assignmnet_grades, String assignment_class, String assignment_submitted_by, String assignment_div, Boolean active, String score_type, Integer assignment_credit, Integer attachment_count, String attachement_identifier, String school_class_id) {
        this.id = id;
        this.assignment_subject = assignment_subject;
        this.assignment_postdate = assignment_postdate;
        this.assignment_dauedate = assignment_dauedate;
        this.assignment_description = assignment_description;
        this.assignmnet_grades = assignmnet_grades;
        this.assignment_class = assignment_class;
        this.assignment_submitted_by = assignment_submitted_by;
        this.assignment_div = assignment_div;
        this.active = active;
        this.score_type = score_type;
        this.assignment_credit = assignment_credit;
        this.attachment_count = attachment_count;
        this.attachement_identifier = attachement_identifier;
        School_class_id = school_class_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssignment_subject() {
        return assignment_subject;
    }

    public void setAssignment_subject(String assignment_subject) {

        this.assignment_subject =  assignment_subject;
    }

    public Date getAssignment_postdate() {
        return assignment_postdate;
    }

    public void setAssignment_postdate(Date assignment_postdate) {
        this.assignment_postdate = assignment_postdate;
    }

    public Date getAssignment_dauedate() {
        return assignment_dauedate;
    }

    public void setAssignment_dauedate(Date assignment_dauedate) {
        this.assignment_dauedate = assignment_dauedate;
    }

    public String getAssignment_description() {
        return assignment_description;
    }

    public void setAssignment_description(String assignment_description) {
        this.assignment_description = assignment_description;
    }

    public String getAssignmnet_grades() {
        return assignmnet_grades;
    }

    public void setAssignmnet_grades(String assignmnet_grades) {
        this.assignmnet_grades = assignmnet_grades;
    }

    public String getAssignment_class() {
        return assignment_class;
    }

    public void setAssignment_class(String assignment_class) {
        this.assignment_class = assignment_class;
    }

    public String getAssignment_submitted_by() {
        return assignment_submitted_by;
    }

    public void setAssignment_submitted_by(String assignment_submitted_by) {
        this.assignment_submitted_by = assignment_submitted_by;
    }

    public String getAssignment_div() {
        return assignment_div;
    }

    public void setAssignment_div(String assignment_div) {
        this.assignment_div = assignment_div;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getScore_type() {
        return score_type;
    }

    public void setScore_type(String score_type) {
        this.score_type = score_type;
    }

    public Integer getAssignment_credit() {
        return assignment_credit;
    }

    public void setAssignment_credit(Integer assignment_credit) {
        this.assignment_credit = assignment_credit;
    }

    public Integer getAttachment_count() {
        return attachment_count;
    }

    public void setAttachment_count(Integer attachment_count) {
        this.attachment_count = attachment_count;
    }

    public String getAttachement_identifier() {
        return attachement_identifier;
    }

    public void setAttachement_identifier(String attachement_identifier) {
        this.attachement_identifier = attachement_identifier;
    }

    public String getSchool_class_id() {
        return School_class_id;
    }

    public void setSchool_class_id(String school_class_id) {
        School_class_id = school_class_id;
    }
}