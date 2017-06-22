package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 5/2/2016.
 */
public class UserDetailListData {
    private String title;
    private String description;
    private int titleimage;

    public UserDetailListData(String title, String description, int titleimage) {
        this.title = title;
        this.description = description;
        this.titleimage = titleimage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTitleimage() {
        return titleimage;
    }

    public void setTitleimage(int titleimage) {
        this.titleimage = titleimage;
    }
}
