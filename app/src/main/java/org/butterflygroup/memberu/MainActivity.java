package org.butterflygroup.memberu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.butterflygroup.memberu.views.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ════════════ CEK MODE GELAP ════════════
        // Menerapkan tema global untuk aplikasi berdasarkan preferensi user
        SharedPreferences prefs = getSharedPreferences("MemberUPrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // ── MODIFIKASI: Paksa KHUSUS Splash Screen untuk selalu berada di Mode Terang ──
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ════════════ LOGIKA TIMER SPLASH SCREEN ════════════
        // Menunda eksekusi selama 2500 milidetik (2.5 detik)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Berpindah ke HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);

            // Hancurkan MainActivity agar ketika user menekan tombol 'Back' di HomeActivity,
            // aplikasi langsung tertutup dan tidak kembali ke layar Splash Screen lagi.
            finish();
        }, 2500);
    }
}