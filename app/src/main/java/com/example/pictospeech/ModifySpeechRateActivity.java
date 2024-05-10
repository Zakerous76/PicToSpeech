package com.example.pictospeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ModifySpeechRateActivity extends AppCompatActivity {

    AppCompatButton decreaseBtn, increaseBtn;
    TextView speechRateTxtV;

    float rate = 1.0F;
    float rateLowerLimit = .5F;
    float rateUpperLimit = 4.0F;
    float rateStep = .5F;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_rate_activity);

        decreaseBtn = findViewById(R.id.decrease_speech_rate_btn);
        increaseBtn = findViewById(R.id.increase_speech_rate_btn);
        speechRateTxtV = findViewById(R.id.speech_rate_txtV);


        // TODO: implement decrease and increase functionalities
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rate>rateLowerLimit){
                    rate -= rateStep;
                    speechRateTxtV.setText(String.format("x%s", rate));
                    Log.d("ModiftSpeechRateActivity", String.format("Rate: %s", rate));
                }
            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rate<rateUpperLimit){
                    rate += rateStep;
                    speechRateTxtV.setText(String.format("x%s", rate));
                    Log.d("ModiftSpeechRateActivity", String.format("Rate: %s", rate));

                }

            }
        });

    }
}