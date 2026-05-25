package org.butterflygroup.memberu.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.butterflygroup.memberu.R;
import org.butterflygroup.memberu.controllers.MainController;

public class HomeActivity extends AppCompatActivity implements MainView {
    private MainController controller;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Berhasil! Isi QR: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
    });

    private androidx.recyclerview.widget.RecyclerView rvMembers;
    private org.butterflygroup.memberu.adapters.MemberAdapter memberAdapter;
    private java.util.List<org.butterflygroup.memberu.models.MemberCard> memberList;
    private org.butterflygroup.memberu.utils.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        View headerContainer = findViewById(R.id.header_container);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);

            int padding16dp = (int) (16 * getResources().getDisplayMetrics().density);

            if (headerContainer != null) {
                headerContainer.setPadding(
                        padding16dp,
                        systemBars.top + padding16dp,
                        padding16dp,
                        padding16dp
                );
            }

            return insets;
        });

        controller = new MainController(this);

        View tombolQris = findViewById(R.id.btn_qris);
        if (tombolQris != null) {
            tombolQris.setOnClickListener(v -> bukaKameraScanner());
        }

        View tombolTambahMember = findViewById(R.id.btn_add_member);
        if (tombolTambahMember != null) {
            tombolTambahMember.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(HomeActivity.this, AddMemberActivity.class);
                startActivity(intent);
            });
        }

        rvMembers = findViewById(R.id.rv_members);
        rvMembers.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        dbHelper = new org.butterflygroup.memberu.utils.DatabaseHelper(this);
        memberList = new java.util.ArrayList<>();

        loadDataFromDatabase();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void bukaKameraScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CustomScannerActivity.class);

        barcodeLauncher.launch(options);
    }

    private void loadDataFromDatabase() {
        memberList.clear();

        android.database.Cursor cursor = dbHelper.getAllMemberCards();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                String merchantName = cursor.getString(cursor.getColumnIndexOrThrow("merchant_name"));
                String memberNumber = cursor.getString(cursor.getColumnIndexOrThrow("member_number"));
                String tier = cursor.getString(cursor.getColumnIndexOrThrow("tier"));

                memberList.add(new org.butterflygroup.memberu.models.MemberCard(
                        id, userId, categoryId, categoryName, merchantName, memberNumber, tier, ""
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }

        memberAdapter = new org.butterflygroup.memberu.adapters.MemberAdapter(memberList);
        rvMembers.setAdapter(memberAdapter);
    }
}