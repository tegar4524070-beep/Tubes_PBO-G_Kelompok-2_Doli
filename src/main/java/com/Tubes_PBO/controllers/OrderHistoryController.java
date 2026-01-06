package com.Tubes_PBO.controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.Tubes_PBO.App;
import com.Tubes_PBO.Database.OrderDAO;
import com.Tubes_PBO.helpers.UserSession;
import com.Tubes_PBO.models.Order;
import com.Tubes_PBO.utils.LogoutUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrderHistoryController implements Initializable {

    @FXML private TableView<Order> historyTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TableColumn<Order, Double> colTotal;
    @FXML private TableColumn<Order, String> colStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (UserSession.getUser() == null) {
            Alert alert = new Alert(AlertType.ERROR, "User belum login!");
            alert.show();
            return;
        }

        // Mapping kolom
        colOrderId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("totalPrice")
        );

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        colDate.setCellValueFactory(cell -> {
            Timestamp t = cell.getValue().getCreatedAt();
            return new SimpleStringProperty(
                t != null
                ? t.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : "-"
            );
        });

        // Load data
        int userId = UserSession.getUser().getId();
        historyTable.getItems().setAll(
                OrderDAO.getOrderHistory(userId)
        );
    }

    // ================= NAVIGATION =================
    @FXML
    private void goToHome() {
        App.changeScene("/fxml/HomeCustomer.fxml");
    }

    @FXML
    private void Order() {
        App.changeScene("/fxml/OrderCustomer.fxml");
    }

    @FXML
private void handleLogout(ActionEvent event) {
    Node source = (Node) event.getSource();
    LogoutUtil.logout(source);
}
}
