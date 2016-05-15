/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.resources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* acquire a wake-lock to prevent the device from going to sleep during the registration */
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BootReceiver");
        wl.acquire();

        /* setup the ResourceAlarm only if the device just finished booting */
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmReceiver.setupAlarm(context);
            Log.d("BootReceiver", "Alarm setup after boot");
        }

        /* release the wake-lock */
        wl.release();
    }
}
