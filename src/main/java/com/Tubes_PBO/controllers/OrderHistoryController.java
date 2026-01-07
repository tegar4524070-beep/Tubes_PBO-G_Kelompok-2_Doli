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

// Interface : Menggunakan Initializable //
public class OrderHistoryController implements Initializable {

    // Visibility : Menggunakan Private //
    @FXML private TableView<Order> historyTable;
    // Visibility : Menggunakan Private //
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TableColumn<Order, Double> colTotal;
    @FXML private TableColumn<Order, String> colStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        if (UserSession.getUser() == null) {
            Alert alert = new Alert(AlertType.ERROR, "User belum login!");
            alert.show();
            return;
        }

        colOrderId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("totalPrice")
        );

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        // Polimorpisme : Menggunakan Node dan Callback //
        // Anonymous Class : Menggunakan Lambda Expression //
        colDate.setCellValueFactory(cell -> {
            Timestamp t = cell.getValue().getCreatedAt();
            return new SimpleStringProperty(
                t != null
                ? t.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : "-"
            );
        });

        int userId = UserSession.getUser().getId();
        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        historyTable.getItems().setAll(
                OrderDAO.getOrderHistory(userId)
        );
    }

    // Visibility : Menggunakan Private //
    @FXML
    private void goToHome() {
        App.changeScene("/fxml/HomeCustomer.fxml");
    }

    @FXML
    private void Order() {
        App.changeScene("/fxml/OrderCustomer.fxml");
    }

    // Visibility : Menggunakan Private //
    @FXML
private void handleLogout(ActionEvent event) {
    // Polimorpisme : Menggunakan Node dan Callback //
    Node source = (Node) event.getSource();
    // Asosiasi : Menggunakan DAO, Model, dan Utils //
    LogoutUtil.logout(source);
}

}
