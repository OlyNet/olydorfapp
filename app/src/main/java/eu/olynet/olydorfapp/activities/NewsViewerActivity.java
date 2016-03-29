package eu.olynet.olydorfapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.fragments.NewsViewerFragment;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class NewsViewerActivity extends AppCompatActivity {

    private ResourceManager rm;

    NewsViewerFragment newsViewerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_view_activity);

        /* instantiate the ResourceManager if necessary */
        rm = ResourceManager.getInstance();
        if (!rm.isInitialized()) {
            rm.init(getApplicationContext());
        }

        /* setup the Fragment */
        if (findViewById(R.id.news_view_activity_fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            newsViewerFragment = new NewsViewerFragment();
            newsViewerFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_view_activity_fragment, newsViewerFragment).commit();
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
