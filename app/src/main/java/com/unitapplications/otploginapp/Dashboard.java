package com.unitapplications.otploginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        SharedPreferences shared_pref = getSharedPreferences(MainActivity.SHARED_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_pref.edit();
        editor.putBoolean(MainActivity.VERIFIED,true);
        editor.apply();
    }
}