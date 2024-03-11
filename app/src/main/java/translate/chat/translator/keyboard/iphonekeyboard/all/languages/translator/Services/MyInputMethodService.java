package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Adapters.CountrySpinnerAdapter;
import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Models.Language;
import com.example.chattranslator.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.List;
import java.util.Objects;

public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView keyboardView;
    private LinearLayout mainLayout;
    private EditText keyboardEditText;
    private Keyboard keyboard;
    private AdView mAdView;
    private boolean isAlphabetsDisplayed = true;
    private int languageState = 0;
    private SharedPreferences sharedPreferences;
    private boolean toggleNumbers, isFocused, keyPressSound, isVibrateEnabled;
    private ImageView imageView;
    private AppCompatSpinner spinnerOne, spinnerTwo;
    private int posSpinnerOne = 0, posSpinnerTwo = 0;
    private List<Language> languages;
    private SpeechRecognizer speechRecognizer;
    private String currLan, textToTranslate, sourceLanCode, destinationLanCode;
    private SpeechToTextService stts;
    private AudioManager audioManager;
    private float vol;
    private Vibrator vibrator;

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.layout_keyboard_eng, null);
        keyboardView = view.findViewById(R.id.keyboard_view);
        mainLayout = view.findViewById(R.id.mainLayout);
        keyboardEditText = view.findViewById(R.id.keyboard_editText);
        imageView = view.findViewById(R.id.backBtn);
        spinnerOne = view.findViewById(R.id.keyboard_spinner_one);
        spinnerTwo = view.findViewById(R.id.keyboard_spinner_two);
        textToTranslate = keyboardEditText.getText().toString();
        mAdView = view.findViewById(R.id.bannerKeyboardView);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        vol = 0.5F;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        LanguageApiService languageApiService = new LanguageApiService();
        languages = languageApiService.fetchData(this);
        stts = new SpeechToTextService();

        sharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE);

        toggleNumbers = sharedPreferences.getBoolean("toggleNumbers", false);
        currLan = sharedPreferences.getString("currLan", null);
        keyPressSound = sharedPreferences.getBoolean("keyPressSound", false);
        isVibrateEnabled = sharedPreferences.getBoolean("isVibrateEnabled", false);

        if (toggleNumbers) {
            keyboard = new Keyboard(this, R.xml.number_pad_with_numbers);
        } else {
            keyboard = new Keyboard(this, R.xml.number_pad);
        }

        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        fetchDataAndPopulateSpinners();

        if (mAdView != null) {
            showBannerAd();
        }

        keyboardEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isFocused = hasFocus;
            }
        });
        spinnerTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                if (selectedLanguage != null) {
                    String destinationLanCodeUpdated = selectedLanguage.getCode();
                    if (!Objects.equals(destinationLanCode, destinationLanCodeUpdated)) {
                        translateText(textToTranslate, sourceLanCode, destinationLanCodeUpdated);
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
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);
                if (selectedLanguage != null) {
                    String sourceLanCodeUpdated = selectedLanguage.getCode();
                    if (!Objects.equals(sourceLanCode, sourceLanCodeUpdated)) {
                        translateText(textToTranslate, sourceLanCodeUpdated, destinationLanCode);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        textToTranslate = keyboardEditText.getText().toString();
        if (!textToTranslate.equals("")) {
            detectLanguage(textToTranslate);
        } else {
            keyboardEditText.addTextChangedListener(new TextWatcher() {
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
        posSpinnerOne = spinnerOne.getSelectedItemPosition();
        posSpinnerTwo = spinnerTwo.getSelectedItemPosition();
        return view;
    }

    private void showBannerAd() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.bannerIdSimple));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.loadAd(adRequest);

    }

    private void clickSound() {
        if (keyPressSound) {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, vol);
        }
    }

    private void vibrate() {
        if (isVibrateEnabled) {
            vibrator.vibrate(50);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        if (!isFocused) {
            if (primaryCode == -101) {
                toggleKeyboardLayout();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                ic.deleteSurroundingText(1, 0);
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            } else if (primaryCode == -102) {
                changeLanguage();
            } else if (primaryCode == 1001) {
                translateView();
            } else if (primaryCode == 1002) {
                openSettings();
            } else if (primaryCode == 1003) {
                voiceTranslateView();
            } else if (primaryCode == 1004) {
                if (!textToTranslate.equals("") && !sourceLanCode.equals("") && !destinationLanCode.equals("")) {
                    translateText(textToTranslate, sourceLanCode, destinationLanCode);
                }
            } else if (primaryCode == 1005) {
                voiceTranslate();
            } else if (primaryCode == 1006) {
                translateView();
            } else {
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
            }
        } else {
            InputConnection icn = keyboardEditText.onCreateInputConnection(new EditorInfo());
            if (primaryCode == -101) {
                changeLanguage();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                icn.deleteSurroundingText(1, 0);
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {
                icn.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            } else if (primaryCode == -102) {
                changeLanguage();
            } else if (primaryCode == 1001) {
                translateView();
            } else if (primaryCode == 1002) {
                openSettings();
            } else if (primaryCode == 1003) {
                voiceTranslateView();
            } else if (primaryCode == 1004) {
                Language selectedLanguageSecond = (Language) spinnerTwo.getSelectedItem();
                destinationLanCode = selectedLanguageSecond.getCode();
                translateText(textToTranslate, sourceLanCode, destinationLanCode);
            } else if (primaryCode == 1005) {
                voiceTranslate();
            } else if (primaryCode == 1006) {
                translateView();
            } else {
                char code = (char) primaryCode;
                icn.commitText(String.valueOf(code), 1);
            }
        }
        clickSound();
        vibrate();
    }
    private void voiceTranslateView() {
        keyboard = new Keyboard(this, R.xml.voice_translate_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
    }

    private void detectLanguage(String string) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextTranslateService textTranslateService = new TextTranslateService(MyInputMethodService.this);
                textTranslateService.identifyLanguage(string, new TextTranslateService.LanguageIdentificationCallback() {
                    @Override
                    public void onLanguageIdentified(String languageCode) {
                        textToTranslate = keyboardEditText.getText().toString();
                        sourceLanCode = languageCode;
                        int pos = getSpinnerPosition(languageCode);
                        spinnerOne.setSelection(pos);
                    }
                });
            }
        }, 300);
    }

    private int getSpinnerPosition(String languageCode) {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).getCode().equals(languageCode)) {
                return i;
            }
        }
        return 0;
    }

    private void translateText(String text, String sourceLan, String destLan) {
        Log.d("TAG", "translateText: with " + text);
        if (textToTranslate.equals("")) {
            keyboardEditText.setError("Please write text to translate");
        } else {

            TranslatorOptions options = new TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLan)
                    .setTargetLanguage(destLan)
                    .build();
            Translator translator = Translation.getClient(options);

            translator.downloadModelIfNeeded();
            translator.translate(text)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            InputConnection ic = getCurrentInputConnection();
                            if (ic != null) {
                                ic.commitText(s + " ", 1);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

    }

    private void translateView() {
        switch (languageState) {
            case 0:
                if (toggleNumbers) {
                    keyboard = new Keyboard(this, R.xml.eng_keyboard_layout_with_numbers);
                    mainLayout.setVisibility(View.VISIBLE);
                } else {
                    keyboard = new Keyboard(this, R.xml.eng_translaion_keyboard_layout);
                    mainLayout.setVisibility(View.VISIBLE);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (toggleNumbers) {
                            keyboard = new Keyboard(v.getContext(), R.xml.number_pad_with_numbers);
                            mainLayout.setVisibility(View.GONE);
                        } else {
                            keyboard = new Keyboard(v.getContext(), R.xml.number_pad);
                            mainLayout.setVisibility(View.GONE);
                            keyboardView.setKeyboard(keyboard);
                        }

                    }
                });
                break;
            case 1:
                keyboard = new Keyboard(this, R.xml.hindi_translaion_keyboard_layout);
                mainLayout.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        keyboard = new Keyboard(v.getContext(), R.xml.hindi_keyboard_layout);
                        mainLayout.setVisibility(View.GONE);
                        keyboardView.setKeyboard(keyboard);
                    }
                });

                break;
            case 2:
                keyboard = new Keyboard(this, R.xml.arabic_translaion_keyboard_layout);
                mainLayout.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        keyboard = new Keyboard(v.getContext(), R.xml.arabic_keyboard_layout);
                        mainLayout.setVisibility(View.GONE);
                        keyboardView.setKeyboard(keyboard);
                    }
                });

                break;
        }
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);


    }

    private void voiceTranslate() {
        stts.getSTTKeyboard(this, new SpeechToTextService.SpeechRecognitionCallBack() {
            @Override
            public void onSpeechRecognised(String data) {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ic.commitText(data + " ", 1);
                }
            }
        });
    }

    private void openSettings() {
        String uriString = "chattranslator://settingactivity";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void changeLanguage() {
        switch (languageState) {
            case 0:
                keyboard = new Keyboard(this, R.xml.hindi_keyboard_layout);
                languageState = 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Hindi", null);
                editor.apply();
                break;
            case 1:
                keyboard = new Keyboard(this, R.xml.arabic_keyboard_layout);
                languageState = 2;
                editor = sharedPreferences.edit();
                editor.putString("Arabic", null);
                editor.apply();
                break;
            case 2:
                if (toggleNumbers)
                {
                    keyboard = new Keyboard(this, R.xml.number_pad_with_numbers);
                }
                else {
                    keyboard = new Keyboard(this, R.xml.number_pad);
                }
                languageState = 0;
                editor = sharedPreferences.edit();
                editor.putString("English", null);
                editor.apply();
                break;
        }
        keyboardView.setKeyboard(keyboard);
    }

    private void fetchDataAndPopulateSpinners() {
                final List<Language> languageList = fetchData();
                if (languageList != null && !languageList.isEmpty()) {
                    populateSpinners(languageList);
                }
    }

    private List<Language> fetchData() {
        LanguageApiService apiClient = new LanguageApiService();
        return apiClient.fetchData(this);
    }

    private void populateSpinners(List<Language> languages) {
        CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(this, languages);
        spinnerOne.setAdapter(adapter);
        spinnerTwo.setAdapter(adapter);
    }

    private void toggleKeyboardLayout() {
        if (isAlphabetsDisplayed) {
            keyboard = new Keyboard(this, R.xml.numbers_special_characters_layout);
        } else {
            toggleOnABC();
        }
        keyboardView.setKeyboard(keyboard);
        isAlphabetsDisplayed = !isAlphabetsDisplayed;
    }

    private void toggleOnABC() {
        switch (languageState) {
            case 0:
                if (toggleNumbers) {
                    keyboard = new Keyboard(this, R.xml.number_pad_with_numbers);
                    mainLayout.setVisibility(View.GONE);
                } else {
                    keyboard = new Keyboard(this, R.xml.number_pad);
                    mainLayout.setVisibility(View.GONE);
                    keyboardView.setKeyboard(keyboard);
                }
                break;
            case 1:
                keyboard = new Keyboard(this, R.xml.hindi_keyboard_layout);
                mainLayout.setVisibility(View.GONE);
                keyboardView.setKeyboard(keyboard);
                break;
            case 2:
                keyboard = new Keyboard(this, R.xml.arabic_keyboard_layout);
                mainLayout.setVisibility(View.GONE);
                keyboardView.setKeyboard(keyboard);
                break;
        }
        keyboardView.setOnKeyboardActionListener(this);

    }

    @Override
    public void onPress(int primaryCode) {}

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {}

    @Override
    public void swipeLeft() {}

    @Override
    public void swipeRight() {}

    @Override
    public void swipeDown() {}

    @Override
    public void swipeUp() {}
}
