package eu.olynet.olydorfapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.olynet.olydorfapp.R;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class JoinUsTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_join_us, container, false);
    }
}
