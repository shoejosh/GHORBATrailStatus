package com.joshshoemaker.trailstatus;

import com.facebook.stetho.Stetho;

/**
 * Created by Josh on 3/30/2016.
 */
public class DebugTrailStatusApp extends TrailStatusApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
