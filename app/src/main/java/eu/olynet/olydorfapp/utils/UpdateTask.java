/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
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
//        ((MainActivity) context).resetUpdating();
    }
}
