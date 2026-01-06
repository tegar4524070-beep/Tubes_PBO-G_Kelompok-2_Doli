package com.Tubes_PBO.controllers;

import com.Tubes_PBO.Database.UserDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleBox;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("customer", "karyawan");
    }

    @FXML
    private void handleRegister() {

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String role = roleBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || role == null) {
            showAlert("Semua field wajib diisi");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert("Password tidak sama");
            return;
        }

        boolean success = UserDAO.register(username, password, role);

        if (success) {
            showAlert("Registrasi berhasil, silakan login");
            closePopup();
        } else {
            showAlert("Username sudah digunakan");
        }
    }

    @FXML
    private void closePopup() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
