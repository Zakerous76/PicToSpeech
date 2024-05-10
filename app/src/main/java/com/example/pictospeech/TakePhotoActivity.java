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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.components.BuildConfig;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
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
    private final int DESIRED_WIDTH = 1280;
    private final int DESIRED_HEIGHT = 960;
    private String imagePath;

    ImageView imgCamera;
    Button btnCamera;
    String TAG = "TakePhotoActivity";
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_a_photo_activity);

        imgCamera = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.take_photo_btn);
        resultTextView = findViewById(R.id.resultEditText);

        // request permission for the camera
        Log.d(TAG, "onCreate: " + ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TakePhotoActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_REQ_CODE);
        } else {
            btnCamera.setEnabled(true);
            btnCamera.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCamera();
                }
            }));
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
        if(resultCode==RESULT_OK){
            if (requestCode == CAMERA_REQ_CODE) {
                // Load the image from the file
                Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
                Bitmap scaledBitmapImg = Bitmap.createScaledBitmap(originalBitmap, DESIRED_WIDTH, DESIRED_HEIGHT, true);

                // Display the image
                imgCamera.setImageBitmap(scaledBitmapImg);
                Toast.makeText(this, " " + scaledBitmapImg.getHeight() + " " + scaledBitmapImg.getWidth(), Toast.LENGTH_SHORT).show();

                // Preprocess the image: Decreases Accuracy

                // Perform text recognition on the preprocessed image
                getTextFromImage(scaledBitmapImg, resultTextView);
            }
        }
    }

    private void getTextFromImage(Bitmap bitmap, TextView txtView) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                String resultText = visionText.getText();
                                Log.d(TAG, "getTextFromImage: " + resultText);
                                // Update the TextView with the recognized text
                                txtView.setText(resultText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Log.e(TAG, "getTextFromImage: Text recognition failed", e);
                                    }
                                });
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
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