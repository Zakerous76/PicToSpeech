package com.example.pictospeech;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ResultActivity extends AppCompatActivity {

    EditText resultEditText;
    AppCompatButton readAloudBtn, copyToClipboardBtn;
    String resultString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        resultString = getIntent().getStringExtra("resultString");
        resultEditText  = findViewById(R.id.waiting_text_view);
        copyToClipboardBtn = findViewById(R.id.copy_to_clipboard_btn);
        readAloudBtn = findViewById(R.id.read_aloud_btn);

        resultEditText.setText(resultString);

        // TODO: implement copy_to_clipboard and read_aloud buttons
        copyToClipboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        readAloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}