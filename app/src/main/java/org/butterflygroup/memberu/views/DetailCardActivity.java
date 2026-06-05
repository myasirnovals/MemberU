package org.butterflygroup.memberu.views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;

import org.butterflygroup.memberu.R;
import org.butterflygroup.memberu.models.MemberCard;
import org.butterflygroup.memberu.utils.DatabaseHelper;

import java.io.ByteArrayOutputStream;

public class DetailCardActivity extends AppCompatActivity {

    public static final String EXTRA_MEMBER_CARD_ID = "member_card_id";

    private DatabaseHelper dbHelper;
    private Cursor currentCursor;

    private TextView tvMemberName;
    private TextView tvMemberTier;
    private TextView tvMemberNumber;
    private TextView tvMemberExpiry;
    private TextView tvMemberNotes;

    private ImageView ivQrCode;
    private ImageView ivBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_card);

        // Views
        MaterialToolbar toolbar = findViewById(R.id.toolbarDetailCard);
        tvMemberName = findViewById(R.id.tvMemberName);
        tvMemberTier = findViewById(R.id.tvMemberTier);
        tvMemberNumber = findViewById(R.id.tvMemberNumber);
        tvMemberExpiry = findViewById(R.id.tvMemberExpiry);
        tvMemberNotes = findViewById(R.id.tvMemberNotes);

        ivQrCode = findViewById(R.id.ivQrCode);
        ivBarcode = findViewById(R.id.ivBarcode);

        toolbar.setNavigationOnClickListener(v -> {
            // kembali ke HomeActivity dan pastikan detail masuk onDestroy
            finish();
            overridePendingTransition(R.anim.anim_click, R.anim.anim_click);
        });

        dbHelper = new DatabaseHelper(this);

        int memberCardId = getIntent().getIntExtra(EXTRA_MEMBER_CARD_ID, -1);
        if (memberCardId == -1) {
            // bila tidak ada id, kembali
            finish();
            return;
        }

        loadDetailFromDatabase(memberCardId);

        // ganti toolbar title agar lebih sesuai
        if (tvMemberName.getText() != null && !tvMemberName.getText().toString().trim().isEmpty()) {
            toolbar.setTitle(tvMemberName.getText().toString().trim());
        }
    }

    private void loadDetailFromDatabase(int memberCardId) {
        currentCursor = dbHelper.getMemberCardById(memberCardId);

        if (currentCursor == null) return;
        if (!currentCursor.moveToFirst()) {
            safeCloseCursor();
            return;
        }

        int id = currentCursor.getInt(currentCursor.getColumnIndexOrThrow("id"));
        int userId = currentCursor.getInt(currentCursor.getColumnIndexOrThrow("user_id"));
        int categoryId = currentCursor.getInt(currentCursor.getColumnIndexOrThrow("category_id"));
        String categoryName = currentCursor.getString(currentCursor.getColumnIndexOrThrow("category_name"));
        String merchantName = currentCursor.getString(currentCursor.getColumnIndexOrThrow("merchant_name"));
        String memberNumber = currentCursor.getString(currentCursor.getColumnIndexOrThrow("member_number"));
        String tier = currentCursor.getString(currentCursor.getColumnIndexOrThrow("tier"));

        // NOTE: saat ini data expiry/notes di database tidak ada.
        // Jadi kita gunakan placeholder.
        String expiry = "-";
        String notes = "-";

        MemberCard card = new MemberCard(
                id, userId, categoryId,
                categoryName,
                merchantName,
                memberNumber,
                tier,
                ""
        );

        tvMemberName.setText(card.getMerchantName());
        tvMemberTier.setText(card.getTier());
        tvMemberNumber.setText("ID: " + card.getMemberNumber());
        tvMemberExpiry.setText("Exp: " + expiry);
        tvMemberNotes.setText(notes);

        // belum ada generate QR/barcode. Biarkan placeholder seperti di XML.
        // Jika nanti ada generate QR, lakukan di sini.
    }

    private void safeCloseCursor() {
        if (currentCursor != null) {
            try {
                currentCursor.close();
            } catch (Exception ignored) {
            }
            currentCursor = null;
        }
    }

    @Override
    protected void onDestroy() {
        safeCloseCursor();
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
        super.onDestroy();
    }
}

