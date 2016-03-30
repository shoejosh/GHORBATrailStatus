package com.joshshoemaker.trailstatus.dal;

import android.content.SharedPreferences;

import com.joshshoemaker.trailstatus.Constants;
import com.joshshoemaker.trailstatus.api.GhorbaService;
import com.joshshoemaker.trailstatus.models.Trail;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

/**
 * Created by Josh on 3/1/2016.
 */
public class TrailService {

    private final GhorbaService ghorbaService;
    private final TrailParser trailParser;
    private SharedPreferences sharedPreferences;

    public TrailService(GhorbaService ghorbaService, TrailParser trailParser, SharedPreferences sharedPreferences) {
        this.ghorbaService = ghorbaService;
        this.trailParser = trailParser;
        this.sharedPreferences = sharedPreferences;
    }

    public Observable<List<Trail>> getTrailData() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Trail.class).findAll().asObservable()
                .first()
                .map(realm::copyFromRealm)
                .doOnTerminate(realm::close);
    }

    public Observable<List<Trail>> updateTrailData() {

        return ghorbaService.getTrailListData()
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    try {
                        return trailParser.getTrailListFromHtml(response.body().string());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .flatMap(Observable::from)
                .flatMap(trail -> loadTrailPageData(trail, trailParser))
                .doOnNext(trail -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(trail);
                    realm.commitTransaction();
                    realm.close();
                })
                .toList()
                .doOnNext(trails -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(Constants.PREFERENCE_UPDATE_TIME_KEY, System.currentTimeMillis());
                    editor.commit();
                });
    }

    public boolean isTrailDataStale() {
        long dataAge = System.currentTimeMillis() - sharedPreferences.getLong(Constants.PREFERENCE_UPDATE_TIME_KEY, 0);
        return dataAge > Constants.STALE_DATA_AGE;
    }

    private Observable<Trail> loadTrailPageData(final Trail trail, final TrailParser trailParser) {
        return ghorbaService.getTrailData(trail.getPageName())
                .map(response -> {
                    try {
                        trailParser.loadTrailDataFromHtml(trail, response.body().string());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                    return trail;
                });
    }
}
