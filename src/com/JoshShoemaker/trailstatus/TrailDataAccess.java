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

public class TrailDataAccess {

	public static List<Trail> GetAllTrails(Trail[] oldTrailData) {
		List<Trail> trails = new ArrayList<Trail>();

		String page = "";
		Date date = new Date();

		try {
			page = PageScraper.getUrlContent("http://ghorba.org/trails"
					+ "?val=" + date.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	public static void LoadPageData(Trail trail) {
		
		String page = "";
		Date now = new Date();

		try {
			page = PageScraper.getUrlContent(trail.getPageUrl() + "?val="
					+ now.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// trail.loadTrailPageData(page);
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
