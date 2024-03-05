package com.example.chattranslator.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.chattranslator.Adapters.CountrySpinnerAdapter;
import com.example.chattranslator.Models.Language;
import com.example.chattranslator.R;
import com.example.chattranslator.Services.LanguageApiService;
import com.example.chattranslator.Services.SpeechToTextService;
import com.example.chattranslator.Services.TextToSpeechManager;
import com.example.chattranslator.Services.TextTranslateService;
import com.example.chattranslator.databinding.FragmentChatTranslatorOutputBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatTranslatorOutput extends Fragment {
    FragmentChatTranslatorOutputBinding fragmentChatTranslatorOutputBinding;
    private Spinner spinnerOne, spinnerTwo;
    private int posSpinnerOne = 0, posSpinnerTwo = 0;
    private List<Language> languages;
    private String text, sourceLanCode, destinationLanCode;
    private TextToSpeechManager tts;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChatTranslatorOutputBinding = FragmentChatTranslatorOutputBinding.inflate(inflater, container, false);
        return fragmentChatTranslatorOutputBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerOne = fragmentChatTranslatorOutputBinding.ctSpinnerOne;
        spinnerTwo = fragmentChatTranslatorOutputBinding.ctSpinnerTwo;
        progressBar = fragmentChatTranslatorOutputBinding.progressBar;

        progressBar.setVisibility(View.VISIBLE);
        LanguageApiService languageApiService = new LanguageApiService();
        languages = languageApiService.fetchData(requireContext());

        tts = new TextToSpeechManager(getContext());
        new FetchDataAsyncTask().execute();

        Bundle bundle = getArguments();
        if (bundle != null) {
            posSpinnerOne = bundle.getInt("posSpinnerOne", 0);
            posSpinnerTwo = bundle.getInt("posSpinnerTwo", 0);

            text = bundle.getString("text");

            sourceLanCode = bundle.getString("slCode");
            destinationLanCode = bundle.getString("tlCode");

            translate(text, sourceLanCode, destinationLanCode);

            String targetLanText = languages.get(posSpinnerTwo).getName();
            String sourceLanguage = languages.get(posSpinnerOne).getName();

            fragmentChatTranslatorOutputBinding.ctLanTextView.setText(sourceLanguage);
            fragmentChatTranslatorOutputBinding.ctLanTextViewTwo.setText(targetLanText);
            fragmentChatTranslatorOutputBinding.ctTextTextView.setText(text);
        }

        spinnerTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                if (selectedLanguage != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    String destinationLanCodeUpdated = selectedLanguage.getCode();
                    fragmentChatTranslatorOutputBinding.ctLanTextViewTwo.setText(languages.get(position).getName());
                    if (!Objects.equals(destinationLanCode, destinationLanCodeUpdated))
                    {
                        translate(text, sourceLanCode, destinationLanCodeUpdated);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                if (selectedLanguage != null) {
                    String sourceLanCodeUpdated = selectedLanguage.getCode();
                    fragmentChatTranslatorOutputBinding.ctLanTextView.setText(languages.get(position).getName());
                    if (!Objects.equals(sourceLanCode, sourceLanCodeUpdated)) {
                        translate(text, sourceLanCodeUpdated, destinationLanCode);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fragmentChatTranslatorOutputBinding.btnCtSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Language selectedLanguage = (Language) spinnerOne.getSelectedItem();
                Locale locale = new Locale(selectedLanguage.getCode());
                if (tts.isLanguageAvailable(locale)) {
                    tts.setLanguage(locale);
                    tts.speak(fragmentChatTranslatorOutputBinding.ctTextTextView.getText().toString());
                }
                else {
                    Snackbar.make(view, "Language not available", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        fragmentChatTranslatorOutputBinding.btnCtSoundTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Language selectedLanguage = (Language) spinnerTwo.getSelectedItem();
                Locale locale = new Locale(selectedLanguage.getCode());
                if (tts.isLanguageAvailable(locale)) {
                    tts.setLanguage(locale);
                    tts.speak(fragmentChatTranslatorOutputBinding.ctTextTextViewTwo.getText().toString());
                }
                else {
                    Snackbar.make(view, "Language not available", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        fragmentChatTranslatorOutputBinding.btnCtCpy.setOnClickListener(v -> copyToClipboard(fragmentChatTranslatorOutputBinding.ctTextTextView.getText().toString()));
        fragmentChatTranslatorOutputBinding.btnCtCpyTwo.setOnClickListener(v -> copyToClipboard(fragmentChatTranslatorOutputBinding.ctTextTextViewTwo.getText().toString()));
        fragmentChatTranslatorOutputBinding.btnCtShare.setOnClickListener(v -> shareText(fragmentChatTranslatorOutputBinding.ctTextTextViewTwo.getText().toString()));
        fragmentChatTranslatorOutputBinding.ctBtnIntercast.setOnClickListener(v -> swapLanguages());
        fragmentChatTranslatorOutputBinding.btnCtNewTranslation.setOnClickListener(v -> {
            ChatTranslatorDefault ctd = new ChatTranslatorDefault();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, ctd);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }


    private void translate(String text, String sourceLan, String destLan) {
        TextTranslateService service = new TextTranslateService(getContext());
        service.translateText(text, sourceLan, destLan, new TextTranslateService.TranslationCallback() {
            @Override
            public void onTranslationComplete(final String translatedText) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    fragmentChatTranslatorOutputBinding.ctTextTextViewTwo.setText(translatedText);
                    progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Translation data", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, "Share via");
        startActivity(intent);
    }

    private void swapLanguages() {
        int spinnerOnePosition = fragmentChatTranslatorOutputBinding.ctSpinnerOne.getSelectedItemPosition();
        int spinnerTwoPosition = fragmentChatTranslatorOutputBinding.ctSpinnerTwo.getSelectedItemPosition();

        fragmentChatTranslatorOutputBinding.ctSpinnerOne.setSelection(spinnerTwoPosition);
        fragmentChatTranslatorOutputBinding.ctSpinnerTwo.setSelection(spinnerOnePosition);
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
            spinnerOne.setSelection(posSpinnerOne);
            spinnerTwo.setSelection(posSpinnerTwo);
        }
    }
}
