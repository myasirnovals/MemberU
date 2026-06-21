package org.butterflygroup.memberu.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.butterflygroup.memberu.R;

public class SettingActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Berhasil! QR: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        setupWindowInsets();
        initBottomNavigation();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, 0);
            return insets;
        });
    }

    private void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_settings);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_cards) {
                    finish(); // Tutup halaman pengaturan dan kembali ke Home
                    overridePendingTransition(0, 0); // Transisi tanpa animasi
                    return false; // Cegah tombol cards berganti warna di halaman ini
                } else if (id == R.id.nav_settings) {
                    return true;
                }
                return false;
            });
        }

        View btnQris = findViewById(R.id.btn_qris);
        if (btnQris != null) {
            btnQris.setOnClickListener(v -> {
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_bounce);
                v.startAnimation(anim);

                v.postDelayed(this::bukaKameraScanner, 150);
            });
        }
    }

    private void bukaKameraScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CustomScannerActivity.class);
        barcodeLauncher.launch(options);
    }
}