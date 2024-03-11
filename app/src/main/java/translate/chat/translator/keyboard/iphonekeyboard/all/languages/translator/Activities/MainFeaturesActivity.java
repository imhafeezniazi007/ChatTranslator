package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.chattranslator.R;
import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Services.SpeechToTextService;
import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Utils.AdManager;
import com.example.chattranslator.databinding.ActivityMainBinding;
import com.example.chattranslator.databinding.FragmentChatTranslatorDefaultBinding;
import com.google.android.ads.nativetemplates.TemplateView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainFeaturesActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    private Button btnCancel, btnExit;
    private ImageView imageView;
    private boolean isFirstBack = true;
    private boolean isFeedbackShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        activityMainBinding.toolbar.setTitle(R.string.string_toolbar_mfa);
        activityMainBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activityMainBinding.toolbar);

        setOnClickListenersForCards();

        requestPermissionsIfNecessary();

        showNativeAd();
        showBannerAd();

        activityMainBinding.btnIcVoiceCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainFeaturesActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                {
                    SpeechToTextService sttService = new SpeechToTextService(activityMainBinding.btnIcVoiceCmd);
                    sttService.getSpeechToText(MainFeaturesActivity.this, new SpeechToTextService.SpeechRecognitionCallBack() {
                        @Override
                        public void onSpeechRecognised(String data) {
                            activityMainBinding.editTextInstantChat.setText(data);
                        }
                    });
                }
                else {
                    requestPermissionsIfNecessary();
                }
            }
        });

        activityMainBinding.btnMfaTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFeaturesActivity.this, ChatTranslatorActivity.class);
                intent.putExtra("text", activityMainBinding.editTextInstantChat.getText().toString());
                startActivity(intent);
                activityMainBinding.editTextInstantChat.setText("");


            }
        });
    }

    private void showNativeAd() {
        TemplateView nativeAdView = activityMainBinding.nativeMainad;

        AdManager adManager = new AdManager(this, nativeAdView);
        adManager.showAd(AdManager.AdType.NATIVE);
    }

    private void showBannerAd()
    {
        AdManager adManager = new AdManager(this, findViewById(R.id.bannerMainView),"top");
        adManager.showAd(AdManager.AdType.BANNER);
    }
    private void requestPermissionsIfNecessary() {
        if (ContextCompat.checkSelfPermission(MainFeaturesActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
        }
        else {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        }
                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            new AlertDialog.Builder(MainFeaturesActivity.this)
                                    .setTitle("Permission Denied")
                                    .setMessage("This application requires microphone permission to proceed. Do you want to allow?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissionsIfNecessary();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }
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
        setClickListener(activityMainBinding.clConstraintThree, SpeechToTextActivity.class);
        setClickListener(activityMainBinding.clConstraintFive, VoiceTranslatorActivity.class);
        setClickListener(activityMainBinding.clConstraintSix, ThemesActivity.class);
        setClickListener(activityMainBinding.clConstraintSeven, SettingActivity.class);
    }

    private void exitActivity() {
        btnCancel = findViewById(R.id.btnBtnCancel);
        btnExit = findViewById(R.id.btnBtnExit);
        imageView = findViewById(R.id.rating_img);

        View exitScreenLayout = getLayoutInflater().inflate(R.layout.layout_exit_screen, null);
        TemplateView nativeAdView = exitScreenLayout.findViewById(R.id.nativeExitAd);

        AdManager adManager = new AdManager(this, nativeAdView);
        adManager.showAd(AdManager.AdType.NATIVE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.whatsapp"));
                startActivity(intent);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstBack = true;
                setContentView(activityMainBinding.getRoot());
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (isFeedbackShown) {
            setContentView(activityMainBinding.getRoot());
            setOnClickListenersForCards();
            isFeedbackShown = false;
        }
        else {
            if (isFirstBack) {
                isFirstBack = false;
                setContentView(R.layout.layout_exit_screen);
                exitActivity();
            } else {
                super.onBackPressed();
            }
        }
    }
}