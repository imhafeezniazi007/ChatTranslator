package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.chattranslator.R;
import com.example.chattranslator.databinding.ActivityThemesBinding;

public class ThemesActivity extends AppCompatActivity {

    ActivityThemesBinding activityThemesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityThemesBinding = ActivityThemesBinding.inflate(getLayoutInflater());
        setContentView(activityThemesBinding.getRoot());
        activityThemesBinding.toolbar.setTitle(R.string.themes);
        activityThemesBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activityThemesBinding.toolbar);

        activityThemesBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}