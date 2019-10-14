package com.fasiculus.barcodereader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fasiculus.barcode.BarcodeReader;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Objects;

public class BarcodeFragment extends Fragment implements BarcodeReader.BarcodeReaderListener {
    private static final String TAG = BarcodeFragment.class.getSimpleName();

    private BarcodeReader barcodeReader;
    private boolean flashEnabled = false;
    private Context context;

    public BarcodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barcodeReader = (BarcodeReader) getChildFragmentManager().findFragmentById(R.id.barcode_fragment);
        Objects.requireNonNull(barcodeReader).setListener(this);
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
        barcodeReader.pauseScanning();
        setResultDialog(barcode.rawValue);
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getActivity(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }

    private void playBeepSound() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.beep);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
        final Dialog dialog = new Dialog(context);
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
        dialog.show();
    }
}
