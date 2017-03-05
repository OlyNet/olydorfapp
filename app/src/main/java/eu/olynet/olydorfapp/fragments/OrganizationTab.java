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
package eu.olynet.olydorfapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.ImageDeserializer;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;
import eu.olynet.olydorfapp.resource.SimpleImageListener;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class OrganizationTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ORG_KEY = "organization_item";

    private SwipeRefreshLayout mRefreshLayout;

    private OrganizationMetaItem organizationDummyItem = null;

    private TextView contentView;
    private ImageView imageView;

    private final List<SimpleImageListener> listeners = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_organization, container, false);

        contentView = (TextView) view.findViewById(R.id.organization_tab_content);
        imageView = (ImageView) view.findViewById(R.id.organization_tab_image);

        /* get the filter OrganizationMetaItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.organizationDummyItem = arguments.getParcelable(ORG_KEY);
        } else {
            throw new RuntimeException("no OrganizationMetaItem present in this Fragment's Bundle");
        }

        /* initiate SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(
                R.id.organization_refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        /* load the actual data */
        loadData(false);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        ResourceManager rm = ProductionResourceManager.getInstance();
        for (SimpleImageListener listener : this.listeners) {
            rm.unregisterImageListener(listener, listener.oldItem);
        }
        listeners.clear();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    /**
     * Call this function to load new data asynchronously.
     *
     * @param forceUpdate whether an update of the cached data should be forced.
     */
    private void loadData(boolean forceUpdate) {
        /* disable swipe to refresh while already refreshing */
        mRefreshLayout.setEnabled(false);

        /* enable the refreshing animation if and only if it is not already enabled */
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(true));
        }

        /* start the AsyncTask that fetches the data */
        new OrganizationUpdateTask(organizationDummyItem, forceUpdate).execute();
    }

    /**
     * This function must be called whenever the information loading is finished and the View
     * needs to be refreshed.
     */
    private void onLoadCompleted(OrganizationItem item) {
        if (item != null) {
            String description = item.getDescription() != null ? item.getDescription() : "";
            Spanned content;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                content = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT);
            } else {
                content = Html.fromHtml(description);
            }
            contentView.setText(content);
            contentView.setMovementMethod(LinkMovementMethod.getInstance());

            /* set the image if one is available */
            if (!Arrays.equals(item.getImage(), ImageDeserializer.MAGIC_VALUE)) {
                byte[] image = item.getImage();
                int screenWidth = UtilsDevice.getScreenWidth(getContext());
                Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                SimpleImageListener listener = new SimpleImageListener(getContext(), item,
                                                                       imageView);
                listeners.add(listener);
                ProductionResourceManager.getInstance().registerImageListener(listener, item);
            }

            /* link to the organization's website in the ImageView's onClickListener */
            imageView.setOnClickListener((view) -> {
                if (item.getLink() != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(item.getLink()));
                    startActivity(intent);
                }
            });
        } else {
            Log.w("OrganizationTab", "OrganizationItem is null");
        }

        /* disable refreshing animation and enable swipe to refresh again */
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(false));
        mRefreshLayout.setEnabled(true);
    }


    /**
     * Queries the ResourceManager for the full OrganizationItem.
     */
    private class OrganizationUpdateTask extends AsyncTask<Void, Void, OrganizationItem> {

        private final OrganizationMetaItem organizationDummyItem;
        private final boolean forceUpdate;

        OrganizationUpdateTask(OrganizationMetaItem organizationDummyItem,
                               boolean forceUpdate) {
            super();
            this.organizationDummyItem = organizationDummyItem;
            this.forceUpdate = forceUpdate;
        }

        @Override
        protected OrganizationItem doInBackground(Void... params) {
            ResourceManager rm = ProductionResourceManager.getInstance();

            /* update OrganizationMetaItem tree */
            TreeSet<AbstractMetaItem<?>> tree = rm.getTreeOfMetaItems(OrganizationMetaItem.class,
                                                                      this.forceUpdate);

            /* sanity check */
            if (tree == null || tree.isEmpty()) {
                return null;
            } else {
                return (OrganizationItem) rm.getItem(OrganizationMetaItem.class,
                                                     this.organizationDummyItem.getId());
            }
        }

        @Override
        protected void onPostExecute(OrganizationItem item) {
            super.onPostExecute(item);

            /* perform the post-load actions */
            onLoadCompleted(item);
        }
    }

}
