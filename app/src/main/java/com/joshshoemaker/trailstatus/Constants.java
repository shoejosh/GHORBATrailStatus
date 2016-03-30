package com.joshshoemaker.trailstatus;

/**
 * Created by Josh on 3/23/2016.
 */
public final class Constants {
    private Constants() {
        //private constructor so it can't be instantiated
    }

    public static final String BASE_URL = "http://ghorba.org/";
    public static final String TRAILS_URL = BASE_URL + "trails/";

    public static final long STALE_DATA_AGE = 1000 * 60 * 60 * 4; //4 hours

    //region Shared Preferences
    public static final String PREFERENCE_FILE_KEY = "com.joshshoemaker.trailstatus.PREFERENCE_FILE_KEY";
    public static final String PREFERENCE_UPDATE_TIME_KEY = "update_time";
}
