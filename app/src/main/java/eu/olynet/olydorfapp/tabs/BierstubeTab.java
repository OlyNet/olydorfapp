package eu.olynet.olydorfapp.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.olynet.olydorfapp.R;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class BierstubeTab extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int numberOfColumns = 1;
        View v = inflater.inflate(R.layout.tab_bierstube, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.BierstubeGridRecyclerView);
        if(getContext().getResources().getConfiguration().orientation == getContext().getResources().getConfiguration().ORIENTATION_LANDSCAPE)
            numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns, GridLayoutManager.VERTICAL, false));
        return v;
    }
}
