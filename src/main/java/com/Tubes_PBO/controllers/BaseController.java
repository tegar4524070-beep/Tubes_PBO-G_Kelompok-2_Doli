package com.Tubes_PBO.controllers;

import javafx.scene.control.Alert;

public abstract class BaseController {

    protected void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}