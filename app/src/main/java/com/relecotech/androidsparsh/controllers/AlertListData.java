package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 10/16/2015.
 */
public class AlertListData {
    String issueDate;
    String title;
    String body;
    String tag;
    String alertStudent;
    String alertStudId;
    String alertDivision;
    String alertClass;
    String submitted_By_to;
    String alertId;
    String attachmentCount;
    String alert_priority;
    int nullId = 0;


    public AlertListData(String issueDate, String title, String body, String submittedBy, String tag, String alertClass, String alertDivision, String alertStudent, String alertStudId, String alertId, String attachmentCount, String alert_priority) {

        super();
        this.issueDate = issueDate;
        this.title = title;
        this.body = body;
        this.submitted_By_to = submittedBy;
        this.tag = tag;
        this.alertStudent = alertStudent;
        this.alertStudId = alertStudId;
        this.alertDivision = alertDivision;
        this.alertClass = alertClass;
        this.alertId = alertId;
        this.attachmentCount = attachmentCount;
        this.alert_priority = alert_priority;

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubmitted_By_to() {
        return submitted_By_to;
    }

    public void setSubmitted_By_to(String submitted_By_to) {
        this.submitted_By_to = submitted_By_to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }

    public String getAlertStudent() {
        return alertStudent;
    }

    public String getAlertStudId() {
        return alertStudId;
    }

    public void setAlertStudId(String alertStudId) {
        this.alertStudId = alertStudId;
    }

    public void setAlertStudent(String alertStudent) {
        this.alertStudent = alertStudent;
    }

    public String getAlertDivision() {
        return alertDivision;
    }

    public void setAlertDivision(String alertDivision) {
        this.alertDivision = alertDivision;
    }

    public String getAlertClass() {
        return alertClass;
    }

    public void setAlertClass(String alertClass) {
        this.alertClass = alertClass;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(String attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public String getAlert_priority() {
        return alert_priority;
    }

    public void setAlert_priority(String alert_priority) {
        this.alert_priority = alert_priority;
    }

    //    public AlertListData(String issueDate, String title, String body, String tag, String alertStudent, String alertStudId, String alertDivision, String alertClass, String submitted_By_to, String alertId, String attachmentCount, String alert_priority) {
//        this.issueDate = issueDate;
//        this.title = title;
//        this.body = body;
//        this.tag = tag;
//        this.alertStudent = alertStudent;
//        this.alertStudId = alertStudId;
//        this.alertDivision = alertDivision;
//        this.alertClass = alertClass;
//        this.submitted_By_to = submitted_By_to;
//        this.alertId = alertId;
//        this.attachmentCount = attachmentCount;
//        this.alert_priority = alert_priority;
//    }
//
//    public String getIssueDate() {
//        return issueDate;
//    }
//
//    public void setIssueDate(String issueDate) {
//        this.issueDate = issueDate;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//
//    public String getTag() {
//        return tag;
//    }
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }
//
//    public String getAlertStudent() {
//        return alertStudent;
//    }
//
//    public void setAlertStudent(String alertStudent) {
//        this.alertStudent = alertStudent;
//    }
//
//    public String getAlertStudId() {
//        return alertStudId;
//    }
//
//    public void setAlertStudId(String alertStudId) {
//        this.alertStudId = alertStudId;
//    }
//
//    public String getAlertDivision() {
//        return alertDivision;
//    }
//
//    public void setAlertDivision(String alertDivision) {
//        this.alertDivision = alertDivision;
//    }
//
//    public String getAlertClass() {
//        return alertClass;
//    }
//
//    public void setAlertClass(String alertClass) {
//        this.alertClass = alertClass;
//    }
//
//    public String getSubmitted_By_to() {
//        return submitted_By_to;
//    }
//
//    public void setSubmitted_By_to(String submitted_By_to) {
//        this.submitted_By_to = submitted_By_to;
//    }
//
//    public String getAlertId() {
//        return alertId;
//    }
//
//    public void setAlertId(String alertId) {
//        this.alertId = alertId;
//    }
//
//    public String getAttachmentCount() {
//        return attachmentCount;
//    }
//
//    public void setAttachmentCount(String attachmentCount) {
//        this.attachmentCount = attachmentCount;
//    }
//
//    public String getAlert_priority() {
//        return alert_priority;
//    }
//
//    public void setAlert_priority(String alert_priority) {
//        this.alert_priority = alert_priority;
//    }
//
//    public int getNullId() {
//        return nullId;
//    }
//
//    public void setNullId(int nullId) {
//        this.nullId = nullId;
//    }




}
