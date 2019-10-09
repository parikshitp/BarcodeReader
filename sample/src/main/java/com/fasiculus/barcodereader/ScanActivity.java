package com.fasiculus.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasiculus.barcode.BarcodeReader;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BarcodeReader barcodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Barcode Scanner");

        // getting barcode instance
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);
        Objects.requireNonNull(barcodeReader).setListener(this);
    }

    @Override
    public void onScanned(final Barcode barcode) {
        barcodeReader.playBeep();

        Intent intent = new Intent(this, QrDetailsActivity.class);
        intent.putExtra("Data", barcode.displayValue);
        intent.putExtra("RawData", barcode.rawValue);
        startActivity(intent);
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        /*Log.e(TAG, "onScannedMultiple: " + barcodes.size());

        StringBuilder codes = new StringBuilder();
        for (Barcode barcode : barcodes) {
            codes.append(barcode.displayValue).append(", ");
        }

        final String finalCodes = codes.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Barcodes: " + finalCodes, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Let's the user tap the activity icon to go 'home'.
     * Requires setHomeButtonEnabled() in onCreate().
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}