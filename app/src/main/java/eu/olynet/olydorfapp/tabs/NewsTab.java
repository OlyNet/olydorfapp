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

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_news, container, false);

        /* initiate SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.news_refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        /* initiate RecycleView */
        mRecyclerView = (RecyclerView) v.findViewById(R.id.news_card_list);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new NewsUpdateTask().execute();
    }

    @Override
    public void onRefresh() {
        /* disable swipe to refresh while already refreshing */
        mRefreshLayout.setEnabled(false);

        // TODO: implement refreshing

        /* do not call this from this function directly but from the NewsUpdateTask */
        onLoadCompleted();
    }

    private void onLoadCompleted() {
        // TODO: update the adapter and notify data set changed

        /* disable refreshing animation and enable swipe to refresh again */
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setEnabled(true);

    }

    protected class NewsUpdateTask extends AsyncTask<Void, Void, List<AbstractMetaItem<?>>> {

        @Override
        protected List<AbstractMetaItem<?>> doInBackground(Void... params) {
            ResourceManager rm = ResourceManager.getInstance();

                /* querying the ResourceManager for the needed data */
            TreeSet<AbstractMetaItem<?>> tree = rm.getTreeOfMetaItems(NewsMetaItem.class);

            List<Integer> ids = new ArrayList<>();
            for(AbstractMetaItem<?> item : tree) {
                ids.add(item.getId());
            }

                /* requesting and returning the result array */
            return rm.getItems(NewsMetaItem.class, ids,
                    new AbstractMetaItem.DateDescComparator());
        }

        @Override
        protected void onPostExecute(List<AbstractMetaItem<?>> result) {
            super.onPostExecute(result);

            mAdapter = new NewsDataAdapter(getContext(), result);

            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            onLoadCompleted();
        }
    }
}
