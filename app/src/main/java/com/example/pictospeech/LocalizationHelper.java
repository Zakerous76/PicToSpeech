package com.example.pictospeech;

import android.content.Context;

import java.util.Locale;

public class LocalizationHelper {

    // List of supported languages
    public static String[] SUPPORTED_LANGUAGES_display = {"English | İnglizce", "Turkish | Türkçe"};
    public static String[] SUPPORTED_LANGUAGES_locale = {"en", "tr"};

    // Get the current language from the system settings. Returns language locale code.
    public static String getCurrentLanguage(Context context) {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

}