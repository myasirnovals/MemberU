package org.butterflygroup.memberu.controllers;

import org.butterflygroup.memberu.views.MainView;

public class MainController {
    private final MainView view;

    public MainController(MainView view) {
        this.view = view;
    }

    public void handleQrisClicked() {
        view.showMessage("Hore! Tombol QRIS diproses dari Controller!");
    }
}