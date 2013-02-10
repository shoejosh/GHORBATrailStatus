package com.JoshShoemaker.trailstatus;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


public class TrailStatusWidgetProvider extends AppWidgetProvider {

	public static String EXTRA_WORD=
		    "com.commonsware.android.appwidget.lorem.WORD";
	
	public static final String ACTION_VIEW_DATA_CHANGED = "com.JoshShoemaker.trailstatus.VIEW_DATA_CHANGED";

	//@Override
	public void onReceive(Context context, Intent intent){
		
		if(intent.getAction() == ACTION_VIEW_DATA_CHANGED)
		{	
			AppWidgetManager awm = AppWidgetManager.getInstance(context);		
			int[] ids = awm.getAppWidgetIds(new ComponentName(context, getClass()));			
			awm.notifyAppWidgetViewDataChanged(ids, R.id.trail_list);
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
            
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);                        
            widget.setRemoteAdapter(R.id.trail_list, svcIntent);
            widget.setOnClickPendingIntent(R.id.btnRefresh, getPendingSelfIntent(context, ACTION_VIEW_DATA_CHANGED));
            
            appWidgetManager.updateAppWidget(appWidgetId, widget);            
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.trail_list);
        }
    }
        
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    
}