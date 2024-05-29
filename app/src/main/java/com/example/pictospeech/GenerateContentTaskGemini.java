package com.example.pictospeech;

import android.os.AsyncTask;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;

public class GenerateContentTaskGemini extends AsyncTask<Void, Void, String> {

    String prompt;
    String apiKey="AIzaSyB4BxDqsKaZUV2gR90-4u1qNWhhkQahHzw";
    GenerativeModel gm;

    GenerativeModelFutures model;


    public GenerateContentTaskGemini(String prompt) {
        this.prompt = prompt;
        if (gm == null) {
            gm = new GenerativeModel("gemini-1.5-flash", apiKey);
            model = GenerativeModelFutures.from(gm);
        }
    }

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

}