package com.fasiculus.barcodereader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasiculus.barcode.BarcodeReader;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Objects;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    private BarcodeReader barcodeReader;
    private boolean flashEnabled = false;

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

        final ImageView img_flash = findViewById(R.id.img_flash);
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
        barcodeReader.pauseScanning();
        setResultDialog(barcode.rawValue);
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void playBeepSound() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
    }

    private void setResultDialog(String output) {
        playBeepSound();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.m_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog1) {
                barcodeReader.resumeScanning();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog1) {
                barcodeReader.resumeScanning();
            }
        });

        TextView tv = dialog.findViewById(R.id.result_tv);
        TextView tv_ok = dialog.findViewById(R.id.tv_ok);
        tv.setText(output);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                barcodeReader.resumeScanning();
            }
        });
        if (!isFinishing()) {
            dialog.show();
        }
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