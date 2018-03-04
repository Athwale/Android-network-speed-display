package com.ondrej.mejzlik.netspeedmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import static com.ondrej.mejzlik.netspeedmonitor.ScreenReceiver.SCREEN_STATE_KEY;
import static com.ondrej.mejzlik.netspeedmonitor.StarterActivity.CHANNEL_ID;
import static com.ondrej.mejzlik.netspeedmonitor.StarterActivity.MANUAL_START;

/**
 * Created by Ondrej Mejzlik on 3/4/18.
 */
public class NetMonitorService extends Service {
    public static final String UPLOAD_KEY = "uploadKey";
    public static final String DOWNLOAD_KEY = "downloadKey";
    public static final int SERVICE_UNIQUE_ID = 7564;
    private BroadcastReceiver screenReceiver = null;
    private Handler mainHandler = null;
    private Thread workerThread = null;
    private Notification.Builder builder = null;
    private Notification notification = null;
    private NotificationManager notificationManager = null;

    /**
     * Screen On and Off broadcasts can not be received by manifest declared receivers, it has to be
     * done this way. This service is started for the first time after boot up and then creates the
     * new receiver.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVICE", "SERVICE ON CREATE CALLED");
        // Screen ON and OFF broadcasts can no be declared in manifest, it has to be done this way.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.screenReceiver = new ScreenReceiver();
        registerReceiver(this.screenReceiver, filter);
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // This service is a foreground service, so we need a notification, it will be used to
        // provide info about the speeds anyway. To update notification, we need to use the same
        // builder.
        this.builder = new Notification.Builder(this, CHANNEL_ID);
        this.notification = this.builder.setSmallIcon(R.drawable.widget_cog_icon).setOnlyAlertOnce(true).build();

        // Start this service as foreground service, it is less likely to get killed.
        startForeground(SERVICE_UNIQUE_ID, this.notification);

        // Get the main thread handler which we are going to use to communicate with UI.
        this.mainHandler = new Handler(getMainLooper()) {
            // This method is called when the main handler receives a message and runs on main
            // thread. Here we can update the icon.
            @Override
            public void handleMessage(Message inputMessage) {
                Log.d("MESSAGE", "FROM THREAD");
                Bundle data = inputMessage.getData();
                if (data.containsKey(UPLOAD_KEY) && data.containsKey(DOWNLOAD_KEY)) {
                    // Construct the string
                    String speeds = "Download: " + String.valueOf(data.getDouble(DOWNLOAD_KEY));
                    speeds += " ";
                    speeds += "Upload: " + String.valueOf(data.getDouble(UPLOAD_KEY));

                    // TODO make more icons and display approx speed by them
                    builder.setContentText(speeds);
                    // Update the icon
                    notificationManager.notify(SERVICE_UNIQUE_ID, notification);
                    Log.d("MESSAGE", "UPLOAD " + String.valueOf(data.getDouble(UPLOAD_KEY)));
                    Log.d("MESSAGE", "DOWNLOAD " + String.valueOf(data.getDouble(DOWNLOAD_KEY)));
                }
            }
        };

        // Tell user that the service has been created
        Toast.makeText(this, R.string.service_created, Toast.LENGTH_LONG).show();
    }

    /**
     * This method runs in a separate thread and does all the work. This method is run always after
     * onCreate, therefore we can create the thread here.
     *
     * @param intent Intent that started this service.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERVICE", "SERVICE ON START COMMAND CALLED");
        if (intent != null) {
            boolean state = intent.getBooleanExtra(SCREEN_STATE_KEY, false);
            // If we are starting by pressing the widget, screen is ON.
            if (intent.getBooleanExtra(MANUAL_START, false)) {
                state = true;
            }
            if (!state) {
                Log.d("SCREEN", "SCREEN WAS OFF");
                // Might be null if the screen is turned off and the service is stopped and destroyed
                if (this.workerThread != null) {
                    this.workerThread.interrupt();
                }
            } else {
                Log.d("SCREEN", "SCREEN WAS ON");
                // Create the worker thread which is going to be an inner class and will have access to the
                // upper class private members.
                this.workerThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // Static method that returns the current thread interrupt status set by thread
                        // interrupt(method). On return the thread dies.
                        int i = 0;
                        while (!Thread.interrupted()) {
                            // TODO implement file observer to measure net speed
                            i++;
                            Message message = mainHandler.obtainMessage();
                            Bundle data = new Bundle();
                            data.putDouble(NetMonitorService.UPLOAD_KEY, 10 + i);
                            data.putDouble(NetMonitorService.DOWNLOAD_KEY, 100 + i);
                            message.setData(data);
                            mainHandler.sendMessage(message);

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                        Log.d("THREAD", "INTERRUPTED");
                        Log.d("THREAD", "DEAD");
                    }
                });
                this.workerThread.start();
            }
        }
        // This makes the system restart the service if it is killed.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SERVICE", "SERVICE DESTROYED");
        // We do not want to leave useless receivers in the system.
        unregisterReceiver(this.screenReceiver);
        // Stop the thread
        if (this.workerThread != null) {
            // The thread will end the run method if interruped.
            this.workerThread.interrupt();
            this.workerThread = null;
            Log.d("THREAD", "KILLED");
        }
    }

    /**
     * Not needed
     *
     * @param intent intent that caused this
     * @return Not needed
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

