package com.joshshoemaker.trailstatus;


import android.app.Application;
import com.joshshoemaker.trailstatus.api.GhorbaService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class TrailStatusApp extends Application {

    private static TrailStatusApp instance;

    private GhorbaService ghorbaService;

    public GhorbaService getGhorbaService()
    {
        return ghorbaService;
    }

    public static TrailStatusApp get() {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ghorba.org/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ghorbaService = retrofit.create(GhorbaService.class);
    }

}
