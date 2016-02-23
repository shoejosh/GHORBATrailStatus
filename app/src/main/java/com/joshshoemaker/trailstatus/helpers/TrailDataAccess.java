package com.joshshoemaker.trailstatus.helpers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Html;

import com.joshshoemaker.trailstatus.TrailStatusApp;
import com.joshshoemaker.trailstatus.api.GhorbaService;
import com.joshshoemaker.trailstatus.models.Trail;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

public class TrailDataAccess {

    public static Observable<List<Trail>> GetTrailData() {
        GhorbaService service = TrailStatusApp.get().getGhorbaService();

        return service.getTrailListData()
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    try {
                        return getTrailListFromHtml(response.body().string());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .flatMap(trails -> Observable.from(trails))
                .flatMap(trail -> LoadTrailPageData(trail))
                .toList();
    }

    private static Observable<Trail> LoadTrailPageData(final Trail trail) {
        GhorbaService service = TrailStatusApp.get().getGhorbaService();
        return service.getTrailData(trail.getPageName())
                .map(response -> {
                    try {
                        loadTrailDataFromHtml(trail, response.body().string());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                    return trail;
                });
    }

    private static List<Trail> getTrailListFromHtml(String html)
    {
        List<Trail> trails = new ArrayList<Trail>();

        String regex = "<tr.*?field-title.*?<a href=\"/trails/(.*?)\">(.*?)</a>.*?trail-status.*?<a.*?>(.*?)</a>.*?trail-condition.*?>(.*?)</td>.*?<em.*?>(.*?)<.*?</tr>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            Trail trail = new Trail(matcher.group(2).trim());
            trail.setPageName(matcher.group(1));
            trail.setStatus(matcher.group(3).replace('\n', ' ').trim());
            trail.setCondition(matcher.group(4).replace('\n', ' ').trim());
            trail.setLastUpdated(matcher.group(5).replace('\n', ' ').trim());

            trails.add(trail);
        }

        return trails;
    }

    private static void loadTrailDataFromHtml(Trail trail, String html)
    {
        String regex = "<a href=\"/trails/" + trail.getPageName()
                + "/(\\d{4}-\\d\\d-\\d\\d)\">(.*)</a>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {

            String date = matcher.group(1);
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
            try {
                trail.setUpdateDate(dateParser.parse(date));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            trail.setShortReport(Html.fromHtml(matcher.group(2)).toString());
        }

        trail.setPageDataUpdated(Calendar.getInstance());
        trail.setUpdatingTrailPageData(false);
    }
}
