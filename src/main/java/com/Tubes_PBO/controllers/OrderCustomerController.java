package com.Tubes_PBO.controllers;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.Tubes_PBO.App;
import com.Tubes_PBO.Database.OrderDAO;
import com.Tubes_PBO.Database.OrderItemDAO;
import com.Tubes_PBO.helpers.UserSession;
import com.Tubes_PBO.models.Order;
import com.Tubes_PBO.models.OrderItem;
import com.Tubes_PBO.utils.LogoutUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

// Interface : Menggunakan Initializable //
public class OrderCustomerController implements Initializable {

    // Visibility : Menggunakan Private //
    @FXML private Label orderNumber;
    @FXML private Label orderTime;
    @FXML private Label orderChannel;
    @FXML private Label paymentMethod;
    @FXML private Label totalLabel;
    @FXML private VBox itemsContainer;

    // Visibility : Menggunakan Private //
    @FXML private Button buyButton;
    @FXML private Button cancelButton;

    // Visibility : Menggunakan Private //
    private Order currentOrder;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        if (UserSession.getUser() == null) {
            System.out.println("❌ USER SESSION NULL!");
            showEmptyOrder();
            disableButtons();
            return;
        }

        int userId = UserSession.getUser().getId();
        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        currentOrder = OrderDAO.getPendingOrderByUser(userId);

        if (currentOrder == null) {
            showEmptyOrder();
            disableButtons();
            return;
        }

        loadOrderInfo();
        loadOrderItems();
        enableButtons();
    }

    // Visibility : Menggunakan Private //
    private void loadOrderInfo() {
        orderNumber.setText("ORDER #" + currentOrder.getId());

        if (currentOrder.getCreatedAt() != null) {
            orderTime.setText(
                currentOrder.getCreatedAt()
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
            );
        } else {
            orderTime.setText("-");
        }

        orderChannel.setText("Offline");
        paymentMethod.setText("Cash");
        totalLabel.setText("Rp " + currentOrder.getTotalPrice());
    }

    private void loadOrderItems() {
        itemsContainer.getChildren().clear();

        // Asosiasi : Menggunakan DAO, Model, dan Utils //
        for (OrderItem item : OrderItemDAO.getItemsByOrderId(currentOrder.getId())) {
            Label label = new Label(
                item.getProductName() +
                " x" + item.getQuantity() +
                " = Rp " + item.getSubtotal()
            );
            itemsContainer.getChildren().add(label);
        }

        if (itemsContainer.getChildren().isEmpty()) {
            itemsContainer.getChildren()
                .add(new Label("Belum ada item"));
        }
    }

    @FXML
private void payOrder() {

    if (currentOrder == null) {
        showInfo("Tidak ada order untuk dibayar");
        return;
    }

    // Polimorfisme : Menggunakan Node, Dialog, dan runtime //
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Metode Pembayaran");
    dialog.setHeaderText("Pilih metode pembayaran");

    ButtonType btnOk = new ButtonType("Bayar", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

    ToggleGroup paymentGroup = new ToggleGroup();
    RadioButton rbCash = new RadioButton("Cash");
    RadioButton rbTransfer = new RadioButton("Transfer Bank");

    rbCash.setToggleGroup(paymentGroup);
    rbTransfer.setToggleGroup(paymentGroup);
    rbCash.setSelected(true);

    ComboBox<String> bankBox = new ComboBox<>();
    bankBox.getItems().addAll("BCA", "Mandiri");
    bankBox.setDisable(true);

    TextField amountField = new TextField();
    amountField.setPromptText("Masukkan nominal transfer");
    amountField.setDisable(true);

    // Anonymous Class : Menggunakan Lambda EventHandler //
    rbTransfer.setOnAction(e -> {
        bankBox.setDisable(false);
        amountField.setDisable(false);
    });

    rbCash.setOnAction(e -> {
        bankBox.setDisable(true);
        amountField.setDisable(true);
    });

    VBox content = new VBox(10,
            rbCash,
            rbTransfer,
            new Label("Bank"),
            bankBox,
            new Label("Nominal"),
            amountField
    );
    dialog.getDialogPane().setContent(content);

    // Anonymous Class : Menggunakan Lambda Expression //
    dialog.showAndWait().ifPresent(result -> {

        if (result != btnOk) return;

        if (rbCash.isSelected()) {
            OrderDAO.closeOrder(currentOrder.getId());
            showInfo("Pembayaran cash berhasil");
            resetOrder();
            return;
        }

        if (bankBox.getValue() == null) {
            showInfo("Pilih bank terlebih dahulu");
            return;
        }

        double bayar;
        try {
            bayar = Double.parseDouble(amountField.getText());
        } catch (Exception e) {
            showInfo("Nominal tidak valid");
            return;
        }

        if (bayar < currentOrder.getTotalPrice()) {
            showInfo("Nominal kurang dari total pembayaran");
            return;
        }

        OrderDAO.closeOrder(currentOrder.getId());
        showInfo("Transfer via " + bankBox.getValue() + " berhasil");
        resetOrder();
    });
}

    @FXML
    private void cancelOrder() {

        if (currentOrder == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Batalkan Pesanan");
        confirm.setHeaderText("Yakin ingin membatalkan pesanan?");
        confirm.setContentText("Pesanan akan dibatalkan");

        confirm.showAndWait().ifPresent(result -> {
            if (result.getButtonData().isDefaultButton()) {
                OrderDAO.cancelOrder(currentOrder.getId());
                showInfo("Pesanan dibatalkan");
                resetOrder();
            }
        });
    }

    private void resetOrder() {
        currentOrder = null;
        showEmptyOrder();
        disableButtons();
    }

    private void showEmptyOrder() {
        itemsContainer.getChildren().clear();
        itemsContainer.getChildren().add(new Label("Belum ada order"));

        orderNumber.setText("-");
        orderTime.setText("-");
        orderChannel.setText("-");
        paymentMethod.setText("-");
        totalLabel.setText("Rp 0");
    }

    private void disableButtons() {
        buyButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void enableButtons() {
        buyButton.setDisable(false);
        cancelButton.setDisable(false);
    }

    @FXML
    private void goToHome() {
        App.changeScene("/fxml/HomeCustomer.fxml");
    }

    // Visibility : Menggunakan Private //
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
private void goToHistory() {
    // Asosiasi : Menggunakan DAO, Model, dan Utils //
    App.changeScene("/fxml/HistoryCustomer.fxml");
}

@FXML
private void handleLogout(ActionEvent event) {
    // Polimorfisme : Menggunakan Node, Dialog, dan runtime //
    Node source = (Node) event.getSource();
    // Asosiasi : Menggunakan DAO, Model, dan Utils //
    LogoutUtil.logout(source);
}

}
