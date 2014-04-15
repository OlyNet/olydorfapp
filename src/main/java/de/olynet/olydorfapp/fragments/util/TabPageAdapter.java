package de.olynet.olydorfapp.fragments.util;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.olynet.olydorfapp.fragments.BierstubeFragment;
import de.olynet.olydorfapp.fragments.DiscoFragment;
import de.olynet.olydorfapp.fragments.LaundryFragment;
import de.olynet.olydorfapp.fragments.LoungeFragment;
import de.olynet.olydorfapp.fragments.StartFragment;

/**
 * @author Simon Domke &lt;simon.domke@olydorf.mhn.de&gt;
 * @version $Id$
 */
public class TabPageAdapter extends FragmentPagerAdapter {
    private String[] tabNames;
    private static final String ARG_POSITION = "position";

    public TabPageAdapter(FragmentManager fm, Resources resources, int tabNamesId)
    {
        super(fm);
        tabNames = resources.getStringArray(tabNamesId);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabNames[position];
    }

    @Override
    public int getCount()
    {
        return tabNames.length;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position) {
            case 1:
                return new BierstubeFragment();
            case 2:
                return new DiscoFragment();
            case 3:
                return new LoungeFragment();
            case 4:
                return new LaundryFragment();
            default:
                return new StartFragment();
        }
    }
}
