package com.joshshoemaker.trailstatus.dal;

import com.joshshoemaker.trailstatus.models.Trail;

import java.util.List;

/**
 * Created by Josh on 2/28/2016.
 */
public interface TrailParser {

    public List<Trail> getTrailListFromHtml(String html);

    public void loadTrailDataFromHtml(Trail trail, String html);
}
