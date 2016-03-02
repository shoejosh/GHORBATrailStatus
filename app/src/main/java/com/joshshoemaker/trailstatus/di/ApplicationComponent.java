package com.joshshoemaker.trailstatus.di;

import com.joshshoemaker.trailstatus.TrailStatusApp;
import com.joshshoemaker.trailstatus.activities.TrailStatusActivity;
import com.joshshoemaker.trailstatus.widgets.TrailStatusViewsFactory;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {
    void inject(TrailStatusApp target);
    void inject(TrailStatusActivity target);
    void inject(TrailStatusViewsFactory target);
}
