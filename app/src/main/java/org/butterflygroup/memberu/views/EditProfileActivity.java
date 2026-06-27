package org.butterflygroup.memberu.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // <-- Pastikan ini di-import
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.butterflygroup.memberu.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etNama, etPhone;
    private Button btnSimpan;
    private ImageView btnBack; // <-- Sudah diubah menjadi ImageView
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnBack = findViewById(R.id.btnBackEdit);
        etNama = findViewById(R.id.etEditNama);
        etPhone = findViewById(R.id.etEditPhone);
        btnSimpan = findViewById(R.id.btnSimpanProfil);

        // Inisialisasi SharedPreferences
        sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

        // Ambil data yang tersimpan saat ini untuk ditampilkan di kolom input form
        String currentUsername = sharedPref.getString("username", "AAAAAAA");
        String currentPhone = sharedPref.getString("phone", "+62 888-8888-8888");

        etNama.setText(currentUsername);
        etPhone.setText(currentPhone);

        btnBack.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> {
            String namaBaru = etNama.getText().toString().trim();
            String phoneBaru = etPhone.getText().toString().trim();

            if (namaBaru.isEmpty() || phoneBaru.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // PROSES SIMPAN PERUBAHAN SECARA LOKAL
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", namaBaru);
            editor.putString("phone", phoneBaru);
            editor.apply(); // Menerapkan penyimpanan data secara background asynchronous

            Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            finish(); // Tutup halaman dan kembali ke ProfileActivity
        });
    }
}