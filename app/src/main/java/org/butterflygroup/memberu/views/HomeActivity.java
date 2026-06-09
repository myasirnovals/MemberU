package org.butterflygroup.memberu.views;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// FIX: Hapus import ChipGroup & Chip karena XML tidak pakai komponen itu
// import com.google.android.material.chip.Chip;       ← DIHAPUS
// import com.google.android.material.chip.ChipGroup;  ← DIHAPUS

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.butterflygroup.memberu.R;
import org.butterflygroup.memberu.adapters.MemberAdapter;
import org.butterflygroup.memberu.controllers.MainController;
import org.butterflygroup.memberu.models.MemberCard;
import org.butterflygroup.memberu.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements MainView {

    // ── Controllers & Helpers ─────────────────────────────────────────────────
    private MainController controller;
    private DatabaseHelper  dbHelper;

    // ── Data ──────────────────────────────────────────────────────────────────
    private List<MemberCard> memberList;
    private List<MemberCard> filteredList;

    // ── Views ─────────────────────────────────────────────────────────────────
    private RecyclerView  rvMembers;
    private MemberAdapter memberAdapter;
    private List<Button>  categoryButtons; // FIX: pakai List<Button>, bukan ChipGroup

    // ── State ─────────────────────────────────────────────────────────────────
    private String activeCategory = "Semua";
    private String searchQuery    = "";

    // ── Scanner ───────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "Berhasil! QR: " + result.getContents(),
                            Toast.LENGTH_LONG).show();
                }
            });

    // ══════════════════════════════════════════════════════════════════════════
    // LIFECYCLE
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupWindowInsets();

        controller   = new MainController(this);
        dbHelper     = new DatabaseHelper(this);
        memberList   = new ArrayList<>();
        filteredList = new ArrayList<>();

        initViews();
        setupClickListeners();
        setupSearch();
        setupCategoryFilter();

        loadDataFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHelper != null) loadDataFromDatabase();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SETUP
    // ══════════════════════════════════════════════════════════════════════════

    private void setupWindowInsets() {
        View headerContainer = findViewById(R.id.header_container);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            int p16 = (int) (16 * getResources().getDisplayMetrics().density);
            if (headerContainer != null) {
                headerContainer.setPadding(p16, systemBars.top + p16, p16, p16);
            }
            return insets;
        });
    }

    private void initViews() {
        rvMembers = findViewById(R.id.rv_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {

        // ── FIX ERROR 1: btn_profile sekarang ada di XML ──────────────────────
        View btnProfile = findViewById(R.id.btn_profile);
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class))
            );
        }

        // ── QRIS ─────────────────────────────────────────────────────────────
        View btnQris = findViewById(R.id.btn_qris);
        if (btnQris != null) {
            btnQris.setOnClickListener(v -> {
                Animation bounce = AnimationUtils.loadAnimation(this, R.anim.anim_bounce);
                v.startAnimation(bounce);
                v.postDelayed(this::bukaKameraScanner, 150);
            });
        }

        // ── Tambah Member ─────────────────────────────────────────────────────
        View btnTambah = findViewById(R.id.btn_add_member);
        if (btnTambah != null) {
            btnTambah.setOnClickListener(v -> {
                Animation click = AnimationUtils.loadAnimation(this, R.anim.anim_click);
                v.startAnimation(click);
                v.postDelayed(() ->
                        startActivity(new Intent(this, AddMemberActivity.class)), 150);
            });
        }
    }

    // FIX ERROR 2: et_search sekarang ada di XML → langsung bekerja
    private void setupSearch() {
        EditText etSearch = findViewById(R.id.et_search);
        if (etSearch == null) return;

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                applyFilter();
            }
        });
    }

    /**
     * FIX ERROR 3: Ganti ChipGroup → Button biasa dengan ID.
     * XML pakai <Button>, bukan <ChipGroup>, jadi disesuaikan.
     */
    private void setupCategoryFilter() {
        Button btnSemua      = findViewById(R.id.btn_cat_semua);
        Button btnGym        = findViewById(R.id.btn_cat_gym);
        Button btnBarbershop = findViewById(R.id.btn_cat_barbershop);
        Button btnLaundry    = findViewById(R.id.btn_cat_laundry);

        categoryButtons = new ArrayList<>();
        if (btnSemua != null)      categoryButtons.add(btnSemua);
        if (btnGym != null)        categoryButtons.add(btnGym);
        if (btnBarbershop != null) categoryButtons.add(btnBarbershop);
        if (btnLaundry != null)    categoryButtons.add(btnLaundry);

        for (Button btn : categoryButtons) {
            btn.setOnClickListener(v -> {
                activeCategory = ((Button) v).getText().toString();
                updateCategoryButtonState((Button) v);
                applyFilter();
            });
        }
    }

    /** Ubah tampilan button: dipilih = biru, lainnya = putih */
    private void updateCategoryButtonState(Button selected) {
        for (Button btn : categoryButtons) {
            boolean aktif = (btn == selected);
            btn.setBackgroundTintList(
                    ColorStateList.valueOf(aktif ? 0xFF4361EE : 0xFFFFFFFF)
            );
            btn.setTextColor(aktif ? 0xFFFFFFFF : 0xFF333333);
        }
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

        Cursor cursor = dbHelper.getAllMemberCards();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                memberList.add(new MemberCard(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("merchant_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("member_number")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tier")),
                        ""
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }

        memberAdapter = new org.butterflygroup.memberu.adapters.MemberAdapter(memberList, card -> {
            android.content.Intent intent = new android.content.Intent(HomeActivity.this, DetailCardActivity.class);
            intent.putExtra(org.butterflygroup.memberu.views.DetailCardActivity.EXTRA_MEMBER_CARD_ID, card.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_click, R.anim.anim_click);
        });
        rvMembers.setAdapter(memberAdapter);
    }
}
