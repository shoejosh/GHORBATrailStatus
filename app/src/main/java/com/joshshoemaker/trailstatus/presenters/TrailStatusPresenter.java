package com.joshshoemaker.trailstatus.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.joshshoemaker.trailstatus.activities.TrailStatusActivity;
import com.joshshoemaker.trailstatus.dal.TrailService;
import com.joshshoemaker.trailstatus.models.Trail;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Josh on 2/24/2016.
 */
public class TrailStatusPresenter extends BasePresenter<List<Trail>, TrailStatusActivity> {

    private static final String TAG = "TrailStatusPresenter";

    private boolean isLoadingData = false;
    private TrailService trailService;

    public TrailStatusPresenter(TrailService trailService) {
        this.trailService = trailService;
    }

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

        //TODO: update trail data if it is stale
        trailService.getTrailData()
                .flatMap(trails1 -> {
                    if(trails1.size() > 0) {
                        return Observable.just(trails1);
                    }

                    view().showProgress(true);
                    isLoadingData = true;
                    return trailService.updateTrailData();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trails -> {
                            setModel(trails);
                            isLoadingData = false;
                            view().showProgress(false);
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                        }
                );
    }

    private void refreshData() {
        view().showProgress(true);
        isLoadingData = true;

        trailService.updateTrailData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trails -> {
                            setModel(trails);
                            isLoadingData = false;
                            view().showProgress(false);
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                        }
                );
    }

    public void onRefreshClicked() {
        refreshData();
    }
}
