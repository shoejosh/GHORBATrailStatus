package com.joshshoemaker.trailstatus.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailConditionReport;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
			row = inflater.inflate(R.layout.item_trail, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.trailName = (TextView) row.findViewById(R.id.trail_name);
			viewHolder.trailStatus = (TextView) row.findViewById(R.id.trail_status_condition);
			viewHolder.trailCondition = (TextView) row.findViewById(R.id.trail_condition);
			viewHolder.lastUpdated = (TextView) row.findViewById(R.id.trail_last_updated);
			
			row.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder)row.getTag();			
		
		holder.trailName.setText(trail.getName());
		holder.trailStatus.setText(trail.getStatus());
        if(trail.getStatus().equalsIgnoreCase("open")) {
            holder.trailStatus.setTextColor(context.getResources().getColor(R.color.trail_open_color));
        }
        else if (trail.getStatus().equalsIgnoreCase("closed")) {
            holder.trailStatus.setTextColor(context.getResources().getColor(R.color.trail_closed_color));
        }

        TrailConditionReport currentConditionReport = trail.getStatusReports().get(0);

        String conditionText = String.format(context.getResources().getString(R.string.trail_condition),
                currentConditionReport.getCondition(),
                currentConditionReport.getShortReport());

        holder.trailCondition.setText(Html.fromHtml(conditionText));

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.US);
        String lastUpdatedText = sdf.format(currentConditionReport.getDate());
        holder.lastUpdated.setText(lastUpdatedText);

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
