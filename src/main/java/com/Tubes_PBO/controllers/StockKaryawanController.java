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

public class StockKaryawanController implements Initializable {

    @FXML private TableView<Product> stockTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Void> colAction;

    private ObservableList<Product> productList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        loadProducts();
    }

    private void setupTable() {
    colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
    colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
    colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

    addButtonToTable();
}

    private void loadProducts() {
        productList = FXCollections.observableArrayList(
                ProductDAO.getAllProducts()
        );
        stockTable.setItems(productList);
    }

    // ======================
    // ADD STOCK BUTTON
    // ======================
    private void addButtonToTable() {

        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btn = new Button("ADD");

            {
                btn.setOnAction(event -> {
                    Product product = getTableView()
                            .getItems()
                            .get(getIndex());

                    showAddStockDialog(product);
                });
            }

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

    // ======================
    // POPUP ADD STOCK
    // ======================
    private void showAddStockDialog(Product product) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tambah Stock");
        dialog.setHeaderText("Produk: " + product.getProductName());
        dialog.setContentText("Masukkan jumlah stock:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int tambahan = Integer.parseInt(value);

                if (tambahan <= 0) {
                    showAlert("Stock harus lebih dari 0");
                    return;
                }

                ProductDAO.addStock(product.getProductId(), tambahan);
                loadProducts(); // refresh table

            } catch (NumberFormatException e) {
                showAlert("Input harus angka");
            }
        });
    }

    // ======================
    // NAVIGATION
    // ======================
    @FXML
    private void goToHome() {
        App.changeScene("/fxml/HomeKaryawan.fxml");
    }

    @FXML
    private void goToOrders() {
        App.changeScene("/fxml/OrdersKaryawan.fxml");
    }

    // ======================
    // ALERT
    // ======================
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
private void handleLogout(ActionEvent event) {
    Node source = (Node) event.getSource();
    LogoutUtil.logout(source);
}
}
