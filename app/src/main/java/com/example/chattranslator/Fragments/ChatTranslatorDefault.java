package com.example.chattranslator.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.chattranslator.Activities.ChatTranslatorActivity;
import com.example.chattranslator.Activities.MainFeaturesActivity;
import com.example.chattranslator.Adapters.CountrySpinnerAdapter;
import com.example.chattranslator.Models.Language;
import com.example.chattranslator.R;
import com.example.chattranslator.Services.LanguageApiService;
import com.example.chattranslator.Services.SpeechToTextService;
import com.example.chattranslator.Services.TextTranslateService;
import com.example.chattranslator.Utils.AdManager;
import com.example.chattranslator.databinding.FragmentChatTranslatorDefaultBinding;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

public class ChatTranslatorDefault extends Fragment {
    private FragmentChatTranslatorDefaultBinding fragmentChatTranslatorDefaultBinding;
    private Spinner spinnerOne, spinnerTwo;
    private String textFromActivity, sourceLanguageCode, targetLanguageCode;
    private List<Language> languages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChatTranslatorDefaultBinding = FragmentChatTranslatorDefaultBinding.inflate(inflater, container, false);
        if (getArguments()!=null) {
            textFromActivity = getArguments().getString("text");
        }
        else {
            textFromActivity = "";
        }
        return fragmentChatTranslatorDefaultBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerOne = fragmentChatTranslatorDefaultBinding.ctSpinnerOne;
        spinnerTwo = fragmentChatTranslatorDefaultBinding.ctSpinnerTwo;

        new FetchDataAsyncTask().execute();

        showNativeAd();

        LanguageApiService languageApiService = new LanguageApiService();
        languages = languageApiService.fetchData(requireContext());

        fragmentChatTranslatorDefaultBinding.ctEditText.setText(textFromActivity);
        String textToBeTranslated = fragmentChatTranslatorDefaultBinding.ctEditText.getText().toString();
        if (!textToBeTranslated.equals("")) {
            detectLanguage(textToBeTranslated);
        }
        else {
            fragmentChatTranslatorDefaultBinding.ctEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        detectLanguage(s.toString());
                    }
                }
            });
        }

        fragmentChatTranslatorDefaultBinding.btnCtDefMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                {
                    fragmentChatTranslatorDefaultBinding.ctEditText.setHint("Speak on beep...");
                    SpeechToTextService sttService = new SpeechToTextService(fragmentChatTranslatorDefaultBinding.btnCtDefMic);
                    sttService.getSpeechToText(getContext(), new SpeechToTextService.SpeechRecognitionCallBack() {
                        @Override
                        public void onSpeechRecognised(String data) {
                            fragmentChatTranslatorDefaultBinding.ctEditText.setText(data);
                        }
                    });
                }
                else {
                    requestPermissionsIfNecessary();
                }
            }
        });
        fragmentChatTranslatorDefaultBinding.btnCtTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = fragmentChatTranslatorDefaultBinding.ctEditText.getText().toString();
                if (!text.isEmpty()) {
                    Language selectedLanguage = (Language) spinnerOne.getSelectedItem();
                    Language selectedLanguageSecond = (Language) spinnerTwo.getSelectedItem();
                    if (selectedLanguage != null && selectedLanguageSecond != null) {
                        targetLanguageCode = selectedLanguageSecond.getCode();

                        Bundle bundle = new Bundle();
                        bundle.putString("text", text);
                        bundle.putString("slCode", sourceLanguageCode);
                        bundle.putString("tlCode", targetLanguageCode);
                        bundle.putInt("posSpinnerOne", spinnerOne.getSelectedItemPosition());
                        bundle.putInt("posSpinnerTwo", spinnerTwo.getSelectedItemPosition());

                        ChatTranslatorOutput cto = new ChatTranslatorOutput();
                        cto.setArguments(bundle);

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, cto);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } else {
                    fragmentChatTranslatorDefaultBinding.ctEditText.setError("Please enter text!");
                }
            }
        });


        fragmentChatTranslatorDefaultBinding.ctBtnIntercast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spinnerOnePosition = spinnerOne.getSelectedItemPosition();
                int spinnerTwoPosition = spinnerTwo.getSelectedItemPosition();

                spinnerOne.setSelection(spinnerTwoPosition);
                spinnerTwo.setSelection(spinnerOnePosition);
            }
        });


        fragmentChatTranslatorDefaultBinding.btnCtDefMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    SpeechToTextService sttService = new SpeechToTextService(fragmentChatTranslatorDefaultBinding.btnCtDefMic);
                    sttService.getSpeechToText(getContext(), new SpeechToTextService.SpeechRecognitionCallBack() {
                        @Override
                        public void onSpeechRecognised(String data) {
                            fragmentChatTranslatorDefaultBinding.ctEditText.setText(data);
                        }
                    });
                } else {
                    requestPermissionsIfNecessary();
                }
            }
        });
    }

    private void showNativeAd() {
        TemplateView nativeAdView = fragmentChatTranslatorDefaultBinding.nativeChatad;

        AdManager adManager = new AdManager(getContext(), nativeAdView);
        adManager.showAd(AdManager.AdType.NATIVE);
    }

    private void detectLanguage(String string) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextTranslateService textTranslateService = new TextTranslateService(getContext());
                textTranslateService.identifyLanguage(string, new TextTranslateService.LanguageIdentificationCallback() {
                    @Override
                    public void onLanguageIdentified(String languageCode) {
                        Log.d("text", "onLanguageIdentified: " +languageCode);
                        sourceLanguageCode = languageCode;
                        int pos = getSpinnerPosition(languageCode);
                        spinnerOne.setSelection(pos);
                    }
                });
            }
        }, 500);
    }


    private int getSpinnerPosition(String languageCode) {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).getCode().equals(languageCode)) {
                return i;
            }
        }
        return 0;
    }


    private void requestPermissionsIfNecessary() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
        }
        else {
            Dexter.withContext(getContext())
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            new AlertDialog.Builder(getContext())
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
            return apiClient.fetchData(getContext());
        }

        @Override
        protected void onPostExecute(List<Language> languageList) {
            super.onPostExecute(languageList);

            if (languageList != null && !languageList.isEmpty()) {
                populateSpinners(languageList);
            }
        }

        private void populateSpinners(List<Language> languages) {
            CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(requireContext(), languages);
            spinnerOne.setAdapter(adapter);
            spinnerTwo.setAdapter(adapter);

        }
    }
}