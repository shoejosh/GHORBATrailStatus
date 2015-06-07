package com.joshshoemaker.trailstatus;


import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class TrailStatusApp extends Application {

    private static TrailStatusApp sInstance;

    private RequestQueue mRequestQueue;

    public RequestQueue getRequestQueue()
    {
        return mRequestQueue;
    }

    public static TrailStatusApp get() {
        return sInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sInstance = this;

        mRequestQueue = Volley.newRequestQueue(this);
    }

}
