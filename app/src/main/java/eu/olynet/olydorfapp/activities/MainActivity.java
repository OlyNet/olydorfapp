/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
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

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private Menu optionsMenu;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
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
        tabs.setCustomTabColorizer(position -> ContextCompat.getColor(getApplicationContext(), R.color.OlympiaDarkBlue));

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
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
