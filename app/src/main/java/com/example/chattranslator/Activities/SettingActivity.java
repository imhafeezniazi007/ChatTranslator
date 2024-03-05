package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import com.example.chattranslator.databinding.ActivitySettingBinding;
import com.example.chattranslator.R;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding activitySettingBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(activitySettingBinding.getRoot());
        activitySettingBinding.toolbar.setTitle(R.string.setting);
        activitySettingBinding.toolbar.setNavigationIcon(R.drawable.back);
        activitySettingBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activitySettingBinding.toolbar);

    }
}