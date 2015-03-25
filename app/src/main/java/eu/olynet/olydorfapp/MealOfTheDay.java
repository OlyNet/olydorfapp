package eu.olynet.olydorfapp;

import java.util.Date;

import eu.olynet.olydorfapp.util.DateUtils;

/**
 * Created by Martin on 22.03.2015.
 */
public class MealOfTheDay {

    public final String name, cook;
    public final Date date;
    public final float price;

    public MealOfTheDay(String name, String cook, Date date, float price) {
        this.name = name;
        this.cook = cook;
        this.date = DateUtils.normalizedDate(date);
        this.price = price;
    }

}
