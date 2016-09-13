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
import eu.olynet.olydorfapp.fragments.AboutFragment;
import eu.olynet.olydorfapp.fragments.BierstubeTab;
import eu.olynet.olydorfapp.fragments.JoinUsTab;
import eu.olynet.olydorfapp.fragments.LaundryTab;
import eu.olynet.olydorfapp.fragments.MealOfTheDayListFragment;
import eu.olynet.olydorfapp.fragments.NewsTab;
import eu.olynet.olydorfapp.fragments.OrganizationTab;
import eu.olynet.olydorfapp.fragments.SettingsFragment;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final Category DEFAULT_CATEGORY = Category.HOME;
    private static final Map<Category, List<Tab>> categoryToTabs;

    static {
        Map<Category, List<Tab>> tmpTabNames = new LinkedHashMap<>();

        /* category Home */
        List<Tab> homeTabs = new ArrayList<>();
        homeTabs.add(new Tab(R.string.tabTitleNews, new NewsTab()));
        tmpTabNames.put(Category.HOME, homeTabs);

        /* category Bierstube */
        OrganizationMetaItem bierstubeOrganization =
                new AbstractMetaItem.DummyFactory<>(OrganizationMetaItem.class).setId(2).build();
        List<Tab> bierstubeTabs = new ArrayList<>();

        OrganizationTab bierstubeOrgTab = new OrganizationTab();
        Bundle bierstubeOrgBundle = new Bundle();
        bierstubeOrgBundle.putParcelable(OrganizationTab.ORG_KEY, bierstubeOrganization);
        bierstubeOrgTab.setArguments(bierstubeOrgBundle);
        bierstubeTabs.add(new Tab(R.string.tabTitleGeneral, bierstubeOrgTab));

        NewsTab bierstubeNewsTab = new NewsTab();
        Bundle bierstubeNewsBundle = new Bundle();
        bierstubeNewsBundle.putParcelable(NewsTab.ORGANIZATION_KEY, bierstubeOrganization);
        bierstubeNewsTab.setArguments(bierstubeNewsBundle);
        bierstubeTabs.add(new Tab(R.string.tabTitleNews, bierstubeNewsTab));

        bierstubeTabs.add(new Tab(R.string.tabTitleMenu, new BierstubeTab()));

        bierstubeTabs.add(new Tab(R.string.tabTitleMealOfTheDay, new MealOfTheDayListFragment()));

        tmpTabNames.put(Category.BIERSTUBE, bierstubeTabs);

        /* category OlyNet */
        OrganizationMetaItem olynetOrganization =
                new AbstractMetaItem.DummyFactory<>(OrganizationMetaItem.class).setId(1).build();
        List<Tab> olynetTabs = new ArrayList<>();

        OrganizationTab olynetOrgTab = new OrganizationTab();
        Bundle olynetOrgBundle = new Bundle();
        olynetOrgBundle.putParcelable(OrganizationTab.ORG_KEY, olynetOrganization);
        olynetOrgTab.setArguments(olynetOrgBundle);
        olynetTabs.add(new Tab(R.string.tabTitleGeneral, olynetOrgTab));

        NewsTab olynetNewsTab = new NewsTab();
        Bundle olynetNewsBundle = new Bundle();
        olynetNewsBundle.putParcelable(NewsTab.ORGANIZATION_KEY, olynetOrganization);
        olynetNewsTab.setArguments(olynetNewsBundle);
        olynetTabs.add(new Tab(R.string.tabTitleNews, olynetNewsTab));

        olynetTabs.add(new Tab(R.string.tabTitleJoinUs, new JoinUsTab()));

        tmpTabNames.put(Category.OLYNET, olynetTabs);

        /* category Laundry */
        List<Tab> laundryTabs = new ArrayList<>();
        laundryTabs.add(new Tab(R.string.tabTitleAbout, new LaundryTab()));
        tmpTabNames.put(Category.LAUNDRY, laundryTabs);

        /* category Settings */
        List<Tab> settingsTabs = new ArrayList<>();
        settingsTabs.add(new Tab(R.string.tabTitleSettings, new SettingsFragment()));
        tmpTabNames.put(Category.SETTINGS, settingsTabs);

        /* category Settings */
        List<Tab> aboutTabs = new ArrayList<>();
        aboutTabs.add(new Tab(R.string.tabTitleAbout, new AboutFragment()));
        tmpTabNames.put(Category.ABOUT, aboutTabs);

        categoryToTabs = Collections.unmodifiableMap(tmpTabNames);
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
        this.tabs = categoryToTabs.get(category);
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
        LAUNDRY(R.string.navDrawerLaundryEntry),
        SETTINGS(R.string.navDrawerSettingsEntry),
        ABOUT(R.string.navDrawerAboutEntry);

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
