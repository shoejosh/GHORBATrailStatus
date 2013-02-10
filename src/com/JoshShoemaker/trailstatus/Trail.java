package com.JoshShoemaker.trailstatus;

public class Trail {
	
	public enum TrailStatus { 
		OPEN, CLOSED, UNKNOWN; 
	
		@Override
		public String toString()
		{
			switch(this) {
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
	
	private String mCondition;
	
	private String mLastUpdated;
	
	public Trail(String name)
	{
		this.mName = name;
		this.mStatus = TrailStatus.UNKNOWN;
	}
	
	public TrailStatus getStatus()
	{
		return mStatus;
	}
	
	public void setStatus(String status)
	{
		if(status.equalsIgnoreCase("open"))
		{
			mStatus = TrailStatus.OPEN;
		}
		else if(status.equalsIgnoreCase("closed"))
		{
			mStatus = TrailStatus.CLOSED;
		}
		else
		{
			mStatus = TrailStatus.UNKNOWN;
		}
		
	}
	
	public void setName(String name)
	{
		mName = name;
	}
	
	public String getName()
	{
		return mName;
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

}
