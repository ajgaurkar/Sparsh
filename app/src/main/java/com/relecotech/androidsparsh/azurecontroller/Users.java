package com.relecotech.androidsparsh.azurecontroller;

/**
 * Created by ajinkya on 12/28/2015.
 */
public class Users {

    @com.google.gson.annotations.SerializedName("email")
    private String user_email;

    @com.google.gson.annotations.SerializedName("user_pin")
    private String user_pin;

    @com.google.gson.annotations.SerializedName("password")
    private String user_password;

    @com.google.gson.annotations.SerializedName("role")
    private String user_role;

    @com.google.gson.annotations.SerializedName("id")
    private String id;

    @com.google.gson.annotations.SerializedName("status")
    private Boolean status;

    public String getBloodgrp() {
        return bloodgrp;
    }

    public void setBloodgrp(String bloodgrp) {
        this.bloodgrp = bloodgrp;
    }

    @com.google.gson.annotations.SerializedName("bloodgrp")
    private String bloodgrp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_pin() {
        return user_pin;
    }

    public void setUser_pin(String user_pin) {
        this.user_pin = user_pin;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Users(String user_email, String user_pin, String user_password, String user_role, String id, Boolean status, String bloodgrp) {
        this.user_email = user_email;
        this.user_pin = user_pin;
        this.user_password = user_password;
        this.user_role = user_role;
        this.id = id;
        this.status = status;
        this.bloodgrp = bloodgrp;
    }

    public Users() {
    }
}
