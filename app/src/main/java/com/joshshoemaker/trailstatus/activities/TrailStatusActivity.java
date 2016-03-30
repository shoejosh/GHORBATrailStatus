package com.joshshoemaker.trailstatus.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.joshshoemaker.trailstatus.PresenterManager;
import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.TrailStatusApp;
import com.joshshoemaker.trailstatus.adapters.TrailListAdapter;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.presenters.TrailStatusPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;

/**
 * Created by Josh on 6/7/2015.
 */
public class TrailStatusActivity extends BaseActivity<TrailStatusPresenter>
{
    private TrailListAdapter adapter;

    //region views
    @Bind(R.id.trail_list)
    ListView listView;

    @Bind(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    //endregion

    //region Activity Events
    @Override
    protected int getContentView() {
        return R.layout.activity_trail_status;
    }

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);

        if(state != null) {
            presenter = PresenterManager.getInstance().restorePresenter(state);
        }
        if(presenter == null) {
            ((TrailStatusApp) getApplication()).getComponent().inject(this);
        }

        adapter = new TrailListAdapter(this);
        listView.setAdapter(adapter);
        this.setTitle(R.string.widget_title);

        swipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    presenter.onRefreshClicked();
                }
            }
        );
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

        Intent intent = new Intent(this, TrailActivity.class);
        intent.putExtra(TrailActivity.EXTRA_TRAIL_PAGE_NAME, trail.getPageName());
        startActivity(intent);
    }

    //endregion

    public void showTrails(List<Trail> trails) {
        adapter.setData(trails);
    }

    public void showProgress(Boolean showProgress)
    {
        //workaround for SwipeRefreshLayout.setRefreshing not working during activity initialization
        //http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(showProgress);
            }
        });
    }

    public void showNetworkError() {
        Toast.makeText(TrailStatusActivity.this, "Error loading trail data. Please try again later.", Toast.LENGTH_LONG).show();
    }
}
