package org.butterflygroup.memberu.views;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.RGBLuminanceSource;

import org.butterflygroup.memberu.R;

import java.io.InputStream;

public class CustomScannerActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private boolean isFlashOn = false;
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    scanImageFromGallery(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        barcodeScannerView.getViewFinder().setVisibility(View.GONE);

        View garisBiru = findViewById(R.id.garis_biru_scanner);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -0.4f,
                Animation.RELATIVE_TO_PARENT, 0.4f);
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        garisBiru.startAnimation(animation);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        ImageView btnFlash = findViewById(R.id.btn_flash);
        btnFlash.setOnClickListener(v -> {
            if (isFlashOn) {
                barcodeScannerView.setTorchOff();
                isFlashOn = false;
                btnFlash.setColorFilter(android.graphics.Color.parseColor("#808080"));
                Toast.makeText(this, "Flash Dimatikan", Toast.LENGTH_SHORT).show();
            } else {
                barcodeScannerView.setTorchOn();
                isFlashOn = true;
                btnFlash.setColorFilter(android.graphics.Color.WHITE);
                Toast.makeText(this, "Flash Dinyalakan", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView btnInfo = findViewById(R.id.btn_info);
        btnInfo.setOnClickListener(v -> Toast.makeText(this, "Arahkan kotak ke kode QRIS", Toast.LENGTH_SHORT).show());

        ImageView btnGallery = findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    private void scanImageFromGallery(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                Result result = new MultiFormatReader().decode(binaryBitmap);

                Intent intent = new Intent();
                intent.putExtra("SCAN_RESULT", result.getText());
                setResult(RESULT_OK, intent);
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Tidak ada kode QR di gambar ini / Gagal membaca", Toast.LENGTH_SHORT).show();
        }
    }
}