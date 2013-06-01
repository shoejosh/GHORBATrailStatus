package com.JoshShoemaker.trailstatus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class TrailStatusActivity extends Activity
{
	private TrailListAdapter adapter;
	
	@Override
	public void onCreate(Bundle state)
	{
		super.onCreate(state);
		setContentView(R.layout.trail_status_activity);
				
		ImageButton refreshBtn = (ImageButton) findViewById(R.id.btnRefresh);
		refreshBtn.setOnTouchListener(new OnTouchListener()
		{
			
			public boolean onTouch(View v, MotionEvent event)
			{
                TrailDataAccess.LoadTrailData(adapter);
				return true;
			}
		});
		
		ListView listView = (ListView) findViewById(R.id.trail_list);	
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Trail trail = (Trail)adapter.getItem(position);
				
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trail.getPageUrl()));
	            startActivity(browserIntent);				
			}			
		});
		
		adapter = new TrailListAdapter(this);
		listView.setAdapter(adapter);
		
		if(state == null)
		{
            TrailDataAccess.LoadTrailData(adapter);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		savedInstanceState.putParcelableArray("trails", adapter.getData());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);		
				
		Parcelable[] parcelableArray = savedInstanceState.getParcelableArray("trails");
		Trail[] trails = null;
		if(parcelableArray != null) 
		{
			trails = new Trail[parcelableArray.length];
			for(int i=0; i< parcelableArray.length; i++)
			{
				trails[i] = (Trail) parcelableArray[i];
			}
		}
		
		if(adapter != null)
		{			
			adapter.setData(trails);
		}
		else
		{
			//this shouldn't happen!
			onCreate(null);
		}
	}
	
}
