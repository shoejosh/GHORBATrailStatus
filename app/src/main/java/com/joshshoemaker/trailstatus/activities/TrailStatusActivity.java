package com.joshshoemaker.trailstatus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.adapters.TrailListAdapter;
import com.joshshoemaker.trailstatus.helpers.TrailDataAccess;
import com.joshshoemaker.trailstatus.models.Trail;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Josh on 6/7/2015.
 */
public class TrailStatusActivity extends BaseActivity {
    private TrailListAdapter adapter;

    //region Views
    @InjectView(R.id.btnRefresh)
    ImageButton refreshBtn;

    @InjectView(R.id.trail_list)
    ListView listView;

    @InjectView(R.id.refreshProgress)
    View refresh;
    //endregion

    //region Activity Events

    @Override
    protected int getContentView() {
        return R.layout.trail_status_activity;
    }

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);

        adapter = new TrailListAdapter(this);
        listView.setAdapter(adapter);

        if(state == null)
        {
            ShowProgress(true);
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
    //endregion

    //region View Events
    @OnClick(R.id.btnRefresh)
    public void onRefeshClicked() {
        ShowProgress(true);
        TrailDataAccess.LoadTrailData(adapter);
    }

    @OnItemClick(R.id.trail_list)
    public void onListItemClicked(int position  ) {
        Trail trail = (Trail)adapter.getItem(position);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trail.getPageUrl()));
        startActivity(browserIntent);
    }
    //endregion

    public void ShowProgress(Boolean showProgress)
    {
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
}
