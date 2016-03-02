package com.joshshoemaker.trailstatus.dal;

import com.joshshoemaker.trailstatus.api.GhorbaService;
import com.joshshoemaker.trailstatus.models.Trail;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

/**
 * Created by Josh on 3/1/2016.
 */
public class TrailService {

    private final GhorbaService ghorbaService;
    private final TrailParser trailParser;

    public TrailService(GhorbaService ghorbaService, TrailParser trailParser) {
        this.ghorbaService = ghorbaService;
        this.trailParser = trailParser;
    }

    public Observable<List<Trail>> getTrailData() {
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
                .toList();
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
