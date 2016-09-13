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
package eu.olynet.olydorfapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.MealOfTheDayViewerActivity;
import eu.olynet.olydorfapp.fragments.MealOfTheDayViewerFragment;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class DailyMealTabAdapter extends RecyclerView.Adapter<DailyMealTabAdapter.ViewHolder> {

    private MealOfTheDayItem mealOfTheDayItem;
    private DailyMealItem dailyMealItem;
    private final Context context;

    /**
     * @param context          the Context.
     * @param mealOfTheDayItem the DailyMealItem.
     */
    public DailyMealTabAdapter(Context context, MealOfTheDayItem mealOfTheDayItem,
                               DailyMealItem dailyMealItem) {
        this.context = context;
        this.mealOfTheDayItem = mealOfTheDayItem;
        this.dailyMealItem = dailyMealItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.card_meal_of_the_day, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position != 0 || mealOfTheDayItem == null || dailyMealItem == null) {
            return;
        }

        /* set the correct mealOfTheDayItem in the ViewHolder for the OnClickListener */
        holder.mealOfTheDayItem = mealOfTheDayItem;
        holder.dailyMealItem = dailyMealItem;

        /* Headline */
        Calendar cal = new GregorianCalendar();
        cal.setTime(mealOfTheDayItem.getDate());
        holder.vHeadline.setText("Tagesessen (" + cal.get(Calendar.DAY_OF_MONTH) + ". " +
                                 cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                                                    Locale.getDefault()) + ")");

        /* Icon */
        holder.vIcon.setImageResource(
                dailyMealItem.isVegetarian() ? R.drawable.carrot_48dp
                                             : R.drawable.meat_48dp);

        /* Name */
        holder.vName.setText(dailyMealItem.getName());

        /* Cook */
        holder.vCook.setText(mealOfTheDayItem.getCook());

        /* Price */
        NumberFormat deDE = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        holder.vPrice.setText(deDE.format(mealOfTheDayItem.getPrice()));

        /* Image */
        byte[] image = mealOfTheDayItem.getImage();
        int screenWidth = UtilsDevice.getScreenWidth(context);
        Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
        if (bitmap == null) { /* fallback to DailyMeal image */
            image = dailyMealItem.getImage();
            bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
        }
        if (bitmap != null) {
            holder.vImage.setImageBitmap(bitmap);
        } else {
            holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
        }
    }

    /**
     * Return the size of your data-set (invoked by the layout manager).
     *
     * @return the number of items present.
     */
    @Override
    public int getItemCount() {
        return mealOfTheDayItem == null ? 0 : 1;
    }

    /**
     * @return the MealOfTheDayItem.
     */
    public MealOfTheDayItem getItem() {
        return mealOfTheDayItem;
    }

    /**
     * @param mealOfTheDayItem the new MealOfTheDayItem.
     */
    public void setItem(MealOfTheDayItem mealOfTheDayItem, DailyMealItem dailyMealItem) {
        this.mealOfTheDayItem = mealOfTheDayItem;
        this.dailyMealItem = dailyMealItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MealOfTheDayItem mealOfTheDayItem;
        DailyMealItem dailyMealItem;

        final TextView vHeadline;
        final ImageView vIcon;
        final ImageView vImage;
        final TextView vName;
        final TextView vPrice;
        final TextView vCook;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent newsViewerIntent = new Intent(context, MealOfTheDayViewerActivity.class);
                newsViewerIntent.setAction(Intent.ACTION_VIEW);
                newsViewerIntent.putExtra(MealOfTheDayViewerFragment.MEAL_OF_THE_DAY_ITEM_KEY,
                                          mealOfTheDayItem);
                newsViewerIntent.putExtra(MealOfTheDayViewerFragment.DAILY_MEAL_KEY, dailyMealItem);
                context.startActivity(newsViewerIntent);
            });

            vHeadline = (TextView) view.findViewById(R.id.meal_of_the_day_headline);
            vIcon = (ImageView) view.findViewById(R.id.meal_of_the_day_icon);
            vImage = (ImageView) view.findViewById(R.id.meal_of_the_day_image);
            vName = (TextView) view.findViewById(R.id.meal_of_the_day_title);
            vPrice = (TextView) view.findViewById(R.id.meal_of_the_day_price);
            vCook = (TextView) view.findViewById(R.id.meal_of_the_day_cook);
        }
    }
}
