package eu.olynet.olydorfapp.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.fragments.MealOfTheDayViewerFragment;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class MealOfTheDayViewerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_of_the_day_viewer);

        /* setup the ActionBar */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.meal_of_the_day_viewer_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            throw new RuntimeException("ActionBar is null");
        }
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle("Daily Meal");

        /* setup the Fragment */
        if (findViewById(R.id.meal_of_the_day_view_activity_fragment) != null) {
            if (savedInstanceState != null) {
                return;
            }

            MealOfTheDayViewerFragment mealOfTheDayViewerFragment = new MealOfTheDayViewerFragment();
            mealOfTheDayViewerFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.meal_of_the_day_view_activity_fragment,
                                                mealOfTheDayViewerFragment)
                                       .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meal_of_the_day_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }
}