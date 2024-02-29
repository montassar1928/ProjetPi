package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuBanque {
    @FXML
    public void handleProfileAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileBanque.fxml"));
            Parent profileView = loader.load();

            // Passer l'utilisateur au contrôleur ProfileClient
            ProfileBanque profileController = loader.getController();
            // On suppose que getCurrentUser() est une méthode publique statique
            profileController.initData(LoginFXML.getCurrentUser());

            Scene profileScene = new Scene(profileView);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(profileScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ProfileBanque.fxml : " + e.getMessage());
        }
    }




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
    public void handBankClients(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BankClients.fxml"));
            Parent afficherBanqueView = loader.load();

            // Passer l'utilisateur au contrôleur BankClients
            BankClients bankClients = loader.getController();
            // On suppose que getCurrentUser() est une méthode publique statique
            bankClients.setCurrentUser(LoginFXML.getCurrentUser()); // Utilisez le getter ici

            Scene afficherBanqueScene = new Scene(afficherBanqueView);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(afficherBanqueScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de BanqueClients.fxml : " + e.getMessage());
        }
    }
    @FXML
    public void handSignupRequest(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignupRequest.fxml"));
            Parent signupRequestview = loader.load();

            // Passer l'utilisateur au contrôleur BankClients
            SignupRequest signupRequest = loader.getController();

            signupRequest.setCurrentUser(LoginFXML.getCurrentUser());

            Scene afficherBanqueScene = new Scene(signupRequestview);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(afficherBanqueScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de signupRequestview.fxml : " + e.getMessage());
        }
    }


}
