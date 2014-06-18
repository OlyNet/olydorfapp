package de.olynet.olydorfapp.ui.list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.olynet.olydorfapp.R;

/**
 * @author Simon Domke &lt;simon.domke@olydorf.mhn.de&gt;
 * @version $Id$
 */
public class StaticOfferExpandableListAdapter extends BaseExpandableListAdapter
{
    List<StaticOfferHeader> models;
    LayoutInflater inflater;

    public StaticOfferExpandableListAdapter(Activity activity, List<StaticOfferHeader> m) {
        models = m;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getGroupCount() {
        return models.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(models.get(i).getChildren() != null)
            return models.get(i).getChildren().size();
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return models.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return models.get(i).getChildren().get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final StaticOfferHeader parent = models.get(i);

        view = inflater.inflate(R.layout.static_offer_list_header, viewGroup, false);

        ((TextView) view.findViewById(R.id.static_offer_list_header_title)).setText(parent.getName());
        ((ImageView) view.findViewById(R.id.static_offer_list_header_icon)).setImageResource(parent.getResource());

        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        final StaticOfferItem item = models.get(i).getChildren().get(i2);

        view = inflater.inflate(R.layout.static_offer_list_item, viewGroup, false);

        ((TextView) view.findViewById(R.id.static_offer_list_item_name)).setText(item.getName());
        ((TextView) view.findViewById(R.id.static_offer_list_item_price)).setText(item.getPrice());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }
}
