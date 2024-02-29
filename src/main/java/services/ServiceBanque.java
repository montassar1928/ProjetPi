package services;

import models.Role;
import models.Users;
import org.mindrot.jbcrypt.BCrypt;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;




public class ServiceBanque {
    private Connection cnx ;



    public ServiceBanque() {
        cnx = DBConnection.getInstance().getCnx();
    }
    public  Users getBanqueByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ? AND role = 'BANQUE'";
        PreparedStatement statement = cnx.prepareStatement(query);
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String nom = resultSet.getString("nom");
            String prenom = resultSet.getString("prenom");
            String password = resultSet.getString("password");
            Date dateCreation = resultSet.getDate("date_creation");
            String adresse = resultSet.getString("adresse");
            String raisonSociale = resultSet.getString("Raison_Sociale");
            int telephone = resultSet.getInt("telephone");
            LocalDate dateNaissance = resultSet.getDate("dateDeNaissance").toLocalDate();
            String statut = resultSet.getString("statut");
            String cin = resultSet.getString("cin");
            byte[] photo = resultSet.getBytes("photo");
            Role role = Role.BANQUE; // Vous pouvez définir le rôle directement ici

            return new Users(id, nom, prenom, email, password, dateCreation, adresse, raisonSociale, telephone, dateNaissance, statut, cin, photo, role);
        }

        // Retourne null si aucun administrateur n'est trouvé avec l'e-mail donné
        return null;
    }

    public List<Users> selectAll() throws SQLException {
        List<Users> usersList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE Role = ? AND statut = ?";

        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setString(1, Role.CLIENT.toString()); // Utiliser Role.CLIENT pour les utilisateurs ayant un rôle de client
            statement.setString(2, "Actif");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Date dateCreation = resultSet.getDate("date_creation");
                    String adresse = resultSet.getString("adresse");
                    String raisonSociale = resultSet.getString("Raison_Sociale");
                    int telephone = resultSet.getInt("telephone");

                    // Vérifier si la date de naissance est nulle
                    LocalDate dateNaissance = null;
                    Date dateObject = resultSet.getDate("dateDeNaissance");
                    if (dateObject != null) {
                        dateNaissance = dateObject.toLocalDate();
                    }

                    String statut = resultSet.getString("statut");
                    String cin = resultSet.getString("cin");
                    byte[] photo = resultSet.getBytes("photo");
                    Role role = Role.CLIENT; // Utiliser directement Role.CLIENT

                    Users user = new Users(id, nom, prenom, email, password, dateCreation, adresse,
                            raisonSociale, telephone, dateNaissance, statut, cin, photo, role);
                    usersList.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return usersList;
    }
    public void deleteOne(Users user) throws SQLException {
        String sql = "UPDATE users SET statut = 'Inactif' WHERE id = ?";
        try {
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setInt(1, user.getId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le compte utilisateur a été rendu inactif avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la désactivation du compte utilisateur : " + e.getMessage());
        }
    }
    public void updateUserInfo(Users user) throws SQLException {
        String sql = "UPDATE users SET nom = ?, prenom = ?, email = ?, adresse = ?, dateDeNaissance = ? WHERE id = ?";
        try {
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getAdresse());
            statement.setDate(5, Date.valueOf(user.getDateNaissance()));
            statement.setInt(6, user.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Informations utilisateur mises à jour avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour des informations utilisateur : " + e.getMessage());
            throw e; // Relancer l'exception pour une gestion appropriée
        }
    }
    public List<Users> selectAll1() throws SQLException {
        List<Users> usersList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE Role = ? AND statut = ?";

        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setString(1, Role.CLIENT.toString()); // Utiliser Role.CLIENT pour les utilisateurs ayant un rôle de client
            statement.setString(2, "Inactif"); // Changer le statut pour rechercher les utilisateurs inactifs

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Date dateCreation = resultSet.getDate("date_creation");
                    String adresse = resultSet.getString("adresse");
                    String raisonSociale = resultSet.getString("Raison_Sociale");
                    int telephone = resultSet.getInt("telephone");

                    // Vérifier si la date de naissance est nulle
                    LocalDate dateNaissance = null;
                    Date dateObject = resultSet.getDate("dateDeNaissance");
                    if (dateObject != null) {
                        dateNaissance = dateObject.toLocalDate();
                    }

                    String statut = resultSet.getString("statut");
                    String cin = resultSet.getString("cin");
                    byte[] photo = resultSet.getBytes("photo");
                    Role role = Role.CLIENT; // Utiliser directement Role.CLIENT

                    Users user = new Users(id, nom, prenom, email, password, dateCreation, adresse,
                            raisonSociale, telephone, dateNaissance, statut, cin, photo, role);
                    usersList.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return usersList;
    }

    public void activateUser(Users user) throws SQLException {
        String sql = "UPDATE users SET statut = 'Actif' WHERE id = ?";
        try {
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setInt(1, user.getId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le compte utilisateur a été activé avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'activation du compte utilisateur : " + e.getMessage());
        }
    }
    public void updatePass(Users user) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            // Hasher le nouveau mot de passe
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            // Paramètres de la requête SQL
            statement.setString(1, hashedPassword);
            statement.setInt(2, user.getId()); // Assurez-vous que l'ID de l'utilisateur est correctement défini

            // Exécution de la requête de mise à jour
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Le mot de passe de l'utilisateur a été mis à jour avec succès.");
            } else {
                System.out.println("Échec de la mise à jour du mot de passe de l'utilisateur.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe de l'utilisateur : " + e.getMessage());
            e.printStackTrace(); // Ajoutez ceci pour obtenir une trace complète de l'exception
        }
    }

}

