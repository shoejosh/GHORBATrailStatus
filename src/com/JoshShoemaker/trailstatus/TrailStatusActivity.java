package com.JoshShoemaker.trailstatus;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
				new GetTrailData().execute(adapter); 
				return true;
			}
		});
		
		ListView listView = (ListView) findViewById(R.id.trail_list);	
		
		adapter = new TrailListAdapter(this);
		listView.setAdapter(adapter);
		
		if(state == null)
		{													
			new GetTrailData().execute(adapter);
		}

	}
	
	private class GetTrailData extends AsyncTask<Object, Integer, Object>
	{		
		@Override
		protected Object doInBackground(Object... params)
		{
			adapter = (TrailListAdapter)params[0];			
			
			List<Trail> trails = TrailDataAccess.GetAllTrails(adapter.getData());			
			return trails;
		}
		
		protected void onPostExecute(Object result)
		{
			List<Trail> items = (List<Trail>)result;
			adapter.setData(items.toArray(new Trail[items.size()]));
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
