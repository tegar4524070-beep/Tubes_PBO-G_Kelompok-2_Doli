package com.Tubes_PBO.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogoutUtil {

    public static void logout(Node node) {
        try {
            SessionUser.clear();

            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(
                FXMLLoader.load(
                    LogoutUtil.class.getResource("/fxml/login1.fxml")
                )
            );
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
