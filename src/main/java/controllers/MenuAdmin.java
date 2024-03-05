package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuAdmin {

    @FXML
    private void handleLogoutAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            Stage primaryStage = new Stage();
            primaryStage.setScene(loginScene);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handBanksAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherBanque.fxml"));
            Parent afficherBanqueView = loader.load();

            // Passer l'utilisateur au contrôleur AfficherBanque
            AfficherBanque afficherBanqueController = loader.getController();
            // On suppose que getCurrentUser() est une méthode publique statique
            afficherBanqueController.setCurrentUser(LoginFXML.getCurrentUser()); // Utilisez le getter ici

            Scene afficherBanqueScene = new Scene(afficherBanqueView);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(afficherBanqueScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de AfficherBanque.fxml : " + e.getMessage());
        }
    }

    @FXML
    public void handSignUpBank(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter Banque.fxml"));
            Parent afficherBanqueView = loader.load();

            // Passer l'utilisateur au contrôleur SignUpBank
            SignUpBank signController = loader.getController();
            // On suppose que getCurrentUser() est une méthode publique statique
            signController.setCurrentUser(LoginFXML.getCurrentUser());

            Scene afficherBanqueScene = new Scene(afficherBanqueView);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(afficherBanqueScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de Ajouter Banque.fxml : " + e.getMessage());
        }
    }
    @FXML
    public void handleProfileAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileAdmin.fxml"));
            Parent profileView = loader.load();

            // Passer l'utilisateur au contrôleur ProfileClient
            ProfileAdmin profileController = loader.getController();
            profileController.initData(LoginFXML.getCurrentUser()); //

            Scene profileScene = new Scene(profileView);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(profileScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ProfileClient.fxml : " + e.getMessage());
        }
    }

}
