package com.joshshoemaker.trailstatus.widgets;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.TrailStatusApp;
import com.joshshoemaker.trailstatus.dal.TrailParserImpl;
import com.joshshoemaker.trailstatus.dal.TrailService;
import com.joshshoemaker.trailstatus.helpers.Utils;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailFactoryImpl;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrailStatusViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private static Trail[] items;
	private Context context = null;

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

		// setup ListItem row view
		RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.trail_list_item);
		row.setTextViewText(R.id.trail_name, trail.getName());
		row.setTextViewText(R.id.trail_status_condition, trail.getStatus().toString());
		row.setTextViewText(R.id.trail_last_updated, trail.getLastUpdatedText());
		row.setInt(R.id.trail_status_condition, "setTextColor", context.getResources().getColor(trail.getStatusColorId()));
		row.setTextViewText(R.id.trail_condition, trail.getFormattedConditionString());

        if( trail.getShortReport() == null || trail.getShortReport().equals(""))
        {
            row.setTextViewText(R.id.trail_condition, "Updating..");
        }

		// add fill in intent for on click event
		Intent i = new Intent();
		i.setData(Uri.parse(trail.getPageUrl()));
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

        TrailService trailService = new TrailService(TrailStatusApp.get().getGhorbaService(), new TrailParserImpl(new TrailFactoryImpl()));

        trailService.getTrailData()
                .retry(2) //retry up to 2 times on error
                .toBlocking() //need to block so widget doesn't try to update the list before the operation completes
				.subscribe(
                        trails -> {
                            items = trails.toArray(new Trail[trails.size()]);

                            // Notify Widget Provider that data has been updated
                            Intent intent = new Intent(context, TrailStatusWidgetProvider.class);
                            intent.setAction(TrailStatusWidgetProvider.ACTION_VIEW_UPDATED);
                            context.sendBroadcast(intent);
                        },
                        throwable -> {
                            // Data hasn't been updated but notify to stop showing progress spinner.
                            Intent intent = new Intent(context, TrailStatusWidgetProvider.class);
                            intent.setAction(TrailStatusWidgetProvider.ACTION_VIEW_UPDATE_FAILED);
                            context.sendBroadcast(intent);
                        }
                );
    }

	public void onCreate()
	{
		// TODO Auto-generated method stub
	}

	public void onDestroy()
	{
		// TODO Auto-generated method stub
	}
}