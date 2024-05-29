package com.example.pictospeech;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ResultActivity extends AppCompatActivity {

    private static final String RATE_KEY = "rate";
    private static final String SPEECH_LANG_KEY = "speech_lang_locale";

    TextView resultTextView;
    AppCompatButton readAloudBtn, copyToClipboardBtn, moreBtn;
    String resultString;
    TextToSpeech textToSpeech;
    String TAG = "ResultActivity";
    SharedPreferences prefs;
    String speechLanguage_value = "en";
    float speechRate = 1.0F;
    Map<String, String> speechLanguageCountryMap = Map.of("en", "US", "tr", "TR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        resultString = getIntent().getStringExtra("resultString");
        resultTextView = findViewById(R.id.result_text_view);
        copyToClipboardBtn = findViewById(R.id.copy_to_clipboard_btn);
        readAloudBtn = findViewById(R.id.read_aloud_btn);
        moreBtn = findViewById(R.id.more_btn);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the OCR Result
        resultTextView.setText(resultString);

        // Restore speechLanguageLocale_value and speech rate from SharedPreferences
        speechRate = prefs.getFloat(RATE_KEY, speechRate);
        speechLanguage_value = prefs.getString(SPEECH_LANG_KEY, speechLanguage_value);

        // Specify the package name for Google's TTS engine
        String googleTTSEnginePackageName = "com.google.android.tts";
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {

                // Set language to Turkish
                int result = textToSpeech.setLanguage(new Locale(speechLanguage_value, speechLanguageCountryMap.get(speechLanguage_value)));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Language data is missing or not supported
                    Log.e(TAG,  speechLanguage_value + " language is not supported");
                } else {
                    // TTS initialization successful, proceed with using TTS
                    Log.d(TAG, "Current Speech Language: " + textToSpeech.getLanguage());
                    // Set speech rate
                    textToSpeech.setSpeechRate(speechRate);
                }
            } else {
                // TTS initialization failed
                Log.e(TAG, "TextToSpeech initialization failed");
            }
        }, googleTTSEnginePackageName);

        copyToClipboardBtn.setOnClickListener(v -> {
            // Get the clipboard manager
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            // Create a ClipData object to store the text
            ClipData clipData = ClipData.newPlainText("label", resultString);

            // Set the ClipData object to the clipboard
            clipboardManager.setPrimaryClip(clipData);

            // Show a toast message indicating that the text has been copied
            Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

        });

        readAloudBtn.setOnClickListener(v -> {
            if(resultString.isEmpty()){
                resultTextView.setError("No text recognized");
            } else {
                textToSpeech.speak(resultString, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResultString();
            }
        });

    }

    private void sendResultString() {
        // Start ResultActivity and pass resultString as an extra
        Intent intent = new Intent(ResultActivity.this, MoreAIActivity.class);
        intent.putExtra("resultString", resultString);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(textToSpeech != null){
            textToSpeech.stop();
        }
    }
}