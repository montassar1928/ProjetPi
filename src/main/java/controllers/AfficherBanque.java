package controllers ;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import models.Role;
import models.Users;
import services.ServiceAdmin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherBanque {
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
    private TableColumn<Users, String> raisonSocialeColumn;

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

    private ServiceAdmin serviceAdmin;
    private ObservableList<Users> usersList;
    private Users currentUser;

    public void initialize() {
        serviceAdmin = new ServiceAdmin();

        // Initialisation des colonnes du TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateCreationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateCreation().toString()));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        raisonSocialeColumn.setCellValueFactory(new PropertyValueFactory<>("raisonSociale"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsersData();

        BanqueTableView.setEditable(true);


        addEditAndDeleteButtonsToTableView();
    }

    private void refresh() {
        loadUsersData();
    }

    private void loadUsersData() {
        try {
            List<Users> allUsers = serviceAdmin.selectAll();
            // Filtrer les utilisateurs avec le rôle "Banque"
            List<Users> bankUsers = allUsers.stream()
                    .filter(user -> user.getRole() == Role.BANQUE)
                    .collect(Collectors.toList());

            // Convertir les dates de naissance de java.sql.Date à LocalDate
            bankUsers.forEach(user -> {
                LocalDate dateNaissance = user.getDateNaissance(); // Récupérer la date de naissance
                if (dateNaissance != null) { // Vérifier si la date de naissance n'est pas nulle
                    user.setDateNaissance(dateNaissance); // Déjà une LocalDate, pas besoin de conversion
                }
            });

            usersList = FXCollections.observableArrayList(bankUsers);
            BanqueTableView.setItems(usersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addEditAndDeleteButtonsToTableView() {
        TableColumn<Users, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setPrefWidth(100);

        Callback<TableColumn<Users, Void>, TableCell<Users, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Users, Void> call(final TableColumn<Users, Void> param) {
                final TableCell<Users, Void> cell = new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.setOnAction(event -> {
                            Users user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });

                        deleteButton.setOnAction(event -> {
                            Users user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttonsBox = new HBox(5);
                            buttonsBox.getChildren().addAll(editButton, deleteButton);
                            setGraphic(buttonsBox);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);
        BanqueTableView.getColumns().add(actionColumn);
    }




    @FXML
    private void handleEditUser(Users user) {
        // Créer une boîte de dialogue pour modifier l'utilisateur
        Dialog<Users> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit User Information");

        // Créer les champs pour modifier les informations de l'utilisateur
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
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convertir le résultat de la boîte de dialogue en un objet utilisateur modifié
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                user.setNom(nomField.getText());
                user.setPrenom(prenomField.getText());
                user.setEmail(emailField.getText());
                user.setAdresse(adresseField.getText());
                user.setTelephone(Integer.parseInt(telephoneField.getText()));
                return user;
            }
            return null; // Retourner null si l'utilisateur a annulé
        });

        // Attendre que l'utilisateur confirme ou annule
        Optional<Users> result = dialog.showAndWait();
        result.ifPresent(updatedUser -> {
            try {
                // Appeler la méthode pour mettre à jour l'utilisateur dans la base de données
                serviceAdmin.updateOne(updatedUser);
                BanqueTableView.refresh();
                refresh();

                System.out.println("Utilisateur mis à jour avec succès : " + updatedUser);
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
            }
        });
    }





    @FXML
    private void handleDeleteUser(Users user) {
        try {
            // Supprimer l'utilisateur de la base de données
            serviceAdmin.deleteOne(user); // Passer usersList en tant que paramètre

            // Mettre à jour la table après la suppression

            refresh();

            System.out.println("Utilisateur supprimé avec succès : " + user);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }
    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser ;
    }


    @FXML
    public void handBanksAction(javafx.event.ActionEvent actionEvent) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/AfficherBanque.fxml"));
            javafx.scene.Parent afficherBanqueView = loader.load();

            AfficherBanque afficherBanqueController = loader.getController();
            afficherBanqueController.setCurrentUser(LoginFXML.getCurrentUser());

            javafx.scene.Scene afficherBanqueScene = new javafx.scene.Scene(afficherBanqueView);
            javafx.stage.Stage primaryStage = (javafx.stage.Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(afficherBanqueScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de AfficherBanque.fxml : " + e.getMessage());
        }
    }
}

