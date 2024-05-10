package com.example.pictospeech;

        import android.content.Intent;
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
        btnCamera = findViewById(R.id.take_photo_btn);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

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
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(originalBitmap);

                int originalWidth = originalBitmap.getWidth();
                int originalHeight = originalBitmap.getHeight();
                Toast.makeText(this, "" + originalWidth + " " + originalHeight, Toast.LENGTH_SHORT).show();

                // Calculate the new width and height by halving the original dimensions
                int newWidth = originalWidth / 2;
                int newHeight = originalHeight / 2;

                Toast.makeText(this, "new: " + newWidth + "x" + newHeight, Toast.LENGTH_SHORT).show();

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                getTextFromImage(resizedBitmap, resultTextView);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
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