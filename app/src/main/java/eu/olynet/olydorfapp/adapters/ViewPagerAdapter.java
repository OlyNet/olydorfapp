/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.fragments.BierstubeTab;
import eu.olynet.olydorfapp.fragments.DummyTab;
import eu.olynet.olydorfapp.fragments.LaundryTab;
import eu.olynet.olydorfapp.fragments.NewsTab;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final Map<Category, List<Tab>> tabNames;

    static {
        Map<Category, List<Tab>> tmpTabNames = new LinkedHashMap<>();

        /* category Home */
        List<Tab> homeTabs = new ArrayList<>();
        homeTabs.add(new Tab(R.string.tabTitleNews, new NewsTab()));
        tmpTabNames.put(Category.HOME, homeTabs);

        /* category Bierstube */
        List<Tab> bierstubeTabs = new ArrayList<>();
        bierstubeTabs.add(new Tab(R.string.tabTitleGeneral, new DummyTab()));
        OrganizationMetaItem bierstubeOrganization =
                new AbstractMetaItem.DummyFactory<>(OrganizationMetaItem.class).setId(2).build();
        NewsTab bierstubeNewsTab = new NewsTab();
        Bundle bierstubeBundle = new Bundle();
        bierstubeBundle.putParcelable(NewsTab.ORG_KEY, bierstubeOrganization);
        bierstubeNewsTab.setArguments(bierstubeBundle);
        bierstubeTabs.add(new Tab(R.string.tabTitleNews, bierstubeNewsTab));
        bierstubeTabs.add(new Tab(R.string.tabTitleMenu, new BierstubeTab()));
        tmpTabNames.put(Category.BIERSTUBE, bierstubeTabs);

        /* category OlyNet */
        List<Tab> olynetTabs = new ArrayList<>();
        olynetTabs.add(new Tab(R.string.tabTitleGeneral, new DummyTab()));
        OrganizationMetaItem olynetOrganization =
                new AbstractMetaItem.DummyFactory<>(OrganizationMetaItem.class).setId(1).build();
        NewsTab olynetNewsTab = new NewsTab();
        Bundle olynetBundle = new Bundle();
        olynetBundle.putParcelable(NewsTab.ORG_KEY, olynetOrganization);
        olynetNewsTab.setArguments(olynetBundle);
        olynetTabs.add(new Tab(R.string.tabTitleNews, olynetNewsTab));
        olynetTabs.add(new Tab(R.string.tabTitleJoinUs, new DummyTab()));
        tmpTabNames.put(Category.OLYNET, olynetTabs);

        /* category Laundry */
        List<Tab> laundryTabs = new ArrayList<>();
        laundryTabs.add(new Tab(R.string.tabTitleAbout, new LaundryTab()));
        tmpTabNames.put(Category.LAUNDRY, laundryTabs);

        tabNames = Collections.unmodifiableMap(tmpTabNames);
    }

    private final Context context;
    private final List<Tab> tabs;

    /**
     * Create a new ViewPagerAdapter
     *
     * @param context  the Context of the Activity.
     * @param fm       the FragmentManager.
     * @param category the selected Category.
     */
    public ViewPagerAdapter(Context context, FragmentManager fm, Category category) {
        super(fm);
        this.context = context;
        this.tabs = tabNames.get(category);
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position).fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(this.tabs.get(position).androidResourceId);
    }

    @Override
    public int getCount() {
        return this.tabs.size();
    }

    /**
     * Represents a Category selectable in the NavigationDrawer.
     */
    public enum Category {
        HOME(R.string.navDrawerHomeEntry),
        BIERSTUBE(R.string.navDrawerBierstubeEntry),
        OLYNET(R.string.navDrawerOlynetEntry),
        LAUNDRY(R.string.navDrawerLaundryEntry);

        public final int androidResourceId;

        Category(int androidResourceId) {
            this.androidResourceId = androidResourceId;
        }
    }

    /**
     * Wrapper representing a Tab. Contains the Fragment and its title.
     */
    private static class Tab {

        public final int androidResourceId;
        public final Fragment fragment;

        public Tab(int androidResourceId, Fragment fragment) {
            this.androidResourceId = androidResourceId;
            this.fragment = fragment;
        }
    }
}
