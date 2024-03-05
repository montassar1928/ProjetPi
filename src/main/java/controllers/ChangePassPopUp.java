package controllers;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Users;
import org.mindrot.jbcrypt.BCrypt;
import services.ServiceUsers;

import java.sql.SQLException;

public class ChangePassPopUp {
    private PasswordField oldPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmNewPasswordField;
    private Button submitButton;
    private Stage popupStage;
    private ServiceUsers serviceUsers;
    private Users currentUser; // Variable pour stocker l'utilisateur courant

    // Constructeur prenant l'utilisateur courant en argument
    public ChangePassPopUp(Users currentUser) {
        this.currentUser = currentUser;
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Change Password");

        serviceUsers = new ServiceUsers(); // Initialisation de ServiceUsers

        // Initialize UI components
        oldPasswordField = new PasswordField();
        newPasswordField = new PasswordField();
        confirmNewPasswordField = new PasswordField();
        submitButton = new Button("Submit");
        submitButton.setOnAction(this::onSubmitButtonClick);

        // Create layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, new Label("Old Password:"), oldPasswordField);
        gridPane.addRow(1, new Label("New Password:"), newPasswordField);
        gridPane.addRow(2, new Label("Confirm New Password:"), confirmNewPasswordField);
        gridPane.addRow(3, submitButton);

        // Add layout to scene
        popupStage.setScene(new Scene(gridPane, 300, 200));
    }

    public void show() {
        popupStage.showAndWait();
    }

    private void onSubmitButtonClick(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmNewPassword = confirmNewPasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // Validation du format du nouveau mot de passe
        if (!newPassword.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}")) {
            showAlert("Erreur", "Le nouveau mot de passe doit contenir au moins 8 caractères, dont des majuscules, des minuscules, un chiffre, et un caractère spécial !");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            showAlert("Erreur", "Le nouveau mot de passe et la confirmation ne correspondent pas.");
            return;
        }

        Users currentUser = LoginFXML.getCurrentUser();
        if (!BCrypt.checkpw(oldPassword, currentUser.getPassword())) {
            showAlert("Erreur", "L'ancien mot de passe est incorrect.");
            return;
        }

        try {
            currentUser.setPassword(newPassword);
            serviceUsers.updateOne(currentUser);

            currentUser = serviceUsers.getClientByEmail(currentUser.getEmail());

            LoginFXML.setCurrentUser(currentUser);

            showAlert("Succès", "Le mot de passe a été mis à jour avec succès.");
            popupStage.close();
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour du mot de passe.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


