package com.Tubes_PBO.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.Tubes_PBO.App;
import com.Tubes_PBO.Database.OrderDAO;
import com.Tubes_PBO.models.Order;
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
import javafx.scene.control.cell.PropertyValueFactory;

// Interface : Menggunakan Initializable //
public class OrdersKaryawanController implements Initializable {

    // Visibility : Menggunakan Private //
    @FXML
    private TableView<Order> tableOrders;

    // Visibility : Menggunakan Private //
    @FXML
    private TableColumn<Order, Integer> colId;

    @FXML
    private TableColumn<Order, Integer> colCustomer;

    @FXML
    private TableColumn<Order, Double> colTotal;

    @FXML
    private TableColumn<Order, String> colStatus;

    @FXML
    private TableColumn<Order, Void> colAction;

    // Visibility : Menggunakan Private //
    private ObservableList<Order> orderList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadOrders();
    }

    // Visibility : Menggunakan Private //
    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        addActionButton();
    }

    // Visibility : Menggunakan Private //
    private void loadOrders() {
        orderList.clear();
        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        List<Order> orders = OrderDAO.getOrdersForKaryawan();
        orderList.addAll(orders);
        tableOrders.setItems(orderList);
    }

    private void addActionButton() {
        // Nested Class : Menggunakan TableCell anonymous //
        // Anonymous Class: Menggunakan new TableCell<>() dan Lambda Expression //
        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btn = new Button("Selesaikan");

            {
                btn.setStyle("""
                    -fx-background-color: #2ecc71;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                """);

                // Anonymous Class: Menggunakan new TableCell<>() dan Lambda Expression //
                btn.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    completeOrder(order);
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

   private void completeOrder(Order order) {
    // Asosiasi : Menggunakan DAO, Model, dan Utils //
    OrderDAO.completeOrder(order.getId());
    tableOrders.getItems().remove(order);


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Selesai");
        alert.setHeaderText(null);
        alert.setContentText("Order ID " + order.getId() + " berhasil diselesaikan");
        alert.showAndWait();

        loadOrders();
    }

@FXML
private void goToHome() {
    // Asosiasi : Menggunakan DAO, Model, dan Utils //
    App.changeScene("/fxml/HomeKaryawan.fxml");
}

@FXML
private void goToStock() {
    App.changeScene("/fxml/StockKaryawan.fxml");
}

@FXML
private void handleLogout(ActionEvent event) {
    // Polimorfisme : Menggunakan Override dan casting //
    Node source = (Node) event.getSource();
    // Asosiasi : Menggunakan DAO, Model, dan Utils //
    LogoutUtil.logout(source);
}

}
