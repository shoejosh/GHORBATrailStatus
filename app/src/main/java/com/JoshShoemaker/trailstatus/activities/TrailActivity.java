package com.JoshShoemaker.trailstatus.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.JoshShoemaker.trailstatus.R;
import com.JoshShoemaker.trailstatus.helpers.GhorbaSiteActions;
import com.JoshShoemaker.trailstatus.models.Trail;

/**
 * Created by Josh on 11/23/13.
 */
public class TrailActivity extends Activity {

    private Trail mTrail = null;
    private final int LOGIN_REQUEST_CODE = 37;

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
        if(GhorbaSiteActions.isLoggedIn())
        {
            StartTrailReportActivity();
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String username = pref.getString("username", "");
        String password = pref.getString("password", "");

        if(!username.isEmpty() && !password.isEmpty())
        {
            //username and password saved, try to login.
            ProgressBar pb = (ProgressBar) findViewById(R.id.pbActivity);
            pb.setVisibility(ProgressBar.VISIBLE);

            new LoginTask().execute(username, password);
        }
        else
        {
            Intent intent = new Intent(v.getContext(), LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == LOGIN_REQUEST_CODE)
        {
            //login successful, go to trail status activity
            StartTrailReportActivity();
        }
    }

    private void StartTrailReportActivity()
    {
        if(mTrail != null)
        {
            Intent intent = new Intent(this, TrailReportActivity.class);
            intent.putExtra("trail", mTrail);
            startActivity(intent);
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... creds) {
            Boolean result = GhorbaSiteActions.login(creds[0], creds[1]);
            return result;
        }

        protected void onPostExecute(Boolean result)
        {
            ProgressBar pb = (ProgressBar) findViewById(R.id.pbActivity);
            pb.setVisibility(ProgressBar.INVISIBLE);

            if(result)
            {
                if(mTrail != null)
                {
                    Intent intent = new Intent(TrailActivity.this, TrailReportActivity.class);
                    intent.putExtra("trail", mTrail);
                    startActivity(intent);
                }
            }
            else
            {
                Intent intent = new Intent(TrailActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        }

    }
}