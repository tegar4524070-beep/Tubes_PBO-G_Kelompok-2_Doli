package com.Tubes_PBO.controllers;

import com.Tubes_PBO.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RoleLogincontroller {

    // Visibility : Menggunakan Private //
    @FXML
    private void onCustomerClick() {
        // Asosiasi : Menggunakan App, Stage, dan FXMLLoader //
        App.setRole("customer");
        // Asosiasi : Menggunakan App, Stage, dan FXMLLoader //
        App.changeScene("/fxml/Login.fxml");
    }

    // Visibility : Menggunakan Private //
    @FXML
    private void onKaryawanClick() {
        App.setRole("karyawan");
        // Asosiasi : Menggunakan App, Stage, dan FXMLLoader //
        App.changeScene("/fxml/Login.fxml");
    }

    // Visibility : Menggunakan Private //
    @FXML
private void openRegisterPopup() {
    try {
        // Asosiasi : Menggunakan App, Stage, dan FXMLLoader //
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/RegisterPopup.fxml")
        );

        // Asosiasi : Menggunakan App, Stage, dan FXMLLoader //
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(loader.load()));
        popupStage.setTitle("Registrasi");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setResizable(false);
        popupStage.showAndWait();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
