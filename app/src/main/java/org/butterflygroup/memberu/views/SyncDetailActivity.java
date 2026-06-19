package org.butterflygroup.memberu.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.butterflygroup.memberu.R;

public class SyncDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MEMBER_CARD_ID = "member_card_id";

    private TextView tvLoadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_detail);

        tvLoadingText = findViewById(R.id.tvSyncLoadingText);

        int memberCardId = getIntent().getIntExtra(EXTRA_MEMBER_CARD_ID, -1);

        // Pastikan teks sesuai (jika dibutuhkan untuk lokal).
        tvLoadingText.setText(R.string.sync_detail_loading_text);

        // Simulasi proses sync: karena saat ini loading hanya mengambil dari DB lokal,
        // kita tampilkan splash sebentar agar terasa seperti loading.
        // Jika nanti ada proses network/QR generation, ganti dengan coroutine/AsyncTask.
        tvLoadingText.postDelayed(() -> {
            Intent intent = new Intent(SyncDetailActivity.this, DetailCardActivity.class);
            intent.putExtra(DetailCardActivity.EXTRA_MEMBER_CARD_ID, memberCardId);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.anim_click, R.anim.anim_click);
        }, 900);
    }
}

