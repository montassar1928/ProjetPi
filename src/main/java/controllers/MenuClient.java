package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static controllers.LoginFXML.getCurrentUser;

public class MenuClient implements Initializable {

    @FXML
    private Button profile_btn;
    @FXML
    private Button Dash_btn;

    private Parent profileView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger la vue du profil
      //*  loadProfileView();
    }

  /*  private void loadProfileView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileClient.fxml"));
            profileView = loader.load();
            profileController = loader.getController();
            System.out.println("Chargement de ProfileClient.fxml réussi.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ProfileClient.fxml : " + e.getMessage());
        }
    }*/


    @FXML
    private void handleDashboardAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClient.fxml"));
            Parent dashboardView = loader.load();

            // Créer une nouvelle scène avec la vue du tableau de bord
            Scene dashboardScene = new Scene(dashboardView);

            // Obtenir la fenêtre actuelle à partir de n'importe quel nœud de la scène
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer la scène actuelle par la nouvelle scène du tableau de bord
            primaryStage.setScene(dashboardScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de DashboardClient.fxml : " + e.getMessage());
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
    public void handleProfileAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileClient.fxml"));
            Parent profileView = loader.load();

            // Passer l'utilisateur au contrôleur ProfileClient
            ProfileClient profileController = loader.getController();
            // On suppose que getCurrentUser() est une méthode publique statique
            profileController.initData(LoginFXML.getCurrentUser()); // Utilisez le getter ici

            Scene profileScene = new Scene(profileView);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(profileScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ProfileClient.fxml : " + e.getMessage());
        }
    }

}





