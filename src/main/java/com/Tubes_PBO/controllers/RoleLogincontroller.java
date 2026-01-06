package com.Tubes_PBO.controllers;

import com.Tubes_PBO.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RoleLogincontroller {

    @FXML
    private void onCustomerClick() {
        App.setRole("customer");
        App.changeScene("/fxml/Login.fxml");
    }

    @FXML
    private void onKaryawanClick() {
        App.setRole("karyawan");
        App.changeScene("/fxml/Login.fxml");
    }

    @FXML
private void openRegisterPopup() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/RegisterPopup.fxml")
        );

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
