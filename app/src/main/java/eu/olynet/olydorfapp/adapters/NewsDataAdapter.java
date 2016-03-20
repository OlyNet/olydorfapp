/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.adapters;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.List;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.NewsItem;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsDataAdapter extends RecyclerView.Adapter<NewsDataAdapter.ViewHolder> {

    private List<AbstractMetaItem<?>> items;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView vDate;
        protected TextView vTitle;
        protected TextView vOrganization;
        protected ImageView vImage;

        public ViewHolder(View view) {
            super(view);
            vOrganization = (TextView) view.findViewById(R.id.newsCardOrganization);
            vDate = (TextView) view.findViewById(R.id.newsCardDate);
            vTitle = (TextView) view.findViewById(R.id.newsCardTitle);
            vImage = (ImageView) view.findViewById(R.id.newsCardImage);
        }
    }

    /**
     * @param context   the Context.
     * @param newsItems the List containing the NewsItems.
     */
    public NewsDataAdapter(Context context, List<AbstractMetaItem<?>> newsItems) {
        this.context = context;
        this.items = newsItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news, parent, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!(items.get(position) instanceof NewsItem)) {
            throw new IllegalArgumentException("the List of AbstractMetaItem<?>s provided does not"
                    + " seem to only contain NewsItems");
        }
        NewsItem newsItem = (NewsItem) items.get(position);

        /* Date */
        SimpleDateFormat localFormat = (SimpleDateFormat) android.text.format.DateFormat.getDateFormat(context);
        holder.vDate.setText(localFormat.format(newsItem.getDate()));

        /* Title */
        holder.vTitle.setText(newsItem.getTitle());

        /* Organization */
        holder.vOrganization.setText(newsItem.getOrganization().getName());

        /* Image */
        byte[] image = newsItem.getImage();
        if (image == null || image.length <= 0) { /* fall back to Organization image */
            image = newsItem.getOrganization().getLogo();
        }
        if (image != null && image.length > 0) { /* finally set the image if one is available */
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            if(imageBitmap == null) {
                holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
            } else {
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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
     * Returns the NewsItem (as AbstractMetaItem) at a certain position within the list. This item
     * must actually exist.
     *
     * @param position the requested NewsItem's position
     * @return the NewsItem
     */
    public AbstractMetaItem<?> getAbstractMetaItem(int position) {
        return items.get(position);
    }

    /**
     * Adds a List of AbstractMetaItems that <b>must</b> be NewsItems to this Adapter
     *
     * @param items the List of AbstractMetaItems to add.
     */
    public void addAbstractMetaItems(List<AbstractMetaItem<?>> items) {
        this.items.addAll(items);
    }

    /**
     * Replaces the internal List of AbstractMetaItems with a new one that <b>must</b> contain only
     * NewsItems.
     *
     * @param items the new List of AbstractMetaItems to use.
     */
    public void replaceAbstractMetaItems(List<AbstractMetaItem<?>> items) {
        this.items.clear();
        this.items.addAll(items);
    }
}
