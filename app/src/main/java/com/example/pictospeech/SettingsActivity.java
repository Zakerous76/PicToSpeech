package com.example.pictospeech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.os.LocaleListCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String SPEECH_LANG_KEY = "speech_lang_locale";

    String speechLanguage_value = "en";
    AppCompatButton modifySpeechRateBtn;
    Spinner appLanguageSpinner, speechLanguageSpinner;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        appLanguageSpinner = findViewById(R.id.app_language_spinner);
        speechLanguageSpinner = findViewById(R.id.speech_language_spinner);
        modifySpeechRateBtn = findViewById(R.id.modify_speech_rate_btn);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore speechLanguageLocale_value from SharedPreferences
        speechLanguage_value = prefs.getString(SPEECH_LANG_KEY, speechLanguage_value);

        // Create an adapter for the language spinner
        LanguageAdapter adapter = new LanguageAdapter(this);
        appLanguageSpinner.setAdapter(adapter);
        speechLanguageSpinner.setAdapter(adapter);

        // Set the selected language in the spinner
        String currentLanguage = LocalizationHelper.getCurrentLanguage(this);
        int languageIndex = adapter.getPosition(currentLanguage);
        appLanguageSpinner.setSelection(languageIndex);

        String currentSpeechLanguage = speechLanguage_value;
        int speechLanguageIndex = adapter.getPosition(currentSpeechLanguage);
        speechLanguageSpinner.setSelection(speechLanguageIndex);

        appLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageLocale = (String) adapter.getItem(position);
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(selectedLanguageLocale);
                // Call this on the main thread as it may require Activity.restart()
                AppCompatDelegate.setApplicationLocales(appLocale);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        speechLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speechLanguage_value = (String) adapter.getItem(position);
                Log.w("TAG", "Speech Language Changed to: " + speechLanguage_value);
                saveSpeechLanguageLocaleToSharedPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        modifySpeechRateBtn.setOnClickListener(v -> {
            Intent i = new Intent(SettingsActivity.this, ModifySpeechRateActivity.class);
            startActivity(i);
        });
    }
    // Method to save the rate to SharedPreferences
    private void saveSpeechLanguageLocaleToSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SPEECH_LANG_KEY, speechLanguage_value);
        editor.apply();
    }
}
