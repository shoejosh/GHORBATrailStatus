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
import android.widget.RemoteViews;

import com.joshshoemaker.trailstatus.R;

import java.text.SimpleDateFormat;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrailStatusWidgetProvider extends AppWidgetProvider {

	public static String EXTRA_WORD=
		    "com.commonsware.android.appwidget.lorem.WORD";
	
	public static final String ACTION_VIEW_DATA_CHANGED = "com.JoshShoemaker.trailstatus.VIEW_DATA_CHANGED";
	
	public static final String ACTION_VIEW_UPDATED = "com.JoshShoemaker.trailstatus.VIEW_DATA_UPDATED";

	//@Override
	public void onReceive(Context context, Intent intent){
		
		AppWidgetManager awm = AppWidgetManager.getInstance(context);		
		int[] ids = awm.getAppWidgetIds(new ComponentName(context, getClass()));	
		
		if(intent.getAction() == ACTION_VIEW_DATA_CHANGED)
		{
            awm.notifyAppWidgetViewDataChanged(ids, R.id.trail_list);
		}
		else if(intent.getAction() == ACTION_VIEW_UPDATED)
		{			
			String time = new SimpleDateFormat("hh:mm a").format(new Date());
			
			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.trail_status_appwidget);   
			widget.setTextViewText(R.id.widget_last_updated, time);
			awm.partiallyUpdateAppWidget(ids, widget);
		}
		else if(intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION)
		{
			//TODO: If Connection has been restored and widget has not been updated recently, update it.
		}
		
		super.onReceive(context, intent);	
	}
	

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
    	                       
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];    
            
            Intent svcIntent = new Intent(context, WidgetService.class );
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,  appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));           
            
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.trail_status_appwidget);         
            
            widget.setRemoteAdapter(R.id.trail_list, svcIntent);
            widget.setOnClickPendingIntent(R.id.btnRefresh, getPendingSelfIntent(context, ACTION_VIEW_DATA_CHANGED));
            
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent browserPendingIntent = PendingIntent.getActivity(context, 0, browserIntent, 0);
            widget.setPendingIntentTemplate(R.id.trail_list, browserPendingIntent);            
            
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