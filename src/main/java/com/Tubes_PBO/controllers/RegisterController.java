package com.Tubes_PBO.controllers;

import com.Tubes_PBO.Database.UserDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    // Visibility : Menggunakan Private //
    @FXML private TextField usernameField;
    // Visibility : Menggunakan Private //
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    // Visibility : Menggunakan Private //
    @FXML private ComboBox<String> roleBox;

    // Visibility : Menggunakan Public //
    @FXML
    public void initialize() {
        roleBox.getItems().addAll("customer", "karyawan");
    }

    // Visibility : Menggunakan Private //
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

        // Asosiasi : Menggunakan UserDAO, Alert, dan Stage //
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
        // Asosiasi : Menggunakan UserDAO, Alert, dan Stage //
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    // Visibility : Menggunakan Private //
    private void showAlert(String msg) {
        // Asosiasi : Menggunakan UserDAO, Alert, dan Stage //
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
