package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Role;
import models.Users;
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

public class SignUPFXML {

    public FontAwesomeIconView UploadButtonid;
    @FXML
    private ResourceBundle resources;
    private Users user;
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

    private ServiceUsers servicePerson;
    DBConnection cnx = DBConnection.getInstance();
    Connection connection = cnx.getCnx();

    @FXML
    void initialize() {
        servicePerson = new ServiceUsers();
    }

    @FXML
    void insertUser() throws SQLException {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String address = addressField.getText();
        String idNumber = idNumberField.getText();
        String phoneText = phoneField.getText(); // Champ de texte pour le numéro de téléphone
        int phoneNumber = 0;
        String photoPath = photoPathField.getText();
        LocalDate birthdate = null;
        if (userExistsWithEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Un utilisateur avec cet email existe déjà.");
            // Vous pouvez lancer une exception ou afficher un message d'erreur selon vos besoins
            return;
        }
        // Vérifier si tous les champs sont remplis
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || idNumber.isEmpty() || birthdatePicker.getValue() == null || photoPath.isEmpty() || phoneText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs !");
            return; // Sortir de la méthode s'il y a des champs vides
        }

        // Vérification du format de l'email
        // Vérification du format de l'email avec les domaines spécifiques
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|fr)")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de l'email invalide ! Veuillez utiliser un email avec un domaine .com ou .fr.");
            return;
        }

        // Conversion du numéro de téléphone et vérification
        try {
            phoneNumber = Integer.parseInt(phoneText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone doit être un nombre entier !");
            return; // Sortir de la méthode si le numéro de téléphone est invalide
        }

        // Vérification de la force du mot de passe
        if (!password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}")) {
            String errorMessage = "Le mot de passe doit contenir au moins 8 caractères, incluant :";

            if (!password.matches(".*[0-9].*")) {
                errorMessage += "\n- Un chiffre";
            }
            if (!password.matches(".*[a-z].*")) {
                errorMessage += "\n- Une lettre minuscule";
            }
            if (!password.matches(".*[A-Z].*")) {
                errorMessage += "\n- Une lettre majuscule";
            }
            if (!password.matches(".*[@#$%^&+=].*")) {
                errorMessage += "\n- Un caractère spécial";
            }
            showAlert(Alert.AlertType.ERROR, "Erreur", errorMessage);
            return;
        }

        // Vérification de l'âge de l'utilisateur
        birthdate = birthdatePicker.getValue();
        if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez avoir au moins 18 ans pour vous inscrire !");
            return;
        }

        // Initialisation de la raison sociale à null si le rôle est "client"
        Users user = new Users(0, firstName, lastName, email, password, new Date(), address, null, phoneNumber, birthdate,"",idNumber,photoBytes , Role.CLIENT);

        try {
            servicePerson.insertOne(user);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur inséré avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
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
    void browsePhoto(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            photoPathField.setText(selectedFile.getAbsolutePath()); // Display the file path in the text field
            photoBytes = readPhoto(selectedFile); // Read the photo into a byte array
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun fichier sélectionné.");
        }
    }
    public void handleCancelButtonClick() {
        // Charger la vue de la page de connexion depuis le fichier FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent loginRoot = loader.load();

            // Créer une nouvelle scène avec la vue de la page de connexion
            Scene loginScene = new Scene(loginRoot);

            // Récupérer la fenêtre principale (stage) à partir du bouton Cancel
            Stage primaryStage = (Stage) cancelButton.getScene().getWindow();

            // Changer la scène pour afficher celle de la page de connexion
            primaryStage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace(); // Gérer les éventuelles erreurs de chargement du fichier FXML
        }
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


}

