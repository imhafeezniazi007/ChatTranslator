package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import java.util.ArrayList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.chattranslator.Adapters.LanguagesAdapter;
import com.example.chattranslator.Models.AvailableLanguage;
import com.example.chattranslator.databinding.ActivitySettingBinding;
import com.example.chattranslator.R;

import java.util.List;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding activitySettingBinding;
    private SharedPreferences sharedPreferences;
    private boolean isShown;
    private String currLan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(activitySettingBinding.getRoot());
        activitySettingBinding.toolbar.setTitle(R.string.setting);
        activitySettingBinding.toolbar.setNavigationIcon(R.drawable.back);
        activitySettingBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activitySettingBinding.toolbar);

        isShown = false;
        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE);

        currLan = sharedPreferences.getString("currLan", null);
        activitySettingBinding.languageNameTextView.setText(currLan);

        if (activitySettingBinding.switchOne.isActivated())
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("toggleNumbers", true);
            editor.apply();
        }

        if (!activitySettingBinding.switchOne.isActivated())
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("toggleNumbers", false);
            editor.apply();
        }

        List<AvailableLanguage> languageList = new ArrayList<>();
        languageList.add(new AvailableLanguage("English"));
        languageList.add(new AvailableLanguage("Hindi"));
        languageList.add(new AvailableLanguage("Arabic"));

        LanguagesAdapter adapter = new LanguagesAdapter(this, languageList);
        activitySettingBinding.lvListVew.setAdapter(adapter);

        activitySettingBinding.clSettingConstraintTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguagePopup(activitySettingBinding.clSettingConstraintTwo);
                activitySettingBinding.constraintMain.setVisibility(View.GONE);
                activitySettingBinding.constraintList.setVisibility(View.VISIBLE);
                isShown = true;
            }
        });

    }

    private void showLanguagePopup(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.language_item, null);

        activitySettingBinding.lvListVew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AvailableLanguage selectedLanguage = (AvailableLanguage) parent.getItemAtPosition(position);
                String languageName = selectedLanguage.getName();
                activitySettingBinding.languageNameTextView.setText(languageName);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("currLan", languageName);
                editor.apply();


                if (isShown) {
                    activitySettingBinding.constraintList.setVisibility(View.GONE);
                    activitySettingBinding.constraintMain.setVisibility(View.VISIBLE);
                    isShown = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isShown) {
            activitySettingBinding.constraintList.setVisibility(View.GONE);
            activitySettingBinding.constraintMain.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}