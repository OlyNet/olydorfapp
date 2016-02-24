package eu.olynet.olydorfapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.TreeSet;

import eu.olynet.olydorfapp.activities.MainActivity;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.resources.ResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class UpdateTask extends AsyncTask<Void, Void, Void> {

    private Context mCon;

    public UpdateTask(Context con) {
        mCon = con;
    }

    @Override
    protected Void doInBackground(Void... nope) {
        ResourceManager rm = ResourceManager.getInstance();
        try {
            TreeSet<AbstractMetaItem<?>> newsMetaTree = rm.getTreeOfMetaItems(NewsMetaItem.class);

            for(AbstractMetaItem<?> metaItem : newsMetaTree) {
                NewsItem result = (NewsItem) rm.getItem(NewsMetaItem.class, metaItem.getId());
                Log.i("UpdateTask", "" + result);
            }

            // TODO: implement view refreshing here

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void nope) {
        // Give some feedback on the UI.
        Toast.makeText(mCon, "Finished complex background function!",
                Toast.LENGTH_LONG).show();

        // Change the menu back
        ((MainActivity) mCon).resetUpdating();
    }
}
