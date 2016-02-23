package com.joshshoemaker.trailstatus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.models.Trail;

import java.util.List;

public class TrailListAdapter extends BaseAdapter
{
	private final Context context;
	private List<Trail> items;

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

		return (items.size());
	}

	public Object getItem(int position)
	{
		return items.get(position);
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
		Trail trail = items.get(position);
		View row = convertView;
				
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


    public void setData(List<Trail> items)
	{
		this.items = items;
		this.notifyDataSetChanged();
	}

	public List<Trail> getData()
	{
		return this.items;
	}

}
