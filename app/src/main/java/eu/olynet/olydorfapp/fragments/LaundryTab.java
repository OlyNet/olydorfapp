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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.LaundryQRScannerActivity;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class LaundryTab extends Fragment {
    final private static int QRScannerRequestCode = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_laundry, container, false);

        // Handle click on the floating action button
        FloatingActionButton cameraButton = (FloatingActionButton) v.findViewById(
                R.id.laundryCameraActionButton);
        cameraButton.setOnClickListener(view -> startActivityForResult(new Intent(getActivity(), LaundryQRScannerActivity.class),
                               QRScannerRequestCode));

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == QRScannerRequestCode && resultCode == Activity.RESULT_OK) {
            // TODO: validate result of scan (is integer, is valid machine number, etc.) and
            // perform registration
            Toast.makeText(getContext(), "Scan: " + data.getStringExtra(
                    getResources().getString(R.string.QR_scan_return)), Toast.LENGTH_SHORT).show();
        }
    }
}
