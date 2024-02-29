package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Users;
import org.mindrot.jbcrypt.BCrypt;
import services.ServiceAdmin;
import services.ServiceBanque;
import services.ServiceUsers;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFXML {
    private static Users currentUser;

    // Méthode pour récupérer l'utilisateur connecté
    public static Users getCurrentUser() {
        return currentUser;
    }

    public TextField plainTextField;
    @FXML
    private TextField EmailField;

    @FXML
    private PasswordField PasswordField;
    @FXML

    private Button LoginBtn;


    @FXML
    private CheckBox ShowPass;

    @FXML
    private Button createButton;

    DBConnection cnx = DBConnection.getInstance();
    Connection connection = cnx.getCnx();
    private ServiceUsers serviceUsers = new ServiceUsers(); // Initialisez votre service ici
    private ServiceAdmin serviceAdmin= new ServiceAdmin();
    private ServiceBanque serviceBanque= new ServiceBanque();

    public static void setCurrentUser(Users currentUser) {
        LoginFXML.currentUser = currentUser;
    }

    @FXML
    void initialize() {
        // Ajoutez un gestionnaire d'événements pour le clic sur le bouton de création de compte
        createButton.setOnAction(event -> handleCreateAccountButtonClick());

        // Ajoutez un gestionnaire d'événements pour la case à cocher "Show Password"
        ShowPass.setOnAction(event -> handleShowPassCheckboxClick());
        plainTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (ShowPass.isSelected()) {
                PasswordField.setText(newValue);
            }
        });

        PasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (ShowPass.isSelected()) {
                plainTextField.setText(newValue);
            }
        });
    }


    @FXML
    void Connexion(ActionEvent event) {
        String email = EmailField.getText();
        String password = PasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

        String requete = "SELECT id, statut, role, password FROM users WHERE email = ?";
        try (PreparedStatement prepare = connection.prepareStatement(requete)) {
            prepare.setString(1, email);

            try (ResultSet result = prepare.executeQuery()) {
                if (result.next()) {
                    String hashedPassword = result.getString("password");
                    String role = result.getString("role");
                    String statut = result.getString("statut");

                    if (BCrypt.checkpw(password, hashedPassword)) {
                        if (statut.equals("Inactif")) {
                            showAlert("Compte Inactif", "Votre compte est inactif. Veuillez contacter l'administrateur.");
                            return; // Ajout de return pour sortir de la méthode si le compte est inactif
                        }
                        if (role.equals("ADMIN")) {
                            showAlert("Connexion réussie", "Bienvenue, admin !");
                            currentUser = new ServiceAdmin().getAdminByEmail(email);

                            setCurrentUser(currentUser);

                            adminScene(event,email);
                        } else if (role.equals("CLIENT")) {
                            showAlert("Connexion réussie", "Bienvenue !");
                            // Récupérer l'utilisateur actuel après connexion réussie
                            setCurrentUser(currentUser);
                            currentUser = serviceUsers.getClientByEmail(email);
                            clientScene(event, email);
                        }
                        else if (role.equals("BANQUE")) {
                            showAlert("Connexion réussie", "Bienvenue Banque !");
                            currentUser = serviceBanque.getBanqueByEmail(email);
                            setCurrentUser(currentUser);
                            BanqueScene(event, email); }
                        else {
                            showAlert("Accès non autorisé", "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
                        }
                    } else {
                        showAlert("Connexion échouée", "Email ou mot de passe incorrect !");
                    }
                } else {
                    showAlert("Connexion échouée", "Email ou mot de passe incorrect !");
                }
            }
        } catch (SQLException ex) {
            showAlert("Erreur de connexion", "Un problème est survenu lors de la tentative de connexion. Veuillez réessayer plus tard.");
            ex.printStackTrace();
        }
    }

    private void clientScene(ActionEvent event, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileClient.fxml"));
            Parent root = loader.load();
            ProfileClient controller = loader.getController();

            Users client = serviceUsers.getClientByEmail(email);
            if (client != null) {
                controller.initData(client);
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                showAlert("Erreur de chargement", "Impossible de charger les informations du client.");
            }
        } catch (IOException ex) {
            showAlert("Erreur de chargement", "Une erreur est survenue lors du chargement de la page de profil du client.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            showAlert("Erreur de base de données", "Une erreur est survenue lors de la récupération des informations du client depuis la base de données.");
            ex.printStackTrace();
        }
    }


    @FXML
    void handleShowPassCheckboxClick() {
        if (ShowPass.isSelected()) {
            // Afficher le mot de passe en clair
            plainTextField.setText(PasswordField.getText());
            plainTextField.setVisible(true);
            PasswordField.setVisible(false);
        } else {
            // Masquer le mot de passe en clair et montrer le PasswordField à nouveau
            PasswordField.setText(plainTextField.getText());
            PasswordField.setVisible(true);
            plainTextField.setVisible(false);
        }
    }

    public void handleCreateAccountButtonClick() {
        // Charger la vue de la page d'inscription depuis le fichier FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUP.fxml"));
            Parent signupRoot = loader.load();

            // Créer une nouvelle scène avec la vue de la page d'inscription
            Scene signupScene = new Scene(signupRoot);

            // Récupérer la fenêtre principale (stage) à partir du bouton de création de compte
            Stage primaryStage = (Stage) createButton.getScene().getWindow();

            // Changer la scène pour afficher celle de la page d'inscription
            primaryStage.setScene(signupScene);
        } catch (IOException e) {
            e.printStackTrace(); // Gérer les éventuelles erreurs de chargement du fichier FXML
        } }


    public void showAlert(String title, String message) {
        // Créer une nouvelle alerte
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); // Définir le titre de l'alerte
        alert.setHeaderText(null); // Pas d'en-tête spécifique
        alert.setContentText(message); // Définir le message de l'alerte

        // Afficher l'alerte et attendre que l'utilisateur la ferme
        alert.showAndWait();
    }

    private void adminScene(ActionEvent event, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileAdmin.fxml"));
            Parent root = loader.load();
            ProfileAdmin controller = loader.getController();

            Users Admin  = serviceAdmin.getAdminByEmail(email);
            if (Admin != null) {
                controller.initData(Admin);
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                showAlert("Erreur de chargement", "Impossible de charger les informations de l'administrateur.");
            }
        } catch (IOException ex) {
            showAlert("Erreur de chargement", "Une erreur est survenue lors du chargement de la page de profil de l'administrateur.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            showAlert("Erreur de base de données", "Une erreur est survenue lors de la récupération des informations de l'administrateur depuis la base de données.");
            ex.printStackTrace();
        }
    }
    private void BanqueScene(ActionEvent event, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileBanque.fxml"));
            Parent root = loader.load();
            ProfileBanque controller = loader.getController();

            Users Banque  = serviceBanque.getBanqueByEmail(email);
            if (Banque != null) {
                controller.initData(Banque);
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                showAlert("Erreur de chargement", "Impossible de charger les informations de l'administrateur.");
            }
        } catch (IOException ex) {
            showAlert("Erreur de chargement", "Une erreur est survenue lors du chargement de la page de profil de l'administrateur.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            showAlert("Erreur de base de données", "Une erreur est survenue lors de la récupération des informations de l'administrateur depuis la base de données.");
            ex.printStackTrace();
        }
    }
}