package com.JoshShoemaker.trailstatus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Josh on 11/23/13.
 */
public class TrailActivity extends Activity {

    private Trail mTrail = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trail_activity);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            mTrail = (Trail)extras.getParcelable("trail");
        }
    }


    public void btnTrailReportClicked(View v)
    {
        if(mTrail != null)
        {
            Intent intent = new Intent(v.getContext(), TrailReportActivity.class);
            intent.putExtra("trail", mTrail);
            startActivity(intent);
        }
    }

    public void btnTrailWebpageClicked(View v)
    {
        if(mTrail != null)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTrail.getPageUrl()));
            startActivity(browserIntent);
        }
    }
}