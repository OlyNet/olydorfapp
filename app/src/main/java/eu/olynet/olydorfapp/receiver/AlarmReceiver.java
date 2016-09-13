/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.olynet.olydorfapp.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static void setupAlarm(Context context) {
        /* setup the Calendar with the current time */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        /* create the PendingIntent */
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        /* setup the AlarmManager */
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis() + 1000 * 10,
                                     AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /* acquire a wake-lock to prevent the device from going to sleep during the cleanup */
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ResourceAlarm");
        wl.acquire();

        /* setup ResourceManager */
        ResourceManager rm = ProductionResourceManager.getInstance();
        if (!rm.isInitialized()) {
            rm.init(context.getApplicationContext());
        }

        /* perform the cleanup */
        rm.cleanup();
        Log.d("ResourceAlarm", "ResourceManager.cleanup() completed.");

        /* release the wake-lock */
        wl.release();
    }
}
