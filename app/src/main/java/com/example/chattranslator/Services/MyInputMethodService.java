package com.example.chattranslator.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.chattranslator.Adapters.CountrySpinnerAdapter;
import com.example.chattranslator.Models.Language;
import com.example.chattranslator.R;

import java.util.List;

public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView keyboardView;
    private LinearLayout mainLayout;
    private EditText keyboardEditText;
    private Keyboard keyboard;
    private boolean isAlphabetsDisplayed = true;
    private int languageState = 0;
    private SharedPreferences sharedPreferences;
    private boolean toggleNumbers;
    private ImageView imageView;
    private AppCompatSpinner spinnerOne, spinnerTwo;
    private int posSpinnerOne = 0, posSpinnerTwo = 0;
    private List<Language> languages;
    private String currLan;

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.layout_keyboard_eng, null);
        keyboardView = view.findViewById(R.id.keyboard_view);
        mainLayout = view.findViewById(R.id.mainLayout);
        keyboardEditText = view.findViewById(R.id.keyboard_editText);
        imageView = view.findViewById(R.id.backBtn);
        spinnerOne = view.findViewById(R.id.keyboard_spinner_one);
        spinnerTwo = view.findViewById(R.id.keyboard_spinner_two);


        sharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE);
        toggleNumbers = sharedPreferences.getBoolean("toggleNumbers", false);
        currLan = sharedPreferences.getString("currLan", null);

        if (toggleNumbers) {
            keyboard = new Keyboard(this, R.xml.number_pad_with_numbers);
        } else {
            keyboard = new Keyboard(this, R.xml.number_pad);
        }

        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        fetchDataAndPopulateSpinners();

        posSpinnerOne = spinnerOne.getSelectedItemPosition();
        posSpinnerTwo = spinnerTwo.getSelectedItemPosition();
        return view;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        if (primaryCode == -101) {
            toggleKeyboardLayout();
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            ic.deleteSurroundingText(1, 0);
        } else if (primaryCode == Keyboard.KEYCODE_DONE) {
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        } else if (primaryCode == -102) {
            changeLanguage();
        } else if (primaryCode == 1001) {
            textTranslate();
        } else if (primaryCode == 1002) {
            openSettings();
        } else if (primaryCode == 1003) {
            voiceTranslate();
        } else {
            char code = (char) primaryCode;
            ic.commitText(String.valueOf(code), 1);
        }
    }

    private void textTranslate() {
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

//    @Override
//    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        if (keyCode == Keyboard.KEYCODE_DELETE) {
//            InputConnection ic = getCurrentInputConnection();
//            CharSequence textBeforeCursor = ic.getTextBeforeCursor(Integer.MAX_VALUE, 0);
//            int length = textBeforeCursor != null ? textBeforeCursor.length() : 0;
//            ic.deleteSurroundingText(length, 0);
//            return true;
//        }
//        return super.onKeyLongPress(keyCode, event);
//    }

    private void toggleKeyboardLayout() {
        if (isAlphabetsDisplayed) {
            keyboard = new Keyboard(this, R.xml.numbers_special_characters_layout);
        } else {
            keyboard = new Keyboard(this, R.xml.number_pad);
        }
        keyboardView.setKeyboard(keyboard);
        isAlphabetsDisplayed = !isAlphabetsDisplayed;
    }

    @Override
    public void onPress(int primaryCode) {}

    @Override
    public void onRelease(int primaryCode) {}

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
