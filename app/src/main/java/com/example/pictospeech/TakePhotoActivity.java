package com.example.pictospeech;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class TakePhotoActivity extends AppCompatActivity {
    private final int CAMERA_REQ_CODE = 100;
    ImageView imgCamera, preprocessedImg;
    Button btnCamera;
    String TAG = "TakePhotoActivity";
    String resultText;
    TextView resultTextView, resultTextView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_a_photo);

        imgCamera = findViewById(R.id.imageView);
        preprocessedImg = findViewById(R.id.imageView2);
        btnCamera = findViewById(R.id.take_photo_btn);
        resultTextView = findViewById(R.id.resultEditText);
        resultTextView2 = findViewById(R.id.resultEditText2);

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
        startActivityForResult(iCamera, CAMERA_REQ_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: Find a way to tell the user to hit ok twice
        if(resultCode==RESULT_OK){
            if (requestCode==CAMERA_REQ_CODE){
                // for camera
                Bitmap img = (Bitmap) (data.getExtras().get("data"));
                imgCamera.setImageBitmap(img);

                // Preprocess the image
                // TODO: Find a way to have the recognizer ingest higher resolution image
                Bitmap preprocessedImage = ImagePreprocessor.preprocessImage(img);
                preprocessedImg.setImageBitmap(preprocessedImage);

                // Perform text recognition on the preprocessed image
                getTextFromImage(img, resultTextView);
                getTextFromImage(preprocessedImage, resultTextView2);
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

}