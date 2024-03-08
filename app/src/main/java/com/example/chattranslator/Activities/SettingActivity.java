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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.chattranslator.Adapters.LanguagesAdapter;
import com.example.chattranslator.Models.AvailableLanguage;
import com.example.chattranslator.Utils.AdManager;
import com.example.chattranslator.databinding.ActivitySettingBinding;
import com.example.chattranslator.R;

import java.util.List;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding activitySettingBinding;
    private SharedPreferences sharedPreferences;
    private boolean isShown, isChecked;
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

        activitySettingBinding.toolbar.setNavigationOnClickListener(v -> finish());

        isShown = false;
        sharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE);

        currLan = sharedPreferences.getString("currLan", null);
        isChecked = sharedPreferences.getBoolean("isChecked", true);

        activitySettingBinding.switchOne.setChecked(isChecked);

        activitySettingBinding.languageNameTextView.setText(currLan);

        activitySettingBinding.switchOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("toggleNumbers", isChecked);
                editor.putBoolean("isChecked", isChecked);
                editor.apply();
            }
        });

        showBannerAd();

        List<AvailableLanguage> languageList = new ArrayList<>();
        languageList.add(new AvailableLanguage("English"));
        languageList.add(new AvailableLanguage("Hindi"));
        languageList.add(new AvailableLanguage("Arabic"));

        LanguagesAdapter adapter = new LanguagesAdapter(this, languageList);
        activitySettingBinding.lvListVew.setAdapter(adapter);

        activitySettingBinding.clSettingConstraintOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imeManager != null) {
                    imeManager.showInputMethodPicker();
                }
            }
        });
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

    private void showBannerAd()
    {
        AdManager adManager = new AdManager(this, "top");
        adManager.showAd(AdManager.AdType.BANNER);
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

    @Override
    protected void onResume() {
        super.onResume();
        isChecked = sharedPreferences.getBoolean("isChecked", true);
        activitySettingBinding.switchOne.setChecked(isChecked);
    }
}