package controllers ;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import models.Role;
import models.Users;
import services.ServiceAdmin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    SignUPFXML emailexist = new SignUPFXML();


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
                            dialog.close(); // Fermer la boîte de dialogue
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
            serviceAdmin.deleteOne(user);


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
            FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/AfficherBanque.fxml"));
            Parent afficherBanqueView = loader.load();

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

