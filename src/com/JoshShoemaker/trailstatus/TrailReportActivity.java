package com.JoshShoemaker.trailstatus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Josh on 1/18/14.
 */
public class TrailReportActivity extends Activity {

    private Trail mTrail;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trail_report);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            mTrail = (Trail)extras.getParcelable("trail");
        }
    }

    public void btnStatusSubmitClicked(View v)
    {
        if(mTrail != null)
        {
            Spinner conditionSpinner = (Spinner) findViewById(R.id.spinnerConditions);
            EditText shortDescText = (EditText) findViewById(R.id.txtShortDescription);
            EditText descText = (EditText) findViewById(R.id.txtDescription);

            String condition = String.valueOf(conditionSpinner.getSelectedItem());
            String shortDesc = String.valueOf(shortDescText.getText());
            String desc = String.valueOf(descText.getText());

            //GhorbaSiteActions.postTrailReport(Integer.parseInt(mTrail.getId()), condition, shortDesc, desc);
            mTrail.setCondition(condition);
            mTrail.setShortReport(shortDesc);
            mTrail.setDescription(desc);
            new PostTrailStatusTask().execute(mTrail);
        }
    }

    private class PostTrailStatusTask extends AsyncTask<Trail, Void, Void> {

        protected Void doInBackground(Trail... trails) {
            GhorbaSiteActions.postTrailReport(Integer.parseInt(trails[0].getId()), trails[0].getCondition(), trails[0].getShortReport(), trails[0].getDescription());
            return null;
        }

    }
}
