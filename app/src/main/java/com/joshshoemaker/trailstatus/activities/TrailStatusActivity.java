package com.joshshoemaker.trailstatus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.adapters.TrailListAdapter;
import com.joshshoemaker.trailstatus.helpers.TrailDataAccess;
import com.joshshoemaker.trailstatus.models.Trail;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Josh on 6/7/2015.
 */
public class TrailStatusActivity extends BaseActivity {
    private TrailListAdapter adapter;

    //region Views
    @Bind(R.id.btnRefresh)
    ImageButton refreshBtn;

    @Bind(R.id.trail_list)
    ListView listView;

    @Bind(R.id.refreshProgress)
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
            loadData();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.putParcelable("trails", Parcels.wrap(adapter.getData()));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState == null) {
            return;
        }

        List<Trail> trails = Parcels.unwrap(savedInstanceState.getParcelable("trails"));

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
        loadData();
    }

    @OnItemClick(R.id.trail_list)
    public void onListItemClicked(int position  ) {
        Trail trail = (Trail)adapter.getItem(position);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trail.getPageUrl()));
        startActivity(browserIntent);
    }
    //endregion

    private void loadData() {
        showProgress(true);
        TrailDataAccess.GetTrailData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trails -> {
                            adapter.setData(trails);
                            showProgress(false);
                        },
                        throwable -> {}
                );
    }

    public void showProgress(Boolean showProgress)
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
