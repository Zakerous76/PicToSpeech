package com.example.pictospeech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ScanPhotoActivity extends AppCompatActivity {
    AppCompatButton uploadAPhoto;
    AppCompatButton takeAPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_a_photo_activity);

        uploadAPhoto = findViewById(R.id.select_from_gallery_btn);
        takeAPhoto = findViewById(R.id.take_a_photo_btn);

        // TODO: implement upload/take a photo buttons
        uploadAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScanPhotoActivity.this, UploadFromGalleryActivity.class);
                startActivity(i);
            }
        });
        takeAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScanPhotoActivity.this, TakePhotoActivity.class);
                startActivity(i);
            }
        });


    }
}