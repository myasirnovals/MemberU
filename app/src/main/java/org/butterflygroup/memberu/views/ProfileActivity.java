package org.butterflygroup.memberu.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private TextView       btnBack, btnEdit, tvLihatSemua, tvUsername, tvPhone;
    private RelativeLayout itemGantiPassword, btnLogout;
    private Switch         switchBiometrik, switchVerif;

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.profileRoot), (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        initViews();
        setupListeners();
    }

    // PENTING: Memuat ulang data setiap kali halaman profil aktif kembali
    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
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

        // Tambahkan inisialisasi komponen teks profil Anda
        tvUsername        = findViewById(R.id.tvUsername);
        tvPhone           = findViewById(R.id.tvPhone);
    }

    // Fungsi membaca data tersimpan
    private void loadProfileData() {
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "AAAAAAA"); // "AAAAAAA" adalah nilai default awal
        String phone = sharedPref.getString("phone", "+62 888-8888-8888");

        if (tvUsername != null) tvUsername.setText(username);
        if (tvPhone != null) tvPhone.setText(phone);
    }

    // ── Setup click listener ──────────────────────────────────────────────────
    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        tvLihatSemua.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        itemGantiPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        switchBiometrik.setOnCheckedChangeListener((btn, checked) ->
                Toast.makeText(this,
                        checked ? "PIN & Biometrik diaktifkan" : "PIN & Biometrik dinonaktifkan",
                        Toast.LENGTH_SHORT).show());

        switchVerif.setOnCheckedChangeListener((btn, checked) ->
                Toast.makeText(this,
                        checked ? "Verifikasi 2 Langkah aktif" : "Verifikasi 2 Langkah nonaktif",
                        Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar dari Akun")
                .setMessage("Apakah kamu yakin ingin keluar dari akun ini?")
                .setPositiveButton("Ya, Keluar", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}