package com.example.chattranslator.Services;// TextTranslateService.java

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.chattranslator.Activities.MainFeaturesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

public class TextTranslateService {

    private LanguageIdentifier languageIdentifier;
    private Context mContext;

    public TextTranslateService(Context context) {
        languageIdentifier = LanguageIdentification.getClient();
        mContext = context;
    }

    public void identifyLanguage(String text, final LanguageIdentificationCallback callback) {
        languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String languageCode) {
                        if (languageCode != null && !languageCode.equals("und")) {
                            callback.onLanguageIdentified(languageCode);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }

    public void translateText(String text, String sourceLanguageCode, String targetLanguageCode, final TranslationCallback callback) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(targetLanguageCode)
                .build();
        Translator translator = Translation.getClient(options);

        translator.translate(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        callback.onTranslationComplete(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        promptModelDownload(translator, text, callback);
                    }
                });
    }

    private void promptModelDownload(Translator translator, String text, TranslationCallback callback) {
        new AlertDialog.Builder(mContext)
                .setTitle("Model not downloaded")
                .setMessage("This device requires translation model to be downloaded. Do you want to download it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "Downloading...", Toast.LENGTH_SHORT).show();
                        downloadModelAndTranslate(translator, text, callback);
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

    private void downloadModelAndTranslate(Translator translator, String text, TranslationCallback callback) {
        translator.downloadModelIfNeeded()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext, "Model downloaded successfully.", Toast.LENGTH_SHORT).show();
                        translator.translate(text)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {
                                        callback.onTranslationComplete(translatedText);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Translation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Model download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface LanguageIdentificationCallback {
        void onLanguageIdentified(String languageCode);
    }

    public interface TranslationCallback {
        void onTranslationComplete(String translatedText);
    }
}
