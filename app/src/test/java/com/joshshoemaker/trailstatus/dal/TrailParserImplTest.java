package com.joshshoemaker.trailstatus.dal;

import com.google.common.io.Files;
import com.joshshoemaker.trailstatus.TestUtils;
import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailFactory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.*;


/**
 * Created by Josh on 2/28/2016.
 */
public class TrailParserImplTest {

    private TrailParserImpl trailParser;
    private Trail mockTrail;
    private String trailListPageHtml;
    private String trailPageHtml;

    @Before
    public void init() throws Exception {
        TrailFactory mockTrailFactory = mock(TrailFactory.class);
        mockTrail = mock(Trail.class);
        trailParser = new TrailParserImpl(mockTrailFactory);
        when(mockTrailFactory.createInstance()).thenReturn(mockTrail);

        trailListPageHtml = "<tbody>\n" +
                "          <tr class=\"odd trail-Open views-row-first\">\n" +
                "                  <td class=\"views-field views-field-title\">\n" +
                "            <a href=\"/trails/double-lake-recreation-area\">Double Lake Recreation Area</a>          </td>\n" +
                "                  <td class=\"views-field views-field-field-trail-status\">\n" +
                "            <a href=\"/trails/open\">Open</a>          </td>\n" +
                "                  <td class=\"views-field views-field-field-trail-condition\">\n" +
                "            Muddy in spots          </td>\n" +
                "                  <td class=\"views-field views-field-changed\">\n" +
                "            <em class=\"placeholder\">3 days 17 hours</em> ago          </td>\n" +
                "                  <td class=\"views-field views-field-name\">\n" +
                "            <span class=\"username\">jmackey</span>          </td>\n" +
                "              </tr>\n" +
                "          <tr class=\"even trail-Open\">\n" +
                "                  <td class=\"views-field views-field-title\">\n" +
                "            <a href=\"/trails/cypresswood-collins-park\">Cypresswood (Collins Park)</a>          </td>\n" +
                "                  <td class=\"views-field views-field-field-trail-status\">\n" +
                "            <a href=\"/trails/open\">Open</a>          </td>\n" +
                "                  <td class=\"views-field views-field-field-trail-condition\">\n" +
                "            Tacky          </td>\n" +
                "                  <td class=\"views-field views-field-changed\">\n" +
                "            <em class=\"placeholder\">4 weeks 1 day</em> ago          </td>\n" +
                "                  <td class=\"views-field views-field-name\">\n" +
                "            <span class=\"username\">mustang65</span>          </td>\n" +
                "              </tr>\n" +
                "          \n" +
                "      </tbody>";

        File file = TestUtils.getFileFromPath(this, "trail.html");
        trailPageHtml = TestUtils.getStringFromFile(file);
    }

    @Test
    public void testGetTrailListFromHtml_returnsCorrectNumberOfTrails() {
        List<Trail> trails = trailParser.getTrailListFromHtml(trailListPageHtml);

        assertEquals(trails.size(), 2);
    }

    @Test
    public void testGetTrailListFromHtml_parsesTrailNameCorrectly() {
        trailParser.getTrailListFromHtml(trailListPageHtml);

        verify(mockTrail).setName(eq("Double Lake Recreation Area"));
        verify(mockTrail).setName(eq("Cypresswood"));
    }

    @Test
    public void testGetTrailListFromHtml_parsesTrailPageNameCorrectly() {
        trailParser.getTrailListFromHtml(trailListPageHtml);

        verify(mockTrail).setPageName(eq("double-lake-recreation-area"));
        verify(mockTrail).setPageName(eq("cypresswood-collins-park"));
    }

    @Test
    public void testGetTrailListFromHtml_parsesStatusCorrectly() {
        trailParser.getTrailListFromHtml(trailListPageHtml);

        verify(mockTrail, times(2)).setStatus(eq("Open"));
    }

    @Test
    public void testLoadTrailDataFromHtml_parsesDifficultyCorrectly() {
        trailParser.loadTrailDataFromHtml(mockTrail, trailPageHtml);

        verify(mockTrail).setDifficulty("Intermediate, Expert");
    }

    @Test
    public void testLoadTrailDataFromHtml_parsesLengthCorrectly() {
        trailParser.loadTrailDataFromHtml(mockTrail, trailPageHtml);

        verify(mockTrail).setLength("6.50 miles");
    }

    @Test
    public void testLoadTrailDataFromHtml_parsesTechnicalRatingCorrectly() {
        trailParser.loadTrailDataFromHtml(mockTrail, trailPageHtml);

        verify(mockTrail).setTechnicalRating("2-4(TTF's)");
    }

    @Test
    public void testLoadTrailDataFromHtml_parsesDescriptionCorrectly() {
        trailParser.loadTrailDataFromHtml(mockTrail, trailPageHtml);

        verify(mockTrail).setDescription("Jack Brooks Park offers some of the most challenging trails in the Houston Area. For you city and northern Houston dwellers, it’s worth a weekend drive down to Hitchcock. The maintenance crew led by the most excellent trail steward, Booker, does an incredible job of maintaining the trail and adding Technical Trail Features (TTFs) that make for a fun ride, in full cooperation with the the Galveston Country Parks department. Trails are one way please follow arrows & try not to go backwards.");
    }


    /*@Test
    public void testLoadTrailDataFromHtml_parsesCorrectUpdateDate() {
        when(mockTrail.getPageName()).thenReturn("memorial-park");

        trailParser.loadTrailDataFromHtml(mockTrail, trailPageHtml);

        ArgumentCaptor<Date> argument = ArgumentCaptor.forClass(Date.class);
        verify(mockTrail).setDate(argument.capture());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(sdf.format(argument.getValue()), "2016-02-22");
    }*/
}