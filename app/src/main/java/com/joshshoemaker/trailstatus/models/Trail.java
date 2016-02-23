package com.joshshoemaker.trailstatus.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.helpers.Utils;

import org.parceler.Parcel;

@Parcel
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

	String name;

	TrailStatus status;
	
	String parenName;

	String condition;

	String lastUpdated;
	
	Date updateDate;
	
	String pageName;

	String shortReport;
	
	public Trail(String name) {
		this.setName(name);
		this.status = TrailStatus.UNKNOWN;
	}
	
	public TrailStatus getStatus() {
		return status;
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
		this.status = status;
	}
	
	public void setName(String name) {
		name = name.trim();
		if (name.endsWith(")")) {
			int index = name.lastIndexOf("(");
			if (index != -1) {
				parenName = name.substring(index);
				name = name.substring(0, index).trim();
			}
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPageName() {
		return this.pageName;
	}

	public String getPageUrl() {
		return "http://ghorba.org/trails/" + pageName;
	}

	public String getShortReport() {
		return shortReport;
	}

	public void setShortReport(String shortReport) {
		this.shortReport = shortReport;
	}

	public Date getUpdateDate()
	{
		return updateDate;
	}
	
	public void setUpdateDate(Date date)
	{
		updateDate = date;
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
}

