package org.butterflygroup.memberu.views;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.butterflygroup.memberu.R;

public class SettingActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    // Kunci untuk SharedPreferences (Penyimpanan Lokal)
    private static final String PREFS_NAME = "MemberUPrefs";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";
    private static final String KEY_NOTIF = "notification_enabled";

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

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        setupWindowInsets();
        initBottomNavigation();

        // Memanggil fungsi untuk mengaktifkan aksi menu
        setupMenuActions();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, 0);
            return insets;
        });
    }

    private void setupMenuActions() {
        // 1. Logika Notifikasi (Switch)
        SwitchCompat switchNotif = findViewById(R.id.switch_notif);
        boolean isNotifEnabled = sharedPreferences.getBoolean(KEY_NOTIF, true); // Default ON
        switchNotif.setChecked(isNotifEnabled);

        switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_NOTIF, isChecked).apply();
            String status = isChecked ? "diaktifkan" : "dimatikan";
            Toast.makeText(this, "Notifikasi " + status, Toast.LENGTH_SHORT).show();
        });

        // 2. Logika Mode Gelap (Switch)
        SwitchCompat switchDarkMode = findViewById(R.id.switch_dark_mode);
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false); // Default OFF
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simpan preferensi
            sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();

            // Ubah tema aplikasi secara instan
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // 3. Logika Rate App
        LinearLayout menuRate = findViewById(R.id.menu_rate);
        menuRate.setOnClickListener(v -> {
            try {
                // Buka aplikasi Play Store
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                // Jika tidak ada Play Store, buka via Browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });

        // 4. Logika Share App
        LinearLayout menuShare = findViewById(R.id.menu_share);
        menuShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Aplikasi MemberU");
            String shareMessage = "Halo! Kelola semua kartu membermu (Gym, Laundry, dll) secara offline dengan mudah menggunakan MemberU. Yuk coba sekarang!";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Bagikan via"));
        });

        // 5. Logika Contact (Email)
        LinearLayout menuContact = findViewById(R.id.menu_contact);
        menuContact.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:")); // Hanya aplikasi email yang merespons
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@memberu.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bantuan - MemberU App");

            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Tidak ada aplikasi Email yang terinstall", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout menuPrivacy = findViewById(R.id.menu_privacy);
        menuPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, PrivacyActivity.class);
            startActivity(intent);
        });

        LinearLayout menuTerms = findViewById(R.id.menu_terms);
        menuTerms.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, TermsActivity.class);
            startActivity(intent);
        });

        LinearLayout menuFeedback = findViewById(R.id.menu_feedback);
        menuFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });
    }

    private void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);

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