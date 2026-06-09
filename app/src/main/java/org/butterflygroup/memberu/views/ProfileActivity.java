package org.butterflygroup.memberu.views;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.butterflygroup.memberu.R;

public class ProfileActivity extends AppCompatActivity {

    // ── View references ──────────────────────────────────────────────────────
    private TextView       btnBack, btnEdit, tvLihatSemua;
    private RelativeLayout itemGantiPassword, btnLogout;
    private Switch         switchBiometrik, switchVerif;

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Edge-to-edge padding (sama seperti MainActivity)
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.profileRoot), (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        initViews();
        setupListeners();
    }

    // ── Inisialisasi view ─────────────────────────────────────────────────────
    private void initViews() {
        btnBack           = findViewById(R.id.btnBack);
        btnEdit           = findViewById(R.id.btnEdit);
        tvLihatSemua      = findViewById(R.id.tvLihatSemua);
        itemGantiPassword = findViewById(R.id.itemGantiPassword);
        btnLogout         = findViewById(R.id.btnLogout);
        switchBiometrik   = findViewById(R.id.switchBiometrik);
        switchVerif       = findViewById(R.id.switchVerif);
    }

    // ── Setup click listener ──────────────────────────────────────────────────
    private void setupListeners() {

        // Tombol kembali → tutup Activity ini
        btnBack.setOnClickListener(v -> finish());

        // Tombol edit profil
        btnEdit.setOnClickListener(v ->
                Toast.makeText(this, "Fitur edit profil segera hadir", Toast.LENGTH_SHORT).show());

        // Link "Lihat Semua" member
        tvLihatSemua.setOnClickListener(v ->
                Toast.makeText(this, "Menampilkan semua member", Toast.LENGTH_SHORT).show());

        // Item Ganti Password
        itemGantiPassword.setOnClickListener(v ->
                Toast.makeText(this, "Halaman ganti password", Toast.LENGTH_SHORT).show());

        // Toggle Biometrik
        switchBiometrik.setOnCheckedChangeListener((btn, checked) ->
                Toast.makeText(this,
                        checked ? "PIN & Biometrik diaktifkan" : "PIN & Biometrik dinonaktifkan",
                        Toast.LENGTH_SHORT).show());

        // Toggle Verifikasi 2 Langkah
        switchVerif.setOnCheckedChangeListener((btn, checked) ->
                Toast.makeText(this,
                        checked ? "Verifikasi 2 Langkah aktif" : "Verifikasi 2 Langkah nonaktif",
                        Toast.LENGTH_SHORT).show());

        // Tombol Logout → tampilkan dialog konfirmasi
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    // ── Dialog Konfirmasi Logout ──────────────────────────────────────────────
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar dari Akun")
                .setMessage("Apakah kamu yakin ingin keluar dari akun ini?")
                .setPositiveButton("Ya, Keluar", (dialog, which) -> {
                    // TODO: bersihkan session / SharedPreferences di sini
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}