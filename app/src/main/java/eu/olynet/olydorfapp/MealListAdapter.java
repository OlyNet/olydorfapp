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

/**
 * @author Martin Herrmann <martin.herrmann@olynet.eu>
 */
public class MealListAdapter extends BaseAdapter {

    private final Context context;
    private final JSONArray mealItems;

    public MealListAdapter(Context context, JSONArray mealItems) {
        super();
        this.context = context;
        this.mealItems = mealItems;
    }

    @Override
    public int getCount() {
        return mealItems.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mealItems.get(position);
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

        try {
            date.setText(mealItems.getJSONObject(position).getString("date"));
            meal.setText(mealItems.getJSONObject(position).getString("meal"));
            price.setText(mealItems.getJSONObject(position).getString("price"));
            cook.setText(mealItems.getJSONObject(position).getString("cook"));
        } catch (JSONException e) {
            date.setText("");
            meal.setText("ERROR");
            price.setText("");
            cook.setText("");
            Log.e("JSON Error", e.toString());
        }

        return rowView;
    }
}
