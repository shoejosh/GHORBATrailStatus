package com.joshshoemaker.trailstatus.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.joshshoemaker.trailstatus.views.ExpandableListCardView;
import com.joshshoemaker.trailstatus.PresenterManager;
import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.adapters.TrailConditionReportAdapter;
import com.joshshoemaker.trailstatus.models.TrailConditionReport;
import com.joshshoemaker.trailstatus.presenters.TrailPresenter;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Josh on 3/24/2016.
 */
public class TrailActivity extends BaseActivity<TrailPresenter> {

    public static final String EXTRA_TRAIL_PAGE_NAME = "com.joshshoemaker.trailstatus.TRAIL_PAGE_NAME";

    @Bind(R.id.expandable_trail_conditions_card)
    ExpandableListCardView expandableTrailConditionsCardView;

    @Override
    protected int getContentView() {
        return R.layout.activity_trail;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        if(state != null) {
            presenter = PresenterManager.getInstance().restorePresenter(state);
        }
        if(presenter == null) {
            String trailPageName = getIntent().getStringExtra(EXTRA_TRAIL_PAGE_NAME);
            presenter = new TrailPresenter();
            presenter.setTrailPageName(trailPageName);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String trailPageName = getIntent().getStringExtra(EXTRA_TRAIL_PAGE_NAME);
        presenter.setTrailPageName(trailPageName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setActivityTitle(String title) {
        setTitle(title);
    }

    public void setTrailConditionReports(List<TrailConditionReport> reports) {
        TrailConditionReportAdapter adapter = new TrailConditionReportAdapter(this, reports);
        expandableTrailConditionsCardView.setAdapter(adapter);
    }
}
