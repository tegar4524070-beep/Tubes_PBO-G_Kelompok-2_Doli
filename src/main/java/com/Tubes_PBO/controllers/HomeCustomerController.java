package com.Tubes_PBO.controllers;

import java.util.Optional;

import com.Tubes_PBO.App;
import com.Tubes_PBO.Database.OrderDAO;
import com.Tubes_PBO.helpers.UserSession;
import com.Tubes_PBO.utils.LogoutUtil;
import com.Tubes_PBO.utils.SceneSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class HomeCustomerController {

    /* =========================
       BUTTON HEADER
       ========================= */

    @FXML
    private void ALL(ActionEvent event) {
        System.out.println("ALL clicked (Customer)");
    }

    @FXML
    private void Donuts(ActionEvent event) {
        System.out.println("DONUTS clicked (Customer)");
    }

    @FXML
    private void Order(ActionEvent event) {
        goToOrderPage();
    }

    /* =========================
       SEARCH
       ========================= */
    @FXML
    private void searchDonut(ActionEvent event) {
        showAlert("Fitur search masih dalam pengembangan");
    }

    /* =========================
       BUTTON BUY (MULTI BUY + POPUP)
       ========================= */
    @FXML
    private void buyProduct(ActionEvent event) {

        // Cek login
        if (UserSession.getUser() == null) {
            showAlert("Silakan login terlebih dahulu");
            return;
        }

        Button btn = (Button) event.getSource();
        int productId = Integer.parseInt(btn.getUserData().toString());
        int userId = UserSession.getUser().getId();

        // Popup input jumlah
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Buy Donut");
        dialog.setHeaderText("Masukkan jumlah donat");
        dialog.setContentText("Jumlah:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                int qty = Integer.parseInt(result.get());

                if (qty <= 0) {
                    showAlert("Jumlah harus lebih dari 0");
                    return;
                }

                boolean success = OrderDAO.addProductToOrder(userId, productId, qty);

                if (success) {
                    showAlert("Berhasil membeli " + qty + " donat");
                } else {
                    showAlert("Gagal menambahkan donat");
                }

            } catch (NumberFormatException e) {
                showAlert("Masukkan angka yang valid");
            }
        }
    }

    /* =========================
       NAVIGATION
       ========================= */

    @FXML
    private void goToOrderPage() {
        App.changeScene("/fxml/OrderCustomer.fxml");
    }

    @FXML
private void goToHistory(ActionEvent event) {
    App.changeScene("/fxml/HistoryCustomer.fxml");
}

    @FXML
    private void goToHome(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/HomeCustomer.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================
       ALERT HELPER
       ========================= */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
private void handleLogout(ActionEvent event) {
    Node source = (Node) event.getSource();
    LogoutUtil.logout(source);
}

}
