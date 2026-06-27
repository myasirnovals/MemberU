package org.butterflygroup.memberu.views;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue; // Import ditambahkan untuk membaca nilai tema
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    private MainController controller;
    private DatabaseHelper dbHelper;
    private RecyclerView rvMembers;
    private MemberAdapter memberAdapter;
    private List<MemberCard> memberList;
    private List<MemberCard> filteredList;
    private List<Button> categoryButtons;
    private String activeCategory = "Semua";
    private String searchQuery = "";

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Berhasil! QR: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupWindowInsets();

        controller = new MainController(this);
        dbHelper = new DatabaseHelper(this);
        memberList = new ArrayList<>();
        filteredList = new ArrayList<>();
        categoryButtons = new ArrayList<>();

        initViews();
        setupClickListeners();
        setupBottomNavigation();
        setupSearch();
        setupCategoryFilter();
        loadDataFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDatabase();

        // Pastikan tab "Cards/Home" selalu terlihat aktif saat kembali ke halaman ini
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.nav_cards).setChecked(true);
        }
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, 0);
            return insets;
        });
    }

    private void initViews() {
        rvMembers = findViewById(R.id.rv_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        View btnQris = findViewById(R.id.btn_qris);
        if (btnQris != null) {
            btnQris.setOnClickListener(v -> {
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_bounce);
                v.startAnimation(anim);
                v.postDelayed(this::bukaKameraScanner, 150);
            });
        }

        View btnTambah = findViewById(R.id.btn_add_member);
        if (btnTambah != null) {
            btnTambah.setOnClickListener(v -> startActivity(new Intent(this, AddMemberActivity.class)));
        }

        // ── MODIFIKASI PROFIL: Menghilangkan kilatan layar ──
        View btnProfile = findViewById(R.id.btn_profile);
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // Paksa tanpa animasi
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        View btnLihatPromo = findViewById(R.id.btn_lihat_promo);
        if (btnLihatPromo != null) {
            btnLihatPromo.setOnClickListener(v -> startActivity(new Intent(this, PromoActivity.class)));
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.nav_cards).setChecked(true);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_settings) {
                    // ── MODIFIKASI PENGATURAN: Menghilangkan animasi lompatan ikon & layar ──
                    Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // Matikan animasi inten
                    startActivity(intent);
                    overridePendingTransition(0, 0); // Matikan animasi transisi

                    // Kembalikan false agar item di menu bawah tidak ter-highlight (tidak lompat membesar)
                    return false;
                }

                return true;
            });
        }
    }

    private void setupSearch() {
        EditText search = findViewById(R.id.et_search);
        if (search == null) return;

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase();
                applyFilter();
            }
        });
    }

    // ════════════ FILTER KATEGORI ════════════
    private void setupCategoryFilter() {
        Button btnSemua = findViewById(R.id.btn_cat_semua);
        Button btnGym = findViewById(R.id.btn_cat_gym);
        Button btnBarbershop = findViewById(R.id.btn_cat_barbershop);
        Button btnLaundry = findViewById(R.id.btn_cat_laundry);

        if (btnSemua != null) categoryButtons.add(btnSemua);
        if (btnGym != null) categoryButtons.add(btnGym);
        if (btnBarbershop != null) categoryButtons.add(btnBarbershop);
        if (btnLaundry != null) categoryButtons.add(btnLaundry);

        for (Button btn : categoryButtons) {
            btn.setOnClickListener(v -> {
                activeCategory = btn.getText().toString();
                updateCategoryUI();
                applyFilter();
            });
        }
        updateCategoryUI();
    }

    // ── MODIFIKASI: Tombol kategori sekarang otomatis ikut redup di Mode Gelap ──
    private void updateCategoryUI() {
        // colorPrimary adalah bawaan dari library appcompat resmi
        int colorPrimary = getThemeColor(androidx.appcompat.R.attr.colorPrimary);

        // Sisanya adalah bawaan dari library Material Design resmi
        int colorOnPrimary = getThemeColor(com.google.android.material.R.attr.colorOnPrimary);
        int colorSurfaceVariant = getThemeColor(com.google.android.material.R.attr.colorSurfaceVariant);
        int colorOnSurface = getThemeColor(com.google.android.material.R.attr.colorOnSurface);

        for (Button btn : categoryButtons) {
            if (btn.getText().toString().equalsIgnoreCase(activeCategory)) {
                // Tombol yang sedang dipilih (Aktif)
                btn.setBackgroundTintList(ColorStateList.valueOf(colorPrimary));
                btn.setTextColor(colorOnPrimary);
            } else {
                // Tombol yang tidak dipilih (Redup mengikuti background tema)
                btn.setBackgroundTintList(ColorStateList.valueOf(colorSurfaceVariant));
                btn.setTextColor(colorOnSurface);
            }
        }
    }

    // Fungsi pembantu untuk mengambil nilai warna hex dari atribut tema (?attr/...)
    private int getThemeColor(int attrResId) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attrResId, typedValue, true);
        return typedValue.data;
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
                memberList.add(
                        new MemberCard(
                                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                                cursor.getInt(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))),
                                cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("merchant_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("member_number")),
                                cursor.getString(cursor.getColumnIndexOrThrow("tier")),
                                ""
                        )
                );
            } while (cursor.moveToNext());
            cursor.close();
        }
        applyFilter();
    }

    // ════════════ PENYARINGAN GABUNGAN ════════════
    private void applyFilter() {
        filteredList.clear();

        for (MemberCard card : memberList) {
            boolean matchSearch = card.getMerchantName().toLowerCase().contains(searchQuery);

            boolean matchCategory = activeCategory.equalsIgnoreCase("Semua") ||
                    card.getCategoryName().equalsIgnoreCase(activeCategory);

            if (matchSearch && matchCategory) {
                filteredList.add(card);
            }
        }

        memberAdapter = new MemberAdapter(filteredList, card -> {
            Intent intent = new Intent(this, DetailCardActivity.class);
            intent.putExtra(DetailCardActivity.EXTRA_MEMBER_CARD_ID, card.getId());
            startActivity(intent);
        });

        rvMembers.setAdapter(memberAdapter);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}