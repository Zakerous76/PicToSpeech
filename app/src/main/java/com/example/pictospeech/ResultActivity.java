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
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity {

    private static final String RATE_KEY = "rate";
    private static final String SPEECH_LANG_KEY = "speech_lang_locale";

    EditText resultEditTextView;
    AppCompatButton readAloudBtn, copyToClipboardBtn;
    String resultString;
    TextToSpeech textToSpeech;
    String TAG = "ResultActivity";
    SharedPreferences prefs;
    String speechLanguage_value = "en";
    float speechRate = 1.0F;
    Map<String, String> speechLanguageCountryMap = Map.of("en", "US", "tr", "TR");
    String apiKey="AIzaSyB4BxDqsKaZUV2gR90-4u1qNWhhkQahHzw";

    // Use a model that's applicable for your use case
// The Gemini 1.5 models are versatile and work with most use cases
    GenerativeModel gm;

    // Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
    GenerativeModelFutures model;
//    String prompt = "Make the following text to be grammatically correct and coherent. " +
//            "Return plain text with no formatting." +
//            "Replace the misspelled words with the closest correct word." +
//            "At the very end, write a short sentence about the what the text is about";

    // TODO: Come up with better prompts
String prompt = "The following text is the result from an OCR operation. What do you think the " +
        "following text is trying to say. Return a corrected plain text with no formatting and no " +
        "explanation ."+
        "And also At the very end, write a short sentence about the what the text is about";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);


        resultEditTextView = findViewById(R.id.result_edit_text);
        copyToClipboardBtn = findViewById(R.id.copy_to_clipboard_btn);
        readAloudBtn = findViewById(R.id.read_aloud_btn);
//        resultEditTextView.setText(resultString);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore speechLanguageLocale_value and speech rate from SharedPreferences
        speechRate = prefs.getFloat(RATE_KEY, speechRate);
        speechLanguage_value = prefs.getString(SPEECH_LANG_KEY, speechLanguage_value);

        prompt = "Return a response in the following language: (" + speechLanguage_value + ")\n" + prompt + resultString;

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

        resultEditTextView.setText("");
        resultString = getIntent().getStringExtra("resultString");
        gm = new GenerativeModel("gemini-1.5-flash", apiKey);
        model = GenerativeModelFutures.from(gm);
        prompt += resultString;
        new GenerateContentTask().execute();

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
                resultEditTextView.setError("No text recognized");
            } else {
                textToSpeech.speak(resultString, TextToSpeech.QUEUE_FLUSH, null, null);
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
                    .addText(prompt)
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
            resultEditTextView.setText(resultString+"\n______________\n"+result);
        }
    }
}