package com.JoshShoemaker.trailstatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.cookie.DateUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrailStatusViewsFactory implements RemoteViewsService.RemoteViewsFactory {
  private static Trail[] items;
  private Context context=null;

  public TrailStatusViewsFactory(Context context, Intent intent) {
      this.context=context;
      //appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
  }
  
  public int getCount() {
	  if(items == null)
	  {
		  return 0;
	  }
	  
	  return(items.length);
  }

  public RemoteViews getViewAt(int position) {    
    
    Trail trail = items[position];
    
    if(trail.shouldUpdatePageData())
    {
		if (Utils.isNetworkConnected(context)) {
			TrailDataAccess.LoadPageData(trail);
		}	
    }
    
    //trail.setShortReport("A long short report to test layout wrapping stuffs and stuff, like you know? I need to fix it if it doesn't work! A long short report to test layout wrapping stuffs and stuff, like you know? I need to fix it if it doesn't work!");
       
    //setup ListItem row view
    RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.trail_list_item);
    row.setTextViewText(R.id.trail_name, trail.getName());             
    row.setTextViewText(R.id.trail_status_condition, trail.getStatus().toString() );
    row.setTextViewText(R.id.trail_last_updated, trail.getLastUpdatedText());
    row.setInt(R.id.trail_status_condition, "setTextColor", trail.getStatusColor(context));             
    row.setTextViewText(R.id.trail_condition, trail.getFormattedConditionString());             
    
            
    //add fill in intent for on click event
    Intent i = new Intent();    
    i.setData(Uri.parse(trail.getPageUrl()));
    
    //Bundle extras=new Bundle();
    //extras.putString(ExampleAppWidgetProvider.EXTRA_WORD, items[position]);
    //i.putExtras(extras);
    
    row.setOnClickFillInIntent(R.id.trail_list_item_view, i);

    return(row);
  }

  public RemoteViews getLoadingView() {
    return(null);
  }
  
  public int getViewTypeCount() {
    return(1);
  }

  public long getItemId(int position) {
    return(position);
  }

  public boolean hasStableIds() {
    return(true);
  }

  public void onDataSetChanged() {
	  
	  if(!Utils.isNetworkConnected(context))
	  {
		  return;
	  }
	  
	  List<Trail> trails = TrailDataAccess.GetAllTrails(items);	 
	  
	  items = trails.toArray(new Trail[trails.size()]);	  
	  
	  //Notify Widget Provider that data has been updated	  
	  Intent intent = new Intent(context, TrailStatusWidgetProvider.class);
	  intent.setAction(TrailStatusWidgetProvider.ACTION_VIEW_UPDATED);
	  context.sendBroadcast(intent);	  
  }

  public void onCreate() {
	// TODO Auto-generated method stub	
  }

  public void onDestroy() {
	// TODO Auto-generated method stub
	
  }
}