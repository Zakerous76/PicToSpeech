package com.example.pictospeech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.os.LocaleListCompat;

public class OptionsActivity extends AppCompatActivity {

    AppCompatButton modifySpeechRateBtn;
    Spinner languageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);

        languageSpinner = findViewById(R.id.language_spinner);
        modifySpeechRateBtn = findViewById(R.id.modify_speech_rate_btn);


        // Create an adapter for the language spinner
        LanguageAdapter adapter = new LanguageAdapter(this);
        languageSpinner.setAdapter(adapter);

        // Set the selected language in the spinner
        String currentLanguage = LocalizationHelper.getCurrentLanguage(this);
        int languageIndex = adapter.getPosition(currentLanguage);
        languageSpinner.setSelection(languageIndex);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = (String) adapter.getItem(position);
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(selectedLanguage);
                // Call this on the main thread as it may require Activity.restart()
                AppCompatDelegate.setApplicationLocales(appLocale);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        modifySpeechRateBtn.setOnClickListener(v -> {
            Intent i = new Intent(OptionsActivity.this, ModifySpeechRateActivity.class);
            startActivity(i);
        });
    }
}
