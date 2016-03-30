package com.joshshoemaker.trailstatus.activities;

/**
 * Created by Josh on 3/30/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.TaskStackBuilder;

/**
 * Helper 'proxy' activity that simply accepts an activity intent and synthesize a back-stack
 * for it, per Android's design guidelines for navigation from widgets and notifications.
 */
public class TaskStackBuilderProxyActivity extends Activity {
    private static final String EXTRA_INTENTS = "com.joshshoemaker.trailstatus.extra.INTENTS";

    public static Intent getTemplate(Context context) {
        return new Intent(context, TaskStackBuilderProxyActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getFillIntent(Intent... intents) {
        return new Intent().putExtra(EXTRA_INTENTS, intents);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TaskStackBuilder builder = TaskStackBuilder.create(this);
        Intent proxyIntent = getIntent();
        if (!proxyIntent.hasExtra(EXTRA_INTENTS)) {
            finish();
            return;
        }

        for (Parcelable parcelable : proxyIntent.getParcelableArrayExtra(EXTRA_INTENTS)) {
            builder.addNextIntent((Intent) parcelable);
        }

        builder.startActivities();
        finish();
    }
}