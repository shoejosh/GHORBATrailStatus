package com.JoshShoemaker.trailstatus.helpers;


import com.JoshShoemaker.trailstatus.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GhorbaSiteActions {

    private static HttpClient httpClient;
    private static HttpContext localContext;

    private static Boolean login(String username, String password)
    {
        String html = "";

        try {

            //Load page to get form build id

            HttpGet get = new HttpGet(Constants.GHORBA_URL + "trails");
            HttpResponse getResponse = httpClient.execute(get, localContext);
            html = EntityUtils.toString(getResponse.getEntity());
            String formBuildId = getInputValueByName(html, "form_build_id");
            if(formBuildId == null)
            {
                return false;
            }


            HttpPost post = new HttpPost(Constants.GHORBA_URL + "trails?destination=trails");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name", username));
            nameValuePairs.add(new BasicNameValuePair("pass", password));
            nameValuePairs.add(new BasicNameValuePair("form_build_id", formBuildId ));
            nameValuePairs.add(new BasicNameValuePair("form_id", "user_login_block"));
            nameValuePairs.add(new BasicNameValuePair("op", "Log in"));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(post, localContext);

            html = EntityUtils.toString(response.getEntity()).toLowerCase();
            if(html.contains("unrecognized username or password"))
            {
                //invalid username or password
                return false;
            }

            if(!html.contains("<a href=\"/user/logout\">"))
            {
                //?
                return false;
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static String getInputValueByName(String page, String inputName)
    {
        String regex = "<input.*name=\"" + inputName + "\".*value=\"(.*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(page);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String postTrailReport(int trailId, String condition, String shortDescription, String description)
    {
        httpClient = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        if(!login("tjs", "biatch"))
        {
            //handle login failed
            return "Failed to login to GHORBA website";
        }

        String html = "";
        try {

            //Load page to get form build id
            HttpGet get = new HttpGet(Constants.GHORBA_URL + "node/add/trail-condition-report");
            HttpResponse getResponse = httpClient.execute(get, localContext);
            html = EntityUtils.toString(getResponse.getEntity());
            String formBuildId = getInputValueByName(html, "form_build_id");
            String formToken = getInputValueByName(html, "form_token");


            HttpPost post = new HttpPost(Constants.GHORBA_URL + "node/add/trail-condition-report");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("field_trail_node[und]", Integer.toString(trailId) ));
            nameValuePairs.add(new BasicNameValuePair("form_build_id", formBuildId ));
            nameValuePairs.add(new BasicNameValuePair("form_token", formToken ));
            nameValuePairs.add(new BasicNameValuePair("form_id", "trail_condition_report_node_form"));
            nameValuePairs.add(new BasicNameValuePair("field_trail_condition[und]", condition));
            nameValuePairs.add(new BasicNameValuePair("title", shortDescription));
            nameValuePairs.add(new BasicNameValuePair("body[und][0][value]", description));
            nameValuePairs.add(new BasicNameValuePair("body[und][0][format]", "plain_text"));
            nameValuePairs.add(new BasicNameValuePair("op", "Save"));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(post, localContext);

            html = EntityUtils.toString(response.getEntity());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
