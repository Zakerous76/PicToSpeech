package com.example.pictospeech;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    TextView resultTextView;
    AppCompatButton readAloudBtn, copyToClipboardBtn;
    String resultString;
    TextToSpeech textToSpeech;
    String TAG = "ResultActivity";
    SharedPreferences prefs;
    float speechRate = 1.0F;
    private static final String RATE_KEY = "rate";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        resultString = getIntent().getStringExtra("resultString");
        resultTextView = findViewById(R.id.waiting_text_view);
        copyToClipboardBtn = findViewById(R.id.copy_to_clipboard_btn);
        readAloudBtn = findViewById(R.id.read_aloud_btn);
        resultTextView.setText(resultString);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        speechRate = prefs.getFloat(RATE_KEY, speechRate);
        Log.e(TAG, "onCreate: speechRate => " + speechRate);

// For configuring the locale of the applcation. Might be useful when adding different language support
//        Configuration config = getResources().getConfiguration();
//        config.setLocale(new Locale("tr", "TR"));
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Specify the package name for Google's TTS engine
        String googleTTSEnginePackageName = "com.google.android.tts";
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    // Set language to Turkish
                    int result = textToSpeech.setLanguage(new Locale("tr", "TR"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Language data is missing or not supported
                        Log.e(TAG, "Turkish language is not supported");
                    } else {
                        // TTS initialization successful, proceed with using TTS
                        textToSpeech.setSpeechRate(speechRate);

                    }
                } else {
                    // TTS initialization failed
                    Log.e(TAG, "TextToSpeech initialization failed");
                }
            }
        }, googleTTSEnginePackageName);

        // TODO: implement read_aloud buttons
        copyToClipboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the clipboard manager
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData object to store the text
                ClipData clipData = ClipData.newPlainText("label", resultString);

                // Set the ClipData object to the clipboard
                clipboardManager.setPrimaryClip(clipData);

                // Show a toast message indicating that the text has been copied
                Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

            }
        });

        readAloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resultString.isEmpty()){
                    resultTextView.setError("No text recognized");
                } else {
                    textToSpeech.speak(resultString, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(textToSpeech != null){
            textToSpeech.stop();
        }
    }
}