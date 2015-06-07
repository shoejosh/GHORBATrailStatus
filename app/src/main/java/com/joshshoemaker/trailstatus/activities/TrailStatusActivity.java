package com.joshshoemaker.trailstatus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.adapters.TrailListAdapter;
import com.joshshoemaker.trailstatus.helpers.TrailDataAccess;
import com.joshshoemaker.trailstatus.models.Trail;

/**
 * Created by Josh on 6/7/2015.
 */
public class TrailStatusActivity extends AppCompatActivity {
    private TrailListAdapter adapter;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        setContentView(R.layout.trail_status_activity);

        ImageButton refreshBtn = (ImageButton) findViewById(R.id.btnRefresh);
        refreshBtn.setOnTouchListener(new View.OnTouchListener()
        {

            public boolean onTouch(View v, MotionEvent event)
            {
                final int action = event.getAction();

                if(action == MotionEvent.ACTION_UP)
                {
                    ShowProgress(true);
                    TrailDataAccess.LoadTrailData(adapter);
                }

                ImageButton btn = (ImageButton) v;
                return btn.onTouchEvent(event);
            }
        });

        ListView listView = (ListView) findViewById(R.id.trail_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            ShowProgress(true);
            TrailDataAccess.LoadTrailData(adapter);
        }

    }

    public void ShowProgress(Boolean showProgress)
    {
        final View refresh = findViewById(R.id.refreshProgress);
        final ListView listView = (ListView) findViewById(R.id.trail_list);

        if(showProgress)
        {
            refresh.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
        else
        {
            refresh.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
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
