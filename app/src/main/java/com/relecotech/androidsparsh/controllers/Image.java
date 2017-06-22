package com.relecotech.androidsparsh.controllers;

import java.io.Serializable;

public class Image implements Serializable {

    private String name;
    private String small, medium, large;
    private String description;

    public Image(String name, String small, String medium, String large, String description) {
        this.name = name;
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.description = description;
    }

    public Image() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
