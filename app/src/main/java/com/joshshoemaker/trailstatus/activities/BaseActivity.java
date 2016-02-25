package com.joshshoemaker.trailstatus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.joshshoemaker.trailstatus.PresenterManager;
import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.presenters.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Josh on 6/7/2015.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {

    protected abstract int getContentView();

    protected T presenter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.bindView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        presenter.unbindView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        PresenterManager.getInstance().savePresenter(presenter, outState);
    }
}
