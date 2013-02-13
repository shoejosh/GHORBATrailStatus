package com.JoshShoemaker.trailstatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class TrailStatusViewsFactory implements RemoteViewsService.RemoteViewsFactory {
  private static Trail[] items;
  private Context context=null;

  public TrailStatusViewsFactory(Context context, Intent intent) {
      this.context=context;
      //appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
  }
  
  public int getCount() {
	  return(items.length);
  }

  public RemoteViews getViewAt(int position) {
    RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.trail_list_item);
    
    Trail trail = items[position];
    
    row.setTextViewText(R.id.trail_name, trail.getName());     
    row.setTextViewText(R.id.trail_last_updated, trail.getLastUpdated());
    row.setTextViewText(R.id.trail_status_condition, trail.getStatus().toString() + " - " + trail.getCondition());
    
    Trail.TrailStatus status = trail.getStatus();
    
    int colorId = R.color.trail_open_color;
    switch(status)
    {
    	case OPEN:
    		colorId = R.color.trail_open_color;
    		break;
    	case CLOSED:
    		colorId = R.color.trail_closed_color;
    		break;
    	case UNKNOWN:
    		colorId = R.color.white;
    		
    }
    int color = this.context.getResources().getColor(colorId);
    
    row.setInt(R.id.trail_status_condition, "setTextColor", color);
    //row.setInt(R.id.trail_list_item_view, "setBackgroundResource", colorId);
    
            
    Intent i = new Intent();
    //Bundle extras=new Bundle();
    
    i.setData(Uri.parse(trail.getPageUrl()));
    
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
	  
	  List<Trail> trails = new ArrayList<Trail>();
	  
	  String page = "";
	  Date date = new Date();
	  
	  try {
		  page = PageScraper.getUrlContent("http://ghorba.org/trails" + "?val=" + date.getTime());
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	
	  String regex = "<tr.*?field-title.*?<a href=\"/trails(.*?)\">(.*?)</a>.*?trail-status.*?<a.*?>(.*?)</a>.*?trail-condition.*?>(.*?)</td>.*?<em.*?>(.*?)<.*?</tr>";
	  Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
	  Matcher matcher = pattern.matcher(page);
	  while(matcher.find())
	  {	
		  Trail trail = new Trail(matcher.group(2).trim());
		  trail.setPageName(matcher.group(1));
		  trail.setStatus(matcher.group(3).replace('\n', ' ').trim());
		  trail.setCondition(matcher.group(4).replace('\n', ' ').trim());
		  trail.setLastUpdated(matcher.group(5).replace('\n', ' ').trim());
		  
		  trails.add(trail);
	  }
	  
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