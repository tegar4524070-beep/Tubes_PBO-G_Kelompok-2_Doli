package com.Tubes_PBO.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.Tubes_PBO.App;
import com.Tubes_PBO.Database.ProductDAO;
import com.Tubes_PBO.models.Product;
import com.Tubes_PBO.utils.LogoutUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

// Interface : Menggunakan Initializable //
// Inheritance : Menggunakan implement //
public class StockKaryawanController implements Initializable {

    // Visibility : Menggunakan Private //
    @FXML private TableView<Product> stockTable;
    // Visibility : Menggunakan Private //
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Void> colAction;

    // Visibility : Menggunakan Private //
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        loadProducts();
    }

    // Visibility : Menggunakan Private //
    private void setupTable() {
    colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
    colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
    colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

    addButtonToTable();
}

    // Visibility : Menggunakan Private //
    private void loadProducts() {
        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        productList = FXCollections.observableArrayList(
                ProductDAO.getAllProducts()
        );
        stockTable.setItems(productList);
    }

    private void addButtonToTable() {

        // Nested Class : Menggunakan TableCell anonymous //
        // Anonymous Class: Menggunakan Anonymous dan Lambda //
        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btn = new Button("ADD");

            {
                // Anonymous Class: Menggunakan Anonymous dan Lambda //
                btn.setOnAction(event -> {
                    Product product = getTableView()
                            .getItems()
                            .get(getIndex());

                    showAddStockDialog(product);
                });
            }

            // Polimorfisme : Menggunakan Override dan casting //
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void showAddStockDialog(Product product) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tambah Stock");
        dialog.setHeaderText("Produk: " + product.getProductName());
        dialog.setContentText("Masukkan jumlah stock:");

        // Anonymous Class: Menggunakan Anonymous dan Lambda //
        dialog.showAndWait().ifPresent(value -> {
            try {
                int tambahan = Integer.parseInt(value);

                if (tambahan <= 0) {
                    showAlert("Stock harus lebih dari 0");
                    return;
                }

                // Asosiasi : Menggunakan DAO, Model, dan Utils //
                ProductDAO.addStock(product.getProductId(), tambahan);
                loadProducts();

            } catch (NumberFormatException e) {
                showAlert("Input harus angka");
            }
        });
    }

    @FXML
    private void goToHome() {
        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        App.changeScene("/fxml/HomeKaryawan.fxml");
    }

    @FXML
    private void goToOrders() {
        App.changeScene("/fxml/OrdersKaryawan.fxml");
    }

    // Visibility : Menggunakan Private //
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
private void handleLogout(ActionEvent event) {
    // Polimorfisme : Menggunakan Override dan casting //
    Node source = (Node) event.getSource();
    // Asosiasi : Menggunakan DAO, Model, dan Utils //
    LogoutUtil.logout(source);
}
}
