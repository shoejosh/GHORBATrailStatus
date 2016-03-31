package com.joshshoemaker.trailstatus;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Josh on 3/30/2016.
 */
public class ReleaseTrailStatusApp extends TrailStatusApp {
    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
    }
}
