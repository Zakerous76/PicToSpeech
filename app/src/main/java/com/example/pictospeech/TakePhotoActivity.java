package com.example.pictospeech;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.components.BuildConfig;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TakePhotoActivity extends AppCompatActivity {
    private final int CAMERA_REQ_CODE = 100;
    // TODO: Use Camera2 api to have full control over the aspect ratio and the resolution
    private String imagePath;

    String resultString;
    String TAG = "TakePhotoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_activity);

        // request permission for the camera
        Log.d(TAG, "onCreate: " + ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TakePhotoActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_REQ_CODE);
        } else {
            startCamera();
        }
        
    }

    private void startCamera() {

        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a file to save the image
        File imageFile = createImageFile();
        if (imageFile != null) {
            // Get the file path
            imagePath = imageFile.getAbsolutePath();
            // Configure the camera intent to save the image to the file
            iCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    imageFile));
        startActivityForResult(iCamera, CAMERA_REQ_CODE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: Find a way to tell the user to hit ok twice
        if(requestCode == CAMERA_REQ_CODE){
            if (resultCode==RESULT_OK) {
                // Load the image from the file
                Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
                int originalWidth = originalBitmap.getWidth();
                int originalHeight = originalBitmap.getHeight();

                // Calculate the new width and height by halving the original dimensions
                int newWidth = originalWidth / 2;
                int newHeight = originalHeight / 2;

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                getTextFromImage(resizedBitmap);
            } else {
                // If resultCode is not OK, handle the failure or inform the user accordingly
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                finish(); // Finish the activity or take appropriate action
            }
        } else {
            Toast.makeText(this, "Something wrong: requestCode=="+requestCode, Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    private void sendResultString() {
        // Start ResultActivity and pass resultString as an extra
        Intent intent = new Intent(TakePhotoActivity.this, ResultActivity.class);
        intent.putExtra("resultString", resultString);
        startActivity(intent);
        finish(); // Finish the current activity to prevent it from staying in the back stack
    }


    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                        .addOnSuccessListener(visionText -> {
                            // Task completed successfully
                            resultString = visionText.getText();
                            Log.d(TAG, "getTextFromImage: " + resultString);
                            sendResultString();
                        })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                    Log.e(TAG, "getTextFromImage: Text recognition failed", e);
                                });
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile;
        try {
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return imageFile;
    }

}