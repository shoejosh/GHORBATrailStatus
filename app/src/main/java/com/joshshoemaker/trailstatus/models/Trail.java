package com.joshshoemaker.trailstatus.models;

import com.joshshoemaker.trailstatus.Constants;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Josh on 3/21/2016.
 */
public class Trail extends RealmObject {

    @PrimaryKey
    private String pageName;
    private String name;
    private String status;
    private RealmList<TrailConditionReport> statusReports;

    //region getters/setters

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RealmList<TrailConditionReport> getStatusReports() {
        return statusReports;
    }

    public void setStatusReports(RealmList<TrailConditionReport> statusReports) {
        this.statusReports = statusReports;
    }

    //endregion

    //region helper methods

    public String getPageUrl() {
        return Constants.BASE_URL + getPageName();
    }

    //endregion
}
