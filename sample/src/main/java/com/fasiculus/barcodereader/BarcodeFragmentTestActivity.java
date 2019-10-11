package com.fasiculus.barcodereader;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class BarcodeFragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_fragment_test);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Barcode Scanner");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BarcodeFragment bf = new BarcodeFragment();
        ft.add(R.id.container, bf);
        ft.commit();
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
