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

import com.joshshoemaker.trailstatus.PresenterManager;
import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.adapters.TrailListAdapter;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.presenters.TrailStatusPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;

/**
 * Created by Josh on 6/7/2015.
 */
public class TrailStatusActivity extends BaseActivity<TrailStatusPresenter> {
    private TrailListAdapter adapter;

    //region views
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

        if(state == null) {
            presenter = new TrailStatusPresenter();
        } else {
            presenter = PresenterManager.getInstance().restorePresenter(state);
        }

        adapter = new TrailListAdapter(this);
        listView.setAdapter(adapter);
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
                presenter.onRefreshClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    public void showTrails(List<Trail> trails) {
        adapter.setData(trails);
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
