package com.JoshShoemaker.trailstatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.cookie.DateUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
	  if(items == null)
	  {
		  return 0;
	  }
	  
	  return(items.length);
  }

  public RemoteViews getViewAt(int position) {
    RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.trail_list_item);
    
    Trail trail = items[position];
    
    if(trail.shouldUpdatePageData())
    {
    	loadPageData(trail);    	
    }
    
    //trail.setShortReport("A long short report to test layout wrapping stuffs and stuff, like you know? I need to fix it if it doesn't work! A long short report to test layout wrapping stuffs and stuff, like you know? I need to fix it if it doesn't work!");
        
    row.setTextViewText(R.id.trail_name, trail.getName());             
    row.setTextViewText(R.id.trail_status_condition, trail.getStatus().toString() );
             
    SpannableString str = new SpannableString(trail.getCondition() + " - " + trail.getShortReport());
    str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, trail.getCondition().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    row.setTextViewText(R.id.trail_condition, str);
    
    SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
    String lastUpdated = sdf.format(trail.getUpdateDate()).toString();
    
    if(Utils.isSameDate(trail.getUpdateDate(), new Date()))
    {    	
    	//updated within last hour?
    	int index = trail.getLastUpdated().indexOf("min") + 3;
		//min index = "1 min" = 5. max index = "59 min" = 6  
    	if(index < 5 || index > 6)    	
    	{
    		
    		//updated within the last day?
    		index = trail.getLastUpdated().indexOf("hours") + 5;        
        	//min index = "1 hours" = 7. max index = "23 hours" = 8  
        	if(index < 7 || index > 8)
        	{
        		index = -1;
        	}
        	
    	}
    	
    	if(index > 0)
    	{
	    	lastUpdated = trail.getLastUpdated();
			lastUpdated = lastUpdated.substring(0, index);
    	}
    }
    row.setTextViewText(R.id.trail_last_updated, lastUpdated);
    
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
    
            
    Intent i = new Intent();
    
    i.setData(Uri.parse(trail.getPageUrl()));
    
    //Bundle extras=new Bundle();
    //extras.putString(ExampleAppWidgetProvider.EXTRA_WORD, items[position]);
    //i.putExtras(extras);
    
    row.setOnClickFillInIntent(R.id.trail_list_item_view, i);

    return(row);
  }

  private void loadPageData(Trail trail) {
	  
	  if(!Utils.isNetworkConnected(context))
	  {
		  return;
	  }
	  
	  String page = "";
	  Date date = new Date();
	  
	  try {
		  page = PageScraper.getUrlContent(trail.getPageUrl() + "?val=" + date.getTime());
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	  
	  trail.loadTrailPageData(page);
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
	  
	  List<Trail> trails = new ArrayList<Trail>();
	  
	  String page = "";
	  Date date = new Date();
	  
	  try {
		  page = PageScraper.getUrlContent("http://ghorba.org/trails" + "?val=" + date.getTime());
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	
	  String regex = "<tr.*?field-title.*?<a href=\"/trails/(.*?)\">(.*?)</a>.*?trail-status.*?<a.*?>(.*?)</a>.*?trail-condition.*?>(.*?)</td>.*?<em.*?>(.*?)<.*?</tr>";
	  Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
	  Matcher matcher = pattern.matcher(page);
	  
	  int count = 0;
	  while(matcher.find())
	  {	
		  Trail trail = null;
		  
		  if(items != null && items.length > count)
		  {
			  Trail prevTrail = (Trail)items[count];
			  if(prevTrail.getPageName().equals(matcher.group(1)))
			  {
				  trail = prevTrail;
			  }
		  }
		  
		  if(trail == null)
		  {
			  trail = new Trail(matcher.group(2).trim());
		  }
			  		  
		  trail.setPageName(matcher.group(1));
		  trail.setStatus(matcher.group(3).replace('\n', ' ').trim());
		  trail.setCondition(matcher.group(4).replace('\n', ' ').trim());
		  trail.setLastUpdated(matcher.group(5).replace('\n', ' ').trim());
		  
		  trails.add(trail);
			  
		  count++;
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