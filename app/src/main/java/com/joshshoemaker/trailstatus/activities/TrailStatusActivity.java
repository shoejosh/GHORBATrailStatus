package com.joshshoemaker.trailstatus.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.adapters.TrailListAdapter;
import com.joshshoemaker.trailstatus.helpers.TrailDataAccess;
import com.joshshoemaker.trailstatus.models.Trail;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Josh on 6/7/2015.
 */
public class TrailStatusActivity extends BaseActivity {
    private TrailListAdapter adapter;

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

        this.setTitle(R.string.widget_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_refresh);
        Drawable menuIcon = DrawableCompat.wrap(item.getIcon());
        DrawableCompat.setTint(menuIcon, getResources().getColor(R.color.white));
        item.setIcon(menuIcon);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                loadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
