package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 3/4/2016.
 */
public class AlertStudentListData {
    private String id;
    private String fullname;

    public AlertStudentListData(String fullname, String id) {
        this.fullname = fullname;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
