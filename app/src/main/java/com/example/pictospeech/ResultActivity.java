package com.example.pictospeech;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ResultActivity extends AppCompatActivity {

    EditText resultEditText;
    AppCompatButton readAloudBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        resultEditText  = findViewById(R.id.resultEditText);
        readAloudBtn = findViewById(R.id.read_aloud_btn);

        // TODO: implement read_aloud button
        readAloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}