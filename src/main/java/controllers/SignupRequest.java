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
import javafx.scene.text.Text;
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

public class SignupRequest {
    @FXML
    private TableView<Users> SignTableView;

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
    @FXML
    private TextField searchField;


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

        serviceBanque = new ServiceBanque();
        addAcceptAndRejectButtonsToTable();


        loadUsersData();

        SignTableView.setEditable(true);
    }



    private void loadUsersData() {
        try {
            List<Users> allUsers = serviceBanque.selectAll1();
            List<Users> bankUsers = allUsers.stream()
                    .filter(user -> user.getRole() == Role.CLIENT)
                    .collect(Collectors.toList());

            usersList = FXCollections.observableArrayList(bankUsers);
            SignTableView.setItems(usersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void refresh() {
        loadUsersData();
    }
    private void addAcceptAndRejectButtonsToTable() {
        // Création des colonnes des boutons "Accepter" et "Rejeter"
        TableColumn<Users, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accepter");
            private final Button rejectButton = new Button("Rejeter");

            {
                // Logique pour accepter un utilisateur
                acceptButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    try {
                        serviceBanque.activateUser(user); // Activer l'utilisateur inactif
                        refresh(); // Rafraîchir la liste des utilisateurs après l'activation
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.err.println("Erreur lors de l'activation de l'utilisateur : " + e.getMessage());
                    }
                });


                rejectButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    // Supprimer la ligne de la TableView
                    SignTableView.getItems().remove(user);
                    System.out.println("Utilisateur rejeté : " + user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttonsContainer = new HBox(acceptButton, rejectButton);
                    buttonsContainer.setSpacing(5);
                    setGraphic(buttonsContainer);
                }
            }
        });

        // Ajout de la colonne des actions à la table
        SignTableView.getColumns().add(actionColumn);
    }

    @FXML
    private void handleSearch() {

        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            ObservableList<Users> filteredList = usersList.filtered(user ->
                    user.getNom().toLowerCase().contains(keyword.toLowerCase()) ||
                            user.getPrenom().toLowerCase().contains(keyword.toLowerCase())
            );
            SignTableView.setItems(filteredList);
        } else {
            SignTableView.setItems(usersList);
        }
    }

}






