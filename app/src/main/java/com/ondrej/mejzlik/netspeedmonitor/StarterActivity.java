package com.ondrej.mejzlik.netspeedmonitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * This is a helper starter activity that does not have any layout. It's purpose is to start the
 * service when the user clicks on the starter widget. It first checks whether the service is
 * already running and if so, tells the user and does not start it again.
 */
public class StarterActivity extends Activity {
    public static final String MANUAL_START = "manualStart";
    public static final String CHANNEL_ID = "network monitor channel id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is a starter activity, only run the service if it is not running already.
        // If by accident manager returned null and the service would be run again, it would
        // run the onHandleIntent method and still do nothing since this intent does not have
        // the required extras.

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        // Get the notification manager which might be null
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            // We are not able to create notification channel the manager can not be retrieved, tell
            // the user and quit, do not start service.
            Toast.makeText(this, R.string.service_failed_to_start, Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            // Construct the channel
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            if (!notificationManager.getNotificationChannels().contains(channel)) {
                // Register the channel with the system if the channel does not exist already
                notificationManager.createNotificationChannel(channel);
            }
            // TODO remove this test
            if (!notificationManager.getNotificationChannels().contains(channel)) {
                Log.d("CHANNEL", "EXISTS ALREADY");
            }
        }

        if (!this.isNetServiceRunning()) {
            Context context = getApplicationContext();
            Intent startService = new Intent(context, NetMonitorService.class);
            // This is used to indicate that the user is starting the service manually via widget
            // and the screen state may be ignored, since the first broadcast we receive is OFF.
            startService.putExtra(MANUAL_START, true);
            context.startForegroundService(startService);
        } else {
            Toast.makeText(this, R.string.service_running, Toast.LENGTH_LONG).show();
        }

        // Kill this activity as soon as the service is set up.
        this.finish();
    }

    /**
     * This method checks whether the NetMonitor service is running and returns true if yes.
     *
     * @return true if NetMonitorService is running.
     */
    private boolean isNetServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (NetMonitorService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } else {
            Toast.makeText(this, R.string.service_failed_to_start, Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
