package eu.olynet.olydorfapp.resources;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BootReceiver");
        wl.acquire();

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ResourceAlarm.setupAlarm(context);

            Log.i("BootReceiver", "Alarm setup after boot");
        }
        wl.release();
    }
}
