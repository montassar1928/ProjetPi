package controllers;

import controllers.ChangePassPopUp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Users;
import services.ServiceUsers;

import java.io.IOException;

import static controllers.LoginFXML.getCurrentUser;

public class ProfileClient {

    @FXML
    private Label Birthid;

    @FXML
    private Label EmailID;

    @FXML
    private Label creationID;

    @FXML
    private ImageView imageProfile;

    @FXML
    private Text labelFirstName;

    @FXML
    private Text labelName;

    @FXML
    private Label telephoneID;

    private ServiceUsers serviceUsers;

    public ProfileClient() {
        serviceUsers = new ServiceUsers();
    }

    public void initData(Users user) {
        labelName.setText(user.getNom());
        labelFirstName.setText(user.getPrenom());
        EmailID.setText(user.getEmail());
        Birthid.setText(user.getDateNaissance().toString()); // Conversion de LocalDate en String
        creationID.setText(user.getDateCreation().toString()); // Conversion de Date en String
        telephoneID.setText(String.valueOf(user.getTelephone()));
    }

    @FXML
    private void openChangePasswordPopup(ActionEvent event) {
        try {
            // Récupérer l'utilisateur courant
            Users currentUser = getCurrentUser(); // Assurez-vous d'avoir une méthode pour récupérer l'utilisateur courant

            // Création de la fenêtre de changement de mot de passe en passant l'utilisateur courant
            ChangePassPopUp changePassPopUp = new ChangePassPopUp(currentUser);

            // Affichage de la fenêtre modale
            changePassPopUp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openDeletePopup(ActionEvent event) {
        try {
            // Récupérer l'utilisateur courant
            Users currentUser = getCurrentUser(); // Assurez-vous d'avoir une méthode pour récupérer l'utilisateur courant

            // Création de la fenêtre de suppression de compte en passant l'utilisateur courant
            DeletePopUp deletePopUp = new DeletePopUp(currentUser);

            // Affichage de la fenêtre modale
            deletePopUp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

