package com.example.pictospeech;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.View;
        import android.view.textservice.SpellCheckerInfo;
        import android.widget.Button;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.mlkit.vision.common.InputImage;
        import com.google.mlkit.vision.text.Text;
        import com.google.mlkit.vision.text.TextRecognition;
        import com.google.mlkit.vision.text.TextRecognizer;
        import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

        import java.io.FileNotFoundException;
        import java.io.InputStream;

public class UploadFromGalleryActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    String resultString = "";
    private final int CAMERA_REQ_CODE = 100;
    Button cancelBtn;
    String TAG = "UploadFromGalleryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_activity);

        cancelBtn = findViewById(R.id.cancel_btn);
        // As soon as it enters, it should give user the ability to upload photos
        openGallery();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(UploadFromGalleryActivity.this, ScanPhotoActivity.class);
                startActivity(goBack);
            }
        });


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void sendResultString() {
        // Start ResultActivity and pass resultString as an extra
        Intent intent = new Intent(UploadFromGalleryActivity.this, ResultActivity.class);
        intent.putExtra("resultString", resultString);
        startActivity(intent);
        finish(); // Finish the current activity to prevent it from staying in the back stack
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

                int originalWidth = originalBitmap.getWidth();
                int originalHeight = originalBitmap.getHeight();

                // Calculate the new width and height by halving the original dimensions
                int newWidth = originalWidth / 2;
                int newHeight = originalHeight / 2;

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                getTextFromImage(resizedBitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text visionText) {
                    // Task completed successfully
                    for (Text.TextBlock block : visionText.getTextBlocks()) {
                        String blockText = cleanText(block.getText());
                        resultString += blockText + ".\n\n";
                    }
                    sendResultString();
                    Log.d(TAG, "getTextFromImage: " + resultString);
                }
                })
                .addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            Log.e(TAG, "getTextFromImage: Text recognition failed", e);
                        }
                    }
                );
    }
    // TODO: implement this if needed
    public String cleanText(String text){
        // Replace multiple spaces with a single space
        text = text.replaceAll("\\s+", " ");
        text = text.trim();
//        return toSentenceCase(text);
        return toWordSentenceCase(text);
    }
    public String toSentenceCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Convert the entire string to lowercase first
        String lowerCased = input.toLowerCase();
        // Capitalize the first letter
        return lowerCased.substring(0, 1).toUpperCase() + lowerCased.substring(1);
    }

    public String toWordSentenceCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();

    }

}