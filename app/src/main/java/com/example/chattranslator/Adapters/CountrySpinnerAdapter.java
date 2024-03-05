package com.example.chattranslator.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chattranslator.Models.Language;
import com.example.chattranslator.R;

import java.util.List;

public class CountrySpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Language> languageList;

    public CountrySpinnerAdapter(Context context, List<Language> languageList) {
        this.context = context;
        this.languageList = languageList;
    }

    @Override
    public int getCount() {
        return languageList.size();
    }

    @Override
    public Object getItem(int position) {
        return languageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.layout_custom_spinner, null);
            viewHolder = new ViewHolder();
            viewHolder.flagImageView = view.findViewById(R.id.flagImageView);
            viewHolder.countryNameTextView = view.findViewById(R.id.countryNameTextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Language language = languageList.get(position);

        viewHolder.countryNameTextView.setText(language.getName());
        viewHolder.flagImageView.setImageBitmap(language.getFlag());

        return view;
    }


    static class ViewHolder {
        ImageView flagImageView;
        TextView countryNameTextView;
    }
}
