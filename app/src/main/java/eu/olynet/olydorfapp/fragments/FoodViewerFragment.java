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
import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class FoodViewerFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final String FOOD_ITEM_KEY = "food_item";

    private SwipeRefreshLayout mRefreshLayout;

    private FoodItem foodItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* get the MealOfTheDayItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.foodItem = arguments.getParcelable(FOOD_ITEM_KEY);
        } else {
            Log.e("FoodViewerFrag", "arguments bundle is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* inflate the layout for this Fragment */
        View view = inflater.inflate(R.layout.fragment_food_viewer, container, false);

        /* set SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.food_view_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnabled(false); /* disable for now */

        /* set the data */
        if (foodItem != null) {

            /* Name */
            TextView mealOfTheDayName = (TextView) view.findViewById(R.id.food_view_name);
            mealOfTheDayName.setText(foodItem.getName());

            /* Cook */
//            TextView mealOfTheDayCook = (TextView) view.findViewById(R.id.food_view_cook);
//            mealOfTheDayCook.setText(mealOfTheDayItem.getCook());

            /* Icon */
            ImageView mealOfTheDayIcon = (ImageView) view.findViewById(R.id.food_view_icon);
            mealOfTheDayIcon.setImageResource(foodItem.isVegetarian() ? R.drawable.carrot_48dp
                    : R.drawable.meat_48dp);

            /* Image */
            ImageView imageView = (ImageView) view.findViewById(R.id.food_view_image);
            byte[] image = foodItem.getImage();
            int screenWidth = UtilsDevice.getScreenWidth(getContext());
            Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
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
