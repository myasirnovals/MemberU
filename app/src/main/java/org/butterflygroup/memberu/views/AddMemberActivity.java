package org.butterflygroup.memberu.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.butterflygroup.memberu.R;
import android.widget.ImageView;

public class AddMemberActivity extends AppCompatActivity {

    private EditText etMerchantName, etMemberNumber, etCategory;
    private Button btnSaveMember;
    private org.butterflygroup.memberu.utils.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        etMerchantName = findViewById(R.id.et_merchant_name);
        etMemberNumber = findViewById(R.id.et_member_number);
        etCategory = findViewById(R.id.et_category);
        btnSaveMember = findViewById(R.id.btn_save_member);

        dbHelper = new org.butterflygroup.memberu.utils.DatabaseHelper(this);

        btnSaveMember.setOnClickListener(v -> {
            simpanDataKeDatabase();
        });

        ImageView btnBackCustom = findViewById(R.id.btn_back_custom);
        if (btnBackCustom != null) {
            btnBackCustom.setOnClickListener(v -> {
                finish();
            });
        }
    }

    private void simpanDataKeDatabase() {
        String merchantName = etMerchantName.getText().toString().trim();
        String memberNumber = etMemberNumber.getText().toString().trim();
        String categoryInput = etCategory.getText().toString().trim().toUpperCase();

        if (merchantName.isEmpty() || memberNumber.isEmpty() || categoryInput.isEmpty()) {
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = 1;
        if (categoryInput.equals("BARBERSHOP")) {
            categoryId = 2;
        } else if (categoryInput.equals("LAUNDRY")) {
            categoryId = 3;
        }

        boolean isSuccess = dbHelper.insertMemberCard(
                1,
                categoryId,
                merchantName,
                memberNumber,
                "Silver"
        );

        if (isSuccess) {
            Toast.makeText(this, "Kartu Member Berhasil Disimpan! 🎉", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan ke database ❌", Toast.LENGTH_SHORT).show();
        }
    }
}