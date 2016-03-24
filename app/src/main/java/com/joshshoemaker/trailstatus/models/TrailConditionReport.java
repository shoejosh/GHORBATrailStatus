package com.joshshoemaker.trailstatus.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Josh on 3/21/2016.
 */
public class TrailConditionReport extends RealmObject {

    private String condition;
    private String shortReport;
    private Date updateDate;

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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
