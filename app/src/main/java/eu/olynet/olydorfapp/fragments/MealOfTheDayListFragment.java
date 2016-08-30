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
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.MealOfTheDayListAdapter;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resource.ItemFilter;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;
import eu.olynet.olydorfapp.utils.SwipeRefreshLayoutWithEmpty;
import eu.olynet.olydorfapp.utils.UpdateAction;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class MealOfTheDayListFragment extends Fragment
        implements SwipeRefreshLayoutWithEmpty.OnRefreshListener {

    private static final int DEFAULT_COUNT = 10;

    private SwipeRefreshLayoutWithEmpty mRefreshLayout;
    private MealOfTheDayListAdapter mAdapter;

    private boolean refreshing = false;
    private boolean noFurtherResults = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_meal_of_the_day, container, false);

        /* initiate MealOfTheDayListTabAdapter */
        mAdapter = new MealOfTheDayListAdapter(getContext(), new ArrayList<>(), new TreeMap<>());

        /* setup the LayoutManager */
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        /* initiate RecycleView */
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(
                R.id.meal_of_the_day_card_list);
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
                    loadData(UpdateAction.ADD, DEFAULT_COUNT, false);
                }
            }
        });

        /* get the EmptyView and register it and the RecyclerView on the adapter */
        View mEmptyView = view.findViewById(R.id.meal_of_the_day_card_list_empty);
        mAdapter.registerViews(mRecyclerView, mEmptyView);

        /* initiate SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayoutWithEmpty) view.findViewById(
                R.id.meal_of_the_day_refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData(UpdateAction.REPLACE, DEFAULT_COUNT, false);
    }

    @Override
    public void onRefresh() {
        loadData(UpdateAction.REPLACE, DEFAULT_COUNT, true);
    }

    /**
     * Call this function to load new data asynchronously.
     *
     * @param action      what action to perform.
     * @param limit       how many new items to fetch at most.
     * @param forceUpdate whether an update of the cached data should be forced.
     */
    private void loadData(UpdateAction action, int limit, boolean forceUpdate) {
        /* set local refreshing variable */
        refreshing = true;

        /* disable swipe to refresh while already refreshing */
        mRefreshLayout.setEnabled(false);

        /* enable the refreshing animation if and only if it is not already enabled */
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(true));
        }

        /* start the AsyncTask that fetches the data */
        new MealOfTheDayUpdateTask(action, limit, forceUpdate).execute();
    }

    /**
     * This function must be called whenever the information loading is finished and the View
     * needs to be refreshed.
     *
     * @param action   the kind of action that has been performed.
     * @param position the position of the first new element (if applicable).
     * @param count    the number of new items affected (if applicable).
     */
    private void onLoadCompleted(UpdateAction action, int position, int count) {
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

    /**
     *
     */
    class MealOfTheDayUpdateTask extends AsyncTask<Void, Void, ResultStructure> {

        private final UpdateAction action;
        private final AbstractMetaItem<?> lastItem;
        private final int limit;
        private final boolean forceUpdate;

        public MealOfTheDayUpdateTask(UpdateAction action, int limit, boolean forceUpdate) {
            super();
            this.action = action;
            this.limit = limit;
            this.forceUpdate = forceUpdate;

            /* get the ID of the last item of this view */
            int size = mAdapter.getItemCount();
            if (this.action == UpdateAction.ADD && size > 0) {
                this.lastItem = mAdapter.getAbstractMetaItem(size - 1);
            } else {
                this.lastItem = null;
            }
        }

        @Override
        protected ResultStructure doInBackground(Void... params) {
            ResourceManager rm = ProductionResourceManager.getInstance();

            /* update OrganizationMetaItem tree */
            rm.getTreeOfMetaItems(OrganizationMetaItem.class, forceUpdate);
            rm.getTreeOfMetaItems(DailyMealMetaItem.class, forceUpdate);

            /* the correct Comparator */
            Comparator<AbstractMetaItem<?>> comparator = new AbstractMetaItem.DateAscComparator();

            /* filter only relevant items (i.e. today and in the future) */
            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            ItemFilter filter = abstractMetaItem ->
                    abstractMetaItem.getDate().compareTo(cal.getTime()) >= 0;

            /* querying the ResourceManager for the needed data */
            TreeSet<AbstractMetaItem<?>> resultTree = rm.getTreeOfMetaItems(
                    MealOfTheDayMetaItem.class,
                    this.limit,
                    this.lastItem,
                    comparator,
                    filter,
                    forceUpdate);

            /* null and empty check */
            if (resultTree == null || resultTree.isEmpty()) {
                return new ResultStructure();
            }

            /* getting the ids for only the necessary MealOfTheDayItems */
            List<Integer> ids = new ArrayList<>();
            for (AbstractMetaItem<?> item : resultTree) {
                ids.add(item.getId());
            }

            /* requesting the necessary MealOfTheDayItems */
            List<AbstractMetaItem<?>> mealOfTheDayItems = rm.getItems(MealOfTheDayMetaItem.class,
                    ids, comparator);

            /* get the necessary DailyMealItems */
            Set<Integer> dailyMealIds = new HashSet<>();
            for(AbstractMetaItem<?> item : mealOfTheDayItems) {
                MealOfTheDayItem mealOfTheDayItem = (MealOfTheDayItem) item;
                if (mealOfTheDayItem.getDailyMeal() != -1) {
                    dailyMealIds.add(mealOfTheDayItem.getDailyMeal());
                }
            }
            rm.getTreeOfMetaItems(DailyMealMetaItem.class, forceUpdate);
            List<AbstractMetaItem<?>> dailyMealItems = rm.getItems(DailyMealMetaItem.class,
                    dailyMealIds, null);

            /* sanity check */
            if (dailyMealItems == null || dailyMealItems.isEmpty()) {
                Log.w("MotDListFrag", "empty or null daily meal list");
                return new ResultStructure();
            }

            /* build the mapping */
            Map<Integer, DailyMealItem> dailyMealMap = new TreeMap<>();
            for (AbstractMetaItem<?> dailyMealItem : dailyMealItems) {
                dailyMealMap.put(dailyMealItem.getId(), (DailyMealItem) dailyMealItem);
            }

            /* return the combined result */
            return new ResultStructure(mealOfTheDayItems, dailyMealMap);
        }

        @Override
        protected void onPostExecute(ResultStructure result) {
            super.onPostExecute(result);

            /* get the old number of items (which is equivalent to the first new item) */
            int position = mAdapter.getItemCount();

            switch (this.action) {
                case ADD:
                    mAdapter.addAbstractMetaItems(result.mealOfTheDayItems, result.dailyMealMap);
                    break;
                default:
                    mAdapter.replaceAbstractMetaItems(result.mealOfTheDayItems,
                            result.dailyMealMap);
            }

            /* perform the post-load actions */
            onLoadCompleted(this.action, position, result.mealOfTheDayItems.size());
        }
    }

    private class ResultStructure {

        private final List<AbstractMetaItem<?>> mealOfTheDayItems;
        private final Map<Integer, DailyMealItem> dailyMealMap;

        /**
         * Empty result.
         */
        private ResultStructure() {
            this.mealOfTheDayItems = new ArrayList<>();
            this.dailyMealMap = new TreeMap<>();
        }

        private ResultStructure(List<AbstractMetaItem<?>> mealOfTheDayItems,
                                Map<Integer, DailyMealItem> dailyMealMap) {
            this.mealOfTheDayItems = mealOfTheDayItems;
            this.dailyMealMap = dailyMealMap;
        }

    }
}
