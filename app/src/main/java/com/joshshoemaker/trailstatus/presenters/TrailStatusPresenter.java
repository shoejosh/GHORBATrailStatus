package com.joshshoemaker.trailstatus.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.joshshoemaker.trailstatus.activities.TrailStatusActivity;
import com.joshshoemaker.trailstatus.dal.TrailService;
import com.joshshoemaker.trailstatus.helpers.Utils;
import com.joshshoemaker.trailstatus.models.Trail;

import java.util.List;

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
        if(view() != null) {
            view().showTrails(model);
        }
    }

    @Override
    public void bindView(@NonNull TrailStatusActivity view) {
        super.bindView(view);

        if(isLoadingData) {
            view.showProgress(true);
        }
        else if(model == null) {
            loadData();
        }
        else if (trailService.isTrailDataStale() && Utils.isNetworkConnected(view)) {
            refreshData();
        }
    }

    private void loadData() {

        trailService.getTrailData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trails -> {
                            setModel(trails);

                            if(trails.size() == 0 || trailService.isTrailDataStale()) {
                                if(view() != null && Utils.isNetworkConnected(view())) {
                                    refreshData();
                                }
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                            Crashlytics.logException(throwable);
                            if(view() != null && Utils.isNetworkConnected(view())) {
                                refreshData();
                            }
                        }
                );
    }

    private void refreshData() {
        if(view() == null) {
            return;
        }

        view().showProgress(true);
        isLoadingData = true;

        trailService.updateTrailData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trails -> {
                            setModel(trails);
                            isLoadingData = false;
                            if(view() != null) {
                                view().showProgress(false);
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                            Crashlytics.logException(throwable);
                            isLoadingData = false;
                            if(view() != null) {
                                view().showProgress(false);
                                view().showNetworkError();
                            }
                        }
                );
    }

    public void onRefreshClicked() {
        refreshData();
    }
}
