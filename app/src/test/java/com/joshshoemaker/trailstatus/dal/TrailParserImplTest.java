package com.joshshoemaker.trailstatus.dal;

import com.joshshoemaker.trailstatus.models.Trail;
import com.joshshoemaker.trailstatus.models.TrailFactory;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;


/**
 * Created by Josh on 2/28/2016.
 */
public class TrailParserImplTest extends TestCase {

    private TrailParserImpl trailParser;
    private Trail mockTrail;
    private String trailListPageHtml;
    private String trailPageHtml;

    public void setUp() throws Exception {
        super.setUp();

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

        trailPageHtml = "<tbody>\n" +
                "          <tr class=\"odd views-row-first\">\n" +
                "                  <td class=\"views-field views-field-field-trail-condition\">\n" +
                "            Dry/Loose          </td>\n" +
                "                  <td class=\"views-field views-field-title\">\n" +
                "            <a href=\"/trails/memorial-park/2016-02-22\">Rodeo coming to town</a>          </td>\n" +
                "                  <td class=\"views-field views-field-created\">\n" +
                "            <em class=\"placeholder\">6 days 8 hours</em> ago          </td>\n" +
                "                  <td class=\"views-field views-field-name\">\n" +
                "            <span class=\"username\">Dkoonsusaavid</span>          </td>\n" +
                "              </tr>\n" +
                "          <tr class=\"even\">\n" +
                "                  <td class=\"views-field views-field-field-trail-condition\">\n" +
                "            Dry/Packed          </td>\n" +
                "                  <td class=\"views-field views-field-title\">\n" +
                "            <a href=\"/trails/memorial-park/2016-02-17-0\">Trails are open. </a>          </td>\n" +
                "                  <td class=\"views-field views-field-created\">\n" +
                "            <em class=\"placeholder\">1 week 3 days</em> ago          </td>\n" +
                "                  <td class=\"views-field views-field-name\">\n" +
                "            <span class=\"username\">silverbiker</span>          </td>\n" +
                "              </tr>\n" +
                "          <tr class=\"even views-row-last\">\n" +
                "                  <td class=\"views-field views-field-field-trail-condition\">\n" +
                "            Muddy in spots          </td>\n" +
                "                  <td class=\"views-field views-field-title\">\n" +
                "            <a href=\"/trails/memorial-park/2016-01-23\">Trails in fair shape, Green has some structural damage, incl bridge out</a>          </td>\n" +
                "                  <td class=\"views-field views-field-created\">\n" +
                "            <em class=\"placeholder\">1 month 6 days</em> ago          </td>\n" +
                "                  <td class=\"views-field views-field-name\">\n" +
                "            <span class=\"username\">BdM</span>          </td>\n" +
                "              </tr>\n" +
                "      </tbody>";
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
        verify(mockTrail).setName(eq("Cypresswood (Collins Park)"));
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
    public void testGetTrailListFromHtml_parsesConditionCorrectly() {
        trailParser.getTrailListFromHtml(trailListPageHtml);

        verify(mockTrail).setCondition(eq("Muddy in spots"));
        verify(mockTrail).setCondition(eq("Tacky"));
    }

    @Test
    public void testGetTrailListFromHtml_parsesLastUpdatedCorrectly() {
        trailParser.getTrailListFromHtml(trailListPageHtml);

        verify(mockTrail).setLastUpdated(eq("3 days 17 hours"));
        verify(mockTrail).setLastUpdated(eq("4 weeks 1 day"));
    }

    @Test
    public void testLoadTrailDataFromHtml_parsesCorrectUpdateDate() {
        when(mockTrail.getPageName()).thenReturn("memorial-park");

        trailParser.loadTrailDataFromHtml(mockTrail, trailPageHtml);

        ArgumentCaptor<Date> argument = ArgumentCaptor.forClass(Date.class);
        verify(mockTrail).setUpdateDate(argument.capture());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(sdf.format(argument.getValue()), "2016-02-22");
    }

    @Test
    public void testLoadTrailDataFromHtml_parsesCorrectShortReport() {
        when(mockTrail.getPageName()).thenReturn("memorial-park");

        trailParser.loadTrailDataFromHtml(mockTrail, trailPageHtml);

        verify(mockTrail).setShortReport(eq("Rodeo coming to town"));
    }
}