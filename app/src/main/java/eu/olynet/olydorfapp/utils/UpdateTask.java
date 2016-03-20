/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import eu.olynet.olydorfapp.activities.MainActivity;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class UpdateTask extends AsyncTask<Void, Void, Void> {

    private Context mCon;

    public UpdateTask(Context con) {
        mCon = con;
    }

    @Override
    protected Void doInBackground(Void... nope) {
        ResourceManager rm = ResourceManager.getInstance();

        rm.getTreeOfMetaItems(OrganizationMetaItem.class);
        rm.getTreeOfMetaItems(DailyMealMetaItem.class);
        TreeSet<AbstractMetaItem<?>> metaTree = rm.getTreeOfMetaItems(MealOfTheDayMetaItem.class, 0,
                null, new AbstractMetaItem.DateAscComparator());

        MealOfTheDayMetaItem filterItem = new MealOfTheDayMetaItem(new Date(), null);
        MealOfTheDayMetaItem metaItem = (MealOfTheDayMetaItem) metaTree.floor(filterItem);

        if(metaItem != null) {
            MealOfTheDayItem meal = (MealOfTheDayItem) rm.getItem(MealOfTheDayMetaItem.class,
                    metaItem.getId());
            Log.e("UpdateTask", meal != null ? meal.toString() : "null");
        }


        // TODO: implement view refreshing here

        return null;

    }

    @Override
    protected void onPostExecute(Void nope) {
        // Give some feedback on the UI.
        Toast.makeText(mCon, "Finished complex background function!",
                Toast.LENGTH_LONG).show();

        // Change the menu back
        ((MainActivity) mCon).resetUpdating();
    }
}
