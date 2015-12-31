package eu.olynet.olydorfapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import eu.olynet.olydorfapp.activities.MainActivity;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class UpdateTask extends AsyncTask<Void, Void, Void> {

    private Context mCon;

    public UpdateTask(Context con)
    {
        mCon = con;
    }

    @Override
    protected Void doInBackground(Void... nope) {
        try {
            // Set a time to simulate a long update process.
            Thread.sleep(4000);

            // TODO: implement view refreshing here

            return null;

        } catch (Exception e) {
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