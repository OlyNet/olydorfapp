package eu.olynet.olydorfapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class OrganizationTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ORG_KEY = "organization_item";

    private SwipeRefreshLayout mRefreshLayout;

    private OrganizationMetaItem organizationDummyItem = null;

    private TextView contentView;
    private ImageView imageView;

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
            String description = item.getDescription();
            contentView.setText(Html.fromHtml(description != null ? description : ""));

            /* set the image if one is available */
            byte[] image = item.getLogo();
            if (image != null && image.length > 0) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                if (imageBitmap != null) {
                    DisplayMetrics dm = new DisplayMetrics();
                    WindowManager windowManager = (WindowManager) getContext().getSystemService(
                            Context.WINDOW_SERVICE);
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    imageView.setImageBitmap(imageBitmap);
                }
            }
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
    class OrganizationUpdateTask extends AsyncTask<Void, Void, OrganizationItem> {

        private final OrganizationMetaItem organizationDummyItem;
        private final boolean forceUpdate;

        public OrganizationUpdateTask(OrganizationMetaItem organizationDummyItem,
                                      boolean forceUpdate) {
            super();
            this.organizationDummyItem = organizationDummyItem;
            this.forceUpdate = forceUpdate;
        }

        @Override
        protected OrganizationItem doInBackground(Void... params) {
            ResourceManager rm = ProductionResourceManager.getInstance();

            /* update OrganizationMetaItem tree */
            rm.getTreeOfMetaItems(OrganizationMetaItem.class, forceUpdate);

            return (OrganizationItem) rm.getItem(OrganizationMetaItem.class,
                                                 this.organizationDummyItem.getId());
        }

        @Override
        protected void onPostExecute(OrganizationItem item) {
            super.onPostExecute(item);

            /* perform the post-load actions */
            onLoadCompleted(item);
        }
    }

}
