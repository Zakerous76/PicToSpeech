package com.example.pictospeech;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.view.LayoutInflater;
import android.widget.TextView;

public class LanguageAdapter extends BaseAdapter {

    private final Context context;
    private final String[] languages_display;
    private final String[] languages_locale;

    public LanguageAdapter(Context context) {
        this.context = context;
        // Assuming LocalizationHelper.SUPPORTED_LANGUAGES is already defined elsewhere.
        languages_locale = LocalizationHelper.SUPPORTED_LANGUAGES_locale;
        languages_display = LocalizationHelper.SUPPORTED_LANGUAGES_display;
    }

    @Override
    public int getCount() {
        return languages_locale.length;
    }

    // Method to get the language locale name
    @Override
    public Object getItem(int position) {
        return languages_locale[position];
    }

    @Override
    public long getItemId(int position) {
        // You can return the position as ID if there's no unique ID to associate with each item.
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item_layout, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(languages_display[position]);

        return convertView;
    }
    // Method to find the position of a specific language
    public int getPosition(String language_locale) {
        for (int i = 0; i < languages_locale.length; i++) {
            if (languages_locale[i].equalsIgnoreCase(language_locale)) {
                return i;
            }
        }
        return -1; // Return -1 if the language is not found
    }
}
