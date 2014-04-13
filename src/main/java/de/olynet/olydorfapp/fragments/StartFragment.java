package de.olynet.olydorfapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.olynet.olydorfapp.R;

/**
 * @author Simon Domke &lt;simon.domke@olydorf.mhn.de&gt;
 * @version $Id$
 */
public class StartFragment extends BaseFragment
{
    @Override
    public int getTitleResourceId() {
        return R.string.start;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_start, container, false);
    }
}
