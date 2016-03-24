package com.joshshoemaker.trailstatus.dal;

import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailConditionReport;
import com.joshshoemaker.trailstatus.models.TrailFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmList;

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

        String regex = "<tr.*?field-title.*?<a href=\"/trails/(.*?)\">(.*?)</a>.*?trail-status.*?<a.*?>(.*?)</a>.*?</tr>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            Trail trail = trailFactory.createInstance();
            String name = matcher.group(2).trim();
            if (name.endsWith(")")) {
                int index = name.lastIndexOf("(");
                if (index != -1) {
                    name = name.substring(0, index).trim();
                }
            }
            trail.setName(name);

            trail.setPageName(matcher.group(1));
            trail.setStatus(matcher.group(3).replace('\n', ' ').trim());

            trails.add(trail);
        }

        return trails;
    }

    @Override
    public void loadTrailDataFromHtml(Trail trail, String html) {
        String regex = "<td.*?trail-condition.*?>(.*?)</td>.*?"
                + "<a href=\"/trails/" + trail.getPageName()
                + "/(\\d{4}-\\d\\d-\\d\\d).*?\">(.*?)</a>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        RealmList<TrailConditionReport> statusReports = new RealmList<>();
        while (matcher.find()) {
            TrailConditionReport trailConditionReport = new TrailConditionReport();

            trailConditionReport.setCondition(matcher.group(1).trim());

            String date = matcher.group(2);
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
            try {
                trailConditionReport.setUpdateDate(dateParser.parse(date));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            trailConditionReport.setShortReport(matcher.group(3).trim());
            statusReports.add(trailConditionReport);
        }
        trail.setStatusReports(statusReports);
    }
}
