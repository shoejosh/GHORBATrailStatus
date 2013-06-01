package com.JoshShoemaker.trailstatus;


public interface ITrailListAdapter {

    public void setData(Trail[] items);

    public Trail[] getData();

    public void trailUpdated();
}
