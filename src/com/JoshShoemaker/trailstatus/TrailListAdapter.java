package com.JoshShoemaker.trailstatus;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RemoteViews;
import android.widget.TextView;

public class TrailListAdapter extends BaseAdapter implements ITrailListAdapter
{
	private final Context context;
	private Trail[] items;

	public TrailListAdapter(Context context)
	{
		this.context = context;
	}

	public int getCount()
	{
		if (items == null)
		{
			return 0;
		}

		return (items.length);
	}

	public Object getItem(int position)
	{
		return items[position];
	}

	public long getItemId(int position)
	{
		return position;
	}

	static class ViewHolder {
		public TextView trailName;
		public TextView trailStatus;
		public TextView trailCondition;
		public TextView lastUpdated;
	}

	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Trail trail = items[position];
		View row = convertView;
				
		if (trail.shouldUpdatePageData())
		{
			trail.setUpdatingTrailPageData(true);
			//new GetTrailPageData().execute(this, trail);
            TrailDataAccess.LoadTrailPageData(this, trail);
		}
				
		if(row == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.trail_list_item, parent, false);		
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.trailName = (TextView) row.findViewById(R.id.trail_name);
			viewHolder.trailStatus = (TextView) row.findViewById(R.id.trail_status_condition);
			viewHolder.trailCondition = (TextView) row.findViewById(R.id.trail_condition);
			viewHolder.lastUpdated = (TextView) row.findViewById(R.id.trail_last_updated);
			
			row.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder)row.getTag();			
		
		holder.trailName.setText(trail.getName());
		holder.trailStatus.setText(trail.getStatus().toString());
		holder.trailStatus.setTextColor(trail.getStatusColor(context));			
		holder.trailCondition.setText(trail.getFormattedConditionString());
		holder.lastUpdated.setText(trail.getLastUpdatedText());		
		
		return row;
		
	}


	public void setData(Trail[] items)
	{
		this.items = items;
		this.notifyDataSetChanged();
	}
	
	public Trail[] getData()
	{
		return this.items;
	}

    @Override
    public void trailUpdated() {
        this.notifyDataSetChanged();
    }

}
