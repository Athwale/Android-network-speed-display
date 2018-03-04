package com.ondrej.mejzlik.netspeedmonitor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Ondrej Mejzlik on 3/4/18.
 * This class provides the starter widget. The only purpose is to be able to start the service, then
 * this widget can be removed from the screen.
 */
public class NetMonitorStarter extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.net_monitor_starter);
            // Start the service and tell the user
            Intent intent = new Intent(context, StarterActivity.class);
            // We should be able to start the service directly with getService, but it does not
            // work no matter what I tried, so we are using a starter activity that does not have
            // any layout and does not display anything, runs the service and immediately finishes.
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Not needed
    }

    @Override
    public void onDisabled(Context context) {
        // Not needed
    }
}

