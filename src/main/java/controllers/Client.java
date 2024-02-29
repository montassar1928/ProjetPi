package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class Client {
    @FXML
    private static BorderPane mainBorderPane; // Déclarer mainBorderPane comme statique

    public static BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    // Méthode statique pour initialiser mainBorderPane
    public static void setMainBorderPane(BorderPane borderPane) {
        mainBorderPane = borderPane;
    }
}

