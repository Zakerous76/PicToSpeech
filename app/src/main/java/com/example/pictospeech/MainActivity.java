package com.example.pictospeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final int CAMERA_REQ_CODE = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    AppCompatButton optionsBtn, scanPicBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        optionsBtn = findViewById(R.id.settings_btn);
        scanPicBtn = findViewById(R.id.scan_a_pic_btn);

        // Check if the app has permission to access the camera
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_REQ_CODE);
        }

        optionsBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        });

        scanPicBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ScanPhotoActivity.class);
            startActivity(i);
        });

    }


}