package com.example.pictospeech;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

public class MoreAIActivity extends AppCompatActivity {

    private static final String RATE_KEY = "rate";
    private static final String SPEECH_LANG_KEY = "speech_lang_locale";

    TextView resultTextView, geminiResponseTextView;
    AppCompatButton readAloudBtn, copyToClipboardBtn, explainBtn, summarizeBtn, enhanceBtn;
    String resultString;
    TextToSpeech textToSpeech;
    String TAG = "MoreAIActivity";
    SharedPreferences prefs;
    String speechLanguage_value = "en";
    float speechRate = 1.0F;
    Map<String, String> speechLanguageCountryMap = Map.of("en", "US", "tr", "TR");
    String apiKey="AIzaSyB4BxDqsKaZUV2gR90-4u1qNWhhkQahHzw";
    GenerativeModel gm;
    GenerativeModelFutures model;
    String responseLanguage;
    String originalLanguagePrompt = "Respond with the language of the text without translating it to English.";
    String executingPrompt;
    String enhance_prompt = "Make the following text to be grammatically correct and coherent. " +
            "Return plain text with no formatting." +
            "Replace the misspelled words with the closest correct word. " +
            "Respond with the original language of the text:\n";

    // TODO: Come up with better prompts
    String explain_prompt = "The following text is the result from an OCR operation. What do you think the " +
            "following text is trying to say and what it is about. Return a plain text with no styling:\n";
    String summarize_prompt = "Summarize the following text with the context in mind:\n";
    private String displayString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_ai_activity);

        resultString = getIntent().getStringExtra("resultString");
        displayString = resultString;

        geminiResponseTextView = findViewById(R.id.result_text_view);
        copyToClipboardBtn = findViewById(R.id.copy_to_clipboard_btn);
        readAloudBtn = findViewById(R.id.read_aloud_btn);
        explainBtn = findViewById(R.id.explain_btn);
        summarizeBtn = findViewById(R.id.summarize_btn);
        enhanceBtn = findViewById(R.id.enhance_btn);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the OCR Result
        geminiResponseTextView.setText(resultString);

        // Initialize Gemini Model
        gm = new GenerativeModel("gemini-1.5-flash", apiKey);
        model = GenerativeModelFutures.from(gm);

        // Restore speechLanguageLocale_value and speech rate from SharedPreferences
        speechRate = prefs.getFloat(RATE_KEY, speechRate);
        speechLanguage_value = prefs.getString(SPEECH_LANG_KEY, speechLanguage_value);

        responseLanguage = "Return response in the (" + speechLanguage_value + ") language.";
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
            ClipData clipData = ClipData.newPlainText("label", displayString);

            // Set the ClipData object to the clipboard
            clipboardManager.setPrimaryClip(clipData);

            // Show a toast message indicating that the text has been copied
            Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

        });

        readAloudBtn.setOnClickListener(v -> {
            if(resultString.isEmpty()){
                resultTextView.setError("No text recognized");
            } else {
                textToSpeech.speak(displayString, TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(getApplicationContext(), "Please wait for the response...", Toast.LENGTH_SHORT).show();
            }
        });

        explainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executingPrompt = responseLanguage+explain_prompt+resultString;
                new GenerateContentTask().execute();
                Toast.makeText(getApplicationContext(), "Please wait while we are preparing an EXPLAINATION...", Toast.LENGTH_LONG).show();
            }

        });

        summarizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Set proper enhance lang
                executingPrompt = originalLanguagePrompt + summarize_prompt + resultString;
                new GenerateContentTask().execute();
                Toast.makeText(getApplicationContext(), "Please wait while we are SUMMARIZING the text...", Toast.LENGTH_LONG).show();

            }

        });

        enhanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executingPrompt = enhance_prompt+resultString;
                new GenerateContentTask().execute();
                Toast.makeText(getApplicationContext(), "Please wait while we are ENHANCING the text...", Toast.LENGTH_LONG).show();

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

    private class GenerateContentTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // Create the Content object
            Content content = new Content.Builder()
                    .addText(executingPrompt)
                    .build();

            // Execute the generateContent() method
            GenerateContentResponse response = null;
            try {
                response = model.generateContent(content).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Return the result text
            return response.getText();
        }

        @Override
        protected void onPostExecute(String result) {
            // Update the UI with the result text
            displayString = result;
            geminiResponseTextView.setText(result.strip());
        }
    }
}