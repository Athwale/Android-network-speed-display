package com.ondrej.mejzlik.netspeedmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class receives broadcasts when screen is turned on or off. These broadcasts can not be
 * declared in manifest as they are protected. Therefore this receiver is for the first time
 * registered when the NetMonitorService is first run in the onCreate method. And is unregistered
 * when the service is killed. Then this receiver uses the service and invokes the onHandleIntent
 * method with an intent that says what state the screen is in.
 */
public class ScreenReceiver extends BroadcastReceiver {
    public static final String SCREEN_STATE_KEY = "screenStateKey";
    // False = screen OFF, true = screen ON
    public static boolean screenState = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            ScreenReceiver.screenState = false;
            Log.d("SCREEN RECEIVER", "SCREEN OFF");
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            ScreenReceiver.screenState = true;
            Log.d("SCREEN RECEIVER", "SCREEN ON");
        }
        // This receiver is made by the NetMonitorService when it is created. Then starting the
        // service here calls onHandleIntent not onCreate and passes the screen state.
        Intent handleScreenState = new Intent(context, NetMonitorService.class);
        handleScreenState.putExtra(SCREEN_STATE_KEY, screenState);
        context.startService(handleScreenState);
    }
}
