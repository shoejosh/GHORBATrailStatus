package com.joshshoemaker.trailstatus.dal;

import com.joshshoemaker.trailstatus.helpers.Utils;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailConditionReport;
import com.joshshoemaker.trailstatus.models.TrailFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

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

        Document document = Jsoup.parse(html);
        trail.setDifficulty(getTrailDifficulty(document));
        trail.setLength(getTrailLength(document));
        trail.setTechnicalRating(getTrailTechnicalRating(document));
        trail.setDescription(getTrailDescription(document));

        trail.setStatusReports(getTrailConditionReports(document));
    }

    private String getTrailDifficulty(Document document) {
        String difficulty = "";

        Element difficultyDiv = document.select("div.field-name-field-trail-difficulty").first();
        if(difficultyDiv != null) {
            Elements difficultyDivs = difficultyDiv.select("div.field-item");

            List<String> difficulties = new ArrayList<>();
            for(Element diffDiv : difficultyDivs) {
                difficulties.add(diffDiv.text());
            }

            difficulty = Utils.joinString(", ", difficulties);
        }

        return  difficulty;
    }

    private String getTrailLength(Document document) {
        String length = null;
        Element lengthElement = document.select("div.field-name-field-trail-length div.field-items div").first();
        if(lengthElement != null) {
            length = lengthElement.text();
        }

        return length;
    }

    private String getTrailTechnicalRating(Document document) {
        String technicalRating = null;
        Element element = document.select("div.field-name-body p strong:contains(Technical Rating:)").first();
        if(element != null) {
            technicalRating = ((TextNode)element.nextSibling()).text().trim();
        }

        return technicalRating;
    }

    private String getTrailDescription(Document document) {
        String description = null;
        Element element = document.select("div.field-name-body p strong:contains(Description:)").first();
        if(element != null) {
            description = "";
            Node node = element;
            while(node.nextSibling() != null) {
                node = node.nextSibling();
                if(node instanceof Element) {
                    if(((Element)node).tagName().equals("strong")) {
                        break;
                    }
                    continue;
                }
                if(node instanceof TextNode) {
                    description += ((TextNode)node).text();
                }
            }
            description = description.trim();
        }

        return description;
    }

    private RealmList<TrailConditionReport> getTrailConditionReports(Document document) {

        RealmList<TrailConditionReport> statusReports = new RealmList<>();

        Elements reportRows = document.select("div.view-reports-on-trails tbody tr");
        for(Element report : reportRows) {
            TrailConditionReport trailConditionReport = new TrailConditionReport();

            Element conditionElement = report.select("td.views-field-field-trail-condition").first();
            if(conditionElement != null) {
                trailConditionReport.setCondition(conditionElement.text());
            }
            Element shortReportElement = report.select("td.views-field-title a").first();
            if(shortReportElement != null) {
                String url = shortReportElement.attr("href");
                int startIndex = url.lastIndexOf("/") + 1;
                String date = url.substring(startIndex, startIndex + 10);
                SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    trailConditionReport.setDate(dateParser.parse(date));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                trailConditionReport.setShortReport(shortReportElement.text());

            }

            statusReports.add(trailConditionReport);
        }

        return statusReports;
    }
}
