package eu.olynet.olydorfapp.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Martin on 25.03.2015.
 */
public class DateUtils {

    public static Date normalizedDate(Date date) {
        Date res = date;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }
}
