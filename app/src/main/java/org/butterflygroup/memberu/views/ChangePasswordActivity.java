package org.butterflygroup.memberu.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // <-- Import baru
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.butterflygroup.memberu.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etLama, etBaru, etKonfirmasi;
    private Button btnSimpan;
    private ImageView btnBack; // <-- Sudah diubah menjadi ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnBack = findViewById(R.id.btnBackPassword);
        etLama = findViewById(R.id.etPasswordLama);
        etBaru = findViewById(R.id.etPasswordBaru);
        etKonfirmasi = findViewById(R.id.etPasswordKonfirmasi);
        btnSimpan = findViewById(R.id.btnSimpanPassword);

        btnBack.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> {
            String lama = etLama.getText().toString();
            String baru = etBaru.getText().toString();
            String konfirmasi = etKonfirmasi.getText().toString();

            if (lama.isEmpty() || baru.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!baru.equals(konfirmasi)) {
                Toast.makeText(this, "Konfirmasi password baru tidak cocok!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (baru.length() < 6) {
                Toast.makeText(this, "Password baru minimal 6 karakter!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Logika update password asli ditaruh di sini nanti
            Toast.makeText(this, "Password berhasil diubah!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}