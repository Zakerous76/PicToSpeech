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
    private static final String APP_LANG_KEY = "app_lang_locale";

    String speechLanguage_locale_value = "en";
    String appLanguage_locale_value = "en";
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
        speechLanguage_locale_value = prefs.getString(SPEECH_LANG_KEY, speechLanguage_locale_value);
        appLanguage_locale_value = prefs.getString(APP_LANG_KEY, appLanguage_locale_value);

        // Create an adapter for the language spinner
        LanguageAdapter adapter_speech = new LanguageAdapter(this);
        LanguageAdapter adapter_app = new LanguageAdapter(this);

        appLanguageSpinner.setAdapter(adapter_app);
        speechLanguageSpinner.setAdapter(adapter_speech);

        // Set the selected language in the spinner
        int languageIndex = adapter_app.getPosition(appLanguage_locale_value);
        appLanguageSpinner.setSelection(languageIndex);

        int speechLanguageIndex = adapter_speech.getPosition(speechLanguage_locale_value);
        speechLanguageSpinner.setSelection(speechLanguageIndex);

        appLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageLocale = (String) adapter_app.getItem(position);
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(selectedLanguageLocale);
                Log.e("appLocale", appLocale.toString());
//                Toast.makeText(SettingsActivity.this, "App Language Changed to: " + appLanguage_locale_value, Toast.LENGTH_SHORT).show();;
                saveAppLanguageLocaleToSharedPreferences();
                // Call this on the main thread as it may require Activity.restart()
                AppCompatDelegate.setApplicationLocales(appLocale);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        speechLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speechLanguage_locale_value = (String) adapter_speech.getItem(position);
//                Toast.makeText(SettingsActivity.this, "Speech Language Changed to: " + speechLanguage_locale_value, Toast.LENGTH_SHORT).show();;
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
        editor.putString(SPEECH_LANG_KEY, speechLanguage_locale_value);
        editor.apply();
    }
    private void saveAppLanguageLocaleToSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(APP_LANG_KEY, appLanguage_locale_value);
        editor.apply();
    }
}
