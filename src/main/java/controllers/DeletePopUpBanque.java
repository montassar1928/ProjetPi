package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Users;
import org.mindrot.jbcrypt.BCrypt;
import services.ServiceAdmin;
import services.ServiceBanque;

import java.io.IOException;
import java.sql.SQLException;

import static controllers.LoginFXML.getCurrentUser;

public class DeletePopUpBanque {
    private Stage stage;
    private Users currentUser;
    private ServiceBanque serviceBanque;
    private PasswordField passwordField; // Déclaration du champ passwordField

    public DeletePopUpBanque(Users currentUser) {
        this.currentUser = currentUser;
        this.serviceBanque = new ServiceBanque();

        stage = new Stage();
        stage.setTitle("Delete Account");
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        passwordField = new PasswordField(); // Initialisation du passwordField
        passwordField.setPromptText("Enter your password");
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> handleConfirm());
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> stage.close());
        root.getChildren().addAll(passwordField, confirmButton, cancelButton);

        stage.setScene(new Scene(root, 300, 200));
    }

    public void show() {
        stage.showAndWait();
    }

    private void handleConfirm() {
        String enteredPassword = passwordField.getText();
        if (BCrypt.checkpw(enteredPassword, currentUser.getPassword())) {
            try {
                currentUser.setStatut("Inactif");
                serviceBanque.deleteOne(currentUser);

                showAlert("Account Deleted", "Your account has been successfully deleted.");

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
    @FXML
    private void openDeletePopup(ActionEvent event) {
        try {
            Users currentUser = getCurrentUser(); // Assurez-vous d'avoir une méthode pour récupérer l'utilisateur courant

            DeletePopUpAdmin deletePopUp = new DeletePopUpAdmin(currentUser);

            deletePopUp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
