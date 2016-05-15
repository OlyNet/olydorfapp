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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.NewsTabAdapter;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int DEFAULT_COUNT = 10;

    private enum Action {REPLACE, ADD}

    private SwipeRefreshLayout mRefreshLayout;
    private NewsTabAdapter mAdapter;

    private boolean refreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_news, container, false);

        /* initiate NewsTabAdapter */
        mAdapter = new NewsTabAdapter(getContext(), new ArrayList<AbstractMetaItem<?>>());

        /* setup the LayoutManager */
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        /* initiate RecycleView */
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.news_card_list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // https://stackoverflow.com/questions/26543131/how-to-implement-endless-list-with-recyclerview
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                /* check for scroll down, second-to-last item visible and not already refreshing */
                if (dy > 0 && !mRefreshLayout.isRefreshing() && !refreshing &&
                        mLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                mAdapter.getItemCount() - 2) {
                    loadData(Action.ADD, DEFAULT_COUNT, false);
                }
            }
        });

        /* initiate SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void loadData(Action action, int limit, boolean forceUpdate) {
        /* set local refreshing variable */
        refreshing = true;

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
        new NewsUpdateTask(action, limit, forceUpdate).execute();
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

        /* disable refreshing animation and enable swipe to refresh again */
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setEnabled(true);
        refreshing = false;
    }

    /**
     *
     */
    protected class NewsUpdateTask extends AsyncTask<Void, Void, List<AbstractMetaItem<?>>> {

        private final Action action;
        private final AbstractMetaItem<?> lastItem;
        private final int limit;
        private final boolean forceUpdate;

        public NewsUpdateTask(Action action, int limit, boolean forceUpdate) {
            super();
            this.action = action;
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
        protected List<AbstractMetaItem<?>> doInBackground(Void... params) {
            ResourceManager rm = ResourceManager.getInstance();

            /* update OrganizationMetaItem tree */
            rm.getTreeOfMetaItems(OrganizationMetaItem.class, forceUpdate);

            /* querying the ResourceManager for the needed data and order it correctly */
            TreeSet<AbstractMetaItem<?>> resultTree = rm.getTreeOfMetaItems(NewsMetaItem.class,
                    this.limit, this.lastItem, new AbstractMetaItem.DateDescComparator(),
                    forceUpdate);
            if (resultTree == null || resultTree.isEmpty()) {
                return new ArrayList<>();
            }

            /* getting just the ids for the needed NewsItems */
            List<Integer> ids = new ArrayList<>();
            for (AbstractMetaItem<?> item : resultTree) {
                ids.add(item.getId());
            }

            /* requesting and returning the result array */
            return rm.getItems(NewsMetaItem.class, ids, new AbstractMetaItem.DateDescComparator());
        }

        @Override
        protected void onPostExecute(List<AbstractMetaItem<?>> result) {
            super.onPostExecute(result);

            /* get the old number of items (which is equivalent to the first new item) */
            int position = mAdapter.getItemCount();

            switch (this.action) {
                case ADD:
                    mAdapter.addAbstractMetaItems(result);
                    break;
                default:
                    mAdapter.replaceAbstractMetaItems(result);
            }

            /* perform the post-load actions */
            onLoadCompleted(this.action, position, result.size());
        }
    }
}
