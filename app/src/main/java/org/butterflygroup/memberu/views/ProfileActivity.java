package org.butterflygroup.memberu.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.butterflygroup.memberu.R;

import java.io.File;
import java.io.FileOutputStream;

public class ProfileActivity extends AppCompatActivity {

    // ── View references ──────────────────────────────────────────────────────
    private ImageView      btnBack, ivProfileFoto;
    private TextView       btnEdit, tvUsername, tvPhone, tvInisialFoto;
    private RelativeLayout itemGantiPassword, btnLogout;
    private Switch         switchBiometrik, switchVerif;
    private CardView       btnUbahFoto;

    // ── 1. Launcher Buka Galeri ──────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            // Kunci izin akses gambar agar tidak hilang saat HP restart
                            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            saveImageUriToPrefs(imageUri.toString());
                            setProfileImage(imageUri);
                        } catch (Exception e) {
                            Toast.makeText(this, "Gagal memuat gambar dari galeri", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    // ── 2. Launcher Buka Kamera ──────────────────────────────────────────────
    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    // Simpan hasil jepretan kamera ke memori internal aplikasi
                    Uri savedUri = saveBitmapToInternalStorage(bitmap);
                    if (savedUri != null) {
                        saveImageUriToPrefs(savedUri.toString());
                        setProfileImage(savedUri);
                    } else {
                        Toast.makeText(this, "Gagal menyimpan foto dari kamera", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

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

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    // ── Inisialisasi view ─────────────────────────────────────────────────────
    private void initViews() {
        btnBack           = findViewById(R.id.btnBack);
        btnEdit           = findViewById(R.id.btnEdit);
        itemGantiPassword = findViewById(R.id.itemGantiPassword);
        btnLogout         = findViewById(R.id.btnLogout);
        switchBiometrik   = findViewById(R.id.switchBiometrik);
        switchVerif       = findViewById(R.id.switchVerif);
        tvUsername        = findViewById(R.id.tvUsername);
        tvPhone           = findViewById(R.id.tvPhone);

        btnUbahFoto       = findViewById(R.id.btnUbahFoto);
        tvInisialFoto     = findViewById(R.id.tvInisialFoto);
        ivProfileFoto     = findViewById(R.id.ivProfileFoto);
    }

    // Fungsi membaca data tersimpan
    private void loadProfileData() {
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        tvUsername.setText(sharedPref.getString("username", "AAAAAAA"));
        tvPhone.setText(sharedPref.getString("phone", "+62 888-8888-8888"));

        // Memuat Foto Profil jika sudah ada
        String savedImageUri = sharedPref.getString("profile_image_uri", null);
        if (savedImageUri != null) {
            setProfileImage(Uri.parse(savedImageUri));
        }
    }

    // ── Fungsi-fungsi Bantuan (Utilities) ─────────────────────────────────────

    // Menampilkan gambar ke UI
    private void setProfileImage(Uri imageUri) {
        if (ivProfileFoto != null && tvInisialFoto != null) {
            ivProfileFoto.setImageURI(imageUri);
            ivProfileFoto.setVisibility(View.VISIBLE);
            tvInisialFoto.setVisibility(View.GONE);
        }
    }

    // Menyimpan alamat lokasi foto ke SharedPreferences
    private void saveImageUriToPrefs(String uriString) {
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        sharedPref.edit().putString("profile_image_uri", uriString).apply();
    }

    // Menyimpan hasil foto kamera ke penyimpanan internal agar tidak hilang
    private Uri saveBitmapToInternalStorage(Bitmap bitmap) {
        try {
            File file = new File(getFilesDir(), "profile_camera_image.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return Uri.fromFile(file); // Kembalikan Uri lokal
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ── Setup click listener ──────────────────────────────────────────────────
    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        itemGantiPassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));

        switchBiometrik.setOnCheckedChangeListener((btn, checked) ->
                Toast.makeText(this, checked ? "PIN & Biometrik aktif" : "PIN & Biometrik nonaktif", Toast.LENGTH_SHORT).show());

        switchVerif.setOnCheckedChangeListener((btn, checked) ->
                Toast.makeText(this, checked ? "Verif 2 Langkah aktif" : "Verif 2 Langkah nonaktif", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // ── Aksi Klik Foto Profil -> Munculkan Pop-up ──
        btnUbahFoto.setOnClickListener(v -> showImageSourceDialog());
    }

    // ── Dialog Pilihan Galeri / Kamera ────────────────────────────────────────
    private void showImageSourceDialog() {
        String[] options = {"Pilih dari Galeri", "Ambil Foto (Kamera)"};

        new AlertDialog.Builder(this)
                .setTitle("Ubah Foto Profil")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Opsi 1: Buka Galeri
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        galleryLauncher.launch(intent);
                    } else if (which == 1) {
                        // Opsi 2: Buka Kamera dengan pengaman (try-catch)
                        try {
                            cameraLauncher.launch(null);
                        } catch (Exception e) {
                            Toast.makeText(ProfileActivity.this, "Gagal membuka kamera! Pastikan aplikasi memiliki akses kamera.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar dari Akun")
                .setMessage("Apakah kamu yakin ingin keluar dari akun ini?")
                .setPositiveButton("Ya, Keluar", (dialog, which) -> finish())
                .setNegativeButton("Batal", null)
                .show();
    }
}