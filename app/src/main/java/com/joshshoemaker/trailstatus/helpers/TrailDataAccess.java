package com.joshshoemaker.trailstatus.helpers;

import java.io.IOException;
import java.util.List;

import com.joshshoemaker.trailstatus.TrailStatusApp;
import com.joshshoemaker.trailstatus.api.GhorbaService;
import com.joshshoemaker.trailstatus.dal.TrailParser;
import com.joshshoemaker.trailstatus.dal.TrailParserImpl;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailFactoryImpl;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

public class TrailDataAccess {

    public static Observable<List<Trail>> GetTrailData() {
        GhorbaService service = TrailStatusApp.get().getGhorbaService();
        TrailParserImpl trailParser = new TrailParserImpl(new TrailFactoryImpl());

        return service.getTrailListData()
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    try {
                        return trailParser.getTrailListFromHtml(response.body().string());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .flatMap(trails -> Observable.from(trails))
                .flatMap(trail -> LoadTrailPageData(trail, trailParser))
                .toList();
    }

    private static Observable<Trail> LoadTrailPageData(final Trail trail, final TrailParser trailParser) {
        GhorbaService service = TrailStatusApp.get().getGhorbaService();
        return service.getTrailData(trail.getPageName())
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
