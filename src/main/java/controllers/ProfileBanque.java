package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import models.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import services.ServiceBanque;
import services.ServiceUsers;

import static controllers.LoginFXML.getCurrentUser;


public class ProfileBanque {
    @FXML

    private Button Deleteid;

    @FXML
    private Label Birthid;
    @FXML
    private Button Changepass;


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
   ServiceBanque serviceBanque ;
    public ProfileBanque() {
        serviceBanque = new ServiceBanque();
    }

    public void initData(Users user) {
        labelName.setText(user.getNom());
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
            ChangePassPopupBanque changePassPopUp = new ChangePassPopupBanque(currentUser);

            // Affichage de la fenêtre modale
            changePassPopUp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openDeletePoopup(ActionEvent event) {
        try {
            // Récupérer l'utilisateur courant
            Users currentUser = getCurrentUser(); // Assurez-vous d'avoir une méthode pour récupérer l'utilisateur courant

            // Création de la fenêtre de suppression de compte en passant l'utilisateur courant
            DeletePopUpBanque deletePopUp = new DeletePopUpBanque(currentUser);

            // Affichage de la fenêtre modale
            deletePopUp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
