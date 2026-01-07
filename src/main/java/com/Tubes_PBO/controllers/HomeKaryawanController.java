package com.Tubes_PBO.controllers;

import com.Tubes_PBO.App;
import com.Tubes_PBO.Database.ProductDAO;
import com.Tubes_PBO.models.Product;
import com.Tubes_PBO.utils.LogoutUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class HomeKaryawanController {

    // Visibility : Menggunakan Private //
    @FXML
    private void ALL() {
        System.out.println("ALL clicked (Karyawan)");
    }

    @FXML
    private void Donuts() {
        System.out.println("DONUTS clicked (Karyawan)");
    }

    @FXML
    private void STOCK() {
        System.out.println("STOCK clicked (Karyawan)");
    }

    @FXML
    private void Order() {
        System.out.println("ORDER clicked (Karyawan)");
    }

    private void showAlert(String msg) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setHeaderText(null);
    alert.setContentText(msg);
    alert.showAndWait();
}

    @FXML
    private void searchDonut() {
        System.out.println("SEARCH clicked (Karyawan)");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Search");
        alert.setContentText("Fitur search karyawan masih dalam proses.");
        alert.show();
    }

    @FXML
    private void Buy() {
        System.out.println("ADD clicked (Karyawan)");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Tambah Stok");
        alert.setContentText("Stok berhasil ditambahkan!");
        alert.show();
    }

    // Visibility : Menggunakan Public //
    @FXML
public void goToStock(ActionEvent event) {
    try {
        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/StockKaryawan.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Visibility : Menggunakan Private //
@FXML
private void handleAddStock(ActionEvent event) {

    Button btn = (Button) event.getSource();
    int productId = Integer.parseInt(btn.getUserData().toString());

    // Asosiasi : Menggunakan DAO, Model, dan Ultis //
    Product product = ProductDAO.getById(productId);

    if (product == null) {
        showAlert("Produk tidak ditemukan");
        return;
    }

    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Tambah Stock");
    dialog.setHeaderText("Produk: " + product.getProductName());
    dialog.setContentText("Masukkan jumlah stock:");

    // Anonymous Class : Menggunakan Lambda Expression //
    dialog.showAndWait().ifPresent(value -> {
        try {
            int tambahan = Integer.parseInt(value);

            if (tambahan <= 0) {
                showAlert("Stock harus lebih dari 0");
                return;
            }

            // Asosiasi : Menggunakan DAO, Model, dan Ultis //
            ProductDAO.addStock(productId, tambahan);
            showAlert("Stock berhasil ditambahkan");

        } catch (NumberFormatException e) {
            showAlert("Input harus berupa angka");
        }
    });
}

@FXML
private void goToOrders() {
    // Asosiasi : Menggunakan DAO, Model, dan Ultis //
    App.changeScene("/fxml/OrdersKaryawan.fxml");
}

@FXML
private void handleLogout(ActionEvent event) {
    Node source = (Node) event.getSource();
    // Asosiasi : Menggunakan DAO, Model, dan Ultis //
    LogoutUtil.logout(source);
}

}
