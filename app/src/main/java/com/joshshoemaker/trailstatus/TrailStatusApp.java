package com.joshshoemaker.trailstatus;


import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.joshshoemaker.trailstatus.di.ApplicationComponent;
import com.joshshoemaker.trailstatus.di.ApplicationModule;
import com.joshshoemaker.trailstatus.di.DaggerApplicationComponent;
import io.fabric.sdk.android.Fabric;

public class TrailStatusApp extends Application {

    private static TrailStatusApp instance;

    private ApplicationComponent component;

    public static TrailStatusApp get() {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;

        component = DaggerApplicationComponent.builder()
                        .applicationModule(new ApplicationModule(this))
                        .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }

}
