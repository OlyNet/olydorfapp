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

import java.text.NumberFormat;
import java.util.Locale;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.DrinkItem;
import eu.olynet.olydorfapp.model.DrinkSizeItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class DrinkViewerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String DRINK_ITEM_KEY = "drink_item";

    private SwipeRefreshLayout mRefreshLayout;

    private DrinkItem drinkItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* get the DrinkItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.drinkItem = arguments.getParcelable(DRINK_ITEM_KEY);
        } else {
            Log.e("DrinkViewerFrag", "arguments bundle is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* inflate the layout for this Fragment */
        View view = inflater.inflate(R.layout.fragment_drink_viewer, container, false);

        /* set SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.drink_view_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnabled(false); /* disable for now */

        /* set the data */
        if (drinkItem != null) {

            /* Name */
            TextView drinkName = (TextView) view.findViewById(R.id.drink_view_name);
            drinkName.setText(drinkItem.getName());

            /* Icon */
//            ImageView drinkIcon = (ImageView) view.findViewById(R.id.drink_view_icon);
//            drinkIcon.setImageResource(drinkItem.isVegetarian() ? R.drawable.carrot
//                                                                : R.drawable.meat);

            /* Image */
            ImageView drinkImage = (ImageView) view.findViewById(R.id.drink_view_image);
            byte[] image = drinkItem.getImage();
            int screenWidth = UtilsDevice.getScreenWidth(getContext());
            Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
            if (bitmap != null) {
                drinkImage.setImageBitmap(bitmap);
            } else {
                drinkImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
            }

            /* Amounts */
            TextView drinkAmounts = (TextView) view.findViewById(R.id.drink_view_amounts);
            String amountPrices = "";
            NumberFormat deDE = NumberFormat.getCurrencyInstance(Locale.GERMANY);
            for (DrinkSizeItem drinkSize : drinkItem.getDrinkSizes()) {
                amountPrices += drinkSize.getSize() + " l" + "\t-\t"
                                + deDE.format(drinkSize.getPrice()) + "\n";
            }
            drinkAmounts.setText(amountPrices.substring(0, amountPrices.length() - 1));
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
