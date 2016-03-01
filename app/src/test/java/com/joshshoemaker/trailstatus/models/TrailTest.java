package com.joshshoemaker.trailstatus.models;

import com.joshshoemaker.trailstatus.R;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by Josh on 2/28/2016.
 */
public class TrailTest {

    private Trail trail;

    @Before
    public void init() {
        trail = new Trail();
    }

    @Test
    public void testSetStatus_open() {
        trail.setStatus("open");

        assertEquals(trail.getStatus(), Trail.TrailStatus.OPEN);
    }

    @Test
    public void testSetStatus_openCaseInsensitive() {
        trail.setStatus("oPeN");

        assertEquals(trail.getStatus(), Trail.TrailStatus.OPEN);
    }

    @Test
    public void testSetStatus_closed() {
        trail.setStatus("closed");

        assertEquals(trail.getStatus(), Trail.TrailStatus.CLOSED);
    }

    @Test
    public void testSetStatus_closedCaseInsensitive() {
        trail.setStatus("cLOseD");

        assertEquals(trail.getStatus(), Trail.TrailStatus.CLOSED);
    }

    @Test
    public void testSetStatus_unknown() {
        trail.setStatus("anything");

        assertEquals(trail.getStatus(), Trail.TrailStatus.UNKNOWN);
    }

    @Test
    public void testSetName_shouldTrimAndSetName() {
        trail.setName(" Name of trail  ");

        assertEquals(trail.getName(), "Name of trail");
    }

    @Test
    public void testSetName_shouldNotIncludeTextInParenthesis() {
        trail.setName("Name of trail (don't include this) ");

        assertEquals(trail.getName(), "Name of trail");
    }

    @Test
    public void testGetLastUpdatedText_shouldReturnFormattedDateIfDateIsNotToday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.US);
        String lastUpdated = sdf.format(cal.getTime());
        trail.setLastUpdated("last updated");
        trail.setUpdateDate(cal.getTime());

        assertEquals(trail.getLastUpdatedText(), lastUpdated);
    }

    @Test
    public void testGetLastUpdatedText_shouldReturnMinutesIfUpdatedInLastHour() {
        Calendar cal = Calendar.getInstance();

        trail.setLastUpdated("30 min ago");
        trail.setUpdateDate(cal.getTime());

        assertEquals(trail.getLastUpdatedText(), "30 min");
    }

    @Test
    public void testGetLastUpdatedText_shouldReturnHoursIfUpdatedInLastDay() {
        Calendar cal = Calendar.getInstance();

        trail.setLastUpdated("22 hours 42 min ago");
        trail.setUpdateDate(cal.getTime());

        assertEquals(trail.getLastUpdatedText(), "22 hours");
    }

    @Test
    public void testGetLastUpdatedText_shouldReturnEmptyWhenUpdateDateIsNull() {
        trail.setUpdateDate(null);

        assertEquals(trail.getLastUpdatedText(), "");
    }

    @Test
    public void testGetLastUpdatedText_shouldReturnEmptyWhenLastUpdatedIsNull() {
        trail.setLastUpdated(null);

        assertEquals(trail.getLastUpdatedText(), "");
    }

    @Test
    public void testGetStatusColorId_open() {
        trail.setStatus("open");

        int colorId = trail.getStatusColorId();

        assertEquals(colorId, R.color.trail_open_color);
    }

    @Test
    public void testGetStatusColorId_closed() {
        trail.setStatus("closed");

        int colorId = trail.getStatusColorId();

        assertEquals(colorId, R.color.trail_closed_color);
    }
}