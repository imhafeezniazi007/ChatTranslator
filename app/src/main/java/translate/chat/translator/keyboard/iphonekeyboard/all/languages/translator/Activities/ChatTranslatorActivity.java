package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Fragments.ChatTranslatorDefault;
import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.OnAdsCallback;
import com.example.chattranslator.R;

import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Utils.AdManager;
import com.example.chattranslator.databinding.ActivityChatTranslatorBinding;

public class ChatTranslatorActivity extends AppCompatActivity {

    ActivityChatTranslatorBinding activityChatTranslatorBinding;
    private boolean isAdShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatTranslatorBinding = ActivityChatTranslatorBinding.inflate(getLayoutInflater());
        setContentView(activityChatTranslatorBinding.getRoot());
        activityChatTranslatorBinding.toolbar.setTitle(R.string.toolbar_cta);
        activityChatTranslatorBinding.toolbar.setNavigationIcon(R.drawable.back);
        activityChatTranslatorBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activityChatTranslatorBinding.toolbar);

        activityChatTranslatorBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdShown) {
                    showInterstitialAd();
                }else {
                    finish();
                }
            }
        });

        String text = getIntent().getStringExtra("text");
        replaceFragment(new ChatTranslatorDefault(), text);
    }

    private void replaceFragment(Fragment fragment, String text) {
        Bundle bundle = new Bundle();
        bundle.putString("text", text);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void showInterstitialAd() {
        AdManager adManager = new AdManager(ChatTranslatorActivity.this);
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
            Log.d("TAG_AD", "onBackPressed: else!");
            super.onBackPressed();
        }
    }
}