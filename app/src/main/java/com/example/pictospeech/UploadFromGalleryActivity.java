package com.example.pictospeech;

        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
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

        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.mlkit.vision.common.InputImage;
        import com.google.mlkit.vision.text.Text;
        import com.google.mlkit.vision.text.TextRecognition;
        import com.google.mlkit.vision.text.TextRecognizer;
        import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

        import java.io.FileNotFoundException;
        import java.io.InputStream;

public class UploadFromGalleryActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;


    private final int CAMERA_REQ_CODE = 100;
    ImageView imgCamera, preprocessedImg;
    Button btnCamera;
    String TAG = "UploadFromGalleryActivity";
    String resultText;
    TextView resultTextView, resultTextView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(v -> openGallery());

        preprocessedImg = findViewById(R.id.imageView2);
        resultTextView = findViewById(R.id.resultEditText);
        resultTextView2 = findViewById(R.id.resultEditText2);


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Load the selected image into an ImageView
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

                // Perform further processing or upload the image
                // For example, you can compress the image before uploading
                // Or directly upload it to a server or cloud storage
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }


//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // TODO: Find a way to tell the user to hit ok twice
//        if(resultCode==RESULT_OK){
//            if (requestCode==CAMERA_REQ_CODE){
//                // for camera
//                Bitmap img = (Bitmap) (data.getExtras().get("data"));
//                imgCamera.setImageBitmap(img);
//
//                // Preprocess the image
//                // TODO: Find a way to have the recognizer ingest higher resolution image
//                Bitmap preprocessedImage = ImagePreprocessor.preprocessImage(img);
//                preprocessedImg.setImageBitmap(preprocessedImage);
//
//                // Perform text recognition on the preprocessed image
//                getTextFromImage(img, resultTextView);
//                getTextFromImage(preprocessedImage, resultTextView2);
//            }
//        }
//    }

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