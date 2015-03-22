package eu.olynet.olydorfapp;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Martin on 22.03.2015.
 */
public class MealOfTheDay {

    public static Date normalizeDate(Date date) {
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

    public final String name, cook;
    public final Date date;
    public final float price;

    public MealOfTheDay(String name, String cook, Date date, float price) {
        this.name = name;
        this.cook = cook;
        this.date = normalizeDate(date);
        this.price = price;
    }

}
