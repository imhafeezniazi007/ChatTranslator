package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Models.Language;

import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageApiService {

    private List<Language> languageList;
    public List<Language> fetchData(Context context) {
         languageList = new ArrayList<>();

        for (String languages : TranslateLanguage.getAllLanguages()) {
            Language language = new Language();
            Locale locale = new Locale(languages);
            String str = locale.getDisplayLanguage();
            Bitmap btm = getFlagBitmap(context, languages);
            language.setName(str);
            language.setCode(languages);
            language.setFlag(btm);
            languageList.add(language);
        }
        return languageList;
    }

    private String getFlagEmoji(String countryCode) {
        countryCode = countryCode.toUpperCase();
        int OFFSET = 127397;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < countryCode.length(); i++) {
            sb.appendCodePoint(countryCode.charAt(i) + OFFSET);
        }
        return sb.toString();
    }

    public Bitmap getFlagBitmap(Context context, String countryCode) {
        String flagEmoji = getFlagEmoji(countryCode);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(100);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        int width = (int) paint.measureText(flagEmoji);
        int height = (int) paint.getTextSize();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(flagEmoji, width / 2f, height * 0.75f, paint);

        return bitmap;
    }

    public int getSpinnerPosition(String languageCode)
    {
        Language selectedLanguage = null;
        for (Language language : languageList) {
            if (language.getCode().equals(languageCode)) {
                selectedLanguage = language;
                break;
            }
        }
        return languageList.indexOf(selectedLanguage);
    }
}