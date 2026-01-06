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

public class OrderCustomerController implements Initializable {

    // ======================
    // FXML COMPONENT
    // ======================
    @FXML private Label orderNumber;
    @FXML private Label orderTime;
    @FXML private Label orderChannel;
    @FXML private Label paymentMethod;
    @FXML private Label totalLabel;
    @FXML private VBox itemsContainer;

    @FXML private Button buyButton;
    @FXML private Button cancelButton;

    // ======================
    // STATE
    // ======================
    private Order currentOrder;

    // ======================
    // INITIALIZE
    // ======================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (UserSession.getUser() == null) {
            System.out.println("❌ USER SESSION NULL!");
            showEmptyOrder();
            disableButtons();
            return;
        }

        int userId = UserSession.getUser().getId();
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

    // ======================
    // LOAD ORDER INFO
    // ======================
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

    // ======================
    // LOAD ORDER ITEMS
    // ======================
    private void loadOrderItems() {
        itemsContainer.getChildren().clear();

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

    // ======================
    // BUY / PAY
    // ======================
    @FXML
private void payOrder() {

    if (currentOrder == null) {
        showInfo("Tidak ada order untuk dibayar");
        return;
    }

    // Dialog utama
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Metode Pembayaran");
    dialog.setHeaderText("Pilih metode pembayaran");

    ButtonType btnOk = new ButtonType("Bayar", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

    // =========================
    // UI CONTENT
    // =========================
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

    // =========================
    // ACTION
    // =========================
    dialog.showAndWait().ifPresent(result -> {

        if (result != btnOk) return;

        // CASH
        if (rbCash.isSelected()) {
            OrderDAO.closeOrder(currentOrder.getId());
            showInfo("Pembayaran cash berhasil");
            resetOrder();
            return;
        }

        // TRANSFER
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

        // sukses transfer
        OrderDAO.closeOrder(currentOrder.getId());
        showInfo("Transfer via " + bankBox.getValue() + " berhasil");
        resetOrder();
    });
}


    // ======================
    // CANCEL ORDER
    // ======================
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

    // ======================
    // RESET UI
    // ======================
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

    // ======================
    // NAVIGATION
    // ======================
    @FXML
    private void goToHome() {
        App.changeScene("/fxml/HomeCustomer.fxml");
    }

    // ======================
    // ALERT
    // ======================
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
private void goToHistory() {
    App.changeScene("/fxml/HistoryCustomer.fxml");
}

@FXML
private void handleLogout(ActionEvent event) {
    Node source = (Node) event.getSource();
    LogoutUtil.logout(source);
}


}
