/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.customViews.TintOnStateImageView;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NavigationDrawerItemsAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<Pair<Integer, String>> navDrawerItems;

    public NavigationDrawerItemsAdapter(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        navDrawerItems = new ArrayList<>();

        /* Home */
        navDrawerItems.add(Pair.create(R.drawable.ic_home_white_24dp,
                                       context.getResources()
                                              .getString(R.string.navDrawerHomeEntry)));
        /* Bierstube */
        navDrawerItems.add(Pair.create(R.drawable.ic_local_dining_white_24dp,
                                       context.getResources()
                                              .getString(R.string.navDrawerBierstubeEntry)));
        /* OlyNet */
        navDrawerItems.add(Pair.create(R.drawable.ic_favorite_white_24dp,
                                       context.getResources()
                                              .getString(R.string.navDrawerOlynetEntry)));
        /* Laundry */
        navDrawerItems.add(Pair.create(R.drawable.ic_local_laundry_service_white_24dp,
                                       context.getResources()
                                              .getString(R.string.navDrawerLaundryEntry)));
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_drawer_item, null);
        }

        /* retrieve the text view control and set the name to the String part of our pair list */
        TextView itemName = (TextView) convertView.findViewById(R.id.navDrawerItemText);
        itemName.setText(navDrawerItems.get(position).second);

        /* set the image view's image according to the item list */
        TintOnStateImageView itemIcon = (TintOnStateImageView) convertView.findViewById(
                R.id.navDrawerItemIcon);
        itemIcon.setImageResource(navDrawerItems.get(position).first);

        return convertView;
    }
}
