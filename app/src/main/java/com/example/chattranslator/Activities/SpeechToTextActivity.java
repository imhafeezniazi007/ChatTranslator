package com.example.chattranslator.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chattranslator.Adapters.CountrySpinnerAdapter;
import com.example.chattranslator.Models.Language;
import com.example.chattranslator.R;
import com.example.chattranslator.Services.LanguageApiService;
import com.example.chattranslator.Services.SpeechToTextService;
import com.example.chattranslator.Services.TextToSpeechManager;
import com.example.chattranslator.Services.TextTranslateService;
import com.example.chattranslator.databinding.ActivityChatTranslatorBinding;
import com.example.chattranslator.databinding.ActivitySpeechToTextBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.Locale;

public class SpeechToTextActivity extends AppCompatActivity {

    ActivitySpeechToTextBinding activitySpeechToTextBinding;
    private Spinner spinner;

    private TextTranslateService service;
    private List<Language> languages;
    private String sourceLanCode, destLanCode;
    private TextToSpeechManager ttsm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySpeechToTextBinding = ActivitySpeechToTextBinding.inflate(getLayoutInflater());
        setContentView(activitySpeechToTextBinding.getRoot());
        activitySpeechToTextBinding.toolbar.setTitle(R.string.speech_to_text);
        activitySpeechToTextBinding.toolbar.setNavigationIcon(R.drawable.back);
        activitySpeechToTextBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activitySpeechToTextBinding.toolbar);

        activitySpeechToTextBinding.placeholderTV.setVisibility(View.VISIBLE);

        ttsm = new TextToSpeechManager(SpeechToTextActivity.this);
        service = new TextTranslateService(SpeechToTextActivity.this);
        spinner = activitySpeechToTextBinding.ctSpinnerOne;
        LanguageApiService languageApiService = new LanguageApiService();
        languages = languageApiService.fetchData(SpeechToTextActivity.this);
        activitySpeechToTextBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new FetchDataAsyncTask().execute();

        activitySpeechToTextBinding.btnSttVoiceCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SpeechToTextActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                {
                    activitySpeechToTextBinding.placeholderTV.setHint("Speak on beep...");
                    SpeechToTextService sttService = new SpeechToTextService(activitySpeechToTextBinding.btnSttVoiceCmd);
                    sttService.getSpeechToText(SpeechToTextActivity.this, new SpeechToTextService.SpeechRecognitionCallBack() {
                        @Override
                        public void onSpeechRecognised(String data) {
                            activitySpeechToTextBinding.placeholderTV.setVisibility(View.GONE);
                            activitySpeechToTextBinding.ctTextTextViewTwo.setText(data);
                            activitySpeechToTextBinding.progressBar.setVisibility(View.VISIBLE);
                            service.identifyLanguage(activitySpeechToTextBinding.ctTextTextViewTwo.getText().toString(), new TextTranslateService.LanguageIdentificationCallback() {
                                @Override
                                public void onLanguageIdentified(String languageCode) {
                                    sourceLanCode = languageCode;
                                    int pos = getSpinnerPosition(languageCode);
                                    spinner.setSelection(pos);
                                    String sourceLanguage = languages.get(spinner.getSelectedItemPosition()).getName();
                                    activitySpeechToTextBinding.ctLanTextViewTwo.setText(sourceLanguage);
                                }
                            });
                        }
                    });

                }
                else {
                    requestPermissionsIfNecessary();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                destLanCode = selectedLanguage.getCode();

                if (sourceLanCode != null) {
                    service.translateText(activitySpeechToTextBinding.ctTextTextViewTwo.getText().toString(), sourceLanCode, destLanCode, new TextTranslateService.TranslationCallback() {
                        @Override
                        public void onTranslationComplete(String translatedText) {
                            activitySpeechToTextBinding.progressBar.setVisibility(View.GONE);
                            activitySpeechToTextBinding.btnCtShare.setVisibility(View.VISIBLE);
                            activitySpeechToTextBinding.btnCtCpyTwo.setVisibility(View.VISIBLE);
                            activitySpeechToTextBinding.btnCtSoundTwo.setVisibility(View.VISIBLE);
                            activitySpeechToTextBinding.ctTextTextViewTwo.setText(translatedText);
                            String updatedLan = languages.get(position).getName();
                            activitySpeechToTextBinding.ctLanTextViewTwo.setText(updatedLan);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activitySpeechToTextBinding.btnCtSoundTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Language selectedLanguage = (Language) spinner.getSelectedItem();
                Locale locale = new Locale(selectedLanguage.getCode());
                if (ttsm.isLanguageAvailable(locale)) {
                    ttsm.setLanguage(locale);
                    ttsm.speak(activitySpeechToTextBinding.ctTextTextViewTwo.getText().toString());
                }
                else {
                    Snackbar.make(getCurrentFocus(), "Language not available", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        activitySpeechToTextBinding.btnCtCpyTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(activitySpeechToTextBinding.ctTextTextViewTwo.getText().toString());
            }
        });

        activitySpeechToTextBinding.btnCtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText(activitySpeechToTextBinding.ctTextTextViewTwo.getText().toString());
            }
        });

    }

    private int getSpinnerPosition(String languageCode) {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).getCode().equals(languageCode)) {
                return i;
            }
        }
        return 0;
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Translation data", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(SpeechToTextActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, "Share via");
        startActivity(intent);
    }

    private void requestPermissionsIfNecessary() {
        if (ContextCompat.checkSelfPermission(SpeechToTextActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
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
                            new AlertDialog.Builder(SpeechToTextActivity.this)
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

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, List<Language>> {

        @Override
        protected List<Language> doInBackground(Void... voids) {
            LanguageApiService apiClient = new LanguageApiService();
            return apiClient.fetchData(getParent());
        }

        @Override
        protected void onPostExecute(List<Language> languageList) {
            super.onPostExecute(languageList);

            if (languageList != null && !languageList.isEmpty()) {
                populateSpinners(languageList);
            }
        }

        private void populateSpinners(List<Language> languages) {
            CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(SpeechToTextActivity.this, languages);
            spinner.setAdapter(adapter);
        }
    }
}