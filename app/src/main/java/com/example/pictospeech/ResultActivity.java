package com.example.pictospeech;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ResultActivity extends AppCompatActivity {

    TextView resultTextView;
    AppCompatButton readAloudBtn, copyToClipboardBtn;
    String resultString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        resultString = getIntent().getStringExtra("resultString");
        resultTextView = findViewById(R.id.waiting_text_view);
        copyToClipboardBtn = findViewById(R.id.copy_to_clipboard_btn);
        readAloudBtn = findViewById(R.id.read_aloud_btn);

        resultTextView.setText(resultString);

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

            }
        });

    }


}