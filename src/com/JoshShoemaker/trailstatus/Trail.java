package com.JoshShoemaker.trailstatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class Trail implements Parcelable {

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
	
	private Boolean mStatusChanged;

	private String mParenName;

	private String mCondition;

	private String mLastUpdated;
	
	private Date mUpdateDate;
	
	private Calendar mPageDataUpdated;

	private String mPageName;

	private String mShortReport;
	
	private Boolean mUpdatingPageData = false;

	public Trail(String name) {
		this.setName(name);
		this.mStatus = TrailStatus.UNKNOWN;
	}
	
	public Trail(Parcel in)
	{
		readFromParcel(in);
	}

	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(mName);
		dest.writeString(mStatus.toString());
		dest.writeByte((byte) (mStatusChanged ? 1 : 0));
		dest.writeString(mParenName);
		dest.writeString(mCondition);
		dest.writeString(mLastUpdated);
		dest.writeLong(mUpdateDate == null ? -1 : mUpdateDate.getTime());
		dest.writeLong(mPageDataUpdated == null ? -1 : mPageDataUpdated.getTimeInMillis());
		dest.writeString(mPageName);
		dest.writeString(mShortReport);	
		dest.writeByte((byte) (mUpdatingPageData? 1 : 0));
	}
	
	private void readFromParcel(Parcel in)
	{
		mName = in.readString();
		this.setStatus(in.readString());
		mStatusChanged = in.readByte() == 1;
		mParenName = in.readString();
		mCondition = in.readString();
		mLastUpdated = in.readString();
		
		long val = in.readLong();
		if(val != -1)
		{
			mUpdateDate = new Date(val);
		}
		
		val = in.readLong();
		if(val != -1)
		{
			mPageDataUpdated = Calendar.getInstance();
			mPageDataUpdated.setTimeInMillis(val);
		}
		
		mPageName = in.readString();
		mShortReport = in.readString();
		mUpdatingPageData = in.readByte() == 1;
	}
	
	public TrailStatus getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		if (status.equalsIgnoreCase("open")) {
			this.setStatus(TrailStatus.OPEN);
		} else if (status.equalsIgnoreCase("closed")) {
			this.setStatus(TrailStatus.CLOSED);
		} else {
			this.setStatus(TrailStatus.UNKNOWN);
		}
	}
	
	public void setStatus(TrailStatus status)
	{
		mStatusChanged = (mStatus != TrailStatus.UNKNOWN && !mStatus.equals(status));
		
		mStatus = status;
	}

	public Boolean getStatusChanged()
	{
		return mStatusChanged;
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
	
	public void setUpdateDate(Date date)
	{
		mUpdateDate = date;
	}
	
	public void setPageDataUpdated(Calendar cal)
	{
		mPageDataUpdated = cal;
	}
	
	public void setUpdatingTrailPageData(Boolean updating)
	{
		mUpdatingPageData = updating;
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
		
		this.mPageDataUpdated = Calendar.getInstance();
	}
	
	public Boolean shouldUpdatePageData()
	{
		if(mUpdatingPageData)
		{
			return false;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
				
		if(mShortReport == null || mStatusChanged || mPageDataUpdated.before(cal))
		{
			return true;
		}
		return false;
	}
	
	public String getLastUpdatedText()
	{
		if(this.getUpdateDate() == null || this.getLastUpdated() == null)
		{
			return "";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
	    String lastUpdated = sdf.format(this.getUpdateDate()).toString();
	    
	    if(Utils.isSameDate(this.getUpdateDate(), new Date()))
	    {    	
	    	//updated within last hour?
	    	int index = this.getLastUpdated().indexOf("min") + 3;
			//min index = "1 min" = 5. max index = "59 min" = 6  
	    	if(index < 5 || index > 6)    	
	    	{
	    		
	    		//updated within the last day?
	    		index = this.getLastUpdated().indexOf("hours") + 5;        
	        	//min index = "1 hours" = 7. max index = "23 hours" = 8  
	        	if(index < 7 || index > 8)
	        	{
	        		index = -1;
	        	}
	        	
	    	}
	    	
	    	if(index > 0)
	    	{
		    	lastUpdated = this.getLastUpdated();
				lastUpdated = lastUpdated.substring(0, index);
	    	}
	    }
	    
	    return lastUpdated;
	}

	public int getStatusColor(Context context)
	{
		Trail.TrailStatus status = this.getStatus();
	    
	    int colorId = R.color.trail_open_color;
	    switch(status)
	    {
	    	case OPEN:
	    		colorId = R.color.trail_open_color;
	    		break;
	    	case CLOSED:
	    		colorId = R.color.trail_closed_color;
	    		break;
	    	case UNKNOWN:
	    		colorId = R.color.white;
	    		
	    }
	    return context.getResources().getColor(colorId);
	}
	
	public SpannableString getFormattedConditionString()
	{
		if(this.getCondition() == null || this.getShortReport() == null)
		{
			return new SpannableString("");
		}
		
		SpannableString str = new SpannableString(this.getCondition() + " - " + this.getShortReport());
	    str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, this.getCondition().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    return str;
	}

	public int describeContents()
	{
		return 0;
	}
	
	public static final Parcelable.Creator<Trail> CREATOR = new Parcelable.Creator<Trail>()
	{
		public Trail createFromParcel(Parcel in) {
			return new Trail(in);
		}
		
		public Trail[] newArray(int size) {
			return new Trail[size];
		}
	};
}

