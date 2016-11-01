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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.BierstubeTabAdapter;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.DrinkMetaItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.resource.ItemFilter;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class BierstubeTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mRefreshLayout;
    private BierstubeTabAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_bierstube, container, false);

        /* initiate NewsTabAdapter */
        mAdapter = new BierstubeTabAdapter(getContext());

        /* setup the LayoutManager */
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1,
                                                                       GridLayoutManager.VERTICAL,
                                                                       false);

        /* initiate RecycleView */
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(
                R.id.BierstubeGridRecyclerView);
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
    private void loadData(boolean forceUpdate) {
        /* disable swipe to refresh while already refreshing */
        mRefreshLayout.setEnabled(false);

        /* enable the refreshing animation if and only if it is not already enabled */
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(true));
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
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(false));
        mRefreshLayout.setEnabled(true);
    }

    /**
     *
     */
    class BierstubeUpdateTask extends AsyncTask<Void, Void, ResultStructure> {

        private final boolean forceUpdate;

        public BierstubeUpdateTask(boolean forceUpdate) {
            super();
            this.forceUpdate = forceUpdate;
        }

        @Override
        protected ResultStructure doInBackground(Void... params) {
            ResourceManager rm = ProductionResourceManager.getInstance();

            /* get all DrinkItems */
            List<Integer> drinkIds = new ArrayList<>();
            for (AbstractMetaItem<?> item : rm.getTreeOfMetaItems(DrinkMetaItem.class,
                                                                  this.forceUpdate)) {
                drinkIds.add(item.getId());
            }
            List<AbstractMetaItem<?>> drinkItems = rm.getItems(DrinkMetaItem.class, drinkIds, null);

            /* get all FoodItems */
            List<Integer> foodIds = new ArrayList<>();
            for (AbstractMetaItem<?> item : rm.getTreeOfMetaItems(FoodMetaItem.class,
                                                                  this.forceUpdate)) {
                foodIds.add(item.getId());
            }
            List<AbstractMetaItem<?>> foodItems = rm.getItems(FoodMetaItem.class, foodIds, null);

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
            TreeSet<AbstractMetaItem<?>> metaTree = rm.getTreeOfMetaItems(
                    MealOfTheDayMetaItem.class,
                    1,
                    null,
                    comparator,
                    filter,
                    forceUpdate);

            /* sanity check */
            if (metaTree == null || metaTree.isEmpty()) {
                Log.w("BierstubeTab", "MealOfTheDayItem metaTree is null or empty");
                return new ResultStructure(null, null, foodItems, drinkItems);
            }

            /* filter out the correct meta item */
            MealOfTheDayMetaItem filterItem = new AbstractMetaItem.DummyFactory<>(
                    MealOfTheDayMetaItem.class).setDate(new Date()).build();
            MealOfTheDayMetaItem metaItem = (MealOfTheDayMetaItem) metaTree.floor(filterItem);

            /* get the correct meal */
            MealOfTheDayItem mealOfTheDayItem = null;
            if (metaItem != null) {
                /* verify that this metaItem is indeed for today */
                Calendar itemDate = Calendar.getInstance();
                itemDate.setTime(metaItem.getDate());
                Calendar now = Calendar.getInstance();

                /* fetch the full item only if the date matches */
                if (itemDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    itemDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                    itemDate.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
                    mealOfTheDayItem = (MealOfTheDayItem) rm.getItem(MealOfTheDayMetaItem.class,
                                                                     metaItem.getId());
                }
            }

            /* sanity check */
            if (mealOfTheDayItem == null) {
                return new ResultStructure(null, null, foodItems, drinkItems);
            }

            /* get the corresponding DailyMealItem */
            rm.getTreeOfMetaItems(DailyMealMetaItem.class, this.forceUpdate);
            DailyMealItem dailyMealItem = (DailyMealItem) rm.getItem(DailyMealMetaItem.class,
                                                                     mealOfTheDayItem
                                                                             .getDailyMeal());

            /* return the combined results */
            return new ResultStructure(mealOfTheDayItem, dailyMealItem, foodItems, drinkItems);
        }

        @Override
        protected void onPostExecute(ResultStructure result) {
            super.onPostExecute(result);

            /* update the Adapter */
            mAdapter.setData(result.mealOfTheDayItem, result.dailyMealItem, result.foodItems,
                             result.drinkItems);

            /* perform the post-load actions */
            onLoadCompleted();
        }
    }

    private class ResultStructure {

        final MealOfTheDayItem mealOfTheDayItem;
        final DailyMealItem dailyMealItem;
        final List<AbstractMetaItem<?>> foodItems;
        final List<AbstractMetaItem<?>> drinkItems;

        private ResultStructure(MealOfTheDayItem mealOfTheDayItem,
                                DailyMealItem dailyMealItem, List<AbstractMetaItem<?>> foodItems,
                                List<AbstractMetaItem<?>> drinkItems) {
            this.mealOfTheDayItem = mealOfTheDayItem;
            this.dailyMealItem = dailyMealItem;
            this.foodItems = foodItems;
            this.drinkItems = drinkItems;
        }

    }
}
