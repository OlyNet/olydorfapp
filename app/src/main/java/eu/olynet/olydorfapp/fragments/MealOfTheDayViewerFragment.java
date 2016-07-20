package eu.olynet.olydorfapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class MealOfTheDayViewerFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ITEM_KEY = "meal_of_the_day_item";

    private SwipeRefreshLayout mRefreshLayout;
    private MealOfTheDayItem item = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* get the MealOfTheDayItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.item = arguments.getParcelable(ITEM_KEY);
        } else {
            Log.e("MealOTDFrag", "arguments is null");
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
        if (item != null) {

            /* Name */
            TextView mealOfTheDayName = (TextView) view.findViewById(
                    R.id.meal_of_the_day_view_name);
            mealOfTheDayName.setText(item.getDailyMeal().getName());

            /* Cook */
            TextView mealOfTheDayCook = (TextView) view.findViewById(
                    R.id.meal_of_the_day_view_cook);
            mealOfTheDayCook.setText(item.getCook());

            /* Icon */
            ImageView mealOfTheDayIcon = (ImageView) view.findViewById(
                    R.id.meal_of_the_day_view_icon);
            mealOfTheDayIcon.setImageResource(
                    item.getDailyMeal().isVegetarian() ? R.drawable.carrot_48dp
                                                       : R.drawable.meat_48dp);

            /* Image */
            ImageView mealOfTheDayImage = (ImageView) view.findViewById(
                    R.id.meal_of_the_day_view_image);
            byte[] image = item.getImage();
            if (image != null && image.length > 0) { /* finally set the image if one is available */
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                if (imageBitmap == null) {
                    mealOfTheDayImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
                } else {
                    DisplayMetrics dm = new DisplayMetrics();
                    WindowManager windowManager = (WindowManager) getContext().getSystemService(
                            Context.WINDOW_SERVICE);
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    mealOfTheDayImage.setImageBitmap(imageBitmap);
                }
            } else {
                mealOfTheDayImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
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
