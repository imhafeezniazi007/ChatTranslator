package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Models.AvailableLanguage;
import com.example.chattranslator.R;

import java.util.List;

public class LanguagesAdapter extends ArrayAdapter<AvailableLanguage> {
    private Context context;
    private List<AvailableLanguage> languageList;

    public LanguagesAdapter(@NonNull Context context, @NonNull List<AvailableLanguage> objects) {
        super(context, 0, objects);
        this.context = context;
        this.languageList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.language_item, parent, false);
        }

        AvailableLanguage currentLanguage = languageList.get(position);

        TextView languageName = listItem.findViewById(R.id.language_name);
        languageName.setText(currentLanguage.getName());

        return listItem;
    }
}