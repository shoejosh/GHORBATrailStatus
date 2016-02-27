package com.joshshoemaker.trailstatus.presenters;

import android.support.annotation.NonNull;

import com.joshshoemaker.trailstatus.activities.TrailStatusActivity;
import com.joshshoemaker.trailstatus.helpers.TrailDataAccess;
import com.joshshoemaker.trailstatus.models.Trail;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Josh on 2/24/2016.
 */
public class TrailStatusPresenter extends BasePresenter<List<Trail>, TrailStatusActivity> {

    private boolean isLoadingData = false;

    @Override
    protected void updateView() {
        view().showTrails(model);
    }

    @Override
    public void bindView(@NonNull TrailStatusActivity view) {
        super.bindView(view);

        if(isLoadingData) {
            view().showProgress(true);
        }
        else if(model == null && !isLoadingData) {
            loadData();
        }
    }

    private void loadData() {
        view().showProgress(true);
        isLoadingData = true;
        TrailDataAccess.GetTrailData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trails -> {
                            setModel(trails);
                            isLoadingData = false;
                            view().showProgress(false);
                        },
                        throwable -> {}
                );
    }

    public void onRefreshClicked() {
        loadData();
    }
}