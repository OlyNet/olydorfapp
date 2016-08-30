/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.NewsTabAdapter;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resource.ItemFilter;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;
import eu.olynet.olydorfapp.utils.SwipeRefreshLayoutWithEmpty;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsTab extends Fragment implements SwipeRefreshLayoutWithEmpty.OnRefreshListener {

    public static final String ORGANIZATION_KEY = "organization_item";
    private static final int DEFAULT_COUNT = 10;

    private SwipeRefreshLayoutWithEmpty mRefreshLayout;
    private NewsTabAdapter mAdapter;

    private boolean refreshing = false;
    private boolean noFurtherResults = false;

    private OrganizationMetaItem filterOrganization = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_news, container, false);

        /* get the filter OrganizationMetaItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.filterOrganization = arguments.getParcelable(ORGANIZATION_KEY);
        } else {
            Log.d("NewsTab", "arguments bundle is null");
        }

        /* initiate NewsTabAdapter */
        mAdapter = new NewsTabAdapter(getContext(), new ArrayList<>(), new TreeMap<>());

        /* setup the LayoutManager */
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        /* initiate RecycleView */
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.news_card_list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        /* http://stackoverflow.com/a/26643292/3997552 */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                /* check for scroll down, second-to-last item visible and not already refreshing */
                if (!noFurtherResults && dy > 0 && !mRefreshLayout.isRefreshing() && !refreshing &&
                        mLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                mAdapter.getItemCount() - 2) {
                    loadData(Action.ADD, DEFAULT_COUNT, false);
                }
            }
        });

        /* get the EmptyView and register it and the RecyclerView on the adapter */
        View mEmptyView = view.findViewById(R.id.news_card_list_empty);
        mAdapter.registerViews(mRecyclerView, mEmptyView);

        /* initiate SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayoutWithEmpty) view.findViewById(R.id.news_refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData(Action.REPLACE, DEFAULT_COUNT, false);
    }

    @Override
    public void onRefresh() {
        loadData(Action.REPLACE, DEFAULT_COUNT, true);
    }

    /**
     * Call this function to load new data asynchronously.
     *
     * @param action      what action to perform.
     * @param limit       how many new items to fetch at most.
     * @param forceUpdate whether an update of the cached data should be forced.
     */
    private void loadData(Action action, int limit, boolean forceUpdate) {
        /* set local refreshing variable */
        refreshing = true;

        /* disable swipe to refresh while already refreshing */
        mRefreshLayout.setEnabled(false);

        /* enable the refreshing animation if and only if it is not already enabled */
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(true));
        }

        /* start the AsyncTask that fetches the data */
        new NewsUpdateTask(action, limit, filterOrganization, forceUpdate).execute();
    }

    /**
     * This function must be called whenever the information loading is finished and the View
     * needs to be refreshed.
     *
     * @param action   the kind of action that has been performed.
     * @param position the position of the first new element (if applicable).
     * @param count    the number of new items affected (if applicable).
     */
    private void onLoadCompleted(Action action, int position, int count) {
        switch (action) {
            case ADD:
                if (count > 0) {
                    mAdapter.notifyItemRangeInserted(position, count);
                }
                break;
            default:
                mAdapter.notifyDataSetChanged();
        }

        /* check the visibility of the adapter */
        mAdapter.checkVisibility();

        /* whether further results are expected */
        noFurtherResults = count < DEFAULT_COUNT;

        /* disable refreshing animation and enable swipe to refresh again */
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(false));
        mRefreshLayout.setEnabled(true);
        refreshing = false;
    }

    private enum Action {REPLACE, ADD}

    /**
     *
     */
    class NewsUpdateTask extends AsyncTask<Void, Void, ResultStructure> {

        private final Action action;
        private final AbstractMetaItem<?> lastItem;
        private final OrganizationMetaItem filterOrganization;
        private final int limit;
        private final boolean forceUpdate;

        public NewsUpdateTask(Action action, int limit, OrganizationMetaItem filterOrganization,
                              boolean forceUpdate) {
            super();
            this.action = action;
            this.filterOrganization = filterOrganization;
            this.limit = limit;
            this.forceUpdate = forceUpdate;

            /* get the ID of the last item of this view */
            int size = mAdapter.getItemCount();
            if (this.action == Action.ADD && size > 0) {
                this.lastItem = mAdapter.getAbstractMetaItem(size - 1);
            } else {
                this.lastItem = null;
            }
        }

        @Override
        protected ResultStructure doInBackground(Void... params) {
            ResourceManager rm = ProductionResourceManager.getInstance();

            /* Organization filter */
            ItemFilter filter = abstractMetaItem -> filterOrganization == null ||
                    abstractMetaItem.getOrganization() == this.filterOrganization.getId();

            /* querying the ResourceManager for the needed data and order it correctly */
            TreeSet<AbstractMetaItem<?>> resultTree = rm.getTreeOfMetaItems(NewsMetaItem.class,
                    this.limit,
                    this.lastItem,
                    new AbstractMetaItem.DateDescComparator(),
                    filter,
                    forceUpdate);

            /* sanity check */
            if (resultTree == null || resultTree.isEmpty()) {
                return new ResultStructure();
            }

            /* getting the ids for only the necessary NewsItems */
            List<Integer> ids = new ArrayList<>();
            Set<Integer> orgIds = new HashSet<>();
            for (AbstractMetaItem<?> item : resultTree) {
                ids.add(item.getId());
                if (item.getOrganization() != -1) {
                    orgIds.add(item.getOrganization());
                }
            }

            /* requesting the NewsItems */
            List<AbstractMetaItem<?>> newsItems =  rm.getItems(NewsMetaItem.class, ids,
                    new AbstractMetaItem.DateDescComparator());

            /* get the necessary OrganizationItems */
            rm.getTreeOfMetaItems(OrganizationMetaItem.class, forceUpdate);
            List<AbstractMetaItem<?>> organizationItems = rm.getItems(OrganizationMetaItem.class,
                    orgIds, null);

            /* sanity check */
            if (organizationItems == null || organizationItems.isEmpty()) {
                Log.w("NewsTab", "empty or null organization list");
                return new ResultStructure();
            }

            /* build the mapping */
            Map<Integer, OrganizationItem> organizationMap = new TreeMap<>();
            for(AbstractMetaItem<?> organizationItem : organizationItems) {
                organizationMap.put(organizationItem.getId(), (OrganizationItem) organizationItem);
            }

            /* return the combined result */
            return new ResultStructure(newsItems, organizationMap);
        }

        @Override
        protected void onPostExecute(ResultStructure result) {
            super.onPostExecute(result);

            /* get the old number of items (which is equivalent to the first new item) */
            int position = mAdapter.getItemCount();

            switch (this.action) {
                case ADD:
                    mAdapter.addAbstractMetaItems(result.newsItems, result.organizationMap);
                    break;
                default:
                    mAdapter.replaceAbstractMetaItems(result.newsItems, result.organizationMap);
            }

            /* perform the post-load actions */
            onLoadCompleted(this.action, position, result.newsItems.size());
        }
    }

    private class ResultStructure {

        private final List<AbstractMetaItem<?>> newsItems;
        private final Map<Integer, OrganizationItem> organizationMap;

        /**
         * Empty result.
         */
        private ResultStructure() {
            this.newsItems = new ArrayList<>();
            this.organizationMap = new TreeMap<>();
        }

        private ResultStructure(List<AbstractMetaItem<?>> newsItems,
                                Map<Integer, OrganizationItem> organizationMap) {
            this.newsItems = newsItems;
            this.organizationMap = organizationMap;
        }

    }
}
