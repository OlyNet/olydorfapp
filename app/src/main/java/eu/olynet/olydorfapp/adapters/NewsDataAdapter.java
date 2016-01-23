package eu.olynet.olydorfapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.Organization;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsDataAdapter extends RecyclerView.Adapter<NewsDataAdapter.ViewHolder> {
    private List<NewsMetaItem> items;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected TextView vDate;
        protected TextView vTitle;
        protected TextView vOrganization;

        public ViewHolder(View v) {
            super(v);
            vOrganization = (TextView) v.findViewById(R.id.newsCardOrganization);
            vDate = (TextView) v.findViewById(R.id.newsCardDate);
            vTitle = (TextView) v.findViewById(R.id.newsCardTitle);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NewsDataAdapter(List<NewsMetaItem> myDataset) {
        items = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_news, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new NewsDataAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsMetaItem newsMetaItem = items.get(position);

        /* Date */
        holder.vDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(newsMetaItem.getDate()));

        /* Title */
        holder.vTitle.setText(newsMetaItem.getTitle());

        /* Organization */
        Organization organization = Organization.organizations.get(newsMetaItem.getOrganization());
        String orgName;
        if(organization != null) {
            orgName = organization.getName();
        } else {
            orgName = "";
        }
        holder.vOrganization.setText(orgName);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
