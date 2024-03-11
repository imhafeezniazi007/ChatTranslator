package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.example.chattranslator.databinding.ActivityKeyboardAccessBinding;
import com.example.chattranslator.databinding.ActivitySubscriptionBinding;

import java.util.List;

public class KeyboardAccessActivity extends AppCompatActivity {

    ActivityKeyboardAccessBinding activityKeyboardAccessBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityKeyboardAccessBinding = ActivityKeyboardAccessBinding.inflate(getLayoutInflater());
        setContentView(activityKeyboardAccessBinding.getRoot());

        if (isCustomKeyboardEnabled())
        {
            startActivity(new Intent(KeyboardAccessActivity.this, MainFeaturesActivity.class));
            finish();
        }
        activityKeyboardAccessBinding.appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            }
        });
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
        if (isCustomKeyboardEnabled())
        {
            startActivity(new Intent(KeyboardAccessActivity.this, MainFeaturesActivity.class));
            finish();
        }
    }
}