package com.JoshShoemaker.trailstatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.JoshShoemaker.trailstatus.PageScraper.ApiException;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return(new LoremViewsFactory(this.getApplicationContext(),
                                 intent));
  }
}