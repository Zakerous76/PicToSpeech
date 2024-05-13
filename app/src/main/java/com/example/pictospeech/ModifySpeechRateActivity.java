package com.example.pictospeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
public class ModifySpeechRateActivity extends AppCompatActivity {
// TODO: Add different options to change the pitch and etc.
    AppCompatButton decreaseBtn, increaseBtn;
    TextView speechRateTxtV;
    SharedPreferences prefs;

    // Initialize default value for rate
    float rate = 1.0F;
    float rateLowerLimit = .5F;
    float rateUpperLimit = 4.0F;
    float rateStep = .5F;

    // Key for rate in SharedPreferences
    private static final String RATE_KEY = "rate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_rate_activity);

        decreaseBtn = findViewById(R.id.decrease_speech_rate_btn);
        increaseBtn = findViewById(R.id.increase_speech_rate_btn);
        speechRateTxtV = findViewById(R.id.speech_rate_txtV);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore rate from SharedPreferences
        rate = prefs.getFloat(RATE_KEY, rate);

        // Update UI with the restored rate
        speechRateTxtV.setText(String.format("x%s", rate));

        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rate > rateLowerLimit) {
                    rate -= rateStep;
                    speechRateTxtV.setText(String.format("x%s", rate));
                    Log.d("ModifySpeechRateActivity", String.format("Rate: %s", rate));
                    saveRateToSharedPreferences(); // Save the updated rate
                }
            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rate < rateUpperLimit) {
                    rate += rateStep;
                    speechRateTxtV.setText(String.format("x%s", rate));
                    Log.d("ModifySpeechRateActivity", String.format("Rate: %s", rate));
                    saveRateToSharedPreferences(); // Save the updated rate
                }
            }
        });

    }

    // Method to save the rate to SharedPreferences
    private void saveRateToSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(RATE_KEY, rate);
        editor.apply();
    }
}
