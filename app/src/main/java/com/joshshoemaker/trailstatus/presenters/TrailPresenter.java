package com.joshshoemaker.trailstatus.presenters;

import com.joshshoemaker.trailstatus.activities.TrailActivity;
import com.joshshoemaker.trailstatus.models.Trail;

import io.realm.Realm;

/**
 * Created by Josh on 3/24/2016.
 */
public class TrailPresenter extends BasePresenter<Trail, TrailActivity> {

    @Override
    protected void updateView() {
        view().setActivityTitle(model.getName());
        view().setStatus(model.getStatus());
        view().setDescription(model.getDescription());
        view().setDifficulty(model.getDifficulty());
        view().setLength(model.getLength());
        view().setTechnicalRating(model.getTechnicalRating());
        view().setTrailConditionReports(model.getStatusReports());
    }


    public void setTrailPageName(String trailPageName) {
        Realm realm = Realm.getDefaultInstance();

        Trail trail = realm.where(Trail.class)
                .equalTo("pageName", trailPageName)
                .findFirst();

        setModel(realm.copyFromRealm(trail));

        realm.close();
    }
}
