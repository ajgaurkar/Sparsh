package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 10/16/2015.
 */
public class RewardsRatingListData {


    String rewardsEventTitle;
    String rewardsEventDate;
    String rewardsEventDescription;
    int rewardsRating;
    String rewardsTypes;
    int nullId = 0;

    public RewardsRatingListData(String rewardsEventTitle, String rewardsTypes, int rewardsRating, String rewardsEventDescription, String rewardsEventDate) {
        this.rewardsEventTitle = rewardsEventTitle;
        this.rewardsTypes = rewardsTypes;
        this.rewardsRating = rewardsRating;
        this.rewardsEventDescription = rewardsEventDescription;
        this.rewardsEventDate = rewardsEventDate;
    }

    public String getRewardsEventTitle() {
        return rewardsEventTitle;
    }

    public void setRewardsEventTitle(String rewardsEventTitle) {
        this.rewardsEventTitle = rewardsEventTitle;
    }

    public String getRewardsEventDate() {
        return rewardsEventDate;
    }

    public void setRewardsEventDate(String rewardsEventDate) {
        this.rewardsEventDate = rewardsEventDate;
    }

    public String getRewardsEventDescription() {
        return rewardsEventDescription;
    }

    public void setRewardsEventDescription(String rewardsEventDescription) {
        this.rewardsEventDescription = rewardsEventDescription;
    }

    public int getRewardsRating() {
        return rewardsRating;
    }

    public void setRewardsRating(int rewardsRating) {
        this.rewardsRating = rewardsRating;
    }

    public String getRewardsTypes() {
        return rewardsTypes;
    }

    public void setRewardsTypes(String rewardsTypes) {
        this.rewardsTypes = rewardsTypes;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }
}
