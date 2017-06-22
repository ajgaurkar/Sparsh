package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 10/16/2015.
 */
public class FeesExtracurricularListdata {

    int nullId = 0;
    private String events;
    private String duedate;
    private String feesamount;
    private String stauts;
    private int serial_no;

    public FeesExtracurricularListdata(int serial_no, String events, String duedate, String feesamount, String stauts) {
        this.events = events;
        this.duedate = duedate;
        this.feesamount = feesamount;
        this.stauts = stauts;
        this.serial_no = serial_no;

    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getFeesamount() {
        return feesamount;
    }

    public void setFeesamount(String feesamount) {
        this.feesamount = feesamount;
    }

    public String getStauts() {
        return stauts;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    public int getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(int serial_no) {
        this.serial_no = serial_no;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }

}
