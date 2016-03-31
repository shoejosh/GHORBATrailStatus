package com.joshshoemaker.trailstatus;

import android.app.Application;
import com.joshshoemaker.trailstatus.di.ApplicationComponent;
import com.joshshoemaker.trailstatus.di.ApplicationModule;
import com.joshshoemaker.trailstatus.di.DaggerApplicationComponent;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
        instance = this;

        component = DaggerApplicationComponent.builder()
                        .applicationModule(new ApplicationModule(this))
                        .build();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("trailstatus.realm")
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public ApplicationComponent getComponent() {
        return component;
    }

}
