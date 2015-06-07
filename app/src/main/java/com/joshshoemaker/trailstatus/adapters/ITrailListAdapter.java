package com.joshshoemaker.trailstatus.adapters;


import com.joshshoemaker.trailstatus.models.Trail;

public interface ITrailListAdapter {

    public void setData(Trail[] items);

    public Trail[] getData();

    public void trailUpdated();
}
