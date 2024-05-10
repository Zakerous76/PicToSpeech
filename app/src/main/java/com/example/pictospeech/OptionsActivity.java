package com.example.pictospeech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class OptionsActivity extends AppCompatActivity {

    AppCompatButton modifySpeechRateBtn;
    ToggleButton voiceAssistantBtn;
    SharedPreferences prefs;


    // Key for voice assistant state in SharedPreferences
    private static final String VOICE_ASSISTANT_KEY = "voice_assistant_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);

        voiceAssistantBtn = findViewById(R.id.voice_assistant_tggl_btn);
        modifySpeechRateBtn = findViewById(R.id.modify_speech_rate_btn);

        // Restore voice assistant state from SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isVoiceAssistantEnabled = prefs.getBoolean(VOICE_ASSISTANT_KEY, false);
        voiceAssistantBtn.setChecked(isVoiceAssistantEnabled);

        voiceAssistantBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(VOICE_ASSISTANT_KEY, isChecked);
            editor.apply();
        });

        modifySpeechRateBtn.setOnClickListener(v -> {
            Intent i = new Intent(OptionsActivity.this, ModifySpeechRateActivity.class);
            startActivity(i);
        });
    }
}
