package com.relecotech.androidsparsh.controllers;

/**
 * Created by yogesh on 14-Oct-16.
 */
public class ScheduleListData {

    String stop;
    String time;

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ScheduleListData(String time, String stop) {
        this.time = time;
        this.stop = stop;
    }
}
