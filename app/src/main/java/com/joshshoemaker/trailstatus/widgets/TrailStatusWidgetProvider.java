package com.joshshoemaker.trailstatus.widgets;

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
import com.joshshoemaker.trailstatus.activities.TaskStackBuilderProxyActivity;
import com.joshshoemaker.trailstatus.activities.TrailStatusActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrailStatusWidgetProvider extends AppWidgetProvider {

	public static final String ACTION_VIEW_DATA_CHANGED = "com.JoshShoemaker.trailstatus.VIEW_DATA_CHANGED";
	public static final String ACTION_VIEW_UPDATED = "com.JoshShoemaker.trailstatus.VIEW_DATA_UPDATED";

	//@Override
	public void onReceive(Context context, Intent intent){

		AppWidgetManager awm = AppWidgetManager.getInstance(context);
		int[] ids = awm.getAppWidgetIds(new ComponentName(context, getClass()));

		if(intent.getAction().equals(ACTION_VIEW_DATA_CHANGED))
		{
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.appwidget_trail_status);
            setWidgetProgressVisibility(true, widget);
            awm.partiallyUpdateAppWidget(ids, widget);

            awm.notifyAppWidgetViewDataChanged(ids, R.id.trail_list);
		}
		else if(intent.getAction().equals(ACTION_VIEW_UPDATED))
		{
			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.appwidget_trail_status);
            setWidgetProgressVisibility(false, widget);
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
            widget.setViewVisibility(R.id.btnRefresh, View.GONE);
            widget.setViewVisibility(R.id.refreshProgress, View.VISIBLE);
        } else {
            widget.setViewVisibility(R.id.btnRefresh, View.VISIBLE);
            widget.setViewVisibility(R.id.refreshProgress, View.GONE);
        }
    }
	

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
    	                       
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];    
            
            final Intent svcIntent = new Intent(context, WidgetService.class )
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            final RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.appwidget_trail_status);
            widget.setRemoteAdapter(R.id.trail_list, svcIntent);
            widget.setEmptyView(R.id.trail_list, android.R.id.empty);


            widget.setOnClickPendingIntent(R.id.btnRefresh, getPendingSelfIntent(context, ACTION_VIEW_DATA_CHANGED));

            final Intent itemClickIntent = TaskStackBuilderProxyActivity.getTemplate(context);
            final PendingIntent onClickPendingIntent = PendingIntent.getActivity(context, 0, itemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.trail_list, onClickPendingIntent);

            final Intent openAppIntent = new Intent(context, TrailStatusActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final PendingIntent openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.header, openAppPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, widget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.trail_list);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
        
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    
}