package com.JoshShoemaker.trailstatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Html;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class TrailDataAccess {

    private static final String BASE_TRAIL_URL = "http://ghorba.org/trails";

    private static String GetTrailsUrl()
    {
        Date date = new Date();

        return BASE_TRAIL_URL + "?val=" + date.getTime();
    }

    public static void LoadTrailData(final ITrailListAdapter adapter)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String page) {
                List<Trail> trails = TrailDataAccess.CreateTrails(page, adapter.getData());
                adapter.setData(trails.toArray(new Trail[trails.size()]));
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //do something;
            }
        };

        RequestQueue requestQueue = TrailStatusApp.get().getRequestQueue();
        requestQueue.add(new StringRequest(TrailDataAccess.GetTrailsUrl(), responseListener, errorListener));
    }

    public static void LoadTrailPageData(final ITrailListAdapter adapter, final Trail trail)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String page) {
                LoadTrailData(trail, page);
                adapter.trailUpdated();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //do something;
            }
        };

        Date now = new Date();
        String url = trail.getPageUrl() + "?val=" + now.getTime();

        RequestQueue requestQueue = TrailStatusApp.get().getRequestQueue();
        requestQueue.add(new StringRequest(url, responseListener, errorListener));

    }

    private static List<Trail> CreateTrails(String page, Trail[] oldTrailData)
    {
        List<Trail> trails = new ArrayList<Trail>();

        String regex = "<tr.*?field-title.*?<a href=\"/trails/(.*?)\">(.*?)</a>.*?trail-status.*?<a.*?>(.*?)</a>.*?trail-condition.*?>(.*?)</td>.*?<em.*?>(.*?)<.*?</tr>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(page);

        int count = 0;
        while (matcher.find()) {
            Trail trail = null;

            if (oldTrailData != null && oldTrailData.length > count) {
                Trail prevTrail = (Trail) oldTrailData[count];
                if (prevTrail.getPageName().equals(matcher.group(1))) {
                    trail = prevTrail;
                }
            }

            if (trail == null) {
                trail = new Trail(matcher.group(2).trim());
            }

            trail.setPageName(matcher.group(1));
            trail.setStatus(matcher.group(3).replace('\n', ' ').trim());
            trail.setCondition(matcher.group(4).replace('\n', ' ').trim());
            trail.setLastUpdated(matcher.group(5).replace('\n', ' ').trim());

            trails.add(trail);

            count++;
        }

        return trails;
    }

    public static List<Trail> GetAllTrails(Trail[] oldTrailData) {
        String page = "";

        try {
            page = PageScraper.getUrlContent(GetTrailsUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CreateTrails(page, oldTrailData);
    }

    public static void LoadPageData(Trail trail) {

        String page = "";
        Date now = new Date();

        try {
            page = PageScraper.getUrlContent(trail.getPageUrl() + "?val="
                    + now.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoadTrailData(trail, page);
    }

    private static void LoadTrailData(Trail trail, String page)
    {
        String regex = "<a href=\"/trails/" + trail.getPageName()
                + "/(\\d{4}-\\d\\d-\\d\\d)\">(.*)</a>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(page);

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
