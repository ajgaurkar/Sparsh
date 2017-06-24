package com.relecotech.androidsparsh.controllers;

import java.io.Serializable;

/**
 * Created by yogesh on 08-Oct-16.
 */
public class BusTrackerListData implements Serializable {

    String time;
    String stop;
    String route;
    String bus_no;
    String bus_Direc;
    String route_no;
    String schedule;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getBus_no() {
        return bus_no;
    }

    public void setBus_no(String bus_no) {
        this.bus_no = bus_no;
    }

    public String getBus_Direc() {
        return bus_Direc;
    }

    public void setBus_Direc(String bus_Direc) {
        this.bus_Direc = bus_Direc;
    }

    public String getRoute_no() {
        return route_no;
    }

    public void setRoute_no(String route_no) {
        this.route_no = route_no;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public BusTrackerListData(String time, String stop, String bus_Direc, String route, String bus_no, String route_no, String schedule) {
        this.time = time;
        this.stop = stop;
        this.route = route;
        this.bus_no = bus_no;
        this.bus_Direc = bus_Direc;
        this.route_no = route_no;
        this.schedule = schedule;
    }

    String direction;
}
