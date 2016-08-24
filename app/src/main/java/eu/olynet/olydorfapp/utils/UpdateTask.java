/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import eu.olynet.olydorfapp.activities.MainActivity;
import eu.olynet.olydorfapp.resource.ProductionResourceManager;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class UpdateTask extends AsyncTask<Void, Void, Void> {

    private final Context context;

    public UpdateTask(Context con) {
        context = con;
    }

    @Override
    protected Void doInBackground(Void... nope) {
        ProductionResourceManager.getInstance().invalidateCache();
        return null;
    }

    @Override
    protected void onPostExecute(Void nope) {
        // Give some feedback on the UI.
        Toast.makeText(context, "Invalidated the cache!", Toast.LENGTH_LONG).show();

        // Change the menu back
        ((MainActivity) context).resetUpdating();
    }
}
