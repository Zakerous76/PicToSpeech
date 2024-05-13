package com.example.pictospeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private final int CAMERA_REQ_CODE = 100;
    private final int MY_DATA_CHECK_CODE = 200;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    AppCompatButton optionsBtn, scanPicBtn;
    private final String TAG = "MainActivity.java";
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the status bar color

        setContentView(R.layout.main_activity);
        optionsBtn = findViewById(R.id.options_btn);
        scanPicBtn = findViewById(R.id.scan_a_pic_btn);

        // Check if the app has permission to access the camera
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_REQ_CODE);
        }

// I am keeping the following for future use
//        // Fire off an intent to check if a TTS engine is installed
//        Intent checkIntent = new Intent();
//        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
//
//        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//                    // Get the list of installed TTS engines
//                    PackageManager packageManager = getPackageManager();
//                    Intent intent = new Intent();
//                    intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//                    startActivityForResult(intent, MY_DATA_CHECK_CODE);
//                } else {
//                    // TTS initialization failed
//                    Log.e(TAG, "TextToSpeech initialization failed");
//                }
//            }
//        });

        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(i);
            }
        });

        scanPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ScanPhotoActivity.class);
                startActivity(i);
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your operations
                // For example, open the gallery or access external storage
                // Your code here...
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied. You won't be able to access the gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }

// I am keeping the following for future use
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == MY_DATA_CHECK_CODE) {
//            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
//                // TTS engine data is available, set the desired engine
//                textToSpeech.setEngineByPackageName("com.google.android.tts");
//                // Set language for the selected TTS engine
//                Locale turkishLocale = new Locale("tr", "TR");
//                textToSpeech.setLanguage(turkishLocale);
//            } else {
//                // TTS engine data is not available, prompt user to install
//                Intent installIntent = new Intent();
//                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//                startActivity(installIntent);
//            }
//        }
//    }


}