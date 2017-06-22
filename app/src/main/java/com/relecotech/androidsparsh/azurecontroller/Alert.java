package com.relecotech.androidsparsh.azurecontroller;

import java.util.Date;

/**
 * Created by amey on 2/27/2016.
 */
public class Alert {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("alert_class")
    private String alert_class;
    @com.google.gson.annotations.SerializedName("alert_divison")
    private String divison;
    @com.google.gson.annotations.SerializedName("postdate")
    private Date postdate;
    @com.google.gson.annotations.SerializedName("alert_description")
    private String alert_description;
    @com.google.gson.annotations.SerializedName("category")
    private String category;
    @com.google.gson.annotations.SerializedName("title")
    private String title;
    @com.google.gson.annotations.SerializedName("active")
    private Boolean active;
    @com.google.gson.annotations.SerializedName("Student_id")
    private String Student_id;
    @com.google.gson.annotations.SerializedName("Teacher_id")
    private String Teacher_id;
    @com.google.gson.annotations.SerializedName("attachementidentifier")
    private String attachement_identifier;
    @com.google.gson.annotations.SerializedName("alert_attachement_count")
    private Integer attachement_count;

    public Alert() {
    }

    public Alert(String id, String alert_class, String divison, Date postdate, String alert_description, String category, String title, Boolean active, String student_id, String teacher_id, String attachement_identifier, Integer attachement_count) {
        this.id = id;
        this.alert_class = alert_class;
        this.divison = divison;
        this.postdate = postdate;
        this.alert_description = alert_description;
        this.category = category;
        this.title = title;
        this.active = active;
        Student_id = student_id;
        Teacher_id = teacher_id;
        this.attachement_identifier = attachement_identifier;
        this.attachement_count = attachement_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlert_class() {
        return alert_class;
    }

    public void setAlert_class(String alert_class) {
        this.alert_class = alert_class;
    }

    public String getDivison() {
        return divison;
    }

    public void setDivison(String divison) {
        this.divison = divison;
    }

    public Date getPostdate() {
        return postdate;
    }

    public void setPostdate(Date postdate) {
        this.postdate = postdate;
    }

    public String getAlert_description() {
        return alert_description;
    }

    public void setAlert_description(String alert_description) {
        this.alert_description = alert_description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getAttachement_identifier() {
        return attachement_identifier;
    }

    public void setAttachement_identifier(String attachement_identifier) {
        this.attachement_identifier = attachement_identifier;
    }

    public Integer getAttachement_count() {
        return attachement_count;
    }

    public void setAttachement_count(Integer attachement_count) {
        this.attachement_count = attachement_count;
    }
}
