package com.example.pictospeech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class OptionsActivity extends AppCompatActivity {
    AppCompatButton modifySpeechRateBtn;
    ToggleButton voiceAssistantBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);

        voiceAssistantBtn = findViewById(R.id.voice_assistant_tggl_btn);
        modifySpeechRateBtn = findViewById(R.id.modify_speech_rate_btn);

        modifySpeechRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OptionsActivity.this, ModifySpeechRateActivity.class);
                startActivity(i);
            }
        });
    }
}
