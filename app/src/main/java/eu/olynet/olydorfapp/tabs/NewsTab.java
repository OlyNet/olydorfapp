/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.NewsDataAdapter;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int DEFAULT_COUNT = 10;

    private enum Action {REPLACE, ADD}

    private SwipeRefreshLayout mRefreshLayout;
    private NewsDataAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_news, container, false);

        /* initiate NewsDataAdapter */
        mAdapter = new NewsDataAdapter(getContext(), new ArrayList<AbstractMetaItem<?>>());

        /* setup the LayoutManager */
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        /* initiate RecycleView */
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.news_card_list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // https://stackoverflow.com/questions/26543131/how-to-implement-endless-list-with-recyclerview
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                /* check for scroll down, second-to-last item visible and not already refreshing */
                if (dy > 0 && !mRefreshLayout.isRefreshing() &&
                        mLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                mAdapter.getItemCount() - 2) {
                    loadData(Action.ADD, DEFAULT_COUNT);
                }
            }
        });

        /* initiate SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.news_refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData(Action.REPLACE, DEFAULT_COUNT);
    }

    @Override
    public void onRefresh() {
        loadData(Action.REPLACE, DEFAULT_COUNT);
    }

    /**
     * Call this function to load new data asynchronously.
     *
     * @param action what action to perform.
     * @param limit how many new items to fetch at most.
     */
    public void loadData(Action action, int limit) {
        /* disable swipe to refresh while already refreshing */
        mRefreshLayout.setEnabled(false);

        /* enable the refreshing animation if and only if it is not already enabled */
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(true);
        }

        /* start the AsyncTask that fetches the data */
        new NewsUpdateTask(action, limit).execute();
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
                mAdapter.notifyItemRangeInserted(position, count);
                break;
            default:
                mAdapter.notifyDataSetChanged();
        }

        /* disable refreshing animation and enable swipe to refresh again */
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setEnabled(true);
    }

    protected class NewsUpdateTask extends AsyncTask<Void, Void, List<AbstractMetaItem<?>>> {

        private final Action action;
        private final int lastID;
        private final int limit;

        public NewsUpdateTask(Action action, int limit) {
            this.action = action;
            this.limit = limit;

            /* get the ID of the last item of this view */
            int size = mAdapter.getItemCount();
            if (size > 0) {
                this.lastID = mAdapter.getAbstractMetaItem(size - 1).getId();
            } else {
                this.lastID = -1;
            }
        }

        @Override
        protected List<AbstractMetaItem<?>> doInBackground(Void... params) {
            ResourceManager rm = ResourceManager.getInstance();

            /* querying the ResourceManager for the needed data and order it correctly */
            TreeSet<AbstractMetaItem<?>> tree = new TreeSet<>(new AbstractMetaItem.DateDescComparator());
            tree.addAll(rm.getTreeOfMetaItems(NewsMetaItem.class));

            // TODO: implement incremental fetching

            int count = 0;
            List<Integer> ids = new ArrayList<>();
            for (AbstractMetaItem<?> item : tree) {
                /* enforce item limit */
                if (count++ >= limit) {
                    break;
                }

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
