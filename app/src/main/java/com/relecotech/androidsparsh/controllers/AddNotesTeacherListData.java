package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 2/17/2016.
 */
public class AddNotesTeacherListData {
    private String id;
    private String message;

    public AddNotesTeacherListData(String message, String id) {
        this.message = message;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
