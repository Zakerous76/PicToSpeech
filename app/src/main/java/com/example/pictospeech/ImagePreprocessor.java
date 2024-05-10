package com.example.pictospeech;
import android.graphics.Bitmap;
import android.graphics.Color;

public class ImagePreprocessor {

    public static Bitmap preprocessImage(Bitmap originalImage) {
        // Apply image sharpening
        Bitmap sharpenedImage = applySharpening(originalImage);

        // Apply contrast adjustment
        return applyContrastAdjustment(sharpenedImage);
    }

    private static Bitmap applySharpening(Bitmap image) {
        // Create a new bitmap with the same dimensions as the original image
        Bitmap sharpenedBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

        // Define the sharpening kernel
        float[] sharpeningKernel = {
                -1, -1, -1,
                -1, 9, -1,
                -1, -1, -1
        };

        // Apply the sharpening kernel to each pixel in the image
        for (int i = 1; i < image.getWidth() - 1; i++) {
            for (int j = 1; j < image.getHeight() - 1; j++) {
                int pixel = applyKernel(image, i, j, sharpeningKernel);
                sharpenedBitmap.setPixel(i, j, pixel);
            }
        }

        return sharpenedBitmap;
    }

    private static Bitmap applyContrastAdjustment(Bitmap image) {
        // Create a new bitmap with the same dimensions as the original image
        Bitmap adjustedBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

        // Define the contrast adjustment factor (increase or decrease as needed)
        float contrastFactor = 1.5f;

        // Apply contrast adjustment to each pixel in the image
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int pixel = image.getPixel(i, j);

                // Extract the RGB components of the pixel
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Adjust the RGB components using the contrast factor
                red = (int) (contrastFactor * (red - 128) + 128);
                green = (int) (contrastFactor * (green - 128) + 128);
                blue = (int) (contrastFactor * (blue - 128) + 128);

                // Clamp the RGB values to the valid range [0, 255]
                red = Math.max(0, Math.min(255, red));
                green = Math.max(0, Math.min(255, green));
                blue = Math.max(0, Math.min(255, blue));

                // Combine the adjusted RGB components into a single pixel
                int adjustedPixel = Color.argb(alpha, red, green, blue);

                // Set the adjusted pixel in the new bitmap
                adjustedBitmap.setPixel(i, j, adjustedPixel);
            }
        }

        return adjustedBitmap;
    }

    private static int applyKernel(Bitmap image, int x, int y, float[] kernel) {
        float sumR = 0, sumG = 0, sumB = 0;

        // Apply the kernel to the neighborhood of the pixel
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // Get the RGB components of the pixel in the neighborhood
                int pixel = image.getPixel(x + i, y + j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Apply the kernel weights to the RGB components
                sumR += kernel[(i + 1) * 3 + (j + 1)] * red;
                sumG += kernel[(i + 1) * 3 + (j + 1)] * green;
                sumB += kernel[(i + 1) * 3 + (j + 1)] * blue;
            }
        }

        // Combine the weighted sums and return the result as a single pixel
        int alpha = Color.alpha(image.getPixel(x, y));
        int red = Math.min(255, Math.max(0, (int) sumR));
        int green = Math.min(255, Math.max(0, (int) sumG));
        int blue = Math.min(255, Math.max(0, (int) sumB));

        return Color.argb(alpha, red, green, blue);
    }
}