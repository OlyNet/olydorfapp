/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.DailyMealDataAdapter;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class BierstubeTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mRefreshLayout;
    private DailyMealDataAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_bierstube, container, false);

        /* initiate NewsDataAdapter */
        mAdapter = new DailyMealDataAdapter(getContext(), null);

        /* setup the LayoutManager */
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1,
                GridLayoutManager.VERTICAL, false);

        /* initiate RecycleView */
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.BierstubeGridRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* initiate SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.BierstubeRefreshLayout);
        mRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /**
     * Call this function to load new data asynchronously.
     */
    public void loadData() {
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
        new BierstubeUpdateTask().execute();
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

    protected class BierstubeUpdateTask extends AsyncTask<Void, Void, AbstractMetaItem<?>> {

        public BierstubeUpdateTask() {
            super();
        }

        @Override
        protected AbstractMetaItem<?> doInBackground(Void... params) {
            ResourceManager rm = ResourceManager.getInstance();

            /* update OrganizationMetaItem and DailyMealMetaItem trees */
            rm.getTreeOfMetaItems(OrganizationMetaItem.class);
            rm.getTreeOfMetaItems(DailyMealMetaItem.class);

            /* querying the ResourceManager for the needed data and order it correctly */
            TreeSet<AbstractMetaItem<?>> metaTree = rm.getTreeOfMetaItems(MealOfTheDayMetaItem.class, 0,
                    null, new AbstractMetaItem.DateAscComparator());

            MealOfTheDayMetaItem filterItem = new MealOfTheDayMetaItem(new Date(), null);
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

            mAdapter.setItem((MealOfTheDayItem) result);

            /* perform the post-load actions */
            onLoadCompleted();
        }
    }
}
