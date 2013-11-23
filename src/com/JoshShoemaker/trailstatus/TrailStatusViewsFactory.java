package com.JoshShoemaker.trailstatus;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrailStatusViewsFactory implements RemoteViewsService.RemoteViewsFactory, ITrailListAdapter
{
	private static Trail[] items;
    private static int updateCount = 0;
	private Context context = null;
    private Calendar lastUpdated;

	public TrailStatusViewsFactory(Context context, Intent intent)
	{
		this.context = context;
		// appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
		// AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public int getCount()
	{
		if (items == null)
		{
			return 0;
		}

		return (items.length);
	}

	public RemoteViews getViewAt(int position)
	{
		Trail trail = items[position];

		// trail.setShortReport("A long short report to test layout wrapping stuffs and stuff, like you know? I need to fix it if it doesn't work! A long short report to test layout wrapping stuffs and stuff, like you know? I need to fix it if it doesn't work!");

		// setup ListItem row view
		RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.trail_list_item);
		row.setTextViewText(R.id.trail_name, trail.getName());
		row.setTextViewText(R.id.trail_status_condition, trail.getStatus().toString());
		row.setTextViewText(R.id.trail_last_updated, trail.getLastUpdatedText());
		row.setInt(R.id.trail_status_condition, "setTextColor", trail.getStatusColor(context));
		row.setTextViewText(R.id.trail_condition, trail.getFormattedConditionString());

        if( trail.getShortReport() == null || trail.getShortReport().equals(""))
        {
            row.setTextViewText(R.id.trail_condition, "Updating..");
        }

		// add fill in intent for on click event
		Intent i = new Intent();
        i.putExtra("trail", trail);
		row.setOnClickFillInIntent(R.id.trail_list_item_view, i);

		return (row);
	}

	public RemoteViews getLoadingView()
	{
		return (null);
	}

	public int getViewTypeCount()
	{
		return (1);
	}

	public long getItemId(int position)
	{
		return (position);
	}

	public boolean hasStableIds()
	{
		return (true);
	}

	public void onDataSetChanged()
	{

        if (!Utils.isNetworkConnected(context))
        {
            return;
        }

        List<Trail> trails = TrailDataAccess.GetAllTrails(items);
        items = trails.toArray(new Trail[trails.size()]);

        //Check if any trail page data needs to be updated
        List<Trail> trailsToUpdate =  new ArrayList<Trail>();
        for(int i=0; i<items.length; i++)
        {
            if(items[i].shouldUpdatePageData())
            {
                trailsToUpdate.add(items[i]);
            }
        }

        updateCount = trailsToUpdate.size();
        for(int i=0; i<updateCount; i++)
        {
            TrailDataAccess.LoadTrailPageData(this, trailsToUpdate.get(i));
        }

        this.lastUpdated = Calendar.getInstance();

		// Notify Widget Provider that data has been updated
		Intent intent = new Intent(context, TrailStatusWidgetProvider.class);
		intent.setAction(TrailStatusWidgetProvider.ACTION_VIEW_UPDATED);
		context.sendBroadcast(intent);
    }

	public void onCreate()
	{
		// TODO Auto-generated method stub
	}

	public void onDestroy()
	{
		// TODO Auto-generated method stub
	}

    @Override
    public void setData(Trail[] items) {

    }

    @Override
    public Trail[] getData() {
        return items;
    }

    @Override
    public synchronized void trailUpdated()
    {
        //sanity check - shouldn't happen
        if(updateCount == 0)
        {
            return;
        }

        updateCount--;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -1);

        //All Trails Page data has been updated, notify widget to update OR last update was more than 1 second ago
        if(updateCount == 0 || lastUpdated.before(cal))
        {
            Intent intent = new Intent(context, TrailStatusWidgetProvider.class);
            intent.setAction(TrailStatusWidgetProvider.ACTION_VIEW_DATA_CHANGED);
            context.sendBroadcast(intent);
        }
    }
}