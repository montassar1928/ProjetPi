package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import models.Role;
import models.Users;
import services.ServiceAdmin;
import services.ServiceUsers;
import utils.DBConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.ResourceBundle;

public class SignUpBank {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField addressField;

    @FXML
    private DatePicker birthdatePicker;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField idNumberField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField photoPathField;

    @FXML
    private Button signUpButton;

    @FXML
    private VBox vboxLayout;

    @FXML
    private TextField raisonSocialeField;

    private ServiceAdmin serviceAdmin;
    private Users currentUser;

    @FXML
    void initialize() {
        serviceAdmin = new ServiceAdmin();
    }


    @FXML
    void insertUser() throws SQLException {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String address = addressField.getText();
        String idNumber = idNumberField.getText();
        String phoneText = phoneField.getText();
        int phoneNumber = 0;
        String photoPath = photoPathField.getText();
        LocalDate birthdate = null;
        String raisonSociale = raisonSocialeField.getText(); // Récupération de la raison sociale
        if (userExistsWithEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Un utilisateur avec cet email existe déjà.");
            // Vous pouvez lancer une exception ou afficher un message d'erreur selon vos besoins
            return;
        }
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || idNumber.isEmpty() || birthdatePicker.getValue() == null || photoPath.isEmpty() || phoneText.isEmpty() || raisonSociale.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de l'email invalide !");
            return;
        }

        try {
            phoneNumber = Integer.parseInt(phoneText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone doit être un nombre entier !");
            return;
        }

        if (!password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe doit contenir au moins 8 caractères, dont des majuscules, des minuscules, un chiffre, et un caractère spécial !");
            return;
        }

        birthdate = birthdatePicker.getValue();
        if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez avoir au moins 18 ans pour vous inscrire !");
            return;
        }

        Users user = new Users(0, firstName, lastName, email, password, new Date(), address, raisonSociale, phoneNumber, birthdate, "", idNumber, photoBytes, Role.BANQUE); // Changement du rôle à Banque

        try {
            serviceAdmin.insertOne(user);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Banque inséré avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'insertion de Banque : " + e.getMessage());
        }
    }

    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private byte[] photoBytes;

    @FXML
    void browsePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Obtenir la fenêtre actuellement affichée
        Window window = vboxLayout.getScene().getWindow();

        // Afficher la boîte de dialogue de sélection de fichier
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            photoPathField.setText(selectedFile.getAbsolutePath());
            photoBytes = readPhoto(selectedFile);
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun fichier sélectionné.");
        }
    }

    @FXML
    public void handleCancelButtonClick() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        addressField.clear();
        idNumberField.clear();
        phoneField.clear();
        photoPathField.clear();
        raisonSocialeField.clear();
        birthdatePicker.setValue(null); // Effacer également la valeur sélectionnée dans le DatePicker
    }

    public byte[] readPhoto(File photoFile) {
        try {
            return Files.readAllBytes(photoFile.toPath());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la lecture de la photo : " + e.getMessage());
            return null;
        }
    }
    private boolean userExistsWithEmail(String email) throws SQLException {
        try {
            Connection connection = DBConnection.getInstance().getCnx();
            if (connection != null && !connection.isClosed()) {
                String query = "SELECT COUNT(*) AS count FROM users WHERE email = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, email);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            int count = resultSet.getInt("count");
                            return count > 0; // Si count est supérieur à 0, cela signifie qu'un utilisateur avec cet email existe déjà
                        }
                    }
                }
            } else {
                System.err.println("La connexion à la base de données est fermée ou invalide.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'utilisateur : " + e.getMessage());
        }
        return false;
    }


    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser ;
    }
}
