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
package eu.olynet.olydorfapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collections;

import eu.olynet.olydorfapp.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * {@link Activity} for handling QR code captures in the application.
 * <p>For the laundry module of our app, we need the ability to decode QR images into a
 * {@link String}
 * that we can then use to identify a hardware device. To simplify scanning, the
 * <i>barcodescanner</i>
 * library by dm77 is applied.</p>
 * <p>Only QR codes are recognized and processed. This activity should always be called for a
 * result,
 * i.e. from a {@link android.support.v4.app.Fragment}</p>
 * <code>
 * startActivityForResult(new Intent(getActivity(), LaundryQRScannerActivity.class), x);
 * </code>
 * <p>The caller can then retrieve the captured code by</p>
 * <code>
 * intentData.getStringExtra(getResources().getString(R.string.QR_scan_return))
 * </code>
 *
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 * @see <a href="https://github.com/dm77/barcodescanner">https://github.com/dm77/barcodescanner</a>
 */
public class LaundryQRScannerActivity extends AppCompatActivity
        implements ZXingScannerView.ResultHandler {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 35681;
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));

        // Show the user a little help what to do now the capture view is displayed
        Toast.makeText(this, R.string.laundryCameraScanMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            startScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
            default:
                Log.e("PermissionResult", "This should not happen: " + requestCode);
        }
    }

    private void startScan() {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("Scanner", rawResult.getText());
        Log.v("Scanner", rawResult.getBarcodeFormat().toString());

        Intent ret = new Intent();
        ret.putExtra(getResources().getString(R.string.QR_scan_return), rawResult.getText());
        setResult(Activity.RESULT_OK, ret);
        finish();
    }
}
