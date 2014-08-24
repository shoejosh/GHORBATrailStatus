package com.JoshShoemaker.trailstatus.interfaces;


import com.JoshShoemaker.trailstatus.models.Trail;

public interface ITrailListAdapter {

    public void setData(Trail[] items);

    public Trail[] getData();

    public void trailUpdated();
}
