package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene; // Ajout de l'importation de Scene
import models.Users;
import services.ServiceUsers;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

public class DeletePopUp {

    private Stage stage;
    private Users currentUser;
    private ServiceUsers serviceUsers;
    private PasswordField passwordField; // Déclaration du champ passwordField

    public DeletePopUp(Users currentUser) {
        this.currentUser = currentUser;
        this.serviceUsers = new ServiceUsers();

        // Créer la fenêtre de suppression de compte
        stage = new Stage();
        stage.setTitle("Delete Account");
        stage.initModality(Modality.APPLICATION_MODAL);

        // Créer les éléments de l'interface utilisateur
        VBox root = new VBox();
        passwordField = new PasswordField(); // Initialisation du passwordField
        passwordField.setPromptText("Enter your password");
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> handleConfirm());
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> stage.close());
        root.getChildren().addAll(passwordField, confirmButton, cancelButton);

        // Afficher la entry modale
        stage.setScene(new Scene(root, 300, 200));
    }

    public void show() {
        stage.showAndWait();
    }

    private void handleConfirm() {
        String enteredPassword = passwordField.getText();
        if (BCrypt.checkpw(enteredPassword, currentUser.getPassword())) {
            try {
                // Mettre à jour le statut du compte de l'utilisateur à "inactif"
                currentUser.setStatut("Inactif");
                serviceUsers.updateOne(currentUser);

                // Afficher une notification de suppression réussie
                showAlert("Account Deleted", "Your account has been successfully deleted.");

                // Fermer la fenêtre de suppression de compte
                stage.close();
                redirectToLoginPage();
            } catch (SQLException ex) {
                showAlert("Error", "An error occurred while deleting your account.");
                ex.printStackTrace();
            }
        } else {
            showAlert("Error", "Incorrect password. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void redirectToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.show();

            // Fermer la fenêtre actuelle (suppression de compte)
            Stage currentStage = (Stage) passwordField.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            showAlert("Error", "An error occurred while loading the login page.");
            ex.printStackTrace();
        }
    }
}

