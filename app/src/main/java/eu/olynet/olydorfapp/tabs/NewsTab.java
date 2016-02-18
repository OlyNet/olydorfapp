package eu.olynet.olydorfapp.tabs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.adapters.NewsDataAdapter;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsTab extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_news, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.news_card_list);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = getContext();
        (new AsyncTask<Void, Void, List<NewsMetaItem>>() {

            @Override
            protected List<NewsMetaItem> doInBackground(Void... params) {
                /* querying the ResourceManager for the needed data */
                TreeSet<AbstractMetaItem<?>> tree = ResourceManager.getInstance()
                        .getTreeOfMetaItems(NewsMetaItem.class);

                /* creating the result ArrayList */
                List<NewsMetaItem> result = new ArrayList<>();
                if (tree == null) {
                    return result;
                }

                /* create a new TreeSet with the correct ordering */
                TreeSet<NewsMetaItem> tmpTree = new TreeSet<>(NewsMetaItem.getDateDescComparator());
                for (AbstractMetaItem<?> item : tree) {
                    tmpTree.add((NewsMetaItem) item);
                }

                /* copying the elements of the TreeSet into the result ArrayList and return it */
                result.addAll(tmpTree);
                return result;
            }

            @Override
            protected void onPostExecute(List<NewsMetaItem> result) {
                super.onPostExecute(result);

                mAdapter = new NewsDataAdapter(result);

                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }).execute();
    }
}
