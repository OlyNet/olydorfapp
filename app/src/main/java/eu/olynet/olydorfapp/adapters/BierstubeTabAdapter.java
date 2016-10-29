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

import org.apache.commons.lang3.NotImplementedException;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.FoodViewerActivity;
import eu.olynet.olydorfapp.activities.MealOfTheDayViewerActivity;
import eu.olynet.olydorfapp.fragments.FoodViewerFragment;
import eu.olynet.olydorfapp.fragments.MealOfTheDayViewerFragment;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class BierstubeTabAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADLINE_TYPE = 0;
    private static final int DAILY_MEAL_TYPE = 1;
    private static final int DAILY_DRINK_TYPE = 2;
    private static final int FOOD_TYPE = 3;
    private static final int DRINK_TYPE = 4;

    private MealOfTheDayItem mealOfTheDayItem;
    private DailyMealItem dailyMealItem;
    private List<AbstractMetaItem<?>> foodItems;

    private final Context context;

    /**
     * @param context          the Context.
     * @param mealOfTheDayItem the MealOfTheDayItem.
     * @param dailyMealItem    the DailyMealItem.
     * @param foodItems        the List containing the FoodItems.
     */
    public BierstubeTabAdapter(Context context, MealOfTheDayItem mealOfTheDayItem,
                               DailyMealItem dailyMealItem, List<AbstractMetaItem<?>> foodItems) {
        this.context = context;
        this.mealOfTheDayItem = mealOfTheDayItem;
        this.dailyMealItem = dailyMealItem;
        this.foodItems = foodItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADLINE_TYPE:
                View headlineView = LayoutInflater.from(parent.getContext())
                                                  .inflate(R.layout.card_headline, parent, false);
                return new HeadlineHolder(headlineView);
            case DAILY_MEAL_TYPE:
                View dailyMealView = LayoutInflater.from(parent.getContext())
                                                   .inflate(R.layout.card_meal_of_the_day, parent,
                                                            false);
                return new DailyMealHolder(dailyMealView);
            case DAILY_DRINK_TYPE:
                throw new NotImplementedException("not yet implemented");
            case FOOD_TYPE:
                View foodView = LayoutInflater.from(parent.getContext())
                                              .inflate(R.layout.card_food, parent, false);
                return new FoodHolder(foodView);
            case DRINK_TYPE:
                throw new NotImplementedException("not yet implemented");
            default:
                throw new RuntimeException("unknown item view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADLINE_TYPE;
        } else if (position == 1) {
            return DAILY_MEAL_TYPE;
        } else if (position == 2) {
            return HEADLINE_TYPE;
        } else {
            return FOOD_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADLINE_TYPE:
                int resID;
                if (position == 0) {
                    resID = R.string.bierstube_headline_daily;
                } else {
                    resID = R.string.bierstube_headline_foods;
                }
                ((HeadlineHolder) holder).vTitle.setText(resID);
                break;
            case DAILY_MEAL_TYPE:
                bindDailyMealHolder((DailyMealHolder) holder);
                break;
            case DAILY_DRINK_TYPE:
                // TODO
                break;
            case FOOD_TYPE:
                bindFoodHolder((FoodHolder) holder, position - 3);
                break;
            case DRINK_TYPE:
                // TODO
                break;
            default:
                throw new RuntimeException("unknown item view type");
        }
    }

    /**
     * Fills the ViewHolder with the information about today's special meal.
     *
     * @param holder the ViewHolder to be filled.
     */
    private void bindDailyMealHolder(DailyMealHolder holder) {
        if (mealOfTheDayItem == null || dailyMealItem == null) {
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
     * Fills the ViewHolder with the information about today's special meal.
     *
     * @param holder the ViewHolder to be filled.
     */
    private void bindFoodHolder(FoodHolder holder, int pos) {
        if (foodItems.size() < pos) {
            return;
        }

        holder.foodItem = (FoodItem) foodItems.get(pos);

        /* Name */
        holder.vName.setText(holder.foodItem.getName());

        /* Icon */
        holder.vIcon.setImageResource(holder.foodItem.isVegetarian() ? R.drawable.carrot_48dp
                                                                     : R.drawable.meat_48dp);

        /* Price */
        NumberFormat deDE = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        holder.vPrice.setText(deDE.format(holder.foodItem.getPrice()));

        /* Image */
        byte[] image = holder.foodItem.getImage();
        int screenWidth = UtilsDevice.getScreenWidth(context);
        Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
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
        return foodItems.size() + 2 + 1;
    }

    /**
     * @param mealOfTheDayItem the new MealOfTheDayItem.
     * @param dailyMealItem    the new DailyMealItem.
     * @param foodItems        the new List of FoodItems.
     */
    public void setData(MealOfTheDayItem mealOfTheDayItem, DailyMealItem dailyMealItem,
                        List<AbstractMetaItem<?>> foodItems) {
        this.mealOfTheDayItem = mealOfTheDayItem;
        this.dailyMealItem = dailyMealItem;
        this.foodItems = foodItems;
    }

    private class HeadlineHolder extends RecyclerView.ViewHolder {

        final TextView vTitle;

        HeadlineHolder(View view) {
            super(view);
            vTitle = (TextView) view.findViewById(R.id.headline_title);
        }
    }

    private class DailyMealHolder extends RecyclerView.ViewHolder {

        MealOfTheDayItem mealOfTheDayItem;
        DailyMealItem dailyMealItem;

        final TextView vHeadline;
        final ImageView vIcon;
        final ImageView vImage;
        final TextView vName;
        final TextView vPrice;
        final TextView vCook;

        DailyMealHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent mealOfTheDayViewerIntent = new Intent(context,
                                                             MealOfTheDayViewerActivity.class);
                mealOfTheDayViewerIntent.setAction(Intent.ACTION_VIEW);
                mealOfTheDayViewerIntent.putExtra(
                        MealOfTheDayViewerFragment.MEAL_OF_THE_DAY_ITEM_KEY,
                        mealOfTheDayItem);
                mealOfTheDayViewerIntent.putExtra(MealOfTheDayViewerFragment.DAILY_MEAL_KEY,
                                                  dailyMealItem);
                context.startActivity(mealOfTheDayViewerIntent);
            });

            vHeadline = (TextView) view.findViewById(R.id.meal_of_the_day_headline);
            vIcon = (ImageView) view.findViewById(R.id.meal_of_the_day_icon);
            vImage = (ImageView) view.findViewById(R.id.meal_of_the_day_image);
            vName = (TextView) view.findViewById(R.id.meal_of_the_day_title);
            vPrice = (TextView) view.findViewById(R.id.meal_of_the_day_price);
            vCook = (TextView) view.findViewById(R.id.meal_of_the_day_cook);
        }
    }

    private class FoodHolder extends RecyclerView.ViewHolder {

        FoodItem foodItem;

        final TextView vName;
        final ImageView vIcon;
        final ImageView vImage;
        final TextView vPrice;

        FoodHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent foodViewerIntent = new Intent(context, FoodViewerActivity.class);
                foodViewerIntent.setAction(Intent.ACTION_VIEW);
                foodViewerIntent.putExtra(FoodViewerFragment.FOOD_ITEM_KEY, foodItem);
                context.startActivity(foodViewerIntent);
            });

            vName = (TextView) view.findViewById(R.id.food_title);
            vIcon = (ImageView) view.findViewById(R.id.food_icon);
            vImage = (ImageView) view.findViewById(R.id.food_image);
            vPrice = (TextView) view.findViewById(R.id.food_price);
        }
    }
}
