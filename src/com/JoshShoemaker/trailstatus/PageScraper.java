package com.JoshShoemaker.trailstatus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.JoshShoemaker.trailstatus.PageScraper.ApiException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class PageScraper {

private static final String TAG = "SimpleWikiHelper";
    
    /**
     * Regular expression that splits "Word of the day" entry into word
     * name, word type, and the first description bullet point.
     */
    public static final String WORD_OF_DAY_REGEX =
        "(?s)\\{\\{wotd\\|(.+?)\\|(.+?)\\|([^#\\|]+).*?\\}\\}";
    
    /**
     * Partial URL to use when requesting the detailed entry for a specific
     * Wiktionary page. Use {@link String#format(String, Object...)} to insert
     * the desired page title after escaping it as needed.
     */
    private static final String WIKTIONARY_PAGE =
        "http://en.wiktionary.org/w/api.php?action=query&prop=revisions&titles=%s&" +
        "rvprop=content&format=json%s";

    /**
     * Partial URL to append to {@link #WIKTIONARY_PAGE} when you want to expand
     * any templates found on the requested page. This is useful when browsing
     * full entries, but may use more network bandwidth.
     */
    private static final String WIKTIONARY_EXPAND_TEMPLATES =
        "&rvexpandtemplates=true";

    /**
     * {@link StatusLine} HTTP status code when no server error has occurred.
     */
    private static final int HTTP_STATUS_OK = 200;

    /**
     * Shared buffer used by {@link #getUrlContent(String)} when reading results
     * from an API request.
     */
    private static byte[] sBuffer = new byte[512];
    
    /**
     * User-agent string to use when making requests. Should be filled using
     * {@link #prepareUserAgent(Context)} before making any other calls.
     */
    private static String sUserAgent = null;
    
    /**
     * Thrown when there were problems contacting the remote API server, either
     * because of a network error, or the server returned a bad status code.
     */
    public static class ApiException extends Exception {
        public ApiException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
        
        public ApiException(String detailMessage) {
            super(detailMessage);
        }
    }

    /**
     * Thrown when there were problems parsing the response to an API call,
     * either because the response was empty, or it was malformed.
     */
    public static class ParseException extends Exception {
        public ParseException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }
	
    /**
     * Prepare the internal User-Agent string for use. This requires a
     * {@link Context} to pull the package name and version number for this
     * application.
     */
    public static void prepareUserAgent(Context context) {
        try {
            // Read package name and version number from manifest
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            sUserAgent = String.format(context.getString(R.string.template_user_agent),
                    info.packageName, info.versionName);
            
        } catch(NameNotFoundException e) {
            Log.e(TAG, "Couldn't find package information in PackageManager", e);
        }
    }
    
	/**
     * Pull the raw text content of the given URL. This call blocks until the
     * operation has completed, and is synchronized because it uses a shared
     * buffer {@link #sBuffer}.
     * 
     * @param url The exact URL to request.
     * @return The raw content returned by the server.
     * @throws ApiException If any connection or server error occurs.
	 * @throws IOException 
     */
    protected static synchronized String getUrlContent(String requestUrl) throws ApiException, IOException {
                
    	URL url = new URL(requestUrl);
    	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    	urlConnection.setRequestMethod("GET");
    	urlConnection.setDoOutput(false);
    	urlConnection.setUseCaches(false);
    	urlConnection.connect();
    	
    	try {
    		InputStream in = new BufferedInputStream(urlConnection.getInputStream());
    		
    		ByteArrayOutputStream content = new ByteArrayOutputStream();
            
            // Read response into a buffered stream
            int readBytes = 0;
            while ((readBytes = in.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            
            // Return result from buffered stream
            String test = new String(content.toByteArray()); 
            return test;
    		
    	}
    	finally {
    		urlConnection.disconnect();
    	}
    	
    	
        // Create client and set our specific user-agent string
    	/*
        HttpClient client = new DefaultHttpClient();        
        HttpGet request = new HttpGet(requestUrl);
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");                
        
        try {
            HttpResponse response = client.execute(request);
            
            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }
    
            // Pull content stream from response
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            
            // Read response into a buffered stream
            int readBytes = 0;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            
            // Return result from buffered stream
            String test = new String(content.toByteArray()); 
            return test;
        } catch (IOException e) {
            throw new ApiException("Problem communicating with API", e);
        }
        */
    }
          
}

class DownloadPageContentTask extends AsyncTask<String, Void, String> {

	protected String doInBackground(String... urls) {
		try {
			return PageScraper.getUrlContent(urls[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return "";
	}		
}
