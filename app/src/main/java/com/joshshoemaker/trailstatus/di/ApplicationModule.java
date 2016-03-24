package com.joshshoemaker.trailstatus.di;

import android.app.Application;
import android.content.Context;

import com.joshshoemaker.trailstatus.Constants;
import com.joshshoemaker.trailstatus.api.GhorbaService;
import com.joshshoemaker.trailstatus.dal.TrailParser;
import com.joshshoemaker.trailstatus.dal.TrailParserImpl;
import com.joshshoemaker.trailstatus.dal.TrailService;
import com.joshshoemaker.trailstatus.models.TrailFactory;
import com.joshshoemaker.trailstatus.models.TrailFactoryImpl;
import com.joshshoemaker.trailstatus.presenters.TrailStatusPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

@Module
public class ApplicationModule {


    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides @Singleton
    public TrailFactory provideTrailFactory() {
        return new TrailFactoryImpl();
    }

    @Provides @Singleton
    public GhorbaService provideGhorbaService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(GhorbaService.class);
    }

    @Provides @Singleton
    public TrailParser provideTrailParser(TrailFactory trailFactory) {
        return new TrailParserImpl(trailFactory);
    }

    @Provides @Singleton
    public TrailService provideTrailService(GhorbaService ghorbaService, TrailParser trailParser) {
        return new TrailService(ghorbaService, trailParser);
    }

    @Provides
    public TrailStatusPresenter provideTrailStatusPresenter(TrailService trailService) {
        return new TrailStatusPresenter(trailService);
    }
}
