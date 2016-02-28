package com.joshshoemaker.trailstatus.models;

/**
 * Created by Josh on 2/28/2016.
 */
public class TrailFactoryImpl implements TrailFactory {
    @Override
    public Trail createInstance() {
        return new Trail();
    }
}
