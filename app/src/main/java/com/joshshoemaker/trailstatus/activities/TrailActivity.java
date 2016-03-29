package com.joshshoemaker.trailstatus.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.joshshoemaker.trailstatus.views.ExpandableListCardView;
import com.joshshoemaker.trailstatus.PresenterManager;
import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.adapters.TrailConditionReportAdapter;
import com.joshshoemaker.trailstatus.models.TrailConditionReport;
import com.joshshoemaker.trailstatus.presenters.TrailPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Josh on 3/24/2016.
 */
public class TrailActivity extends BaseActivity<TrailPresenter> {

    public static final String EXTRA_TRAIL_PAGE_NAME = "com.joshshoemaker.trailstatus.TRAIL_PAGE_NAME";

    private static final int DESCRIPTION_MAX_LINES = 3;
    private boolean descriptionExpanded = false;

    //region Views

    @Bind(R.id.expandable_trail_conditions_card)
    ExpandableListCardView expandableTrailConditionsCardView;

    @Bind(R.id.status)
    TextView statusTextView;

    @Bind(R.id.description)
    TextView descriptionTextView;

    @Bind(R.id.difficulty)
    TextView difficultyTextView;

    @Bind(R.id.technical_rating)
    TextView technicalRatingTextView;

    @Bind(R.id.length)
    TextView lengthTextView;

    @Bind(R.id.description_row)
    View descriptionRow;

    @Bind(R.id.length_row)
    View lengthRow;

    @Bind(R.id.difficulty_row)
    View difficultyRow;

    @Bind(R.id.technical_rating_row)
    View technicalRatingRow;

    //endregion

    //region Activity Events

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

    //endregion

    @OnClick(R.id.description_row)
    public void onDescriptionClicked() {
        if(!descriptionExpanded) {
            descriptionTextView.setMaxLines(Integer.MAX_VALUE);
            descriptionTextView.setEllipsize(null);
            descriptionExpanded = true;
        } else {
            descriptionTextView.setMaxLines(DESCRIPTION_MAX_LINES);
            descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
            descriptionExpanded = false;
        }
    }

    public void setActivityTitle(String title) {
        setTitle(title);
    }

    public void setTrailConditionReports(List<TrailConditionReport> reports) {
        TrailConditionReportAdapter adapter = new TrailConditionReportAdapter(this, reports);
        expandableTrailConditionsCardView.setAdapter(adapter);
    }

    public void setStatus(String status) {
        statusTextView.setText(status);

        if(status.equalsIgnoreCase("open")) {
            statusTextView.setTextColor(getResources().getColor(R.color.trail_open_color));
        }
        else if (status.equalsIgnoreCase("closed")) {
            statusTextView.setTextColor(getResources().getColor(R.color.trail_closed_color));
        }
    }

    public void setDescription(String description) {
        if(description == null || description.length() == 0) {
            descriptionRow.setVisibility(View.GONE);
        } else {
            descriptionTextView.setText(description);
        }
    }

    public void setDifficulty(String difficulty) {
        if(difficulty == null || difficulty.length() == 0) {
            difficultyRow.setVisibility(View.GONE);
        } else {
            difficultyTextView.setText(difficulty);
        }
    }

    public void setLength(String length) {
        if(length == null || length.length() == 0) {
            lengthRow.setVisibility(View.GONE);
        } else {
            lengthTextView.setText(length);
        }
    }

    public void setTechnicalRating(String technicalRating) {
        if(technicalRating == null || technicalRating.length() == 0) {
            technicalRatingRow.setVisibility(View.GONE);
        } else {
            technicalRatingTextView.setText(technicalRating);
        }
    }
}
