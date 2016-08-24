package eu.olynet.olydorfapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.fragments.NewsViewerFragment;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class NewsViewerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_viewer);

        /* setup the ActionBar */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.news_viewer_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            throw new RuntimeException("ActionBar is null");
        }
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle("News");

        /* setup the Fragment */
        if (findViewById(R.id.news_view_activity_fragment) != null) {
            if (savedInstanceState != null) {
                return;
            }

            NewsViewerFragment newsViewerFragment = new NewsViewerFragment();
            newsViewerFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.news_view_activity_fragment,
                                                newsViewerFragment)
                                       .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // TODO: Add information to the intent to return to the correct Fragment
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                                    // Add all of this activity's parents to the back stack
                                    .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                                    .startActivities();
                } else {
                    upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
