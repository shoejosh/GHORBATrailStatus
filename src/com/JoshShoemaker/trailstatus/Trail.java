package com.JoshShoemaker.trailstatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trail {

	public enum TrailStatus {
		OPEN, CLOSED, UNKNOWN;

		@Override
		public String toString() {
			switch (this) {
			case OPEN:
				return "Open";
			case CLOSED:
				return "Closed";
			case UNKNOWN:
				return "";
			}

			return "";
		}
	};

	private String mName;

	private TrailStatus mStatus;

	private String mParenName;

	private String mCondition;

	private String mLastUpdated;
	
	private Date mUpdateDate;

	private String mPageName;

	private String mShortReport;

	public Trail(String name) {
		this.setName(name);
		this.mStatus = TrailStatus.UNKNOWN;
	}

	public TrailStatus getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		if (status.equalsIgnoreCase("open")) {
			mStatus = TrailStatus.OPEN;
		} else if (status.equalsIgnoreCase("closed")) {
			mStatus = TrailStatus.CLOSED;
		} else {
			mStatus = TrailStatus.UNKNOWN;
		}

	}

	public void setName(String name) {
		name = name.trim();
		if (name.endsWith(")")) {
			int index = name.lastIndexOf("(");
			if (index != -1) {
				mParenName = name.substring(index);
				name = name.substring(0, index).trim();
			}
		}
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public String getParenName() {
		return mParenName;
	}

	public String getCondition() {
		return mCondition;
	}

	public void setCondition(String condition) {
		this.mCondition = condition;
	}

	public String getLastUpdated() {
		return mLastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.mLastUpdated = lastUpdated;
	}

	public void setPageName(String pageName) {
		this.mPageName = pageName;
	}

	public String getPageName() {
		return this.mPageName;
	}

	public String getPageUrl() {
		return "http://ghorba.org/trails/" + mPageName;
	}

	public String getShortReport() {
		return mShortReport;
	}

	public void setShortReport(String shortReport) {
		this.mShortReport = shortReport;
	}

	public Date getUpdateDate()
	{
		return mUpdateDate;
	}
	
	public void loadTrailPageData(String pageData) {
		String regex = "<a href=\"/trails/" + getPageName() + "/(\\d{4}-\\d\\d-\\d\\d)\">(.*)</a>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(pageData);

		if (matcher.find()) {
			
			String date = matcher.group(1);
			SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");			
			try {
				this.mUpdateDate = dateParser.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.setShortReport(matcher.group(2));
		}
	}

}
