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

public class Logincontroller {   // 🔥 HURUF BESAR BENAR

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

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

        try (Connection con = DatabaseConection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // 🔥 SIMPAN USER KE SESSION
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));

                UserSession.setUser(user); // 🔥 INI YANG KEMARIN NULL

                // 🔥 PINDAH HALAMAN
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

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login Gagal");
        alert.setContentText(msg);
        alert.show();
    }

    
}
