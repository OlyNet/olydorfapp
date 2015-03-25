package eu.olynet.olydorfapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.olynet.olydorfapp.util.DateUtils;

/**
 * @author Martin Herrmann <martin.herrmann@olynet.eu>
 */
public class MealListAdapter extends BaseAdapter {

    private final Context context;
    private final List<MealOfTheDay> meals = new ArrayList<MealOfTheDay>();

    private DateFormat df;
    private NumberFormat nf;


    public MealListAdapter(Context context, JSONArray mealItems) {
        super();
        this.context = context;

        /* set the date format */
        df = new SimpleDateFormat("dd.MM.yyyy");

        /* set the number format */
        nf = NumberFormat.getCurrencyInstance(Locale.getDefault());
        nf.setCurrency(Currency.getInstance("EUR"));
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            for (int i = 0; i < mealItems.length(); i++) {
                JSONObject cur = mealItems.getJSONObject(i);
                /* get trivial fields */
                String name = cur.getString("meal");
                String cook = cur.getString("cook");

                /* get the date */
                Date date = df.parse(cur.getString("date"));
                if(DateUtils.normalizedDate(date).before(
                        DateUtils.normalizedDate(new Date()))) { // if the date is before 'today'
                    continue;
                }

                /* get the price */
                float price = Float.parseFloat(cur.getString("price"));

                /* append it to our list */
                meals.add(new MealOfTheDay(name, cook, date, price));
            }
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
        } catch (ParseException e) {
            Log.e("Date-Parse Exception", e.toString());
        } catch (NumberFormatException e) {
            Log.e("Float NFE", e.toString());
        }
    }

    @Override
    public int getCount() {
        return meals.size();
    }

    @Override
    public Object getItem(int position) {
        return meals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.menuitem, parent, false);

        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView meal = (TextView) rowView.findViewById(R.id.meal);
        TextView price = (TextView) rowView.findViewById(R.id.price);
        TextView cook = (TextView) rowView.findViewById(R.id.cook);


        MealOfTheDay cur = meals.get(position);

        meal.setText(cur.name);
        cook.setText(cur.cook);
        date.setText(df.format(cur.date));
        price.setText(nf.format(cur.price));

        return rowView;
    }
}
