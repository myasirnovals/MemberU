package org.butterflygroup.memberu.views;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.butterflygroup.memberu.R;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);

        // Menyesuaikan padding layout agar tidak terpotong status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tombol Kembali (Back Arrow)
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Inisialisasi Elemen Input Form
        EditText etFeedback = findViewById(R.id.et_feedback);
        Button btnSubmit = findViewById(R.id.btn_submit_feedback);

        if (btnSubmit != null && etFeedback != null) {
            btnSubmit.setOnClickListener(v -> {
                String feedbackText = etFeedback.getText().toString().trim();

                // Validasi agar pengguna tidak mengirim teks kosong
                if (feedbackText.isEmpty()) {
                    Toast.makeText(this, "Mohon isi masukan Anda terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    // Membuat Intent untuk mengirim konten feedback via email client perangkat
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:")); // Hanya memicu aplikasi email resmi
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@memberu.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Pengguna - MemberU App");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackText);

                    try {
                        startActivity(emailIntent);
                        Toast.makeText(this, "Membuka aplikasi Email untuk mengirim...", Toast.LENGTH_SHORT).show();
                        finish(); // Menutup form feedback setelah mengarahkan ke email
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "Tidak ada aplikasi Email yang terinstall", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}