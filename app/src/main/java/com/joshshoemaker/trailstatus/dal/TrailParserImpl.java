package com.joshshoemaker.trailstatus.dal;

import android.text.Html;

import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Josh on 2/28/2016.
 */
public class TrailParserImpl implements TrailParser {

    private TrailFactory trailFactory;

    public TrailParserImpl(TrailFactory trailFactory)
    {
        this.trailFactory = trailFactory;
    }

    @Override
    public List<Trail> getTrailListFromHtml(String html) {
        List<Trail> trails = new ArrayList<Trail>();

        String regex = "<tr.*?field-title.*?<a href=\"/trails/(.*?)\">(.*?)</a>.*?trail-status.*?<a.*?>(.*?)</a>.*?trail-condition.*?>(.*?)</td>.*?<em.*?>(.*?)<.*?</tr>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            Trail trail = trailFactory.createInstance();
            trail.setName(matcher.group(2).trim());
            trail.setPageName(matcher.group(1));
            trail.setStatus(matcher.group(3).replace('\n', ' ').trim());
            trail.setCondition(matcher.group(4).replace('\n', ' ').trim());
            trail.setLastUpdated(matcher.group(5).replace('\n', ' ').trim());

            trails.add(trail);
        }

        return trails;
    }

    @Override
    public void loadTrailDataFromHtml(Trail trail, String html) {
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

            //trail.setShortReport(Html.fromHtml(matcher.group(2)).toString());
            trail.setShortReport(matcher.group(2).trim());
        }
    }
}
