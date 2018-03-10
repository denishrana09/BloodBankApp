package com.example.denish.bloodbank;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by denish on 10/3/18.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    void activeToolbar(boolean enableHome){
        Log.d(TAG, "activeToolbar: starts");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);

            if(toolbar != null){
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enableHome);
        }
    }
}
