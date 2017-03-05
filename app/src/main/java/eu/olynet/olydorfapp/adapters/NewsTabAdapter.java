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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.NewsViewerActivity;
import eu.olynet.olydorfapp.fragments.NewsViewerFragment;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.ImageDeserializer;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.resource.SimpleImageListener;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsTabAdapter extends RecyclerView.Adapter<NewsTabAdapter.ViewHolder> {

    private final List<AbstractMetaItem<?>> items;
    private final List<SimpleImageListener> listeners;
    private final Map<Integer, OrganizationItem> organizationMap;
    private final Context context;

    private RecyclerView mRecyclerView = null;
    private View mEmptyView = null;

    /**
     * @param context         the Context.
     * @param newsItems       the List containing the NewsItems.
     * @param organizationMap the data structure that maps integers to their corresponding
     *                        OrganizationItems.
     */
    public NewsTabAdapter(Context context, List<AbstractMetaItem<?>> newsItems,
                          Map<Integer, OrganizationItem> organizationMap) {
        this.context = context;
        this.items = newsItems;
        this.organizationMap = organizationMap;

        this.listeners = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.card_news, parent, false);

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkVisibility();
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        ResourceManager rm = ProductionResourceManager.getInstance();
        for (SimpleImageListener listener : this.listeners) {
            rm.unregisterImageListener(listener, listener.oldItem);
        }
        listeners.clear();
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
        if (!(items.get(position) instanceof NewsItem)) {
            throw new IllegalArgumentException(
                    "the List of AbstractMetaItem<?>s provided does not" +
                    " seem to only contain NewsItems");
        }
        NewsItem newsItem = (NewsItem) items.get(position);
        OrganizationItem organizationItem = organizationMap.get(newsItem.getOrganization());
        if (organizationItem == null) {
            throw new IllegalArgumentException("OrganizationItem missing for id "
                                               + newsItem.getId());
        }

        /* set the correct item in the ViewHolder for the OnClickListener */
        holder.newsItem = newsItem;
        holder.organizationItem = organizationItem;

        /* Date */
        SimpleDateFormat localFormat
                = (SimpleDateFormat) android.text.format.DateFormat.getDateFormat(context);
        holder.vDate.setText(localFormat.format(newsItem.getDate()));

        /* Title */
        holder.vTitle.setText(newsItem.getTitle());

        /* Organization */
        holder.vOrganization.setText(organizationItem.getName());

        /* Image */
        if (!Arrays.equals(newsItem.getImage(), ImageDeserializer.MAGIC_VALUE)) {
            byte[] image = newsItem.getImage();
            int screenWidth = UtilsDevice.getScreenWidth(context);
            Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
            if (bitmap != null) {
                holder.vImage.setImageBitmap(bitmap);
            } else {
                holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
            }
        } else {
            holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
            SimpleImageListener listener = new SimpleImageListener(context, newsItem,
                                                                   holder.vImage);
            listeners.add(listener);
            ProductionResourceManager.getInstance()
                                     .registerImageListener(listener, newsItem);
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
     * @param items           the List of AbstractMetaItems to add.
     * @param organizationMap the Map containing the new OrganizationItems.
     */
    public void addAbstractMetaItems(List<AbstractMetaItem<?>> items,
                                     Map<Integer, OrganizationItem> organizationMap) {
        this.items.addAll(items);
        this.organizationMap.putAll(organizationMap);
    }

    /**
     * Replaces the internal List of AbstractMetaItems with a new one that <b>must</b> contain only
     * NewsItems.
     *
     * @param items           the new List of AbstractMetaItems to use.
     * @param organizationMap the Map containing the new OrganizationItems.
     */
    public void replaceAbstractMetaItems(List<AbstractMetaItem<?>> items,
                                         Map<Integer, OrganizationItem> organizationMap) {
        this.items.clear();
        this.items.addAll(items);
        this.organizationMap.clear();
        this.organizationMap.putAll(organizationMap);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        NewsItem newsItem;
        OrganizationItem organizationItem;

        final TextView vDate;
        final TextView vTitle;
        final TextView vOrganization;
        final ImageView vImage;

        ViewHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent newsViewerIntent = new Intent(context, NewsViewerActivity.class);
                newsViewerIntent.setAction(Intent.ACTION_VIEW);
                newsViewerIntent.putExtra(NewsViewerFragment.NEWS_KEY, newsItem);
                newsViewerIntent.putExtra(NewsViewerFragment.ORGANIZATION_KEY, organizationItem);
                context.startActivity(newsViewerIntent);
            });

            vOrganization = (TextView) view.findViewById(R.id.newsCardOrganization);
            vDate = (TextView) view.findViewById(R.id.newsCardDate);
            vTitle = (TextView) view.findViewById(R.id.newsCardTitle);
            vImage = (ImageView) view.findViewById(R.id.newsCardImage);
        }
    }
}
