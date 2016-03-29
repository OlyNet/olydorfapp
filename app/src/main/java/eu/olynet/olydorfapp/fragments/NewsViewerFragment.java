package eu.olynet.olydorfapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class NewsViewerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* instantiate ResourceManager if this has not happened yet */
        ResourceManager rm = ResourceManager.getInstance();
        if (!rm.isInitialized()) {
            rm.init(getContext().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.news_view_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData(false);
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
    public void loadData(boolean forceUpdate) {
        /* disable swipe to refresh while already refreshing */
        mRefreshLayout.setEnabled(false);

        /* enable the refreshing animation if and only if it is not already enabled */
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
        }

        /* start the AsyncTask that fetches the data */
        new BierstubeUpdateTask(forceUpdate).execute();
    }

    /**
     * This function must be called whenever the information loading is finished and the View
     * needs to be refreshed.
     */
    private void onLoadCompleted() {
        mAdapter.notifyDataSetChanged();

        /* disable refreshing animation and enable swipe to refresh again */
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setEnabled(true);
    }

    protected class NewsViewerAsyncTask extends AsyncTask<Void, Void, AbstractMetaItem<?>> {

        private final int id;
        private final boolean forceUpdate;


        public NewsViewerAsyncTask(int id, boolean forceUpdate) {
            super();
            this.id = id;
            this.forceUpdate = forceUpdate;
        }

        @Override
        protected AbstractMetaItem<?> doInBackground(Void... params) {
            ResourceManager rm = ResourceManager.getInstance();

            /* update OrganizationMetaItem and DailyMealMetaItem trees */
            rm.getTreeOfMetaItems(OrganizationMetaItem.class, forceUpdate);
            rm.getTreeOfMetaItems(DailyMealMetaItem.class, forceUpdate);

            /* querying the ResourceManager for the needed data */
            TreeSet<AbstractMetaItem<?>> metaTree = rm.getTreeOfMetaItems(MealOfTheDayMetaItem.class,
                    0, null, new AbstractMetaItem.DateAscComparator(), forceUpdate);

            /* filter out the correct meta item */
            MealOfTheDayMetaItem filterItem = new AbstractMetaItem.DummyFactory<>(
                    MealOfTheDayMetaItem.class)
                    .setDate(new Date())
                    .build();
            MealOfTheDayMetaItem metaItem = (MealOfTheDayMetaItem) metaTree.floor(filterItem);

            /* get the correct meal */
            MealOfTheDayItem meal = null;
            if (metaItem != null) {
                meal = (MealOfTheDayItem) rm.getItem(MealOfTheDayMetaItem.class,
                        metaItem.getId());
            }

            /* requesting and returning the result array */
            return meal;
        }

        @Override
        protected void onPostExecute(AbstractMetaItem<?> result) {
            super.onPostExecute(result);

            /* update the Adapter */
            mAdapter.setItem((MealOfTheDayItem) result);

            /* perform the post-load actions */
            onLoadCompleted();
        }
    }

}
