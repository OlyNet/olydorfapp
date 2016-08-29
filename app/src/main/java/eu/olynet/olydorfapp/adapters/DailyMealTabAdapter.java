/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.adapters;

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

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.MealOfTheDayViewerActivity;
import eu.olynet.olydorfapp.fragments.MealOfTheDayViewerFragment;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class DailyMealTabAdapter extends RecyclerView.Adapter<DailyMealTabAdapter.ViewHolder> {

    private static final int DEFAULT_IMAGE = R.drawable.ic_account_circle_white_64dp;

    private MealOfTheDayItem mealOfTheDayItem;
    private final Context context;

    /**
     * @param context          the Context.
     * @param mealOfTheDayItem the DailyMealItem.
     */
    public DailyMealTabAdapter(Context context, MealOfTheDayItem mealOfTheDayItem) {
        this.context = context;
        this.mealOfTheDayItem = mealOfTheDayItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_meal_of_the_day, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position != 0 || mealOfTheDayItem == null) {
            return;
        }

        /* set the correct mealOfTheDayItem in the ViewHolder for the OnClickListener */
        holder.item = mealOfTheDayItem;

        /* Headline */
        Calendar cal = new GregorianCalendar();
        cal.setTime(mealOfTheDayItem.getDate());
        holder.vHeadline.setText("Fraß des Tages (" + cal.get(Calendar.DAY_OF_MONTH) + ". " +
                cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                        Locale.getDefault()) + ")");

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
                holder.vImage.setImageResource(DEFAULT_IMAGE);
            } else {
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) context.getSystemService(
                        Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(dm);
                holder.vImage.setImageBitmap(imageBitmap);
            }
        } else {
            holder.vImage.setImageResource(DEFAULT_IMAGE);
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
     * @param item the new MealOfTheDayItem.
     */
    public void setItem(MealOfTheDayItem item) {
        this.mealOfTheDayItem = item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MealOfTheDayItem item;

        final TextView vHeadline;
        final ImageView vImage;
        final TextView vName;
        final TextView vPrice;
        final TextView vCook;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent newsViewerIntent = new Intent(context, MealOfTheDayViewerActivity.class);
                newsViewerIntent.setAction(Intent.ACTION_VIEW);
                newsViewerIntent.putExtra(MealOfTheDayViewerFragment.ITEM_KEY, item);
                context.startActivity(newsViewerIntent);
            });

            vHeadline = (TextView) view.findViewById(R.id.meal_of_the_day_headline);
            vImage = (ImageView) view.findViewById(R.id.meal_of_the_day_image);
            vName = (TextView) view.findViewById(R.id.meal_of_the_day_title);
            vPrice = (TextView) view.findViewById(R.id.meal_of_the_day_price);
            vCook = (TextView) view.findViewById(R.id.meal_of_the_day_cook);
        }
    }
}
