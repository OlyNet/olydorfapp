/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Arrays;

import eu.olynet.olydorfapp.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * {@link Activity} for handling QR code captures in the application.
 * <p>For the laundry module of our app, we need the ability to decode QR images into a {@link String}
 * that we can then use to identify a hardware device. To simplify scanning, the <i>barcodescanner</i>
 * library by dm77 is applied.</p>
 * <p>Only QR codes are recognized and processed. This activity should always be called for a result,
 * i.e. from a {@link android.support.v4.app.Fragment}</p>
 * <code>
 *     startActivityForResult(new Intent(getActivity(), LaundryQRScannerActivity.class), x);
 * </code>
 * <p>The caller can then retrieve the captured code by</p>
 * <code>
 *     intentData.getStringExtra(getResources().getString(R.string.QR_scan_return))
 * </code>
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 * @see <a href="https://github.com/dm77/barcodescanner">https://github.com/dm77/barcodescanner</a>
 */
public class LaundryQRScannerActivity extends Activity implements ZXingScannerView.ResultHandler
{
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        mScannerView.setFormats(Arrays.asList(       // Allow QR-codes only
                new BarcodeFormat[]{BarcodeFormat.QR_CODE}));

        // Show the user a little help what to do now the capture view is displayed
        Toast.makeText(this, R.string.laundryCameraScanMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("Scanner", rawResult.getText()); // Prints scan results
        Log.v("Scanner", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        Intent ret = new Intent();
        ret.putExtra(getResources().getString(R.string.QR_scan_return), rawResult.getText());
        setResult(Activity.RESULT_OK, ret);
        finish();
    }
}
