package eu.olynet.olydorfapp.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import eu.olynet.olydorfapp.R;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class AboutFragment extends Fragment {

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        Resources res = getResources();

        /* inflate the layout for this Fragment */
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String version = info.versionName;

        TextView aboutAppVersion = (TextView) view.findViewById(R.id.aboutAppVersion);
        aboutAppVersion.setText(res.getString(R.string.about_version_string, version));

        final Button button = (Button) view.findViewById(R.id.aboutLicenseButton);
        button.setOnClickListener(v -> {
            WebView view1 = (WebView) LayoutInflater.from(context)
                                                    .inflate(R.layout.dialog_licenses, null);
            view1.loadUrl("file:///android_asset/open_source_licenses.html");
            new AlertDialog.Builder(context, R.style.AlertDialog)
                    .setTitle(getString(R.string.about_licenses))
                    .setView(view1)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        });

        /* return the View */
        return view;
    }
}
