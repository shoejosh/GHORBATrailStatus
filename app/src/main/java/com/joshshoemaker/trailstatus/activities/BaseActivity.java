package com.joshshoemaker.trailstatus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.joshshoemaker.trailstatus.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Josh on 6/7/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getContentView();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
    }
}
