package com.joshshoemaker.trailstatus.helpers;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.*;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.joshshoemaker.trailstatus.R;

public class PageScraper {

private static final String TAG = "SimpleWikiHelper";
    
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
     * @param requestUrl The exact URL to request.
     * @return The raw content returned by the server.
	 * @throws IOException 
     */
    protected static synchronized String getUrlContent(String requestUrl) throws IOException {
                
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
