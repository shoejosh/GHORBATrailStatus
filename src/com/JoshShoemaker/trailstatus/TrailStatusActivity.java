package com.JoshShoemaker.trailstatus;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TrailStatusActivity extends Activity
{
	@Override
	public void onCreate(Bundle state)
	{

		super.onCreate(state);
		setContentView(R.layout.trail_status_activity);

		ListView listView = (ListView) findViewById(R.id.trail_list);	

		TrailListAdapter adapter = new TrailListAdapter(this);
		listView.setAdapter(adapter);
		
		new GetTrailData().execute(adapter);

	}

	private class GetTrailData extends AsyncTask
	{
		
		private TrailListAdapter adapter;

		@Override
		protected Object doInBackground(Object... params)
		{
			adapter = (TrailListAdapter)params[0];
			
			List<Trail> trails = TrailDataAccess.GetAllTrails(null);			
			return trails;
		}
		
		protected void onPostExecute(Object result)
		{
			List<Trail> items = (List<Trail>)result;
			adapter.SetData(items.toArray(new Trail[items.size()]));
		}

	}
}
