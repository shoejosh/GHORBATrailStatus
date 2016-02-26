package com.joshshoemaker.trailstatus.widgets;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
      return(new TrailStatusViewsFactory(this.getApplicationContext(), intent));
  }
}