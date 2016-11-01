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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.MealOfTheDayViewerActivity;
import eu.olynet.olydorfapp.fragments.MealOfTheDayViewerFragment;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class MealOfTheDayListAdapter
        extends RecyclerView.Adapter<MealOfTheDayListAdapter.ViewHolder> {

    private final Context context;

    private RecyclerView mRecyclerView = null;
    private View mEmptyView = null;

    private final List<AbstractMetaItem<?>> items;
    private final Map<Integer, DailyMealItem> dailyMealMap;

    /**
     * @param context      the Context.
     * @param newsItems    the List containing the NewsItems.
     * @param dailyMealMap the data structure that maps integers to their corresponding
     *                     DailyMealItems.
     */
    public MealOfTheDayListAdapter(Context context, List<AbstractMetaItem<?>> newsItems,
                                   Map<Integer, DailyMealItem> dailyMealMap) {
        this.context = context;
        this.items = newsItems;
        this.dailyMealMap = dailyMealMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_meal_of_the_day, parent, false);

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkVisibility();
            }
        });

        return new ViewHolder(view);
    }

    /**
     * Register the two Views so they can be displayed/hidden as needed
     *
     * @param mRecyclerView the RecyclerView (will be displayed if this adapter is not empty).
     * @param mEmptyView    the View to be displayed in case this adapter is empty.
     */
    public void registerViews(RecyclerView mRecyclerView, View mEmptyView) {
        /* check for null */
        if (mEmptyView == null) {
            throw new NullPointerException("mEmptyView may not be null");
        }
        if (mRecyclerView == null) {
            throw new NullPointerException("mRecyclerView may not be null");
        }

        /* set the views */
        this.mRecyclerView = mRecyclerView;
        this.mEmptyView = mEmptyView;
    }

    public void checkVisibility() {
        if (mEmptyView != null && mRecyclerView != null) {
            ((Activity) context).runOnUiThread(() -> {
                boolean showEmptyView = getItemCount() == 0;
                mEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
                mRecyclerView.setVisibility(showEmptyView ? View.GONE : View.VISIBLE);
            });
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!(items.get(position) instanceof MealOfTheDayItem)) {
            throw new IllegalArgumentException(
                    "the List of AbstractMetaItem<?>s provided does not" +
                            " seem to only contain MealOfTheDayItems");
        }
        MealOfTheDayItem mealOfTheDayItem = (MealOfTheDayItem) items.get(position);
        DailyMealItem dailyMealItem = dailyMealMap.get(mealOfTheDayItem.getDailyMeal());
        if (dailyMealItem == null) {
            throw new IllegalArgumentException("DailyMealItem missing for id "
                    + mealOfTheDayItem.getId());
        }

        /* set the correct item in the ViewHolder for the OnClickListener */
        holder.mealOfTheDayItem = mealOfTheDayItem;
        holder.dailyMealItem = dailyMealItem;

        /* Headline */
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        holder.vHeadline.setText(df.format(mealOfTheDayItem.getDate()));

        /* Icon */
        holder.vIcon.setImageResource(dailyMealItem.isVegetarian() ? R.drawable.carrot
                : R.drawable.meat);

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
        if (bitmap == null) { /* fallback to DailyMeal image  */
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
        return items.size();
    }

    /**
     * Returns the MealOfTheDayItems (as AbstractMetaItem) at a certain position within the list.
     * This item
     * must actually exist.
     *
     * @param position the requested MealOfTheDayItem's position
     * @return the MealOfTheDayItem
     */
    public AbstractMetaItem<?> getAbstractMetaItem(int position) {
        return items.get(position);
    }

    /**
     * Adds a List of AbstractMetaItems that <b>must</b> be MealOfTheDayItems to this Adapter
     *
     * @param items        the List of AbstractMetaItems to add.
     * @param dailyMealMap the Map containing the new DailyMealItems.
     */
    public void addAbstractMetaItems(List<AbstractMetaItem<?>> items,
                                     Map<Integer, DailyMealItem> dailyMealMap) {
        this.items.addAll(items);
        this.dailyMealMap.putAll(dailyMealMap);
    }

    /**
     * Replaces the internal List of AbstractMetaItems with a new one that <b>must</b> contain only
     * MealOfTheDayItems.
     *
     * @param items        the new List of AbstractMetaItems to use.
     * @param dailyMealMap the Map containing the new DailyMealItems.
     */
    public void replaceAbstractMetaItems(List<AbstractMetaItem<?>> items,
                                         Map<Integer, DailyMealItem> dailyMealMap) {
        this.items.clear();
        this.items.addAll(items);
        this.dailyMealMap.clear();
        this.dailyMealMap.putAll(dailyMealMap);
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
