package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import com.example.chattranslator.Utils.AdManager;
import com.example.chattranslator.databinding.ActivityVoiceTranslatorBinding;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VoiceTranslatorActivity extends AppCompatActivity {

    ActivityVoiceTranslatorBinding activityVoiceTranslatorBinding;
    private Spinner spinnerOne, spinnerTwo;
    private String sourceLanCode, destinationLanCode, text;
    private List<Language> languages;
    private TextTranslateService service;
    private TextToSpeechManager ttsm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVoiceTranslatorBinding = ActivityVoiceTranslatorBinding.inflate(getLayoutInflater());
        setContentView(activityVoiceTranslatorBinding.getRoot());
        activityVoiceTranslatorBinding.toolbar.setTitle(R.string.voice_translator);
        activityVoiceTranslatorBinding.toolbar.setNavigationIcon(R.drawable.back);
        activityVoiceTranslatorBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activityVoiceTranslatorBinding.toolbar);

        activityVoiceTranslatorBinding.placeholderTV.setVisibility(View.VISIBLE);
        sourceLanCode = null;
        destinationLanCode = null;
        text = null;
        spinnerOne = activityVoiceTranslatorBinding.vtSpinnerOne;
        spinnerTwo = activityVoiceTranslatorBinding.vtSpinnerTwo;
        service = new TextTranslateService(VoiceTranslatorActivity.this);
        ttsm = new TextToSpeechManager(VoiceTranslatorActivity.this);

        LanguageApiService languageApiService = new LanguageApiService();
        languages = languageApiService.fetchData(VoiceTranslatorActivity.this);

        new FetchDataAsyncTask().execute();

        showNativeAd();
        activityVoiceTranslatorBinding.btnVtMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityVoiceTranslatorBinding.placeholderTV.setHint("Speak on beep...");
                SpeechToTextService stts = new SpeechToTextService(activityVoiceTranslatorBinding.btnVtMic);
                stts.getSpeechToText(VoiceTranslatorActivity.this, new SpeechToTextService.SpeechRecognitionCallBack() {
                    @Override
                    public void onSpeechRecognised(String data) {
                        activityVoiceTranslatorBinding.btnVtCross.setVisibility(View.VISIBLE);
                        activityVoiceTranslatorBinding.placeholderTV.setVisibility(View.GONE);
                        activityVoiceTranslatorBinding.vtTextView.setText(data);
                        activityVoiceTranslatorBinding.progressBar.setVisibility(View.VISIBLE);
                        service.identifyLanguage(activityVoiceTranslatorBinding.vtTextView.getText().toString(), new TextTranslateService.LanguageIdentificationCallback() {
                            @Override
                            public void onLanguageIdentified(String languageCode) {
                                text = activityVoiceTranslatorBinding.vtTextView.getText().toString();
                                sourceLanCode = languageCode;
                                destinationLanCode = ((Language) spinnerTwo.getSelectedItem()).getCode();
                                int pos = getSpinnerPosition(languageCode);
                                spinnerOne.setSelection(pos);
                                activityVoiceTranslatorBinding.flagVt.setVisibility(View.VISIBLE);
                                activityVoiceTranslatorBinding.flagVt.setImageBitmap(languages.get(pos).getFlag());
                                translate(text, sourceLanCode, destinationLanCode);
                            }
                        });
                    }
                });
            }
        });

        spinnerOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                if (selectedLanguage != null) {
                    String sourceLanCodeUpd = selectedLanguage.getCode();
                    String destinationLanCodeUpd = ((Language) spinnerTwo.getSelectedItem()).getCode();
                    if (!Objects.equals(sourceLanCode, sourceLanCodeUpd)&&!Objects.equals(destinationLanCode, destinationLanCodeUpd)) {
                        translate(text, sourceLanCodeUpd, destinationLanCodeUpd);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                if (selectedLanguage != null) {
                    String destinationLanCodeUpd = selectedLanguage.getCode();
                    String sourceLanCodeUpd = ((Language) spinnerOne.getSelectedItem()).getCode();
                    if (!Objects.equals(sourceLanCode, sourceLanCodeUpd)&&!Objects.equals(destinationLanCode, destinationLanCodeUpd)) {
                        translate(text, sourceLanCodeUpd, destinationLanCodeUpd);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        activityVoiceTranslatorBinding.btnVtCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityVoiceTranslatorBinding.vtTextView.setText("");
                activityVoiceTranslatorBinding.placeholderTV.setHint("Press the icon below to speak");
                activityVoiceTranslatorBinding.placeholderTV.setVisibility(View.VISIBLE);
                activityVoiceTranslatorBinding.flagVt.setVisibility(View.GONE);
                activityVoiceTranslatorBinding.vtLanTextView.setText("");
                activityVoiceTranslatorBinding.btnVtCross.setVisibility(View.GONE);
                activityVoiceTranslatorBinding.btnCtSoundTwo.setVisibility(View.GONE);
                activityVoiceTranslatorBinding.btnCtCpy.setVisibility(View.GONE);
                activityVoiceTranslatorBinding.btnVtShare.setVisibility(View.GONE);
            }
        });

        activityVoiceTranslatorBinding.btnCtCpy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipBoardCopy(activityVoiceTranslatorBinding.vtLanTextView.getText().toString());
            }
        });


        activityVoiceTranslatorBinding.btnVtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText(activityVoiceTranslatorBinding.vtLanTextView.getText().toString());
            }
        });
        activityVoiceTranslatorBinding.vtBtnIntercast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spinnerOnePosition = activityVoiceTranslatorBinding.vtSpinnerOne.getSelectedItemPosition();
                int spinnerTwoPosition = activityVoiceTranslatorBinding.vtSpinnerTwo.getSelectedItemPosition();

                activityVoiceTranslatorBinding.vtSpinnerOne.setSelection(spinnerTwoPosition);
                activityVoiceTranslatorBinding.vtSpinnerTwo.setSelection(spinnerOnePosition);
            }
        });

        activityVoiceTranslatorBinding.btnCtSoundTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Language selectedLanguage = (Language) spinnerTwo.getSelectedItem();
                Locale locale = new Locale(selectedLanguage.getCode());
                if (ttsm.isLanguageAvailable(locale)) {
                    ttsm.setLanguage(locale);
                    ttsm.speak(activityVoiceTranslatorBinding.vtLanTextView.getText().toString());
                }
                else {
                    Snackbar.make(getCurrentFocus(), "Language not available", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showNativeAd() {
        TemplateView nativeAdView = activityVoiceTranslatorBinding.nativeVTad;

        AdManager adManager = new AdManager(this, nativeAdView);
        adManager.showAd(AdManager.AdType.NATIVE);
    }

    private int getSpinnerPosition(String languageCode) {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).getCode().equals(languageCode)) {
                return i;
            }
        }
        return 0;
    }


    private void translate(String text, String sourceLan, String destLan)
    {
        TextTranslateService service = new TextTranslateService(VoiceTranslatorActivity.this);
        if (text!=null && sourceLanCode!=null && destinationLanCode!=null) {
            service.translateText(text, sourceLan, destLan, new TextTranslateService.TranslationCallback() {
                @Override
                public void onTranslationComplete(String translatedText) {
                    activityVoiceTranslatorBinding.progressBar.setVisibility(View.GONE);
                    activityVoiceTranslatorBinding.btnVtShare.setVisibility(View.VISIBLE);
                    activityVoiceTranslatorBinding.btnCtSoundTwo.setVisibility(View.VISIBLE);
                    activityVoiceTranslatorBinding.btnCtCpy.setVisibility(View.VISIBLE);
                    activityVoiceTranslatorBinding.flagVtSecond.setVisibility(View.VISIBLE);
                    activityVoiceTranslatorBinding.flagVtSecond.setImageBitmap(languages
                            .get(spinnerTwo.getSelectedItemPosition())
                            .getFlag());
                    activityVoiceTranslatorBinding.vtLanTextView.setText(translatedText);
                }
            });
        }
    }
    private void clipBoardCopy(String text)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Translation data", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(VoiceTranslatorActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareText(String text)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, "Share via");
        startActivity(intent);
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
            CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(VoiceTranslatorActivity.this, languages);
            spinnerOne.setAdapter(adapter);
            spinnerTwo.setAdapter(adapter);
        }
    }
}