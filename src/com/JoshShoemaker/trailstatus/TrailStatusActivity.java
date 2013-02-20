package com.JoshShoemaker.trailstatus;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class TrailStatusActivity extends Activity {
  @Override
  public void onCreate(Bundle state) {
    
	  super.onCreate(state);	
	  setContentView(R.layout.trail_status_activity);
	  
	  //Intent svcIntent = new Intent(this, WidgetService.class );
      //svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));        
	  
	  //ListView listView = (ListView) findViewById(R.id.trail_list);
	  //listView.setRemoteViewsAdapter(svcIntent);
	  
  }
}