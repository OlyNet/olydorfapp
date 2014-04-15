package de.olynet.olydorfapp.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import de.olynet.olydorfapp.R;
import de.olynet.olydorfapp.fragments.util.TabPageAdapter;

/**
 * @author Simon Domke &lt;simon.domke@olydorf.mhn.de&gt;
 * @version $Id$
 */
public class TabbedFragment extends android.support.v4.app.Fragment
{

    private static String BUNDLE_SELECTEDFRAGMENT = "BDL_SELFRG";

    private int mSelectedFragment;
    private BaseFragment mBaseFragment;

    private PagerSlidingTabStrip mTabStrip;
    private ViewPager pager;
    private TabPageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_tabbed, container, false);

        mTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mTabStrip.setIndicatorColorResource(R.color.OlympiaBlue);
        mTabStrip.setUnderlineColorResource(R.color.OlympiaDarkBlue);
        mTabStrip.setDividerColorResource(R.color.OlympiaOrange);

        pager = (ViewPager) view.findViewById(R.id.pager);
        adapter = new TabPageAdapter(getChildFragmentManager(), getResources(), R.array.tab_names);

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        mTabStrip.setViewPager(pager);

        if (savedInstanceState != null) {
            mSelectedFragment = savedInstanceState.getInt(BUNDLE_SELECTEDFRAGMENT);
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(fragmentManager.findFragmentById(R.id.pager)==null)
                mBaseFragment = selectFragment(mSelectedFragment);

            //if (mBaseFragment==null)
            // mBaseFragment = selectFragment(mSelectedFragment);
        } else {
            mBaseFragment = new StartFragment();
            openFragment(mBaseFragment);
        }

        return view;
    }

    private BaseFragment selectFragment(int position) {
        BaseFragment fragment = null;

        /*
         * Be aware that these position numbers have to be aligned to the order of the entries
         * in @strings/tab_names
         */
        switch (position) {
            case 0:
                fragment = new StartFragment();
                break;
            case 1:
                fragment = new BierstubeFragment();
                break;
            case 2:
                fragment = new DiscoFragment();
                break;
            case 3:
                fragment = new LoungeFragment();
                break;
            case 4:
                fragment = new LaundryFragment();
                break;
            default:
                break;
        }

        return fragment;
    }

    private void openFragment(BaseFragment fragment)
    {
        openFragment(fragment, false);
    }

    private void openFragment(BaseFragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            // Notice that we are using ChildFragmentManager here
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.pager, fragment);

            if(addToBackStack)
                fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
            if (fragment.getTitleResourceId() > 0)
                getActivity().setTitle(fragment.getTitleResourceId());

        }
    }
}
