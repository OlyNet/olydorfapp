package eu.olynet.olydorfapp.fragments;

import android.os.Bundle;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import eu.olynet.olydorfapp.R;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferencesFix(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
