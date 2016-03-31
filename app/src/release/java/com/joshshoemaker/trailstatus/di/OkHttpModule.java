package com.joshshoemaker.trailstatus.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by Josh on 3/30/2016.
 */
@Module
public class OkHttpModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        return client;
    }
}
