package org.butterflygroup.memberu.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.butterflygroup.memberu.R;

public class PromoActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvPromoCode;
    private Button btnCopyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        // Inisialisasi komponen view
        btnBack = findViewById(R.id.btn_back_promo);
        tvPromoCode = findViewById(R.id.tv_promo_code);
        btnCopyCode = findViewById(R.id.btn_copy_code);

        // Fungsi klik tombol kembali ke Dashboard Utama
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Menutup halaman promo saat ini
            }
        });

        // Fungsi klik menyalin kode voucher ke papan klip perangkat
        btnCopyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeToCopy = tvPromoCode.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Voucher MemberU", codeToCopy);

                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(PromoActivity.this, "Kode promo '" + codeToCopy + "' berhasil disalin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}