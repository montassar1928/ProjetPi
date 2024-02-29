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

        // Set cell factories for editable columns

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
    private void handleEditUser(Users user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit User Information");

        // Création des champs pour la saisie des informations
        TextField nomField = new TextField(user.getNom());
        TextField prenomField = new TextField(user.getPrenom());
        TextField emailField = new TextField(user.getEmail());
        TextField adresseField = new TextField(user.getAdresse());
        TextField telephoneField = new TextField(String.valueOf(user.getTelephone()));

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

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true); // Commencez avec le bouton de confirmation désactivé

        // Logique de validation
        Runnable validateFields = () -> {
            boolean allFieldsValid = !nomField.getText().trim().isEmpty() &&
                    !prenomField.getText().trim().isEmpty() &&
                    !emailField.getText().trim().isEmpty() &&
                    !adresseField.getText().trim().isEmpty() &&
                    telephoneField.getText().matches("\\d*"); // Simple vérification de chiffres
            confirmButton.setDisable(!allFieldsValid);
        };

        // Attachez la logique de validation aux propriétés de texte
        nomField.textProperty().addListener((obs, old, newVal) -> validateFields.run());
        prenomField.textProperty().addListener((obs, old, newVal) -> validateFields.run());
        emailField.textProperty().addListener((obs, old, newVal) -> validateFields.run());
        adresseField.textProperty().addListener((obs, old, newVal) -> validateFields.run());
        telephoneField.textProperty().addListener((obs, old, newVal) -> validateFields.run());

        // Initialisez la validation
        validateFields.run();

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            // Mise à jour de l'utilisateur
            user.setNom(nomField.getText());
            user.setPrenom(prenomField.getText());
            user.setEmail(emailField.getText());
            user.setAdresse(adresseField.getText());
            try {
                user.setTelephone(Integer.parseInt(telephoneField.getText()));
            } catch (NumberFormatException e) {
                // Gérer les erreurs de parsing potentielles ici
            }

            try {
                serviceBanque.updateUserInfo(user); // Met à jour les informations utilisateur dans la base de données
                BanqueTableView.refresh();
                refresh(); // Rafraîchit la liste des utilisateurs affichée dans la table
                System.out.println("Utilisateur modifié avec succès : " + user);
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            }
        } else {
            // Le résultat n'était pas présent ou était faux, traiter en conséquence
            System.out.println("La modification de l'utilisateur a été annulée.");
        }
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


