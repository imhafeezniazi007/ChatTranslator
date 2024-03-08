package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.example.chattranslator.R;
import com.example.chattranslator.databinding.ActivitySplashBinding;

import java.util.Calendar;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding activitySplashBinding;
    private static final String PREF_NAME = "MyPrefs";
    private static final String ONE_MONTH = "monthly_subscribed";
    private static final String ONE_YEAR = "yearly_subscribed";
    private static final String ONE_TIME = "one_time_purchased";
    private static final String LAST_SHOWN_KEY = "lastShown";

    private SharedPreferences sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());

        sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        activitySplashBinding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!isSubscribed() && shouldShowActivity()) {
                        showActivity();
                        finish();
                    } else {
                        startMainActivity();
                    }
            }
        });

    }

    private void startMainActivity() {
        if (!isMicPermissionGranted()) {
            startActivity(new Intent(SplashActivity.this, MicAccessActivity.class));
            finish();
        } else if (!isCustomKeyboardEnabled()) {
            startActivity(new Intent(SplashActivity.this, KeyboardAccessActivity.class));
            finish();
        } else{
            startActivity(new Intent(SplashActivity.this, MainFeaturesActivity.class));
            finish();
        }
    }

    private boolean isSubscribed() {
        return sharedPrefs.getBoolean(ONE_MONTH, false) || sharedPrefs.getBoolean(ONE_YEAR, false) || sharedPrefs.getBoolean(ONE_TIME, false);
    }

    private void showActivity() {
        startActivity(new Intent(SplashActivity.this, SubscriptionActivity.class));
    }

    private boolean shouldShowActivity() {
        long lastShown = sharedPrefs.getLong(LAST_SHOWN_KEY, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();

        long timeDiff = currentTime - lastShown;
        if (timeDiff >= getSubscriptionPeriod()) {
            return true;
        }
        return false;
    }

    private long getSubscriptionPeriod() {
        if (isSubscribed()) {
            if (sharedPrefs.getBoolean(ONE_MONTH, false)) {
                return 30 * 24 * 60 * 60 * 1000; // 30 days in milliseconds
            } else if (sharedPrefs.getBoolean(ONE_YEAR, false)) {
                return 365 * 24 * 60 * 60 * 1000; // 365 days in milliseconds
            } else if (sharedPrefs.getBoolean(ONE_TIME, false)) {
                return Long.MAX_VALUE;
            }
        }

        return 3 * 24 * 60 * 60 * 1000;
    }

    private boolean isMicPermissionGranted()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean isCustomKeyboardEnabled() {
        InputMethodManager imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            List<InputMethodInfo> inputMethodInfos = imeManager.getEnabledInputMethodList();
            String packageName = getPackageName();
            for (InputMethodInfo info : inputMethodInfos) {
                if (info.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
}