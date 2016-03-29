package com.joshshoemaker.trailstatus.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Josh on 3/21/2016.
 */
public class TrailConditionReport extends RealmObject {

    private String condition;
    private String shortReport;
    private Date date;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getShortReport() {
        return shortReport;
    }

    public void setShortReport(String shortReport) {
        this.shortReport = shortReport;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
