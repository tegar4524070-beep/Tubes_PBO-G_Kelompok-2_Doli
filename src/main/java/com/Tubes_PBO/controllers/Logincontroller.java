package com.Tubes_PBO.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.Tubes_PBO.App;
import com.Tubes_PBO.Database.DatabaseConection;
import com.Tubes_PBO.helpers.UserSession;
import com.Tubes_PBO.models.User;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Logincontroller {

    // Visibility : Menggunakan Private //
    @FXML
    private TextField usernameField;

    // Visibility : Menggunakan Private //
    @FXML
    private PasswordField passwordField;

    // Visibility : Menggunakan Private //
    @FXML
    private void login() {

        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = App.getRole();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password wajib diisi!");
            return;
        }

        if (role == null) {
            showError("Role belum dipilih!");
            return;
        }

        String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";

        // Asosiasi : Menggunakan DB, Model, dan Session //
        try (Connection con = DatabaseConection.getConnection();
        // Asosiasi : Menggunakan DB, Model, dan Session //
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            // Asosiasi : Menggunakan DB, Model, dan Session //
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // Asosiasi : Menggunakan DB, Model, dan Session //
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));

                // Asosiasi : Menggunakan DB, Model, dan Session //
                UserSession.setUser(user);

                // Asosiasi : Menggunakan DB, Model, dan Session //
                if (role.equals("customer")) {
                    App.changeScene("/fxml/HomeCustomer.fxml");
                } else {
                    App.changeScene("/fxml/HomeKaryawan.fxml");
                }

            } else {
                showError("Username atau password salah!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Terjadi kesalahan saat login!");
        }
    }

    // Visibility : Menggunakan Private //
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login Gagal");
        alert.setContentText(msg);
        alert.show();
    }

    
}
