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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class MealOfTheDayViewerFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final String MEAL_OF_THE_DAY_ITEM_KEY = "meal_of_the_day_item";
    public static final String DAILY_MEAL_KEY = "daily_meal_item";

    private SwipeRefreshLayout mRefreshLayout;

    private MealOfTheDayItem mealOfTheDayItem = null;
    private DailyMealItem dailyMealItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* get the MealOfTheDayItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mealOfTheDayItem = arguments.getParcelable(MEAL_OF_THE_DAY_ITEM_KEY);
            this.dailyMealItem = arguments.getParcelable(DAILY_MEAL_KEY);
        } else {
            Log.e("MealOTDFrag", "arguments bundle is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* inflate the layout for this Fragment */
        View view = inflater.inflate(R.layout.fragment_meal_of_the_day_viewer, container, false);

        /* set SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(
                R.id.meal_of_the_day_view_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnabled(false); /* disable for now */

        /* set the data */
        if (mealOfTheDayItem != null) {

            /* Name */
            TextView mealOfTheDayName = (TextView) view.findViewById(
                    R.id.meal_of_the_day_view_name);
            mealOfTheDayName.setText(dailyMealItem.getName());

            /* Cook */
            TextView mealOfTheDayCook = (TextView) view.findViewById(
                    R.id.meal_of_the_day_view_cook);
            mealOfTheDayCook.setText(mealOfTheDayItem.getCook());

            /* Icon */
            ImageView mealOfTheDayIcon = (ImageView) view.findViewById(
                    R.id.meal_of_the_day_view_icon);
            mealOfTheDayIcon.setImageResource(dailyMealItem.isVegetarian() ? R.drawable.carrot
                    : R.drawable.meat);

            /* Image */
            ImageView imageView = (ImageView) view.findViewById(R.id.meal_of_the_day_view_image);
            byte[] image = mealOfTheDayItem.getImage();
            int screenWidth = UtilsDevice.getScreenWidth(getContext());
            Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
            if (bitmap == null) { /* fallback to DailyMeal image */
                image = dailyMealItem.getImage();
                bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_account_circle_white_64dp);
            }
        }

        /* return the View */
        return view;
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setEnabled(false);
        // TODO: implement replacing of this Fragment
    }

}
