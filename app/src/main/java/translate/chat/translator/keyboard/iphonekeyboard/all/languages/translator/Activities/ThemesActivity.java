package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.OnAdsCallback;
import com.example.chattranslator.R;
import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Utils.AdManager;
import com.example.chattranslator.databinding.ActivityThemesBinding;
import com.google.android.ads.nativetemplates.TemplateView;

public class ThemesActivity extends AppCompatActivity {

    ActivityThemesBinding activityThemesBinding;
    private boolean isAdShown = false;
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
                if (!isAdShown) {
                    showInterstitialAd();
                }else {
                    finish();
                }
            }
        });

        showNativeAd();

    }

    private void showNativeAd() {
        TemplateView nativeAdView = activityThemesBinding.nativeTemplatead;

        AdManager adManager = new AdManager(this, nativeAdView);
        adManager.showAd(AdManager.AdType.NATIVE);
    }

    private void showInterstitialAd() {
        AdManager adManager = new AdManager(ThemesActivity.this);
        adManager.showAd(AdManager.AdType.INTERSTITIAL, new OnAdsCallback() {
            @Override
            public void onDismiss() {
                finish();
            }

            @Override
            public void onError(String err) {

            }
        });
        isAdShown = true;
    }

    @Override
    public void onBackPressed() {
        if (!isAdShown) {
            showInterstitialAd();
        } else {
            super.onBackPressed();
        }
    }
}