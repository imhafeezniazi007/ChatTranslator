package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.chattranslator.R;
import com.example.chattranslator.databinding.ActivityMainBinding;

public class MainFeaturesActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        activityMainBinding.toolbar.setTitle(R.string.string_toolbar_mfa);
        activityMainBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activityMainBinding.toolbar);

        setOnClickListenersForCards();
    }

    private void setClickListener(View view, final Class<?> activityClass) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFeaturesActivity.this, activityClass));
            }
        });
    }
    private void setOnClickListenersForCards() {
        setClickListener(activityMainBinding.clConstraintTwo, ChatTranslatorActivity.class);
//        setClickListener(activityMainBinding.clConstraintThree, BusinessStatusActivity.class);
//        setClickListener(activityMainBinding.clConstraintFive, DirectChatActivity.class);
//        setClickListener(activityMainBinding.clConstraintSix, WhatsDeleteActivity.class);
//        setClickListener(activityMainBinding.clConstraintSeven, QRScannerActivity.class);
    }
}