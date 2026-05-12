package org.butterflygroup.memberu.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.butterflygroup.memberu.R;
import org.butterflygroup.memberu.controllers.MainController;

public class HomeActivity extends AppCompatActivity implements MainView {
    private MainController controller;

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
            tombolQris.setOnClickListener(v -> controller.handleQrisClicked());
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}