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
package eu.olynet.olydorfapp.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.BSD3ClauseLicense;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import eu.olynet.olydorfapp.R;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class AboutFragment extends Fragment {

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getContext();
        Resources res = getResources();

        /* inflate the layout for this Fragment */
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        PackageInfo info;
        try {
            info = this.context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String version = info.versionName;

        TextView aboutAppVersion = (TextView) view.findViewById(R.id.aboutAppVersion);
        aboutAppVersion.setText(res.getString(R.string.about_version_string, version));

        /* setup the license dialog */
        setupLicenseDialog((Button) view.findViewById(R.id.aboutLicenseButton));

        /* return the View */
        return view;
    }

    private void setupLicenseDialog(final Button button) {
        button.setOnClickListener(v -> {
            final Notices notices = new Notices();
            final License apacheLicense = new ApacheSoftwareLicense20();
            final License bsdLicense = new BSD3ClauseLicense();
            final License mitLicense = new MITLicense();

            notices.addNotice(new Notice("AnimatedVectorDrawable",
                                         "https://github.com/chiuki/animated-vector-drawable",
                                         "Copyright 2014 Chiu-Ki Chan",
                                         apacheLicense));
            notices.addNotice(new Notice("Android Volley",
                                         "https://github.com/mcxiaoke/android-volley",
                                         "Copyright 2014 - 2016 Xiaoke Zhang\n" +
                                         "Copyright 2011 The Android Open Source Project",
                                         apacheLicense));
            notices.addNotice(new Notice("Apache Commons IO",
                                         "https://commons.apache.org/",
                                         "Copyright 2002-2011 The Apache Software Foundation",
                                         apacheLicense));
            notices.addNotice(new Notice("Apache Commons Lang",
                                         "https://commons.apache.org/",
                                         "Copyright 2001-2015 The Apache Software Foundation",
                                         apacheLicense));
            notices.addNotice(new Notice("CircleImageView",
                                         "https://github.com/hdodenhof/CircleImageView",
                                         "Copyright 2014 - 2016 Henning Dodenhof",
                                         apacheLicense));
//            notices.addNotice(new Notice("barcodescanner",
//                                         "https://github.com/dm77/barcodescanner",
//                                         "Copyright (c) 2014 Dushyanth Maguluru",
//                                         apacheLicense));
//            notices.addNotice(new Notice("zxing",
//                                         "https://github.com/zxing/zxing",
//                                         "Copyright (c) 2005 Sun Microsystems, Inc.\n" +
//                                         "Copyright (c) 2010-2014 University of Manchester\n" +
//                                         "Copyright (c) 2010-2015 Stian Soiland-Reyes\n" +
//                                         "Copyright (c) 2015 Peter Hull",
//                                         apacheLicense));
            notices.addNotice(new Notice("Disk LRU Cache",
                                         "https://github.com/JakeWharton/DiskLruCache",
                                         "Copyright 2012 Jake Wharton\n" +
                                         "Copyright 2011 The Android Open Source Project",
                                         apacheLicense));
            notices.addNotice(new Notice("dualcache",
                                         "https://github.com/vincentbrison/dualcache",
                                         "Copyright 2016 Vincent Brison.",
                                         apacheLicense));
            notices.addNotice(new Notice("Jackson",
                                         "https://github.com/FasterXML",
                                         "Copyright 2016 FasterXML, LLC",
                                         apacheLicense));
            notices.addNotice(new Notice("LicensesDialog",
                                         "http://psdev.de",
                                         "Copyright 2013 Philip Schiffer <admin@psdev.de>",
                                         apacheLicense));
            notices.addNotice(new Notice("Mockito",
                                         "http://mockito.org/",
                                         "Copyright (c) 2007 Mockito contributors",
                                         mitLicense));
            notices.addNotice(new Notice("Retrofit",
                                         "https://github.com/square/retrofit",
                                         "Copyright 2013 Square, Inc.",
                                         apacheLicense));
            notices.addNotice(new Notice("RoundedImageView",
                                         "https://github.com/vinc3m1/RoundedImageView",
                                         "Copyright 2015 Vincent Mi",
                                         apacheLicense));

            /* finally build and open the dialog */
            new LicensesDialog.Builder(this.context)
                    .setNotices(notices)
                    .build()
                    .show();
        });
    }
}
