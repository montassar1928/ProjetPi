package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import models.Role;
import models.Users;
import services.ServiceBanque;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BankClients {
    @FXML
    private TableView<Users> BanqueTableView;

    @FXML
    private TableColumn<Users, Integer> idColumn;

    @FXML
    private TableColumn<Users, String> nomColumn;

    @FXML
    private TableColumn<Users, String> prenomColumn;

    @FXML
    private TableColumn<Users, String> emailColumn;

    @FXML
    private TableColumn<Users, String> dateCreationColumn;

    @FXML
    private TableColumn<Users, String> adresseColumn;

    @FXML
    private TableColumn<Users, Integer> telephoneColumn;

    @FXML
    private TableColumn<Users, LocalDate> dateNaissanceColumn;

    @FXML
    private TableColumn<Users, String> statutColumn;

    @FXML
    private TableColumn<Users, String> cinColumn;

    @FXML
    private TableColumn<Users, Role> roleColumn;

    private ServiceBanque serviceBanque;
    private ObservableList<Users> usersList;

    public  void setCurrentUser(Users currentUser) {
    }
    SignUPFXML emailexist = new SignUPFXML();



    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateCreationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateCreation().toString()));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));


        serviceBanque = new ServiceBanque();

            addEditAndDeleteButtonsToTable();


        // Load data into TableView
       refresh();

        BanqueTableView.setEditable(true);
    }


    private void loadUsersData() {
        try {
            List<Users> allUsers = serviceBanque.selectAll();
            List<Users> bankUsers = allUsers.stream()
                    .filter(user -> user.getRole() == Role.CLIENT)
                    .collect(Collectors.toList());

            usersList = FXCollections.observableArrayList(bankUsers);
            BanqueTableView.setItems(usersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



  /*  @FXML
    private void handleDeleteUser(Users user) {
        try {
            serviceBanque.deleteOne(user);
            refresh();
            System.out.println("Utilisateur supprimé avec succès : " + user);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }*/

    private void refresh() {
        loadUsersData();
        BanqueTableView.refresh();
    }
    @FXML
    private TextField searchField;

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            ObservableList<Users> filteredList = usersList.filtered(user ->
                            user.getNom().toLowerCase().contains(keyword.toLowerCase()) ||
                                    user.getPrenom().toLowerCase().contains(keyword.toLowerCase()) ||
                                    user.getEmail().toLowerCase().contains(keyword.toLowerCase())
            );
            BanqueTableView.setItems(filteredList);
        } else {
            BanqueTableView.setItems(usersList);
        }
    }

    @FXML
    private void handleEditUser(Users user) {
//popup
        Dialog<Users> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit User Information");

        // Créer les champs pour modifier les informations de l'utilisateur avec les valeurs existantes
        TextField nomField = new TextField(user.getNom());
        TextField prenomField = new TextField(user.getPrenom());
        TextField emailField = new TextField(user.getEmail());
        TextField adresseField = new TextField(user.getAdresse());
        TextField telephoneField = new TextField(String.valueOf(user.getTelephone()));

        // Créer la disposition de la boîte de dialogue
        GridPane grid = new GridPane();
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Adresse:"), 0, 3);
        grid.add(adresseField, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4);
        grid.add(telephoneField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Ajouter les boutons de confirmation et d'annulation
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Désactiver le bouton de confirmation initialement
        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        // Logique de validation des champs
        Runnable validateFields = () -> {
            boolean allFieldsValid = !nomField.getText().trim().isEmpty() &&
                    !prenomField.getText().trim().isEmpty() &&
                    !adresseField.getText().trim().isEmpty() &&
                    !telephoneField.getText().trim().isEmpty() && // Vérification que le numéro de téléphone n'est pas vide
                    telephoneField.getText().matches("\\d*") && // Vérification que le téléphone contient uniquement des chiffres
                    emailField.getText().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|fr)"); // Validation simple de l'email

            confirmButton.setDisable(!allFieldsValid);
        };

        // Attacher la logique de validation aux propriétés de texte de chaque champ
        nomField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        prenomField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        adresseField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        telephoneField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());

        // Initialiser la validation pour refléter l'état initial des champs
        validateFields.run();

        // Stocker l'email initial pour comparer plus tard
        String initialEmail = emailField.getText();

        // Définir la conversion du résultat en cliquant sur le bouton de confirmation
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                // Vérifier si l'e-mail a été modifié
                if (!initialEmail.equals(emailField.getText())) {
                    // Vérification de l'existence de l'e-mail
                    try {
                        if (emailexist.userExistsWithEmail(emailField.getText())) {
                            emailexist.showAlert(Alert.AlertType.ERROR, "Erreur", "Un utilisateur avec cet email existe déjà.");
                            return null; // Empêche la fermeture de la boîte de dialogue
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null; // En cas d'erreur, empêcher la fermeture
                    }
                }

                // Construire l'utilisateur mis à jour et le retourner
                user.setNom(nomField.getText());
                user.setPrenom(prenomField.getText());
                user.setEmail(emailField.getText());
                user.setAdresse(adresseField.getText());
                user.setTelephone(Integer.parseInt(telephoneField.getText()));
                return user; // Retourner l'utilisateur mis à jour
            }

            return null; // Ne rien faire si l'utilisateur annule
        });

        // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
        Optional<Users> result = dialog.showAndWait();
        result.ifPresent(updatedUser -> {
            try {
                // Appeler la méthode pour mettre à jour l'utilisateur dans la base de données
                serviceBanque.updateUserInfo(updatedUser);
                BanqueTableView.refresh();
                refresh();

                System.out.println("Utilisateur mis à jour avec succès : " + updatedUser);
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
            }
        });
    }

    private void addEditAndDeleteButtonsToTable() {
        // Création des boutons "Edit" et "Delete" dans une nouvelle colonne
        TableColumn<Users, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                deleteButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    try {
                        serviceBanque.deleteOne(user);
                        refresh();
                        System.out.println("Utilisateur supprimé avec succès : " + user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttonsContainer = new HBox(editButton, deleteButton);
                    buttonsContainer.setSpacing(5);
                    setGraphic(buttonsContainer);
                }
            }
        });

        // Ajout de la colonne des actions à la table
        BanqueTableView.getColumns().add(actionColumn);
    }

}


