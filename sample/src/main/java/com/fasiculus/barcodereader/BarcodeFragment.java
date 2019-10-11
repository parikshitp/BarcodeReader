package com.fasiculus.barcodereader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fasiculus.barcode.BarcodeReader;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;
import java.util.Objects;

public class BarcodeFragment extends Fragment implements BarcodeReader.BarcodeReaderListener {
    private static final String TAG = BarcodeFragment.class.getSimpleName();

    private BarcodeReader barcodeReader;
    private boolean flashEnabled = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    public BarcodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barcode, container, false);

        barcodeReader = (BarcodeReader) getChildFragmentManager().findFragmentById(R.id.barcode_fragment);
        Objects.requireNonNull(barcodeReader).setListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView img_flash = view.findViewById(R.id.img_flash);
        img_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashEnabled = !flashEnabled;
                img_flash.setImageDrawable(flashEnabled ? getResources().getDrawable(R.drawable.flash_on) : getResources().getDrawable(R.drawable.flash_off));
                barcodeReader.useFlash(flashEnabled);
            }
        });
    }

    @Override
    public void onScanned(final Barcode barcode) {
        //barcodeReader.playBeep();

        handler.post(new Runnable() {
            @Override
            public void run() {
                barcodeReader.pauseScanning();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Result")
                        .setMessage(barcode.rawValue);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        barcodeReader.resumeScanning();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onScannedMultiple(final List<Barcode> barcodes) {
        /*Log.e(TAG, "onScannedMultiple: " + barcodes.size());

        String codes = "";
        for (Barcode barcode : barcodes) {
            codes += barcode.displayValue + ", ";
        }

        final String finalCodes = codes;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Barcodes: " + finalCodes, Toast.LENGTH_SHORT).show();
            }
        });*/
        handler.post(new Runnable() {
            @Override
            public void run() {
                barcodeReader.pauseScanning();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Result")
                        .setMessage(barcodes.get(0).rawValue);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        barcodeReader.resumeScanning();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Log.e(TAG, "onScanError: " + errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getActivity(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
}
