/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.tabs;

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

import eu.olynet.olydorfapp.activities.LaundryQRScannerActivity;
import eu.olynet.olydorfapp.R;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class LaundryTab extends Fragment
{
    final private static int QRScannerRequestCode = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.tab_laundry, container, false);

        // Handle click on the floating action button
        FloatingActionButton cameraButton = (FloatingActionButton) v.findViewById(R.id.laundryCameraActionButton);
        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), LaundryQRScannerActivity.class), QRScannerRequestCode);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if(requestCode == QRScannerRequestCode && resultCode == Activity.RESULT_OK) {
            // TODO: validate result of scan (is integer, is valid machine number, etc.) and perform registration
            Toast.makeText(getContext(), "Scan: "+data.getStringExtra(getResources().getString(R.string.QR_scan_return)), Toast.LENGTH_SHORT).show();
        }
    }
}
