/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.olynet.olydorfapp.fragments.BierstubeTab;
import eu.olynet.olydorfapp.fragments.DummyTab;
import eu.olynet.olydorfapp.fragments.LaundryTab;
import eu.olynet.olydorfapp.fragments.NewsTab;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public enum Category {
        HOME("Home"), BIERSTUBE("Die Bierstube"), OLYNET("OlyNet e.V."), LAUNDRY("Waschraum");

        public final String title;

        private Category(String title) {
            this.title = title;
        }
    }

    private static class Tab {

        public final Class<? extends Fragment> clazz;
        public final CharSequence title;

        public Tab(Class<? extends Fragment> clazz, CharSequence title) {
            this.clazz = clazz;
            this.title = title;
        }
    }

    public static final Map<Category, List<Tab>> tabNames;

    static {
        Map<Category, List<Tab>> tmpTabNames = new LinkedHashMap<>();

        List<Tab> homeTabs = new ArrayList<>();
        homeTabs.add(new Tab(NewsTab.class, "News"));
        tmpTabNames.put(Category.HOME, homeTabs);

        List<Tab> bierstubeTabs = new ArrayList<>();
        bierstubeTabs.add(new Tab(DummyTab.class, "Allgemein"));
        bierstubeTabs.add(new Tab(NewsTab.class, "News"));
        bierstubeTabs.add(new Tab(BierstubeTab.class, "Speisen"));
        tmpTabNames.put(Category.BIERSTUBE, bierstubeTabs);

        List<Tab> olynetTabs = new ArrayList<>();
        olynetTabs.add(new Tab(DummyTab.class, "Allgemein"));
        olynetTabs.add(new Tab(NewsTab.class, "News"));
        olynetTabs.add(new Tab(DummyTab.class, "Mitmachen"));
        tmpTabNames.put(Category.OLYNET, olynetTabs);

        List<Tab> laundryTabs = new ArrayList<>();
        laundryTabs.add(new Tab(LaundryTab.class, "Informationen"));
        tmpTabNames.put(Category.LAUNDRY, laundryTabs);

        tabNames = Collections.unmodifiableMap(tmpTabNames);
    }

    private final List<Tab> tabs;

    /**
     * Create a new ViewPagerAdapter
     *
     * @param fm       the FragmentManager.
     * @param category the selected Category.
     */
    public ViewPagerAdapter(FragmentManager fm, Category category) {
        super(fm);

        this.tabs = tabNames.get(category);
    }

    @Override
    public Fragment getItem(int position) {
        Class<? extends Fragment> clazz = tabs.get(position).clazz;
        try {
            return clazz.cast(clazz.getConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.tabs.get(position).title;
    }

    @Override
    public int getCount() {
        return this.tabs.size();
    }
}
