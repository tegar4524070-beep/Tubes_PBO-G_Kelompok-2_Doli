package com.Tubes_PBO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;
    private static String role;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        changeScene("/fxml/Login1.fxml");
        primaryStage.setTitle("Donat Keliling");
        primaryStage.show();
    }

    public static void changeScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            primaryStage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            System.out.println("❌ ERROR load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void setRole(String r) {
        role = r;
    }

    public static String getRole() {
        return role;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
