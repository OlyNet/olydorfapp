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
package eu.olynet.olydorfapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.NavigationDrawerItemsAdapter;
import eu.olynet.olydorfapp.adapters.ViewPagerAdapter;
import eu.olynet.olydorfapp.customViews.ScrimInsetsFrameLayout;
import eu.olynet.olydorfapp.receiver.BootReceiver;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;
import eu.olynet.olydorfapp.resource.ResourceManager;
import eu.olynet.olydorfapp.sliding.SlidingTabLayout;
import eu.olynet.olydorfapp.utils.UpdateTask;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author <a href="mailto:simon.domke@olynet.eu>Simon Domke</a>
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 2354;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private Menu optionsMenu;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* setup the ActionBar */
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        /* setup Tabs */
        selectTabGroup(ViewPagerAdapter.DEFAULT_CATEGORY);
        initNavigator();

        /* setup ResourceManager */
        ResourceManager rm = ProductionResourceManager.getInstance();
        if (!rm.isInitialized()) {
            rm.init(getApplicationContext());
        }

        /* enable BootReceiver */
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                      PackageManager.DONT_KILL_APP);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkGooglePlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                /* start animation */
                LayoutInflater inflater = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView) inflater.inflate(R.layout.ic_refresh, null);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);
                item.setActionView(iv);
                new UpdateTask(this).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("unused")
    private void checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status,
                                                     REQUEST_CODE_RECOVER_PLAY_SERVICES,
                                                     dialogInterface -> finish())
                                     .show();
            } else {
                Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * Initialize the tab slider.
     *
     * @param category the Category to be displayed in the tabs.
     */
    private void selectTabGroup(final ViewPagerAdapter.Category category) {
        /* creating The ViewPagerAdapter and passing the FragmentManager and the category */
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, getSupportFragmentManager(),
                                                        category);

        /* assigning ViewPager view and setting the adapter */
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        /* assigning the sliding tab layout view */
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true); /* fixed tab sizes */

        /* setting a custom color for the scroll bar indicator of the tab view */
        tabs.setCustomTabColorizer(position -> ContextCompat.getColor(getApplicationContext(),
                                                                      R.color.OlympiaDarkBlue));

        /* setting the ViewPager for the SlidingTabsLayout */
        tabs.setViewPager(pager);

        /* configuring the ActionBar */
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getResources().getString(category.androidResourceId));
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setHomeButtonEnabled(true);
        }
    }

    /**
     * Sets up the NavigationDrawer.
     */
    private void initNavigator() {
        /* NavigationDrawer */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_DrawerLayout);
        mDrawerLayout.setStatusBarBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.primaryDark));
        ScrimInsetsFrameLayout mScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(
                R.id.main_activity_navigation_drawer_rootLayout);

        /* filling the NavBar with items is handled by the Adapter */
        ListView mDrawerList = (ListView) findViewById(R.id.navDrawerItemsListView);
        mDrawerList.setAdapter(new NavigationDrawerItemsAdapter(this));

        /* clickListener setup */
        mDrawerList.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    selectTabGroup(ViewPagerAdapter.Category.HOME);
                    break;
                case 1:
                    selectTabGroup(ViewPagerAdapter.Category.BIERSTUBE);
                    break;
                case 2:
                    selectTabGroup(ViewPagerAdapter.Category.OLYNET);
                    break;
                case 3:
                    selectTabGroup(ViewPagerAdapter.Category.LAUNDRY);
                    break;
                default:
                    Log.e("onItemClick", "Unknown position " + position);
            }
            mDrawerLayout.closeDrawers();
        });

        FrameLayout settingsButton = (FrameLayout) findViewById(R.id.navigation_drawer_settings);
        settingsButton.setOnClickListener(v -> {
            selectTabGroup(ViewPagerAdapter.Category.SETTINGS);
            mDrawerLayout.closeDrawers();
        });

        FrameLayout aboutButton = (FrameLayout) findViewById(R.id.navigation_drawer_about);
        aboutButton.setOnClickListener(v -> {
            selectTabGroup(ViewPagerAdapter.Category.ABOUT);
            mDrawerLayout.closeDrawers();
        });

        /* setup Toggle for the Drawer in the ActionBar */
        ActionBarDrawerToggle tog = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                                                              R.string.navigation_drawer_opened,
                                                              R.string.navigation_drawer_closed) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Disables the burger/arrow animation by default
                super.onDrawerSlide(drawerView, 0);
            }
        };
        mDrawerLayout.addDrawerListener(tog);
        tog.syncState();

        /* NavigationDrawer layout width */
        int minWidth = UtilsDevice.getScreenWidth(this) -
                       UtilsMiscellaneous
                               .getThemeAttributeDimensionSize(this, android.R.attr.actionBarSize);
        int maxWidth = getResources().getDimensionPixelSize(R.dimen.navigation_drawer_max_width);
        mScrimInsetsFrameLayout.getLayoutParams().width = Math.min(minWidth, maxWidth);
    }

    /**
     * Stops the animation of the refresh button.
     */
    public void resetUpdating() {
        MenuItem m = optionsMenu.findItem(R.id.action_refresh);
        if (m.getActionView() != null) {
            /* stop animation */
            m.getActionView().clearAnimation();
            m.setActionView(null);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
