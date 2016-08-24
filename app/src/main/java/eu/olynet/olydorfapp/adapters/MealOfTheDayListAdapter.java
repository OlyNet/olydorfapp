/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.MealOfTheDayViewerActivity;
import eu.olynet.olydorfapp.fragments.MealOfTheDayViewerFragment;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class MealOfTheDayListAdapter
        extends RecyclerView.Adapter<MealOfTheDayListAdapter.ViewHolder> {

    private List<AbstractMetaItem<?>> items;
    private Context context;

    private RecyclerView mRecyclerView = null;
    private View mEmptyView = null;

    /**
     * @param context   the Context.
     * @param newsItems the List containing the NewsItems.
     */
    public MealOfTheDayListAdapter(Context context, List<AbstractMetaItem<?>> newsItems) {
        this.context = context;
        this.items = newsItems;
    }

    // Create new views (invoked by the layout manager)
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
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean showEmptyView = getItemCount() == 0;
                    mEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
                    mRecyclerView.setVisibility(showEmptyView ? View.GONE : View.VISIBLE);
                }
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

        /* set the correct item in the ViewHolder for the OnClickListener */
        holder.item = mealOfTheDayItem;

        /* Headline */
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        holder.vHeadline.setText(df.format(mealOfTheDayItem.getDate()));

        /* Name */
        holder.vName.setText(mealOfTheDayItem.getDailyMeal().getName());

        /* Cook */
        holder.vCook.setText(mealOfTheDayItem.getCook());

        /* Price */
        NumberFormat deDE = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        holder.vPrice.setText(deDE.format(mealOfTheDayItem.getPrice()));

        /* Image */
        byte[] image = mealOfTheDayItem.getImage();
        if (image == null || image.length <= 0) { /* fall back to Meal image */
            image = mealOfTheDayItem.getDailyMeal().getImage();
        }
        if (image != null && image.length > 0) { /* finally set the image if one is available */
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            if (imageBitmap == null) {
                holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
            } else {
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) context.getSystemService(
                        Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(dm);
                holder.vImage.setImageBitmap(imageBitmap);
            }
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
     * @param items the List of AbstractMetaItems to add.
     */
    public void addAbstractMetaItems(List<AbstractMetaItem<?>> items) {
        this.items.addAll(items);
    }

    /**
     * Replaces the internal List of AbstractMetaItems with a new one that <b>must</b> contain only
     * MealOfTheDayItems.
     *
     * @param items the new List of AbstractMetaItems to use.
     */
    public void replaceAbstractMetaItems(List<AbstractMetaItem<?>> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        protected MealOfTheDayItem item;

        protected View view;
        protected TextView vHeadline;
        protected ImageView vImage;
        protected TextView vName;
        protected TextView vPrice;
        protected TextView vCook;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            view.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    Intent newsViewerIntent = new Intent(context, MealOfTheDayViewerActivity.class);
                    newsViewerIntent.setAction(Intent.ACTION_VIEW);
                    newsViewerIntent.putExtra(MealOfTheDayViewerFragment.ITEM_KEY, item);
                    context.startActivity(newsViewerIntent);
                }
            });

            vHeadline = (TextView) view.findViewById(R.id.meal_of_the_day_headline);
            vImage = (ImageView) view.findViewById(R.id.meal_of_the_day_image);
            vName = (TextView) view.findViewById(R.id.meal_of_the_day_title);
            vPrice = (TextView) view.findViewById(R.id.meal_of_the_day_price);
            vCook = (TextView) view.findViewById(R.id.meal_of_the_day_cook);
        }
    }
}
