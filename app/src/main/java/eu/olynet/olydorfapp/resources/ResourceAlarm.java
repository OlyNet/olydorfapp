/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
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
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class ResourceAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* acquire a wake-lock to prevent the device from going to sleep during the cleanup */
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ResourceAlarm");
        wl.acquire();

        /* perform the cleanup */
        ResourceManager rm = ResourceManager.getInstance();
        rm.init(context);
        rm.cleanup();
        Log.d("ResourceAlarm", "ResourceManager.cleanup() completed.");

        /* release the wake-lock */
        wl.release();
    }

    public static void setupAlarm(Context context) {
        /* setup the Calendar with the current time */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        /* create the PendingIntent */
        Intent intent = new Intent(context, ResourceAlarm.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        /* setup the AlarmManager */
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis() + 1000 * 10,
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
