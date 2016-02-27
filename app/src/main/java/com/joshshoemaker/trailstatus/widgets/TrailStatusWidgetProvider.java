package com.joshshoemaker.trailstatus.widgets;

import java.util.Date;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.joshshoemaker.trailstatus.R;

import java.text.SimpleDateFormat;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrailStatusWidgetProvider extends AppWidgetProvider {

	public static final String ACTION_VIEW_DATA_CHANGED = "com.JoshShoemaker.trailstatus.VIEW_DATA_CHANGED";
	public static final String ACTION_VIEW_UPDATE_FAILED = "com.JoshShoemaker.trailstatus.VIEW_UPDATE_FAILED";
	public static final String ACTION_VIEW_UPDATED = "com.JoshShoemaker.trailstatus.VIEW_DATA_UPDATED";

	//@Override
	public void onReceive(Context context, Intent intent){

		AppWidgetManager awm = AppWidgetManager.getInstance(context);
		int[] ids = awm.getAppWidgetIds(new ComponentName(context, getClass()));

		if(intent.getAction().equals(ACTION_VIEW_DATA_CHANGED))
		{
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.trail_status_appwidget);
            setWidgetProgressVisibility(true, widget);
            awm.partiallyUpdateAppWidget(ids, widget);

            awm.notifyAppWidgetViewDataChanged(ids, R.id.trail_list);
		}
		else if(intent.getAction().equals(ACTION_VIEW_UPDATED))
		{
			String time = new SimpleDateFormat("hh:mm a").format(new Date());

			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.trail_status_appwidget);
			widget.setTextViewText(R.id.widget_last_updated, time);
            setWidgetProgressVisibility(false, widget);
			awm.partiallyUpdateAppWidget(ids, widget);
		}
        else if(intent.getAction().equals(ACTION_VIEW_UPDATE_FAILED))
        {
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.trail_status_appwidget);
            setWidgetProgressVisibility(false, widget);
            awm.partiallyUpdateAppWidget(ids, widget);

            awm.partiallyUpdateAppWidget(ids, widget);
        }
		else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
		{
			//TODO: If Connection has been restored and widget has not been updated recently, update it.
		}

		
		super.onReceive(context, intent);	
	}

    private void setWidgetProgressVisibility(boolean progressVisible, RemoteViews widget) {
        if(progressVisible) {
            widget.setViewVisibility(R.id.widget_last_updated, View.GONE);
            widget.setViewVisibility(R.id.btnRefresh, View.GONE);
            widget.setViewVisibility(R.id.refreshProgress, View.VISIBLE);
        } else {
            widget.setViewVisibility(R.id.widget_last_updated, View.VISIBLE);
            widget.setViewVisibility(R.id.btnRefresh, View.VISIBLE);
            widget.setViewVisibility(R.id.refreshProgress, View.GONE);
        }
    }
	

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
    	                       
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];    
            
            Intent svcIntent = new Intent(context, WidgetService.class );
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.trail_status_appwidget);

            widget.setRemoteAdapter(R.id.trail_list, svcIntent);
            widget.setOnClickPendingIntent(R.id.btnRefresh, getPendingSelfIntent(context, ACTION_VIEW_DATA_CHANGED));
            
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent browserPendingIntent = PendingIntent.getActivity(context, 0, browserIntent, 0);
            widget.setPendingIntentTemplate(R.id.trail_list, browserPendingIntent);            
            
            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
        
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    
}