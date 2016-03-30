package com.joshshoemaker.trailstatus.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Utils {

	public static boolean isNetworkConnected(Context context) {
		  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo ni = cm.getActiveNetworkInfo();
		  return ni != null;
		 }
	
	public static boolean isSameDate(Date d1, Date d2)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String sd1 = sdf.format(d1).toString();
		String sd2 = sdf.format(d2).toString();
		
		return sd1.equals(sd2);
		
	}

    public static String joinString(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token: tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }
}
