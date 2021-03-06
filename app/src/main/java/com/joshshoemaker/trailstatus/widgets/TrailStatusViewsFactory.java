package com.joshshoemaker.trailstatus.widgets;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.joshshoemaker.trailstatus.BuildConfig;
import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.TrailStatusApp;
import com.joshshoemaker.trailstatus.activities.TaskStackBuilderProxyActivity;
import com.joshshoemaker.trailstatus.activities.TrailActivity;
import com.joshshoemaker.trailstatus.activities.TrailStatusActivity;
import com.joshshoemaker.trailstatus.dal.TrailService;
import com.joshshoemaker.trailstatus.helpers.Utils;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailConditionReport;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrailStatusViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private static Trail[] items;
	private Context context = null;

    @Inject
    TrailService trailService;

	public TrailStatusViewsFactory(Context context)
	{
        ((TrailStatusApp)context.getApplicationContext()).getComponent().inject(this);
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

	public RemoteViews getViewAt(int position)
	{
		Trail trail = items[position];

		// setup ListItem row view
		RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.item_trail);
		row.setTextViewText(R.id.trail_name, trail.getName());
		row.setTextViewText(R.id.trail_status_condition, trail.getStatus());

        TrailConditionReport currentConditionReport = trail.getStatusReports().get(0);

        String conditionText = String.format(context.getResources().getString(R.string.trail_condition),
                currentConditionReport.getCondition(),
                currentConditionReport.getShortReport());
        row.setTextViewText(R.id.trail_condition, Html.fromHtml(conditionText));

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.US);
        String lastUpdatedText = sdf.format(currentConditionReport.getDate());
		row.setTextViewText(R.id.trail_last_updated, lastUpdatedText);

        if(trail.getStatus().equalsIgnoreCase("open")) {
            row.setInt(R.id.trail_status_condition, "setTextColor", context.getResources().getColor(R.color.trail_open_color));
        }
        else if (trail.getStatus().equalsIgnoreCase("closed")) {
            row.setInt(R.id.trail_status_condition, "setTextColor", context.getResources().getColor(R.color.trail_closed_color));
        }

		// add fill in intent for on click event
        Intent homeIntent = new Intent(context, TrailStatusActivity.class);
		Intent trailIntent = new Intent(context, TrailActivity.class);
        trailIntent.putExtra(TrailActivity.EXTRA_TRAIL_PAGE_NAME, trail.getPageName());
        Intent fillIntent = TaskStackBuilderProxyActivity.getFillIntent(homeIntent, trailIntent);
		row.setOnClickFillInIntent(R.id.trail_list_item_view, fillIntent);

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

        if(!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("AppWidget update"));
        }
        trailService.updateTrailData()
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
                            Crashlytics.logException(throwable);
                            Intent intent = new Intent(context, TrailStatusWidgetProvider.class);
                            intent.setAction(TrailStatusWidgetProvider.ACTION_VIEW_UPDATED);
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